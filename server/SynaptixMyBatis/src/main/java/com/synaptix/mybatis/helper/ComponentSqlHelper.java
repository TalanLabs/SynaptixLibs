package com.synaptix.mybatis.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.google.inject.Inject;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.model.Binome;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.IdRaw;
import com.synaptix.entity.extension.DatabaseClassExtensionDescriptor;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor.Collection;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor.Column;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor.NlsColumn;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.entity.extension.JdbcTypesEnum;
import com.synaptix.mybatis.IRequestBuilder;
import com.synaptix.mybatis.dao.helper.ognl.ComparatorHelper;
import com.synaptix.mybatis.filter.AbstractFilterContext;
import com.synaptix.mybatis.filter.IFilterContext;
import com.synaptix.mybatis.filter.NodeProcessFactory;
import com.synaptix.mybatis.hack.SynaptixConfiguration;
import com.synaptix.service.ServiceException;
import com.synaptix.service.filter.AbstractNode;
import com.synaptix.service.filter.RootNode;
import com.synaptix.service.model.ISortOrder;

public class ComponentSqlHelper {

	private static final Log LOG = LogFactory.getLog(ComponentSqlHelper.class);

	private static final String PAGINATION_ERROR = "paginationError";

	@Inject
	private NodeProcessFactory nodeProcessFactory;

	@Inject(optional = true)
	private Set<HintProcess> hintProcesses;

	private SynaptixConfiguration synaptixConfiguration;

	@Inject
	public ComponentSqlHelper() {
		super();
	}

	@Inject
	public void setConfiguration(SynaptixConfiguration configuration) {
		this.synaptixConfiguration = configuration;
	}

	public <E extends IComponent> Set<String> buildPropertyNames(IFilterContext context, RootNode rootNode) {
		return rootNode != null ? context.buildPropertyNames(rootNode.getNode()) : null;
	}

	public <E extends IComponent> Map<String, Object> buildValueFilterMap(IFilterContext context, RootNode rootNode) {
		return rootNode != null ? context.buildValueFilterMap(rootNode.getNode()) : null;
	}

	public <E extends IComponent> String buildWhereSql(IFilterContext context, RootNode rootNode) {
		return rootNode != null ? context.process(rootNode.getNode()) : null;
	}

	/**
	 * Clean value filter map, remove null or empty value
	 *
	 * @param valueFilterMap
	 * @return
	 */
	public Set<String> getCleanFilters(Map<String, Object> valueFilterMap) {
		Set<String> res = new HashSet<String>();
		if (valueFilterMap != null && !valueFilterMap.isEmpty()) {
			for (Entry<String, Object> entry : valueFilterMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					res.add(entry.getKey());
				}
			}
		}
		return res;
	}

	/**
	 * true if value filter map is no dependency value for build sql
	 *
	 * @param valueFilterMap
	 * @return
	 */
	public boolean isUseSqlCache(Map<String, Object> valueFilterMap) {
		boolean res = true;
		if (valueFilterMap != null && !valueFilterMap.isEmpty()) {
			Iterator<Entry<String, Object>> it = valueFilterMap.entrySet().iterator();
			while (it.hasNext() && res) {
				Entry<String, Object> entry = it.next();
				if (entry.getValue() != null) {
					if (entry.getValue() instanceof IRequestBuilder || entry.getValue() instanceof Binome || entry.getValue() instanceof java.util.Collection || entry.getValue() instanceof Object[]) {
						res = false;
					}
					if (entry.getValue() instanceof String) {
						if ("#".equals((entry.getValue()))) {
							res = false;
						}
					}
				}
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public void buildFilterJoinMap(Set<String> filterSet, Map<String, Join> joinMap, ComponentDescriptor ed, String oldName, String oldAlias) {
		if (filterSet != null && !filterSet.isEmpty()) {
			for (String filter : filterSet) {
				int indexOf = filter.indexOf('.');
				if (indexOf > -1) {
					String head = filter.substring(0, indexOf);
					Class<?> propertyClass = ed.getPropertyClass(head);
					String name = null;
					if (oldName != null) {
						name = oldName + "." + head;
					} else {
						name = head;
					}

					if ((propertyClass != null) && (IComponent.class.isAssignableFrom(propertyClass)) && (IDatabaseComponentExtension.class.isAssignableFrom(propertyClass))) {
						ComponentDescriptor componentDescriptor = ComponentFactory.getInstance().getDescriptor((Class<? extends IComponent>) propertyClass);
						String sqlTableName = getSqlTableName(componentDescriptor);
						if (sqlTableName != null) {
							Join join = joinMap.get(name);
							if (join == null) {
								DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) ed.getPropertyDescriptor(head).getPropertyExtensionDescriptor(
										IDatabaseComponentExtension.class);
								if (dp != null) {
									String colName = null;
									if (dp.getCollection() != null) {
										colName = dp.getCollection().getIdSource();
									} else if (dp.getColumn() != null) {
										colName = dp.getColumn().getSqlName();

									} else {
										LOG.error("There is no annotation on " + name + " in class " + ed.getComponentClass().getSimpleName());
									}
									if ((colName != null) && (!colName.isEmpty())) {
										Integer alias = joinMap.size();
										join = new Join(sqlTableName, alias, true, "t" + alias + ".id = t" + oldAlias + "." + colName);
										joinMap.put(name, join);
									}
								}
							} else {
								join.setInner(true);
							}
							if (join == null) {
								// FIXME on est dans un service
								throw new ServiceException(PAGINATION_ERROR, head + " in " + ed.getComponentClass().getSimpleName() + " has no join", null);
							}
							String alias = join.getAlias().toString();
							String tail = filter.substring(indexOf + 1);
							if (tail.contains(".")) {
								Set<String> newSet = new HashSet<String>();
								newSet.add(tail);
								buildFilterJoinMap(newSet, joinMap, componentDescriptor, name, alias);
							}
						}
					} else {
						// FIXME on est dans un service
						throw new ServiceException(PAGINATION_ERROR, head + " in " + ed.getComponentClass().getSimpleName() + " is not a component or is not a property", null);
					}
				}
			}
		}
	}

	/**
	 * Get a field column name, format MyBatis ex : #{toto}
	 *
	 * @param field
	 * @return
	 */
	public String getField(PropertyDescriptor field) {
		DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) field.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
		StringBuilder sb = new StringBuilder("#{");
		sb.append(field.getPropertyName());

		Class<?> javaType = field.getPropertyClass();
		if (IEntity.class.isAssignableFrom(field.getPropertyClass())) {
			sb.append(".id");
			javaType = IId.class;
		}
		if (javaType != null) {
			sb.append(",javaType=").append(javaType.getName());
		}
		if (dp != null) {
			if (dp.getColumn() != null) {
				if (!dp.getColumn().isNotNull()) {
					sb.append(",jdbcType=").append(dp.getColumn().getJdbcType());
				}
			} else if (dp.getNlsColumn() != null) {
				if (!dp.getNlsColumn().isNotNull()) {
					sb.append(",jdbcType=").append(JdbcTypesEnum.VARCHAR);
				}
			}
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Get the table name with schema if available
	 *
	 * @param ed
	 * @return
	 */
	public String getSqlTableName(ComponentDescriptor ed) {
		DatabaseClassExtensionDescriptor dc = (DatabaseClassExtensionDescriptor) ed.getClassExtensionDescriptor(IDatabaseComponentExtension.class);
		if (dc == null) {
			throw new ServiceException("NO SQL TABLE NAME", ed.getComponentClass().getSimpleName() + " has no sql table name", null);
		}
		String sqlTableName = dc.getSqlTableName();
		if ((dc.getSchema() != null) && (!dc.getSchema().isEmpty())) {
			sqlTableName = new StringBuilder(dc.getSchema()).append(".").append(sqlTableName).toString();
		}
		return sqlTableName;
	}

	/**
	 * Get the column name for property name
	 *
	 * @param cd
	 * @param propertyName
	 * @return
	 */
	public String getSqlColumnName(ComponentDescriptor cd, String propertyName) {
		PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
		if (propertyDescriptor != null) {
			DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) propertyDescriptor.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
			if (dp != null) {
				if (dp.getColumn() != null) {
					return dp.getColumn().getSqlName();
				} else if (dp.getNlsColumn() != null) {
					return dp.getNlsColumn().getSqlName();
				}
			}
		}
		return null;
	}

	public void includeOrderedJoinList(SQL sqlBuilder, Map<String, Join> joinMap) {
		List<Join> joinList = new ArrayList<Join>();
		for (Join join : joinMap.values()) {
			joinList.add(join);
		}
		Collections.sort(joinList, new Comparator<Join>() {
			@Override
			public int compare(Join o1, Join o2) {
				return o1.getAlias().compareTo(o2.getAlias());
			}
		});
		for (Join join : joinList) {
			// if (!join.getExternalToDo()) {
			if (join.isInner()) {
				sqlBuilder.INNER_JOIN(join.getSqlTableName() + " t" + join.getAlias() + " on " + join.getJoinClause());
			} else {
				sqlBuilder.LEFT_OUTER_JOIN(join.getSqlTableName() + " t" + join.getAlias() + " on " + join.getJoinClause());
			}
			// }
		}
	}

	public boolean isNotEmptyValueFilterMap(Map<String, Object> valueFilterMap) {
		boolean res = false;
		if (valueFilterMap != null && !valueFilterMap.isEmpty()) {
			Iterator<Entry<String, Object>> ite = valueFilterMap.entrySet().iterator();
			while (ite.hasNext() && !res) {
				Entry<String, Object> entry = ite.next();
				res = ComparatorHelper.isNotEmpty(entry.getValue());
			}
		}
		return res;
	}

	/**
	 * Build a where filter
	 *
	 * @param prefix
	 * @param operator
	 * @param joinMap
	 * @param ed
	 * @param valueFilterMap
	 * @return
	 */
	public String whereFilters(String prefix, String operator, Map<String, Join> joinMap, ComponentDescriptor ed, Map<String, Object> valueFilterMap) {
		StringBuilder sb = new StringBuilder();
		if (valueFilterMap != null && !valueFilterMap.isEmpty()) {
			boolean first = true;
			Iterator<Entry<String, Object>> ite = valueFilterMap.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, Object> entry = ite.next();
				if (ComparatorHelper.isNotEmpty(entry.getValue())) {
					String key = entry.getKey();
					int indexOf = key.lastIndexOf('.');
					Join join = null;
					if (indexOf != -1) {
						join = joinMap.get(key.substring(0, indexOf));
					}
					if (join == null) {
						join = new Join(null, null, true, null);
					}
					if (join != null) {
						FinalObject finalObject = getFinalObject(ed.getComponentClass(), key);
						if ((finalObject != null) || (IRequestBuilder.class.isAssignableFrom(entry.getValue().getClass()))) {
							if (first) {
								first = false;
							} else {
								sb.append(" ").append(operator).append(" ");
							}
							sb.append("(").append(doFilter(prefix, entry, join, finalObject)).append(")");
						}
					}
				}
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public final FinalObject getFinalObject(Class<? extends IComponent> componentClass, String properties) {
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
		int indexOf = properties.indexOf('.');
		if (indexOf != -1) {
			String head = properties.substring(0, indexOf);
			String tail = properties.substring(indexOf + 1);
			Class<?> propertyClass = cd.getPropertyClass(head);
			if ((propertyClass != null && (IComponent.class.isAssignableFrom(propertyClass)))) {
				return getFinalObject((Class<? extends IComponent>) propertyClass, tail);
			} else {
				// FIXME on est dans un service
				throw new ServiceException(PAGINATION_ERROR, head + " in " + componentClass.getSimpleName() + " is not a component", null);
			}
		} else {
			PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(properties);
			if (propertyDescriptor == null) {
				LOG.debug(properties + " is not a property of " + componentClass.getSimpleName());
				return null;
			} else {
				DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) propertyDescriptor.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
				return new FinalObject(cd, propertyDescriptor, dp);
			}
		}
	}

	private String doFilter(String prefix, Entry<String, Object> entry, Join join, FinalObject finalObject) {
		String res = null;

		String key = entry.getKey();
		int indexOf = key.lastIndexOf('.');
		if (indexOf != -1) {
			key = key.substring(0, indexOf);
		} else {
			key = null;
		}
		Object value = entry.getValue();

		PropertyDescriptor propertyDescriptor = null;
		DatabasePropertyExtensionDescriptor dp = null;
		if (finalObject != null) {
			propertyDescriptor = finalObject.getPropertyDescriptor();
			dp = finalObject.getDatabasePropertyExtensionDescriptor();
		}

		Class<?> columnClass = null;
		String sqlName = "";
		if (dp != null) {
			if ((dp.getCollection() != null) && (dp.getCollection().getIdSource() != null) && (!dp.getCollection().getIdSource().isEmpty())) {
				sqlName = dp.getCollection().getIdSource();
				sqlName = "t" + (join.getAlias() != null ? join.getAlias() : "") + "." + sqlName;
			} else if ((dp.getColumn() != null) && (dp.getColumn().getSqlName() != null)) {
				sqlName = dp.getColumn().getSqlName();
				sqlName = "t" + (join.getAlias() != null ? join.getAlias() : "") + "." + sqlName;
				columnClass = propertyDescriptor.getPropertyClass();
			} else if (dp.getNlsColumn() != null) {
				String pr = new StringBuilder("t").append(join.getAlias() != null ? join.getAlias() : "").append(".").toString();
				StringBuilder sb = new StringBuilder();
				sb.append("p_nls.get_nls_server_message('").append(getSqlTableName(finalObject.componentDescriptor)).append("',");
				sb.append(pr).append("ID,");
				sb.append("'").append(dp.getNlsColumn().getSqlName()).append("',");
				sb.append(pr).append(dp.getNlsColumn().getSqlName()).append(")");
				sqlName = sb.toString();
			}
		}
		if (IRequestBuilder.class.isAssignableFrom(value.getClass())) {
			IRequestBuilder requestBuilder = (IRequestBuilder) value;
			res = requestBuilder.getSqlChunk(sqlName, prefix);
		} else if ((dp.getCollection() != null) && (dp.getCollection().getSqlTableName() != null) && (!"ID".equals(dp.getCollection().getIdTarget()))
				&& (IEntity.class.isAssignableFrom(entry.getValue().getClass()))) {
			// if there is an association, we use WHERE EXISTS(select 1 from
			// t_asso_* a where a.id_target = #{id} and a.id_source =
			// t?.id_source)
			StringBuilder exist = new StringBuilder("EXISTS(SELECT 1 FROM ");
			if ((dp.getCollection().getSchema() != null) && (!dp.getCollection().getSchema().isEmpty())) {
				exist.append(dp.getCollection().getSchema()).append(".");
			}
			exist.append(dp.getCollection().getSqlTableName());
			exist.append(" a WHERE a.").append(dp.getCollection().getIdTarget()).append(" = ").append(getField(prefix, propertyDescriptor, key, "id", null));
			exist.append(" AND a.").append(dp.getCollection().getIdSource()).append(" = ").append(sqlName.substring(0, sqlName.lastIndexOf('.'))).append(".id");
			exist.append(")");
			res = exist.toString();
		} else if (Object[].class.isAssignableFrom(entry.getValue().getClass())) {
			Object[] array = (Object[]) entry.getValue();
			StringBuilder sb = new StringBuilder(sqlName).append(" IN (");
			int i = 0;
			for (Object ob : array) {
				array[i] = ob;
				if (i > 0) {
					sb.append(", ");
				}
				String f = getField(prefix, propertyDescriptor, key, null, i);
				sb.append(f);
				i++;
			}
			res = sb.append(")").toString();
		} else if (java.util.Collection.class.isAssignableFrom(entry.getValue().getClass())) { // temporary...
			java.util.Collection<?> c = (java.util.Collection<?>) entry.getValue();
			Object[] array = new Object[c.size()];
			entry.setValue(array);
			StringBuilder sb = new StringBuilder(sqlName).append(" IN (");
			int i = 0;
			for (Object ob : c) {
				array[i] = ob;
				if (i > 0) {
					sb.append(", ");
				}
				String f = getField(prefix, propertyDescriptor, key, null, i);
				sb.append(f);
				i++;
			}
			res = sb.append(")").toString();
		} else if (IEntity.class.isAssignableFrom(propertyDescriptor.getPropertyClass())) {
			// if there is an entity, we use .id = #{id}
			res = new StringBuilder(sqlName).append(" = ").append(getField(prefix, propertyDescriptor, key, null, null)).toString();
		} else if (boolean.class.isAssignableFrom(propertyDescriptor.getPropertyClass()) || Boolean.class.isAssignableFrom(propertyDescriptor.getPropertyClass())) {
			// if object is a boolean, we simplify the request
			res = new StringBuilder(sqlName).append(" = ").append(getField(prefix, propertyDescriptor, key, null, null)).toString();
		} else if (Binome.class.isAssignableFrom(entry.getValue().getClass())) {
			// if there is a binome, we use >= value1 (if present) and/or <= value2 (if present)

			StringBuilder exist = new StringBuilder();
			Binome<?, ?> val = (Binome<?, ?>) entry.getValue();
			if (val.getValue1() != null) {
				String sqlName2 = sqlName;
				Class<?> javaType = null;
				if ((columnClass == LocalDateTime.class) && (val.getValue1() instanceof LocalDate)) {
					// LocalDateTime >= LocalDate => trunc(LocalDateTime) >= LocalDate
					sqlName2 = String.format("trunc(%s)", sqlName);
					javaType = LocalDate.class;
				}
				exist.append(new StringBuilder(sqlName2).append(" >= ").append(getField(prefix, propertyDescriptor, key, "value1", null, javaType)).toString());
			}
			if (val.getValue1() != null && val.getValue2() != null) {
				exist.append(" AND ");
			}
			if (val.getValue2() != null) {
				String sqlName2 = sqlName;
				Class<?> javaType = null;
				if ((columnClass == LocalDateTime.class) && (val.getValue2() instanceof LocalDate)) {
					// LocalDateTime <= LocalDate => trunc(LocalDateTime) <= LocalDate
					sqlName2 = String.format("trunc(%s)", sqlName);
					javaType = LocalDate.class;
				}
				exist.append(new StringBuilder(sqlName2).append(" <= ").append(getField(prefix, propertyDescriptor, key, "value2", null, javaType)).toString());
			}
			res = exist.toString();
		} else if ((IdRaw.class.isAssignableFrom(entry.getValue().getClass())) || (Enum.class.isAssignableFrom(entry.getValue().getClass()))) {
			res = new StringBuilder(sqlName).append(" = ").append(getField(prefix, propertyDescriptor, key, null, null)).toString();
		} else {
			if ("#".equals(entry.getValue())) {
				res = new StringBuilder(sqlName).append(" is null").toString();
			} else {
				boolean useUpper = true;
				if ((dp.getColumn() != null && dp.getColumn().isUpperOnly()) || (dp.getNlsColumn() != null && dp.getNlsColumn().isUpperOnly())) {
					useUpper = false;
				}

				StringBuilder sb = new StringBuilder();
				if (useUpper) {
					sb.append("UPPER(");
				}
				sb.append(sqlName);
				if (useUpper) {
					sb.append(")");
				}
				sb.append(" like ");
				if (useUpper) {
					sb.append("UPPER(");
				}
				sb.append(getField(prefix, propertyDescriptor, key, null, null));
				if (useUpper) {
					sb.append(")");
				}
				sb.append("|| '%'");
				res = sb.toString();
			}
		}

		return res;
	}

	/**
	 * Create order by
	 *
	 * @param sqlBuilder
	 * @param sortOrders
	 * @param joinMap
	 * @param ed
	 */
	public void orderBy(SQL sqlBuilder, List<ISortOrder> sortOrders, Map<String, Join> joinMap, ComponentDescriptor ed) {
		if (sortOrders != null) {
			for (ISortOrder sortOrder : sortOrders) {
				String propertyName = sortOrder.getPropertyName();
				int indexOf = propertyName.lastIndexOf('.');
				String name = null;
				if (indexOf != -1) {
					name = propertyName.substring(0, indexOf);
				} else {
					name = propertyName;
				}
				Join join = joinMap.get(name);

				String prefix;
				if ((join != null) && (join.getAlias() != null)) {
					prefix = new StringBuilder("t").append(join.getAlias()).append(".").toString();
				} else {
					prefix = "t.";
				}

				ComponentSqlHelper.FinalObject finalObject = getFinalObject(ed.getComponentClass(), propertyName);
				if (finalObject != null) {
					DatabasePropertyExtensionDescriptor db = finalObject.getDatabasePropertyExtensionDescriptor();
					if (db != null) {
						if (db.getColumn() != null) {
							StringBuilder sb = new StringBuilder();
							sb.append(prefix);
							sb.append(db.getColumn().getSqlName());
							sb.append(sortOrder.isAscending() ? " ASC" : " DESC");
							sqlBuilder.ORDER_BY(sb.toString());
						} else if (db.getNlsColumn() != null) {
							StringBuilder sb = new StringBuilder();
							sb.append("p_nls.get_nls_server_message('").append(getSqlTableName(finalObject.componentDescriptor)).append("',");
							sb.append(prefix).append("ID,");
							sb.append("'").append(db.getNlsColumn().getSqlName()).append("',");
							sb.append(prefix).append(db.getNlsColumn().getSqlName()).append(")");
							sb.append(sortOrder.isAscending() ? " ASC" : " DESC");
							sqlBuilder.ORDER_BY(sb.toString());
						}
					}
				}
			}
		}

		sqlBuilder.ORDER_BY("t." + synaptixConfiguration.getRowidName());
	}

	/**
	 * prefix.fix_fieldName([index])(.id).subfix(,jdbcType=...)
	 */
	private String getField(String prefix, PropertyDescriptor field, String fix, String subfix, Integer index) {
		return getField(prefix, field, fix, subfix, index, null);
	}

	private String getField(String prefix, PropertyDescriptor field, String fix, String subfix, Integer index, Class<?> javaType) {
		DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) field.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
		StringBuilder sb = new StringBuilder("#{");
		sb.append(prefix).append(".");
		if (fix != null) {
			sb.append(fix.replaceAll("\\.", "_")).append("_");
		}
		sb.append(field.getPropertyName());
		if (javaType == null) {
			javaType = field.getPropertyClass();
			if (index != null) {
				sb.append("[").append(index).append("]");
				javaType = null;
			}

			if (IEntity.class.isAssignableFrom(field.getPropertyClass())) { // if
																			// field
																			// is an
																			// entity,
																			// we
																			// compare
																			// the
																			// ids
				sb.append(".id");
				javaType = IId.class;
			}
			if (java.util.Collection.class.isAssignableFrom(field.getPropertyClass()) && "id".equals(subfix)) {
				javaType = IId.class;
			}
		}
		if (subfix != null) {
			sb.append(".").append(subfix);
		}
		if (javaType != null) {
			sb.append(",javaType=").append(javaType.getName());
		}
		if ((dp.getColumn() != null) && (!dp.getColumn().isNotNull())) {
			sb.append(",jdbcType=").append(dp.getColumn().getJdbcType());
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Transform value in filter
	 */
	public <E extends IComponent> Map<String, Object> transformValueFilterMap(Class<E> componentClass, Map<String, Object> valueFilterMap) {
		Map<String, Object> newValueFilterMap = new HashMap<String, Object>();
		if (CollectionHelper.isNotEmpty(valueFilterMap)) {
			for (Entry<String, Object> entry : valueFilterMap.entrySet()) {
				Object value = entry.getValue();
				if (entry.getValue() instanceof String) {
					String s = (String) value;
					FinalObject fo = getFinalObject(componentClass, entry.getKey());
					if (fo != null && fo.databasePropertyExtensionDescriptor != null) {
						if ((fo.databasePropertyExtensionDescriptor.getColumn() != null && fo.databasePropertyExtensionDescriptor.getColumn().isUpperOnly())
								|| (fo.databasePropertyExtensionDescriptor.getNlsColumn() != null && fo.databasePropertyExtensionDescriptor.getNlsColumn().isUpperOnly())) {
							value = s.toUpperCase();
						}
					}
				}
				newValueFilterMap.put(entry.getKey().replaceAll("\\.", "_"), value);
			}
		}
		return newValueFilterMap;
	}

	/**
	 * Builds a map for joins<br>
	 */
	public void buildSelectJoinMap(Map<String, Join> joinMap, ComponentDescriptor ed, String name, String oldAlias, Set<String> columns, boolean externalToDo) {
		for (String propertyName : ed.getPropertyNames()) {
			PropertyDescriptor propertyDescriptor = ed.getPropertyDescriptor(propertyName);
			if (propertyDescriptor != null) {
				DatabasePropertyExtensionDescriptor propertyExtensionDescriptor = (DatabasePropertyExtensionDescriptor) propertyDescriptor
						.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
				if (propertyExtensionDescriptor != null) {
					Collection collection = propertyExtensionDescriptor.getCollection();
					if (collection != null) {
						if ((collection.getComponentClass() != null) && (collection.getAlias() != null) && (!collection.getAlias().isEmpty())) {
							ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(collection.getComponentClass());
							StringBuilder sqlTableName = new StringBuilder();
							if ((collection.getSchema() != null) && (!collection.getSchema().isEmpty())) {
								sqlTableName.append(collection.getSchema()).append(".");
							}
							sqlTableName.append(collection.getSqlTableName());
							StringBuilder n = new StringBuilder();
							if (name != null) {
								n.append(name).append(".");
							}
							n.append(propertyName);
							String a = n.toString();
							if (isContained(a, columns, true)) {
								boolean newExternalToDo = externalToDo;
								Join join = joinMap.get(a);
								Integer alias = null;
								if (join == null) {
									alias = joinMap.size();
									// if
									// (!"id".equalsIgnoreCase(collection.getIdTarget()))
									// {

									ComponentDescriptor cd2 = ComponentFactory.getInstance().getDescriptor(collection.getComponentClass());
									DatabaseClassExtensionDescriptor classExtensionDescriptor = (DatabaseClassExtensionDescriptor) cd2.getClassExtensionDescriptor(IDatabaseComponentExtension.class);

									StringBuilder sqlTableName2 = new StringBuilder();
									if ((classExtensionDescriptor.getSchema() != null) && (!classExtensionDescriptor.getSchema().isEmpty())) {
										sqlTableName2.append(classExtensionDescriptor.getSchema()).append(".");
									}
									sqlTableName2.append(classExtensionDescriptor.getSqlTableName());

									boolean asso = false;
									if (!sqlTableName.toString().equals(sqlTableName2.toString())) { // if asso needed
										join = new Join(sqlTableName.toString(), alias, false, "t" + alias + "." + collection.getIdSource() + " = "
												+ (oldAlias != null && !oldAlias.isEmpty() ? "t.t" + oldAlias : "t.") + "id");
										join.setExternal(null);
										join.setExternalDone(true);
										join.setExternalToDo(true);
										joinMap.put(a + "_asso", join);
										newExternalToDo = true;
										asso = true;
									}
									if (!"id".equalsIgnoreCase(collection.getIdTarget())) {
										newExternalToDo = true;
									}

									String oldAlias2 = Integer.toString(alias);
									if (asso) {
										alias++;
										// if ((oldAlias2 != null) &&
										// (!oldAlias2.isEmpty())) {
										join = new Join(sqlTableName2.toString(), alias, false, "t" + alias + ".id = t" + oldAlias2 + "." + collection.getIdTarget());
										// } else {
										// join = new
										// Join(sqlTableName2.toString(), alias,
										// false, "t" + alias + ".id = t." +
										// collection.getIdTarget());
										// }
									} else {
										if (!"id".equalsIgnoreCase(collection.getIdTarget())) {
											if ((oldAlias != null) && (!oldAlias.isEmpty())) {
												join = new Join(sqlTableName2.toString(), alias, false, "t" + alias + "." + collection.getIdTarget() + " = t.t" + oldAlias + "id");
											} else {
												join = new Join(sqlTableName2.toString(), alias, false, "t" + alias + "." + collection.getIdTarget() + " = t.id");
											}
										} else {
											join = new Join(sqlTableName.toString(), alias, false, "t" + alias + ".id = t" + oldAlias + "." + collection.getIdSource());
										}
									}
									if ((!"id".equalsIgnoreCase(collection.getIdTarget())) && (!asso) && (oldAlias != null) && (!oldAlias.isEmpty())) {
										join.setExternal("t" + oldAlias + ".id" + " as t" + oldAlias + collection.getIdSource());
									}
									join.setExternalToDo(newExternalToDo);
								} else {
									alias = join.getAlias();
								}
								joinMap.put(a, join);
								buildSelectJoinMap(joinMap, cd, a, Integer.toString(alias), columns, newExternalToDo);
							}
						}
					}
				}
			}
		}
	}

	private boolean isContained(String key, Set<String> columns, boolean join) {
		if (CollectionHelper.isEmpty(columns)) {
			return true;
		}
		if ("id".equals(key)) {
			return true;
		}
		String regex = "^" + key + "(\\..+)*$";
		Iterator<String> ite = columns.iterator();
		while (ite.hasNext()) {
			String col = ite.next();
			if (col.endsWith("*")) {
				int lastIndexOf = key.lastIndexOf('.');
				if (lastIndexOf != -1) {
					String tree = key.substring(0, lastIndexOf + 1);
					if (join) {
						tree = key;
						if (col.equals(tree + "*")) {
							return true;
						}
					} else if (col.substring(0, col.length() - 1).startsWith(tree)) {
						return true;
					}
				} else if ("*".equals(col)) {
					return true;
				}
			}
			if (col.matches(regex)) {
				return true;
			}
		}
		return false;
	}

	public void buildOrderJoinMap(List<ISortOrder> orderList, Map<String, Join> joinMap, ComponentDescriptor ed, String oldName, String oldAlias) {
		if (CollectionHelper.isNotEmpty(orderList)) {
			for (ISortOrder sortOrder : orderList) {
				if (sortOrder.getPropertyName() != null) {
					int indexOf = sortOrder.getPropertyName().indexOf('.');
					if (indexOf > -1) {
						String head = sortOrder.getPropertyName().substring(0, indexOf);
						Class<?> propertyClass = ed.getPropertyClass(head);
						String name = null;
						if (oldName != null) {
							name = oldName + "." + head;
						} else {
							name = head;
						}

						if (IEntity.class.isAssignableFrom(propertyClass)) {
							@SuppressWarnings("unchecked")
							ComponentDescriptor componentDescriptor = ComponentFactory.getInstance().getDescriptor((Class<IEntity>) propertyClass);
							String sqlTableName = getSqlTableName(componentDescriptor);
							if (sqlTableName != null) {
								Join join = joinMap.get(name);
								if (join == null) {
									DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) ed.getPropertyDescriptor(head).getPropertyExtensionDescriptor(
											IDatabaseComponentExtension.class);
									if (dp != null && dp.getColumn() != null) {
										int alias = joinMap.size();
										join = new Join(sqlTableName, alias, false, "t" + alias + ".id = t" + oldAlias + "." + dp.getColumn().getSqlName());
										joinMap.put(name, join);
									}
								}

								if (join == null) {
									// FIXME on est dans un service
									throw new ServiceException(PAGINATION_ERROR, head + " in " + ed.getComponentClass().getSimpleName() + " has no join", null);
								}
								String alias = join.getAlias().toString();
								String tail = sortOrder.getPropertyName().substring(indexOf + 1);
								if (tail.contains(".")) {
									List<ISortOrder> newSortOrderList = new ArrayList<ISortOrder>();
									ISortOrder newSortOrder = ComponentFactory.getInstance().createInstance(ISortOrder.class);
									newSortOrder.setPropertyName(tail);
									newSortOrderList.add(newSortOrder);
									buildOrderJoinMap(newSortOrderList, joinMap, componentDescriptor, name, alias);
								}
							}
						}
					}
				}
			}
		}
	}

	public void selectFields(SQL sqlBuilder, Map<String, Join> joinMap, ComponentDescriptor ed, String alias, String name, Set<String> columns, StringBuilder external) {
		for (String propertyName : ed.getPropertyNames()) {
			PropertyDescriptor propertyDescriptor = ed.getPropertyDescriptor(propertyName);
			if (propertyDescriptor != null) {
				DatabasePropertyExtensionDescriptor propertyExtensionDescriptor = (DatabasePropertyExtensionDescriptor) propertyDescriptor
						.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
				if (propertyExtensionDescriptor != null) {
					Collection collection = propertyExtensionDescriptor.getCollection();
					Column column = propertyExtensionDescriptor.getColumn();
					NlsColumn nlsMeaning = propertyExtensionDescriptor.getNlsColumn();
					StringBuilder nsb = new StringBuilder();
					if (name != null) {
						nsb.append(name).append(".");
					}
					nsb.append(propertyName);
					String n = nsb.toString();
					if (collection != null) {
						selectFieldsCollection(sqlBuilder, joinMap, alias, collection, n, columns, external);
					} else if (column != null) {
						selectFieldsColumn(sqlBuilder, joinMap, alias, name, column, n, columns, external);
					} else if (nlsMeaning != null) {
						selectFieldsNlsMeaning(sqlBuilder, joinMap, ed, alias, name, nlsMeaning, n, columns, external);
					}
				}
			}
		}
	}

	private void selectFieldsCollection(SQL sqlBuilder, Map<String, Join> joinMap, String alias, Collection collection, String n, Set<String> columns, StringBuilder external) {
		if ((collection.getComponentClass() != null) && (collection.getAlias() != null) && (!collection.getAlias().isEmpty())) {
			ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(collection.getComponentClass());

			selectFields(sqlBuilder, joinMap, cd, (alias != null ? alias : "") + collection.getAlias(), n, columns, external);
		}
	}

	private void selectFieldsColumn(SQL sqlBuilder, Map<String, Join> joinMap, String alias, String name, Column column, String n, Set<String> columns, StringBuilder external) {
		Join join = joinMap.get(name);
		StringBuilder sqlName = new StringBuilder();
		if (isContained(n, columns, false)) {
			if ((join != null) && (join.getAlias() != null)) {
				if ((external == null) && (join.isExternalDone() == false) && (join.getExternal() != null)) {
					sqlName.append(join.getExternal());
				} else {
					sqlName.append("t").append(join.getAlias()).append(".");
				}
			} else {
				sqlName.append("t."); // warning
			}
			if ((join == null) || (!join.getExternalToDo()) || (external != null)) {
				sqlName.append(column.getSqlName());
				String s = sqlName.toString();
				if ((s != null) && (!s.isEmpty())) {
					if (alias != null) {
						if (external != null) {
							if ((join != null) && (join.getExternalToDo())) {
								if (external.length() > 0) {
									external.append(",\n");
								}
								external.append(s + " as " + alias + column.getSqlName());
							}
						} else if ((join == null) || (!join.getExternalToDo())) {
							sqlBuilder.SELECT(s + " as " + alias + column.getSqlName());
						} else if (join.isExternalDone() == false) {
							sqlBuilder.SELECT(s);
							if (join.getExternal() != null) { // on
																// vire
																// les
																// autres
																// select
																// identiques
																// découlant
																// des
																// assos
								for (Entry<String, Join> joinL : joinMap.entrySet()) {
									if (join.getExternal().equals(joinL.getValue().getExternal())) {
										joinL.getValue().setExternalDone(true);
									}
								}
							}
							join.setExternalDone(true);
						}
					} else {
						if (external != null) {
							if ((join != null) && (join.getExternalToDo())) {
								external.append(",\n").append(s);
							}
						} else if ((join == null) || (!join.getExternalToDo())) {
							sqlBuilder.SELECT(s);
						} else if (join.isExternalDone() == false) {
							sqlBuilder.SELECT(s);
							if (join.getExternal() != null) { // on
																// vire
																// les
																// autres
																// select
																// identiques
																// découlant
																// des
																// assos
								for (Entry<String, Join> joinL : joinMap.entrySet()) {
									if (join.getExternal().equals(joinL.getValue().getExternal())) {
										joinL.getValue().setExternalDone(true);
									}
								}
							}
							join.setExternalDone(true);
						}
					}
				}
			}
		}
	}

	private void selectFieldsNlsMeaning(SQL sqlBuilder, Map<String, Join> joinMap, ComponentDescriptor ed, String alias, String name, NlsColumn nlsMeaning, String n, Set<String> columns,
			StringBuilder external) {
		if (external == null) {
			Join join = joinMap.get(name);
			if (isContained(n, columns, false)) {
				String prefix;
				if (join != null && join.getAlias() != null) {
					prefix = new StringBuilder("t").append(join.getAlias()).append(".").toString();
				} else {
					prefix = "t.";
				}
				StringBuilder sb = new StringBuilder();
				sb.append("p_nls.get_nls_server_message('").append(getSqlTableName(ed)).append("',");
				sb.append(prefix).append("ID,");
				sb.append("'").append(nlsMeaning.getSqlName()).append("',");
				sb.append(prefix).append(nlsMeaning.getSqlName()).append(")");
				sb.append(" as ");
				if (alias != null) {
					sb.append(alias);
				}
				sb.append(nlsMeaning.getSqlName());
				sqlBuilder.SELECT(sb.toString());
			}
		}
	}

	public String getCollectionJoin(Map<String, Join> joinMap) {
		StringBuilder sql = new StringBuilder();
		List<Join> joinList = new ArrayList<Join>();
		for (Join join : joinMap.values()) {
			joinList.add(join);
		}
		Collections.sort(joinList, new Comparator<Join>() {
			@Override
			public int compare(Join o1, Join o2) {
				return o1.getAlias().compareTo(o2.getAlias());
			}
		});
		for (Join join : joinList) {
			if (join.getExternalToDo()) {
				sql.append("\nLEFT OUTER JOIN ").append(join.getSqlTableName() + " t" + join.getAlias() + " on " + join.getJoinClause());
			}
		}
		return sql.toString();
	}

	public static final class Join {

		private String sqlTableName;

		private Integer alias;

		private Boolean inner;

		private String joinClause;

		private String external;

		private boolean externalDone;

		private boolean externalToDo;

		public Join(String sqlTableName, Integer alias, boolean inner, String joinClause) {
			this.sqlTableName = sqlTableName;
			this.alias = alias;
			this.inner = inner;
			this.joinClause = joinClause;
			this.external = null;
			this.externalDone = false;
			this.externalToDo = false;
		}

		public void setExternal(String external) {
			this.external = external;
		}

		public String getExternal() {
			return external;
		}

		public void setExternalToDo(boolean externalToDo) {
			this.externalToDo = externalToDo;
		}

		public boolean getExternalToDo() {
			return externalToDo;
		}

		public void setExternalDone(Boolean externalDone) {
			this.externalDone = externalDone;
		}

		public Boolean isExternalDone() {
			return externalDone;
		}

		public String getSqlTableName() {
			return sqlTableName;
		}

		public Integer getAlias() {
			return alias;
		}

		public void setInner(Boolean inner) {
			this.inner = inner;
		}

		public Boolean isInner() {
			return inner;
		}

		public String getJoinClause() {
			return joinClause;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public static final class FinalObject {

		private final ComponentDescriptor componentDescriptor;

		private final PropertyDescriptor propertyDescriptor;

		private final DatabasePropertyExtensionDescriptor databasePropertyExtensionDescriptor;

		public FinalObject(ComponentDescriptor componentDescriptor, PropertyDescriptor propertyDescriptor, DatabasePropertyExtensionDescriptor databasePropertyExtensionDescriptor) {
			super();

			this.componentDescriptor = componentDescriptor;
			this.propertyDescriptor = propertyDescriptor;
			this.databasePropertyExtensionDescriptor = databasePropertyExtensionDescriptor;
		}

		public final ComponentDescriptor getComponentDescriptor() {
			return componentDescriptor;
		}

		public final PropertyDescriptor getPropertyDescriptor() {
			return propertyDescriptor;
		}

		public DatabasePropertyExtensionDescriptor getDatabasePropertyExtensionDescriptor() {
			return databasePropertyExtensionDescriptor;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public <E extends IComponent> IFilterContext createFilterContext(Class<E> componentClass, Map<String, Join> joinMap) {
		return new MyFilterContext<E>(nodeProcessFactory, componentClass, joinMap);
	}

	public String buildHint(ComponentDescriptor componentDescriptor, Map<String, Object> valueFilterMap) {
		if (hintProcesses != null) {
			Iterator<HintProcess> ite = hintProcesses.iterator();
			while (ite.hasNext()) {
				HintProcess hintProcess = ite.next();
				String hint = hintProcess.buildHint(componentDescriptor, valueFilterMap);
				if (hint != null) {
					return hint;
				}
			}
		}
		return "";
	}

	public String buildHint(ComponentDescriptor componentDescriptor, RootNode rootNode) {
		if (hintProcesses != null) {
			Iterator<HintProcess> ite = hintProcesses.iterator();
			while (ite.hasNext()) {
				HintProcess hintProcess = ite.next();
				String hint = hintProcess.buildHint(componentDescriptor, rootNode);
				if (hint != null) {
					return hint;
				}
			}
		}
		return "";
	}

	private class MyFilterContext<E extends IComponent> extends AbstractFilterContext {

		private final Class<E> componentClass;

		private final Map<String, Join> joinMap;

		private final ComponentDescriptor componentDescriptor;

		public MyFilterContext(NodeProcessFactory nodeProcessFactory, Class<E> componentClass, Map<String, Join> joinMap) {
			super(nodeProcessFactory);

			this.componentClass = componentClass;
			this.joinMap = joinMap;
			this.componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
		}

		@Override
		public <F extends AbstractNode> String getParameterFieldName(F node, String propertyName) {
			FinalObject finalObject = getFinalObject(componentClass, propertyName);
			PropertyDescriptor pd = finalObject.getPropertyDescriptor();

			StringBuilder sb = new StringBuilder();
			sb.append(propertyName.replaceAll("\\.", "_"));
			sb.append(node.hashCode());
			if (IEntity.class.isAssignableFrom(pd.getPropertyClass())) { // if
																			// field
																			// is
																			// an
																			// entity,
																			// we
																			// compare
																			// the
																			// ids
				sb.append(".id");
			}
			return sb.toString();
		}

		@Override
		public <F extends AbstractNode> String getMyBatisParameterField(F node, Class<?> javaType, JdbcType jdbcType, String parameterFieldName) {
			StringBuilder sb = new StringBuilder();
			sb.append("#{");
			sb.append("valueFilterMap").append(".");
			sb.append(parameterFieldName);
			if (javaType != null) {
				sb.append(",javaType=").append(javaType.getName());
			}
			if (jdbcType != null) {
				sb.append(",jdbcType=").append(jdbcType.name());
			}
			sb.append("}");
			return sb.toString();
		}

		@Override
		public <F extends AbstractNode> String getSqlName(F node, String propertyName) {
			int indexOf = propertyName.lastIndexOf('.');
			Join join = null;
			if (indexOf != -1) {
				join = joinMap.get(propertyName.substring(0, indexOf));
			}

			FinalObject finalObject = getFinalObject(componentClass, propertyName);
			DatabasePropertyExtensionDescriptor dp = finalObject.getDatabasePropertyExtensionDescriptor();

			String prefix;
			if (join != null && join.getAlias() != null) {
				prefix = new StringBuilder("t").append(join.getAlias()).append(".").toString();
			} else {
				prefix = "t.";
			}

			String sqlName = "";
			if (dp != null) {
				if ((dp.getCollection() != null) && (dp.getCollection().getIdSource() != null) && (!dp.getCollection().getIdSource().isEmpty())) {
					sqlName = new StringBuilder(prefix).append(dp.getCollection().getIdSource()).toString();
				} else if ((dp.getColumn() != null) && (dp.getColumn().getSqlName() != null)) {
					sqlName = new StringBuilder(prefix).append(dp.getColumn().getSqlName()).toString();
				} else if (dp.getNlsColumn() != null) {
					StringBuilder sb = new StringBuilder();
					sb.append("p_nls.get_nls_server_message('").append(getSqlTableName(finalObject.componentDescriptor)).append("',");
					sb.append(prefix).append("ID,");
					sb.append("'").append(dp.getNlsColumn().getSqlName()).append("',");
					sb.append(prefix).append(dp.getNlsColumn().getSqlName()).append(")");
					sqlName = sb.toString();
				}
			}
			return sqlName;
		}
	}
}
