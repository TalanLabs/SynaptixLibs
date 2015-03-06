package com.synaptix.mybatis.filter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.synaptix.service.filter.AbstractNode;

public class NodeProcessFactory {

	private final Injector injector;

	private Map<Class<? extends AbstractNode>, AbstractNodeProcess<?>> nodeProcessMap;

	private boolean initialize;

	@Inject
	public NodeProcessFactory(Injector injector) {
		super();

		this.injector = injector;

		nodeProcessMap = new HashMap<Class<? extends AbstractNode>, AbstractNodeProcess<?>>();
		initialize = true;
	}

	private synchronized void initNodeMap() {
		synchronized (nodeProcessMap) {
			if (initialize) {
				for (Binding<?> binding : injector.getBindings().values()) {
					Type type = binding.getKey().getTypeLiteral().getType();
					if (type instanceof Class) {
						Class<?> beanClass = (Class<?>) type;
						AbstractNodeProcess.NodeProcess nodeProcessAnnotation = beanClass.getAnnotation(AbstractNodeProcess.NodeProcess.class);
						if (nodeProcessAnnotation != null && AbstractNodeProcess.class.isAssignableFrom(beanClass)) {
							AbstractNodeProcess<?> nodeProcess = (AbstractNodeProcess<?>) injector.getInstance(beanClass);

							nodeProcessMap.put(nodeProcessAnnotation.value(), nodeProcess);
						}
					}
				}

				initialize = false;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <F extends AbstractNode, G extends AbstractNodeProcess<F>> G getNodeProcess(F node) {
		initNodeMap();
		assert node != null;
		G nodeProcess = (G) nodeProcessMap.get(node.getClass());
		if (nodeProcess == null) {
			throw new IllegalArgumentException("Not exist a node process for " + node.getClass());
		}
		return nodeProcess;
	}

}
