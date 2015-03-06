package com.synaptix.mybatis.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

public class ComponentColumnsCache {

	private static final Log LOG = LogFactory.getLog(ComponentColumnsCache.class);

	private Map<Class<? extends IComponent>, MyResult> cacheMap;

	@Inject
	public ComponentColumnsCache() {
		super();

		cacheMap = new HashMap<Class<? extends IComponent>, MyResult>();
	}

	public <E extends IComponent> boolean isValid(Class<E> componentClass) {
		MyResult result = getResult(componentClass);
		return result != null ? result.valid && result.level < 4 : false;
	}

	public <E extends IComponent> Set<String> getPropertyNames(Class<E> componentClass) {
		MyResult result = getResult(componentClass);
		return result != null ? result.propertyNames : null;
	}

	public <E extends IComponent> boolean isUseNls(Class<E> componentClass) {
		MyResult result = getResult(componentClass);
		return result != null ? result.useNls : false;
	}

	public <E extends IComponent> Set<String> getNlsPropertyNames(Class<E> componentClass) {
		MyResult result = getResult(componentClass);
		return result != null ? result.nlsPropertyNames : null;
	}

	@SuppressWarnings("unchecked")
	private <E extends IComponent> MyResult getResult(Class<E> componentClass) {
		MyResult res;
		synchronized (componentClass) {
			res = cacheMap.get(componentClass);
			if (res == null) {
				res = new MyResult();
				res.propertyNames = new HashSet<String>();
				res.columns = new HashSet<String>();
				res.valid = true;
				res.useNls = false;
				res.nlsPropertyNames = new HashSet<String>();
				res.level = 1;

				ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
				for (String propertyName : cd.getPropertyNames()) {
					PropertyDescriptor pd = cd.getPropertyDescriptor(propertyName);
					DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) pd.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);

					if (dp != null) {
						if (dp.getCollection() != null) {
							Class<?> propertyClass = cd.getPropertyClass(propertyName);
							if (IComponent.class.isAssignableFrom(propertyClass)) {
								MyResult subResult = getResult((Class<? extends IComponent>) propertyClass);
								Set<String> subPropertyNames = subResult != null ? subResult.propertyNames : null;
								if (subPropertyNames != null && !subPropertyNames.isEmpty()) {
									for (String c : subPropertyNames) {
										String p = ComponentHelper.PropertyDotBuilder.build(propertyName, c);
										res.propertyNames.add(p);
									}
								}
								Set<String> subColumns = subResult != null ? subResult.columns : null;
								if (subColumns != null && !subColumns.isEmpty()) {
									for (String c : subColumns) {
										String p = new StringBuilder().append(dp.getCollection().getAlias()).append(c).toString();
										res.columns.add(p);
										if (p.length() > 30) {
											res.valid = false;
										}
									}
								}

								res.level = Math.max(res.level, subResult.level + 1);
							}
						} else if (dp.getColumn() != null) {
							res.propertyNames.add(propertyName);
							res.columns.add(dp.getColumn().getSqlName());
						} else if (dp.getNlsColumn() != null) {
							res.propertyNames.add(propertyName);
							res.columns.add(dp.getNlsColumn().getSqlName());

							res.useNls = true;
							res.nlsPropertyNames.add(propertyName);
						}
					}
				}

				res.valid = res.valid && res.propertyNames.size() < 1000;

				if (LOG.isDebugEnabled()) {
					LOG.debug("Add cache " + componentClass + " valid " + res.valid + " columns=" + res.propertyNames.size() + " level=" + res.level);
				}
				cacheMap.put(componentClass, res);
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Use cache " + componentClass + " valid " + res.valid + " columns=" + res.propertyNames.size() + " level=" + res.level);
				}
			}
		}
		return res;
	}

	private class MyResult {

		boolean valid;

		Set<String> propertyNames;

		Set<String> columns;

		int level;

		boolean useNls;

		Set<String> nlsPropertyNames;

	}
}
