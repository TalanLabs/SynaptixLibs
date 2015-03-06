package com.synaptix.mybatis.component.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.mapper.IComponentMapper.Param;
import com.synaptix.mybatis.component.mapper.IComponentMapper.Provider;
import com.synaptix.mybatis.component.mapper.IComponentMapper.Select;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.helper.SelectSqlMappedStatement;

class ComponentMapperProxy<E extends IComponentMapper> implements InvocationHandler {

	private static final Log LOG = LogFactory.getLog(ComponentMapperProxy.class);

	private final IDaoSession daoSession;

	private final SelectSqlMappedStatement selectSqlMappedStatement;

	private final Class<E> componentMapperClass;

	private Map<Method, MethodMapper> methodMapperMap;

	public ComponentMapperProxy(IDaoSession daoSession, SelectSqlMappedStatement selectSqlMappedStatement, Class<E> componentMapperClass) {
		super();

		this.daoSession = daoSession;
		this.selectSqlMappedStatement = selectSqlMappedStatement;
		this.componentMapperClass = componentMapperClass;

		methodMapperMap = new HashMap<Method, MethodMapper>();

		for (Method method : componentMapperClass.getMethods()) {
			boolean ok = false;

			Class<?> returnType = method.getReturnType();
			if (returnType != null) {
				if (IComponent.class.isAssignableFrom(returnType)) {
					ok = true;
				} else if (List.class.isAssignableFrom(returnType)) {
					if (method.getGenericReturnType() instanceof ParameterizedType) {
						ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
						if (pt.getActualTypeArguments() != null && pt.getActualTypeArguments().length == 1 && pt.getActualTypeArguments()[0] instanceof Class
								&& IComponent.class.isAssignableFrom((Class<?>) pt.getActualTypeArguments()[0])) {
							ok = true;
						}
					}
				}
			}

			if (ok) {
				if (method.isAnnotationPresent(Select.class)) {
					Select selectAnnotation = method.getAnnotation(Select.class);
					methodMapperMap.put(method, new MethodMapper(method, selectAnnotation.value()));
				} else if (method.isAnnotationPresent(Provider.class)) {
					Provider providerAnnotation = method.getAnnotation(Provider.class);
					Class<?> providerClass = providerAnnotation.value();
					// providerClass.getMethod(method.getName(),
					// method.getParameterTypes());
					// methodMapperMap.put(method, new MethodMapper(method,
					// selectAnnotation.value()));
				} else {

				}
			} else {
				LOG.error("Method is invalide for " + method);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// if (!sqlMap.containsKey(method)) {
		// throw new RuntimeException("Not call method " + method);
		// }
		// String sql = sqlMap.get(method);
		//
		// Class<?> returnType = method.getReturnType();
		// if (IComponent.class.isAssignableFrom(returnType)) {
		// Class<? extends IComponent> componentClass = (Class<? extends
		// IComponent>) returnType;
		// MappedStatement mappedStatement =
		// selectSqlMappedStatement.getSelectSqlMappedStatement(componentClass,
		// sql);
		// return daoSession.getSqlSession().<E>
		// selectOne(mappedStatement.getId(), buildParameter(method, args));
		// } else if (List.class.isAssignableFrom(returnType)) {
		// if (method.getGenericReturnType() instanceof ParameterizedType) {
		// ParameterizedType pt = (ParameterizedType)
		// method.getGenericReturnType();
		// if (pt.getActualTypeArguments() != null &&
		// pt.getActualTypeArguments().length == 1 &&
		// pt.getActualTypeArguments()[0] instanceof Class
		// && IComponent.class.isAssignableFrom((Class<?>)
		// pt.getActualTypeArguments()[0])) {
		// Class<? extends IComponent> componentClass = (Class<? extends
		// IComponent>) pt.getActualTypeArguments()[0];
		// MappedStatement mappedStatement =
		// selectSqlMappedStatement.getSelectSqlMappedStatement(componentClass,
		// sql);
		// return daoSession.getSqlSession().<E>
		// selectList(mappedStatement.getId(), buildParameter(method, args));
		// }
		// }
		// }
		throw new RuntimeException("Method not return IComponent or List<IComponent> " + method);
	}

	private Object buildParameter(Method method, Object[] args) {
		Object res = null;
		if (args.length == 1) {
			res = args[0];
		} else if (args.length > 1) {
			Map<String, Object> map = new HashMap<String, Object>();

			for (int i = 0; i < args.length; i++) {
				Param param = findParamAnnotation(method.getParameterAnnotations()[i]);
				if (param == null) {
					throw new RuntimeException("Method not return IComponent or List<IComponent> " + method);
				}
				map.put(param.value(), args[i]);
			}

			res = map;
		}
		return res;
	}

	private Param findParamAnnotation(Annotation[] as) {
		Param res = null;
		if (as != null) {
			int i = 0;
			while (i < as.length && res == null) {
				if (as[i] instanceof Param) {
					res = (Param) as[i];
				}
			}
		}
		return res;
	}
}
