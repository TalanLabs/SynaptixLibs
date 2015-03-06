package com.synaptix.mybatis.dao.mapper;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.jdbc.SQL;

import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.extension.BusinessPropertyExtensionDescriptor;
import com.synaptix.entity.extension.DatabaseClassExtensionDescriptor;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IBusinessComponentExtension;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

public class BusinessProvider {

	private static final Log LOG = LogFactory.getLog(BusinessProvider.class);

	private static <E extends IEntity> void whereFilter(SQL sqlBuilder, ComponentDescriptor ed, E entity, boolean considerCancelled) {
		for (PropertyDescriptor pd : ed.getPropertyDescriptors()) {
			BusinessPropertyExtensionDescriptor bp = (BusinessPropertyExtensionDescriptor) pd.getPropertyExtensionDescriptor(IBusinessComponentExtension.class);
			if (bp != null) {
				DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) pd.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
				Object value = entity.straightGetProperty(pd.getPropertyName());
				if (value != null) {
					if (IEntity.class.isAssignableFrom(value.getClass())) {
						sqlBuilder.WHERE(new StringBuilder(dp.getColumn().getSqlName()).append(" = #{entity.").append(pd.getPropertyName()).append(".").append("id").append("}").toString());
					} else {
						if (value instanceof String) {
							if (((String) value).trim().isEmpty()) {
								sqlBuilder.WHERE(new StringBuilder().append(dp.getColumn().getSqlName()).append(" is null").toString());
							} else {
								if (dp.getColumn().isUpperOnly()) {
									sqlBuilder.WHERE(new StringBuilder().append(dp.getColumn().getSqlName()).append(" = UPPER(#{entity.").append(pd.getPropertyName()).append("}) ").toString());
								} else {
									sqlBuilder.WHERE(new StringBuilder("UPPER(").append(dp.getColumn().getSqlName()).append(") = UPPER(#{entity.").append(pd.getPropertyName()).append("}) ")
											.toString());
								}
							}
						} else {
							sqlBuilder.WHERE(new StringBuilder().append(dp.getColumn().getSqlName()).append(" = #{entity.").append(pd.getPropertyName()).append("} ").toString());
						}
					}
				} else {
					sqlBuilder.WHERE(new StringBuilder().append(dp.getColumn().getSqlName()).append(" is null").toString());
				}
			}
		}
		if (entity.getId() != null) {
			sqlBuilder.WHERE(new StringBuilder("ID <> #{entity.id}").toString());
		}
		if ((entity instanceof ICancellable) && (!considerCancelled)) {
			sqlBuilder.WHERE("check_cancel='0'");
		}
	}

	/**
	 * Get the table name with schema if available
	 * 
	 * @param ed
	 * @return
	 */
	private static String getSqlTableName(ComponentDescriptor ed) {
		DatabaseClassExtensionDescriptor dc = (DatabaseClassExtensionDescriptor) ed.getClassExtensionDescriptor(IDatabaseComponentExtension.class);
		String sqlTableName = null;
		if (dc != null) {
			sqlTableName = dc.getSqlTableName();
			if ((dc.getSchema() != null) && (!dc.getSchema().isEmpty())) {
				sqlTableName = new StringBuilder(dc.getSchema()).append(".").append(sqlTableName).toString();
			}
		}
		return sqlTableName;
	}

	@SuppressWarnings("unchecked")
	public static <E extends IEntity> String checkUnicityConstraint(Map<String, Object> map) {
		E entity = (E) map.get("entity");
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(entity);

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("count(0)");

		sqlBuilder.FROM(getSqlTableName(ed));
		whereFilter(sqlBuilder, ed, entity, false);

		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	@SuppressWarnings("unchecked")
	public static <E extends IEntity> String getSimilarFromUnicityConstraint(Map<String, Object> map) {
		E entity = (E) map.get("entity");
		Boolean considerCancelled = (Boolean) map.get("considerCancelled");
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(entity);

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("*");

		sqlBuilder.FROM(getSqlTableName(ed));
		whereFilter(sqlBuilder, ed, entity, (considerCancelled != null ? considerCancelled : false));

		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}
}
