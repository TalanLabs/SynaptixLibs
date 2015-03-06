package com.synaptix.entity.extension;

import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.IComponent;
import com.synaptix.component.extension.IClassExtensionDescriptor;
import com.synaptix.component.extension.IPropertyExtensionDescriptor;
import com.synaptix.component.factory.AbstractComponentExtensionProcessor;
import com.synaptix.entity.extension.IDatabaseComponentExtension.Collection;
import com.synaptix.entity.extension.IDatabaseComponentExtension.DefaultValue;
import com.synaptix.entity.extension.IDatabaseComponentExtension.JdbcType;
import com.synaptix.entity.extension.IDatabaseComponentExtension.NlsColumn;
import com.synaptix.entity.extension.IDatabaseComponentExtension.UpperOnly;

public class DatabaseComponentExtensionProcessor extends AbstractComponentExtensionProcessor {

	@Override
	public IClassExtensionDescriptor createClassExtensionDescriptor(Class<? extends IComponent> componentClass) {
		IClassExtensionDescriptor classExtensionDescriptor = null;
		if (IComponent.class.isAssignableFrom(componentClass)) {
			Table entity = componentClass.getAnnotation(Table.class);
			if (entity != null) {
				classExtensionDescriptor = new DatabaseClassExtensionDescriptor(entity.schema(), entity.name());
			}
		}
		return classExtensionDescriptor;
	}

	@Override
	public IPropertyExtensionDescriptor createPropertyExtensionDescriptor(Class<? extends IComponent> componentClass, Method getterMethod) {
		IPropertyExtensionDescriptor propertyExtensionDescriptor = null;
		// Entity entity = componentClass.getAnnotation(Entity.class);
		if (IComponent.class.isAssignableFrom(componentClass)) {
			Column column = getterMethod.getAnnotation(Column.class);
			NlsColumn nlsMeaning = getterMethod.getAnnotation(NlsColumn.class);
			UpperOnly upperOnly = getterMethod.getAnnotation(UpperOnly.class);
			JdbcType jdbcType = getterMethod.getAnnotation(JdbcType.class);
			DefaultValue defaultValue = getterMethod.getAnnotation(DefaultValue.class);

			if (column != null && nlsMeaning != null) {
				DatabasePropertyExtensionDescriptor.NlsColumn nls = new DatabasePropertyExtensionDescriptor.NlsColumn(column.name(), column.length(), !column.nullable(), upperOnly != null);
				propertyExtensionDescriptor = new DatabasePropertyExtensionDescriptor(null, null, nls);
			} else {
				DatabasePropertyExtensionDescriptor.Column col = null;
				if (column != null) {
					JdbcTypesEnum jdbcTypeOld = jdbcType != null ? jdbcType.value() : null;
					if (jdbcTypeOld == null || JdbcTypesEnum.NONE.equals(jdbcTypeOld)) {
						jdbcTypeOld = JdbcTypesEnum.which(getterMethod.getReturnType());
					}
					col = new DatabasePropertyExtensionDescriptor.Column(column.name(), column.length(), !column.nullable(), defaultValue != null ? defaultValue.value() : null, jdbcTypeOld,
							!column.insertable() && !column.updatable(), upperOnly != null);
				}

				Collection collection = getterMethod.getAnnotation(Collection.class);
				DatabasePropertyExtensionDescriptor.Collection collect = null;
				if (collection != null) {
					collect = new DatabasePropertyExtensionDescriptor.Collection(collection.sqlTableName(), collection.schema(), collection.idSource(), collection.idTarget(), collection.alias(),
							collection.componentClass());
				}

				propertyExtensionDescriptor = new DatabasePropertyExtensionDescriptor(col, collect, null);
			}
		}
		return propertyExtensionDescriptor;
	}
}
