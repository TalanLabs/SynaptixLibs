package com.synaptix.component.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.component.IComputedFactory;

public class DefaultComputedFactory implements IComputedFactory {

	private static Log LOG = LogFactory.getLog(DefaultComputedFactory.class);

	@Override
	public <E> E createInstance(Class<E> clazz) {
		E res = null;
		try {
			res = clazz.newInstance();
		} catch (InstantiationException e) {
			LOG.error(e, e);
		} catch (IllegalAccessException e) {
			LOG.error(e, e);
		}
		return res;
	}
}
