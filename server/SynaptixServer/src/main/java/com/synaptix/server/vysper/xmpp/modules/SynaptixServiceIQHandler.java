package com.synaptix.server.vysper.xmpp.modules;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.vysper.xml.fragment.XMLElement;
import org.apache.vysper.xml.fragment.XMLText;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.modules.core.base.handler.DefaultIQHandler;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.protocol.StreamErrorCondition;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.SessionState;
import org.apache.vysper.xmpp.server.response.ServerErrorResponses;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

import com.synaptix.server.vysper.xmpp.hack.IStateStanzaHandler;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServiceException;
import com.synaptix.service.ServicesManager;
import com.synaptix.service.annotations.NotAuthenticate;
import com.synaptix.smackx.service.ConverterStringUtils;

public class SynaptixServiceIQHandler extends DefaultIQHandler implements IStateStanzaHandler {

	private static final Log LOG = LogFactory.getLog(SynaptixServiceIQHandler.class);

	public static final String SYNAPTIX_SERVICE_IQ = "synaptix.service:iq";

	private List<IServiceExecutionObserver> serviceExecutionObservers;

	public SynaptixServiceIQHandler() {
		super();

		serviceExecutionObservers = new ArrayList<IServiceExecutionObserver>();
	}

	public synchronized void addServiceExecutionObserver(IServiceExecutionObserver serviceExecutionObserver) {
		serviceExecutionObservers.add(serviceExecutionObserver);
	}

	public synchronized void removeServiceExecutionObserver(IServiceExecutionObserver serviceExecutionObserver) {
		serviceExecutionObservers.remove(serviceExecutionObserver);
	}

	protected void fireStartExecution(String id, IQStanza stanza, String serviceFactoryName, String serviceName, String methodName, Class<?>[] argTypes, Object[] args, SessionContext sessionContext) {
		for (IServiceExecutionObserver serviceExecutionObserver : serviceExecutionObservers) {
			serviceExecutionObserver.startExecution(id, stanza, serviceFactoryName, serviceName, methodName, argTypes, args, sessionContext);
		}
	}

	protected void fireEndExecution(String id, Object result) {
		for (IServiceExecutionObserver serviceExecutionObserver : serviceExecutionObservers) {
			serviceExecutionObserver.endExecution(id, result);
		}
	}

	@Override
	protected boolean verifyNamespace(Stanza stanza) {
		return verifyInnerNamespace(stanza, SYNAPTIX_SERVICE_IQ);
	}

	@Override
	public boolean checkState(SessionContext sessionContext, SessionStateHolder sessionStateHolder, Stanza stanza) {
		return SessionState.AUTHENTICATED.equals(sessionContext.getState()) || SessionState.ENCRYPTED.equals(sessionContext.getState());
	}

	@Override
	protected Stanza handleGet(IQStanza stanza, ServerRuntimeContext serverRuntimeContext, SessionContext sessionContext) {
		XMLElement requestXMLElement = stanza.getFirstInnerElement().getFirstInnerElement();
		if (requestXMLElement != null && "request".equals(requestXMLElement.getName())) {
			String serviceFactoryName = requestXMLElement.getAttributeValue("serviceFactoryName");
			String serviceName = requestXMLElement.getAttributeValue("serviceName");
			String methodName = requestXMLElement.getAttributeValue("methodName");

			Entity entity = sessionContext.getInitiatingEntity();

			StringBuilder idSb = new StringBuilder(stanza.getID());
			if (entity != null) {
				idSb.append("_").append(entity.getFullQualifiedName()).toString();
			}
			String id = idSb.toString();

			try {
				XMLElement argTypesXMLElement = requestXMLElement.getSingleInnerElementsNamed("argTypes", SYNAPTIX_SERVICE_IQ);
				Class<?>[] argTypes = (Class<?>[]) ConverterStringUtils.stringToObject(argTypesXMLElement.getInnerText().getText());

				XMLElement argsXMLElement = requestXMLElement.getSingleInnerElementsNamed("args", SYNAPTIX_SERVICE_IQ);
				List<Arg> args = parseArgs(argsXMLElement);

				Object[] arg2s = null;

				if (args != null && args.size() > 0) {
					arg2s = new Object[args.size()];

					for (int i = 0; i < args.size(); i++) {
						Arg arg = args.get(i);

						if (arg.callback) {
							Class<?> clazz = argTypes[i];
							arg2s[i] = Proxy.newProxyInstance(SynaptixServiceIQHandler.class.getClassLoader(), new Class<?>[] { clazz }, new CallbackInvocationHandler(stanza, serverRuntimeContext,
									sessionContext, i));
						} else {
							arg2s[i] = arg.value;
						}
					}
				}

				IServiceFactory serviceFactory = ServicesManager.getInstance().getServiceFactory(serviceFactoryName);

				Class<?> serviceClass = getClass().getClassLoader().loadClass(serviceName);
				Object service = serviceFactory.getService(serviceClass);
				Method subMethod = serviceClass.getMethod(methodName, argTypes);

				// Est ce que la méthode à besoin d'être authentifié ?
				if (!subMethod.isAnnotationPresent(NotAuthenticate.class) && !SessionState.AUTHENTICATED.equals(sessionContext.getState())) {
					Stanza errorStanza = ServerErrorResponses.getStreamError(StreamErrorCondition.NOT_AUTHORIZED, sessionContext.getXMLLang(), "could not process incoming stanza", stanza);
					return errorStanza;
				}

				Object result = null;
				try {
					fireStartExecution(id, stanza, serviceFactoryName, serviceName, methodName, argTypes, arg2s, sessionContext);

					result = sendMessage(serviceFactory, service, subMethod, argTypes, arg2s);
				} catch (Throwable t) {
					if (t instanceof ServiceException) {
						ServiceException se = (ServiceException) t;
						if (t.getCause() != null) {
							if (LOG.isDebugEnabled()) {
								LOG.debug(t, t);
							}

							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							t.getCause().printStackTrace(pw);
							Exception cause = new Exception(sw.toString());

							try {
								pw.close();
							} catch (Exception e1) {
							}
							try {
								pw.close();
								sw.close();
							} catch (Exception e1) {
							}

							result = new ServiceException(se.getCode(), se.getMessage(), cause);
						} else {
							if (LOG.isDebugEnabled()) {
								LOG.debug(t, null);
							}

							result = t;
						}
					} else {
						LOG.error(t, t);

						Throwable exception = t;

						ServiceException se = findServiceException(t);
						if (se != null) {
							if (se.getCause() != null) {
								exception = se.getCause();
								SQLException de = findDaoException(se);
								if (de != null) {
									if (de.getCause() != null) {
										exception = de.getCause();
									} else {
										exception = de;
									}
								}
							} else {
								exception = null;
							}
						}

						if (exception != null) {
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							exception.printStackTrace(pw);
							result = new Exception(sw.toString());

							try {
								pw.close();
							} catch (Exception e1) {
							}
							try {
								pw.close();
								sw.close();
							} catch (Exception e1) {
							}
						}

						if (se != null) {
							result = new ServiceException(se.getCode(), se.getMessage(), (Exception) result);
						}
					}
				}

				String resultString = ConverterStringUtils.objectToString(result);

				fireEndExecution(id, result);

				StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
				stanzaBuilder.startInnerElement("query", SYNAPTIX_SERVICE_IQ);
				stanzaBuilder.startInnerElement("result", SYNAPTIX_SERVICE_IQ);
				stanzaBuilder.addText(resultString);
				stanzaBuilder.endInnerElement();
				stanzaBuilder.endInnerElement();

				return stanzaBuilder.build();
			} catch (Exception e) {
				LOG.error(e, e);

				String resultString;

				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Exception result = new Exception(sw.toString());
				try {
					resultString = ConverterStringUtils.objectToString(result);
				} catch (Exception e2) {
					LOG.error("ConverterStringUtils.objectToString(res) " + sessionContext.getInitiatingEntity());
					resultString = null;
				}

				StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
				stanzaBuilder.startInnerElement("query", SYNAPTIX_SERVICE_IQ);
				stanzaBuilder.startInnerElement("result", SYNAPTIX_SERVICE_IQ);
				stanzaBuilder.addText(resultString);
				stanzaBuilder.endInnerElement();
				stanzaBuilder.endInnerElement();

				return stanzaBuilder.build();
			}
		}

		return super.handleGet(stanza, serverRuntimeContext, sessionContext);
	}

	private ServiceException findServiceException(Throwable e) {
		Throwable current = e;
		while (current != null && !(current instanceof ServiceException)) {
			current = current.getCause();
		}
		return (ServiceException) current;
	}

	private SQLException findDaoException(Throwable e) {
		Throwable current = e;
		while (current != null && !(current instanceof SQLException)) {
			current = current.getCause();
		}
		return (SQLException) current;
	}

	private Object sendMessage(IServiceFactory serviceFactory, Object service, Method subMethod, Class<?>[] argTypes, Object[] args) throws Throwable {
		Object res = null;
		try {
			res = subMethod.invoke(service, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		return res;
	}

	// private boolean verifyAuth(Class<?> serviceClass, Method method) {
	// boolean res = true;
	// ServiceAuth methodServiceAuth = method.getAnnotation(ServiceAuth.class);
	// if (methodServiceAuth != null) {
	// res = verifyAuth(methodServiceAuth);
	// } else {
	// ServiceAuth classServiceAuth =
	// serviceClass.getAnnotation(ServiceAuth.class);
	// if (classServiceAuth != null) {
	// res = verifyAuth(classServiceAuth);
	// }
	// }
	// return res;
	// }
	//
	// private boolean verifyAuth(ServiceAuth serviceAuth) {
	// return getAuthsBundleManager().hasAuth(serviceAuth.authsBundle(),
	// serviceAuth.key().object(), serviceAuth.key().action());
	// }

	private List<Arg> parseArgs(XMLElement argsXMLElement) throws Exception {
		List<Arg> res = new ArrayList<Arg>();

		List<XMLElement> argXMLElements = argsXMLElement.getInnerElementsNamed("arg", SYNAPTIX_SERVICE_IQ);
		if (argXMLElements != null && !argXMLElements.isEmpty()) {
			for (XMLElement argXMLElement : argXMLElements) {
				Arg arg = new Arg();
				arg.callback = Boolean.parseBoolean(argXMLElement.getAttributeValue("callback"));
				if (!arg.callback) {
					XMLText xmlText = argXMLElement.getInnerText();
					if (xmlText != null) {
						arg.value = ConverterStringUtils.stringToObject(xmlText.getText());
					} else {
						arg.value = null;
					}
				} else {
					arg.value = null;
				}
				res.add(arg);
			}
		}
		return res;
	}

	private static class Arg {

		public boolean callback;

		public Object value;

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
