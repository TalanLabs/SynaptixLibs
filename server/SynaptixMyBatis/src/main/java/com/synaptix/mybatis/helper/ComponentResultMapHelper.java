package com.synaptix.mybatis.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.type.JdbcType;

import com.google.inject.Inject;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.EntityFields;
import com.synaptix.entity.IId;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor.Collection;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.mybatis.hack.SynaptixConfiguration;

public class ComponentResultMapHelper {

	private final Integer lock = new Integer(0);

	@Inject
	private ComponentColumnsCache componentColumnsCache;

	@Inject
	private SynaptixConfiguration synaptixConfiguration;

	@Inject
	private FindMappedStatement findMappedStatement;

	@Inject
	private SelectNlsMappedStatement selectNlsMappedStatement;

	@Inject
	public ComponentResultMapHelper() {
		super();
	}

	@Deprecated
	public void setComponentColumnsCache(ComponentColumnsCache componentColumnsCache) {
		this.componentColumnsCache = componentColumnsCache;
	}

	@Deprecated
	public void setSynaptixConfiguration(SynaptixConfiguration synaptixConfiguration) {
		this.synaptixConfiguration = synaptixConfiguration;
	}

	/**
	 * Get a result map
	 *
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultMap getResultMap(Class<?> clazz) {
		if (IComponent.class.isAssignableFrom(clazz)) {
			return getResultMap((Class<? extends IComponent>) clazz, null);
		} else {
			String key = buildResultMapKey(clazz, null, "simple");

			ResultMap resultMap;
			if (!synaptixConfiguration.hasComponentResultMap(key)) {
				ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key, clazz, new ArrayList<ResultMapping>(), null);
				resultMap = inlineResultMapBuilder.build();
				synaptixConfiguration.addResultMap(resultMap);
			}
			resultMap = synaptixConfiguration.getComponentResultMap(key);
			return resultMap;
		}
	}

	private ResultMap getResultMap(Class<? extends IComponent> componentClass, String propertyNamePrefix) {
		String key = buildResultMapKey(componentClass, null, "simple");

		ResultMap resultMap;
		if (!synaptixConfiguration.hasComponentResultMap(key)) {
			List<ResultMapping> resultMappings = createResultMappings(componentClass, null, null, propertyNamePrefix, false);
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key, componentClass, resultMappings, null);
			resultMap = inlineResultMapBuilder.build();
			synaptixConfiguration.addResultMap(resultMap);
		}
		resultMap = synaptixConfiguration.getComponentResultMap(key);

		return resultMap;
	}

	/**
	 * Get a nested result map
	 *
	 * @param synaptixConfiguration
	 * @param componentClass
	 * @param columns
	 * @return
	 */
	public ResultMap getNestedResultMap(Class<? extends IComponent> componentClass, Set<String> columns) {
		return getNestedResultMap(componentClass, columns, null);
	}

	private ResultMap getNestedResultMap(Class<? extends IComponent> componentClass, Set<String> columns, String propertyNamePrefix) {
		Set<String> cs = extractColumns(columns, propertyNamePrefix);
		String key = buildResultMapKey(componentClass, cs, "nested");

		ResultMap resultMap;
		if (!synaptixConfiguration.hasComponentResultMap(key)) {
			Set<String> pns = extractPropertyNames(componentClass, columns, propertyNamePrefix);
			List<ResultMapping> resultMappings = createResultMappings(componentClass, pns, columns, propertyNamePrefix, true);
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key, componentClass, resultMappings, null);
			resultMap = inlineResultMapBuilder.build();
			synaptixConfiguration.addResultMap(resultMap);
		}
		resultMap = synaptixConfiguration.getComponentResultMap(key);
		return resultMap;
	}

	private Set<String> extractColumns(Set<String> columns, String columnPrefix) {
		Set<String> res = null;
		if (columns != null && !columns.isEmpty()) {
			res = new HashSet<String>();
			for (String column : columns) {
				if (columnPrefix == null || columnPrefix.isEmpty() || column.startsWith(columnPrefix + ".")) {
					res.add(column);
				}
			}

		}
		return res;
	}

	private Set<String> extractPropertyNames(Class<? extends IComponent> componentClass, Set<String> columns, String propertyNamePrefix) {
		Set<String> res = new HashSet<String>();

		if (propertyNamePrefix == null || propertyNamePrefix.isEmpty() || existPropertyName(columns, null, propertyNamePrefix)) {
			ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
			for (String propertyName : cd.getPropertyNames()) {
				boolean id = "id".equals(propertyName);
				boolean version = "version".equals(propertyName);
				if (id || version || existPropertyName(columns, propertyNamePrefix, propertyName)) {
					res.add(propertyName);
				}
			}
		}
		return res;
	}

	private boolean existPropertyName(Set<String> columns, String parentPropertyName, String propertyName) {
		boolean res = true;
		if (columns != null && !columns.isEmpty()) {
			res = false;
			Iterator<String> it = columns.iterator();
			while (it.hasNext() && !res) {
				String column = it.next();
				if (parentPropertyName != null && !parentPropertyName.isEmpty()) {
					if (column.startsWith(parentPropertyName + ".")) {
						column = column.substring((parentPropertyName + ".").length());
					} else {
						column = null;
					}
				}
				if (column != null) {
					res = "*".equals(column) || column.equals(propertyName) || column.startsWith(propertyName + ".");
				}
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private List<ResultMapping> createResultMappings(Class<? extends IComponent> componentClass, Set<String> propertyNames, Set<String> columns, String propertyNamePrefix, boolean nested) {
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);

		List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();

		if (propertyNames == null) {
			propertyNames = cd.getPropertyNames();
		}

		for (String propertyName : propertyNames) {
			String extendPropertyName = propertyNamePrefix != null && !propertyNamePrefix.isEmpty() ? propertyNamePrefix + "." + propertyName : propertyName;

			PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);

			boolean otherComponent = ComponentFactory.isClass(propertyDescriptor.getPropertyClass());

			DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) propertyDescriptor.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
			if (dp != null) {
				if (dp.getColumn() != null && dp.getCollection() == null && !otherComponent) {
					ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, dp.getColumn().getSqlName(), propertyDescriptor.getPropertyClass());
					if (EntityFields.id().name().equals(propertyName)) {
						resultMappingBuilder.flags(Arrays.asList(ResultFlag.ID));
					}
					if (IId.class.isAssignableFrom(propertyDescriptor.getPropertyClass())) {
						resultMappingBuilder.jdbcType(JdbcType.OTHER);
					}
					resultMappings.add(resultMappingBuilder.build());
				} else if (dp.getCollection() != null) {
					if ((nested) && (StringUtils.isNotBlank(dp.getCollection().getAlias()))) {
						ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, dp.getCollection().getIdSource(),
								propertyDescriptor.getPropertyClass());
						resultMappingBuilder.columnPrefix(dp.getCollection().getAlias());
						ResultMap resultMap = getNestedResultMap((Class<? extends IComponent>) propertyDescriptor.getPropertyClass(), columns, extendPropertyName);
						resultMappingBuilder.nestedResultMapId(resultMap.getId());
						resultMappings.add(resultMappingBuilder.build());
					} else {
						if (otherComponent) {
							ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, dp.getCollection().getIdSource(),
									propertyDescriptor.getPropertyClass());
							MappedStatement mappedStatement = null;
							Collection collection = dp.getCollection();
							if (!"ID".equals(collection.getIdTarget())) {
								mappedStatement = findMappedStatement.getFindComponentsByPropertyNameMappedStatement((Class<? extends IComponent>) propertyDescriptor.getPropertyClass(),
										collection.getIdTarget(), EntityFields.id().name(), false);
							} else {
								mappedStatement = findMappedStatement.getFindComponentsByPropertyNameMappedStatement((Class<? extends IComponent>) propertyDescriptor.getPropertyClass(), EntityFields
										.id().name(), false);
							}
							resultMappingBuilder.nestedQueryId(mappedStatement.getId());
							resultMappings.add(resultMappingBuilder.build());
						} else if (List.class.isAssignableFrom(propertyDescriptor.getPropertyClass())) {
							ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, EntityFields.id().name(), propertyDescriptor.getPropertyClass());
							MappedStatement mappedStatement = findMappedStatement.getFindComponentsByPropertyNameMappedStatement(dp.getCollection().getComponentClass(), dp.getCollection()
									.getSqlTableName(), dp.getCollection().getIdSource(), dp.getCollection().getIdTarget(), "value", true);
							resultMappingBuilder.nestedQueryId(mappedStatement.getId());
							resultMappings.add(resultMappingBuilder.build());
						}
					}
				} else if (dp.getNlsColumn() != null) {
					if (nested) {
						ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, dp.getNlsColumn().getSqlName(), String.class);
						resultMappings.add(resultMappingBuilder.build());
					} else {
						ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, null, String.class);
						List<ResultMapping> composites = new ArrayList<ResultMapping>();
						composites.add(new ResultMapping.Builder(synaptixConfiguration, "id", "ID", synaptixConfiguration.getTypeHandlerRegistry().getUnknownTypeHandler()).build());
						composites.add(new ResultMapping.Builder(synaptixConfiguration, "defaultMeaning", dp.getNlsColumn().getSqlName(), synaptixConfiguration.getTypeHandlerRegistry()
								.getUnknownTypeHandler()).build());
						resultMappingBuilder.composites(composites);
						MappedStatement mappedStatement = selectNlsMappedStatement.getSelectNlsMappedStatement(componentClass, dp.getNlsColumn().getSqlName());
						resultMappingBuilder.nestedQueryId(mappedStatement.getId());
						resultMappings.add(resultMappingBuilder.build());
					}
				}
			}
		}

		return resultMappings;
	}

	private static String buildResultMapKey(Class<?> clazz, Set<String> columns, String prefix) {
		StringBuilder sb = new StringBuilder();
		if (prefix != null && !prefix.isEmpty()) {
			sb.append(prefix).append("-");
		}
		sb.append(clazz.getName());
		if (columns != null && !columns.isEmpty()) {
			List<String> cs = new ArrayList<String>(columns);
			Collections.sort(cs);
			sb.append("?");
			for (String column : columns) {
				sb.append(column).append("&");
			}
		}
		sb.append("-ResultMap");
		return sb.toString();
	}

	/**
	 * Get a result map
	 *
	 * @param synaptixConfiguration
	 * @param componentClass
	 * @param columns
	 * @return
	 */
	public ResultMap getResultMapWithNested(Class<? extends IComponent> componentClass) {
		String key = buildResultMapKey(componentClass, null, "normal");
		ResultMap resultMap;
		if (!synaptixConfiguration.hasComponentResultMap(key)) {
			List<ResultMapping> resultMappings = _createResultMappings1(componentClass);
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key, componentClass, resultMappings, null);
			resultMap = inlineResultMapBuilder.build();
			synchronized (lock) {
				if (!synaptixConfiguration.hasComponentResultMap(key)) {
					synaptixConfiguration.addResultMap(resultMap);
				}
			}
		}
		resultMap = synaptixConfiguration.getComponentResultMap(key);

		return resultMap;
	}

	/**
	 * Create a simple result map without nested and add to configuration, name is componentClass
	 *
	 * @param componentClass
	 * @return
	 */
	public ResultMap createResultMap(Class<? extends IComponent> componentClass) {
		String key = componentClass.getName();
		ResultMap resultMap;
		if (!synaptixConfiguration.hasComponentResultMap(key)) {
			List<ResultMapping> resultMappings = _createResultMappings2(componentClass, false);
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key, componentClass, resultMappings, null);
			resultMap = inlineResultMapBuilder.build();
			synaptixConfiguration.addResultMap(resultMap);
		}
		resultMap = synaptixConfiguration.getComponentResultMap(key);
		return resultMap;
	}

	private List<ResultMapping> _createResultMappings1(Class<? extends IComponent> componentClass) {
		boolean nested = componentColumnsCache.isValid(componentClass);
		return _createResultMappings2(componentClass, nested);
	}

	@SuppressWarnings("unchecked")
	private List<ResultMapping> _createResultMappings2(Class<? extends IComponent> componentClass, boolean nested) {
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);

		List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();
		for (String propertyName : cd.getPropertyNames()) {
			PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);

			boolean otherComponent = ComponentFactory.isClass(propertyDescriptor.getPropertyClass());

			DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) propertyDescriptor.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
			if (dp != null) {
				if (dp.getColumn() != null && dp.getCollection() == null && !otherComponent) {
					ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, dp.getColumn().getSqlName(), propertyDescriptor.getPropertyClass());
					if (EntityFields.id().name().equals(propertyName)) {
						resultMappingBuilder.flags(Arrays.asList(ResultFlag.ID));
					}
					if (IId.class.isAssignableFrom(propertyDescriptor.getPropertyClass())) {
						resultMappingBuilder.jdbcType(JdbcType.OTHER);
					}
					resultMappings.add(resultMappingBuilder.build());
				} else if (dp.getCollection() != null) {
					if (otherComponent) {
						if ((nested) && (StringUtils.isNotBlank(dp.getCollection().getAlias()))) {
							ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, dp.getCollection().getIdSource(),
									propertyDescriptor.getPropertyClass());
							resultMappingBuilder.columnPrefix(dp.getCollection().getAlias());
							ResultMap resultMap = getResultMapWithNested((Class<? extends IComponent>) propertyDescriptor.getPropertyClass());
							resultMappingBuilder.nestedResultMapId(resultMap.getId());
							resultMappings.add(resultMappingBuilder.build());
						} else {
							ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, dp.getCollection().getIdSource(),
									propertyDescriptor.getPropertyClass());
							MappedStatement mappedStatement = null;
							Collection collection = dp.getCollection();
							if (!"ID".equals(collection.getIdTarget())) {
								mappedStatement = findMappedStatement.getFindComponentsByPropertyNameMappedStatement((Class<? extends IComponent>) propertyDescriptor.getPropertyClass(),
										collection.getIdTarget(), EntityFields.id().name(), false);
							} else {
								mappedStatement = findMappedStatement.getFindComponentsByPropertyNameMappedStatement((Class<? extends IComponent>) propertyDescriptor.getPropertyClass(), EntityFields
										.id().name(), false);
							}
							resultMappingBuilder.nestedQueryId(mappedStatement.getId());
							resultMappings.add(resultMappingBuilder.build());
						}
					} else if (List.class.isAssignableFrom(propertyDescriptor.getPropertyClass())) {
						ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, "ID", propertyDescriptor.getPropertyClass());
						MappedStatement mappedStatement = findMappedStatement.getFindComponentsByPropertyNameMappedStatement(dp.getCollection().getComponentClass(), dp.getCollection()
								.getSqlTableName(), dp.getCollection().getIdSource(), dp.getCollection().getIdTarget(), "value", true);
						resultMappingBuilder.nestedQueryId(mappedStatement.getId());
						resultMappings.add(resultMappingBuilder.build());
					}
				} else if (dp.getNlsColumn() != null) {
					if (nested) {
						ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, dp.getNlsColumn().getSqlName(), String.class);
						resultMappings.add(resultMappingBuilder.build());
					} else {
						ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(synaptixConfiguration, propertyName, null, String.class);
						List<ResultMapping> composites = new ArrayList<ResultMapping>();
						composites.add(new ResultMapping.Builder(synaptixConfiguration, "id", "ID", synaptixConfiguration.getTypeHandlerRegistry().getUnknownTypeHandler()).build());
						composites.add(new ResultMapping.Builder(synaptixConfiguration, "defaultMeaning", dp.getNlsColumn().getSqlName(), synaptixConfiguration.getTypeHandlerRegistry()
								.getUnknownTypeHandler()).build());
						resultMappingBuilder.composites(composites);
						MappedStatement mappedStatement = selectNlsMappedStatement.getSelectNlsMappedStatement(componentClass, dp.getNlsColumn().getSqlName());
						resultMappingBuilder.nestedQueryId(mappedStatement.getId());
						resultMappings.add(resultMappingBuilder.build());
					}
				}
			}
		}

		return resultMappings;
	}
}
