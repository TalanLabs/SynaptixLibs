package com.synaptix.service.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.service.model.ISortOrder;

public class SortOrderHelper {

	private static Log LOG = LogFactory.getLog(SortOrderHelper.class);

	private static SortOrderHelper instance = null;
	private AttributeIterator sortIterator;

	private SortOrderHelper() {
	}

	public static SortOrderHelper getInstance() {
		if (instance == null) {
			instance = new SortOrderHelper();
		}
		return instance;
	}

	/**
	 * Extract columns
	 * 
	 * @param sortOrders
	 * @return
	 */
	public Set<String> extractColumns(List<ISortOrder> sortOrders) {
		Set<String> res = new HashSet<String>();
		if (sortOrders != null && !sortOrders.isEmpty()) {
			for (ISortOrder sortOrder : sortOrders) {
				res.add(sortOrder.getPropertyName());
			}
		}
		return res;
	}

	/**
	 * Transform a list of orders into a plain SQL order syntax
	 * 
	 * @param clazz
	 * @param orderList
	 * @return
	 */
	public String transformToSql(Class<? extends IComponent> clazz, List<ISortOrder> orderList) {
		return transformToSql(clazz, orderList, null);
	}

	/**
	 * If orderlist is null, sort by ROWID
	 * 
	 * @param clazz
	 * @param orderList
	 * @param defaultTableName
	 * @return
	 */
	public String transformToSql(Class<? extends IComponent> clazz, List<ISortOrder> orderList, String defaultTableName) {
		return transformToSql(clazz, orderList, defaultTableName, "ROWID");
	}

	private String processTableName(final String tableNameBuffer, final String elmt) {
		StringBuilder tableNameBuilder = new StringBuilder(tableNameBuffer);
		if (tableNameBuilder.length() != 0) {
			tableNameBuilder.append("_");
		}
		tableNameBuilder.append(elmt);
		return tableNameBuilder.toString();
	}

	private String processSqlColumn(final String defaultTableName, final String tableNameBuffer, final String elmt, final boolean isAscending, Class<? extends IComponent> clazz,
			final AttributeIterator sortIterator) {
		StringBuilder sb = new StringBuilder();
		String sqlColumnName = getSqlColumnName(sortIterator.getComponentDescriptor(), elmt);
		if (sqlColumnName == null) {
			LOG.error("ERROR: in class " + clazz.getSimpleName() + ", sqlName of property " + elmt + " doesn't exist");
		} else {
			if (tableNameBuffer.length() != 0) {
				sb.append(tableNameBuffer).append(".");
			} else if (defaultTableName != null) {
				sb.append(defaultTableName).append(".");
			}
			sb.append(sqlColumnName.toString());
			sb.append(isAscending ? " ASC" : " DESC");
		}
		return sb.toString();
	}

	private StringBuilder buildSqlForASortOrder(final String defaultTableName, final ISortOrder so, Class<? extends IComponent> clazz, int position) {
		StringBuilder tableNameBuffer = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		sortIterator = new AttributeIterator(so.getPropertyName(), clazz);
		while (sortIterator.hasNext()) {
			String elmt = sortIterator.next();
			if (sortIterator.isCurrentElmtAnEntity()) {
				tableNameBuffer = new StringBuilder(processTableName(tableNameBuffer.toString(), elmt));
			} else {
				if (position > 0) {
					sb.append(", ");
				}
				sb.append(processSqlColumn(defaultTableName, tableNameBuffer.toString(), elmt, so.isAscending(), clazz, sortIterator));
			}
		}
		return sb;
	}

	/**
	 * If orderlist is null, sort by defaultColumnSort
	 * 
	 * @param clazz
	 * @param orderList
	 * @param defaultTableName
	 * @param defaultColumnSort
	 * @return
	 */
	public String transformToSql(Class<? extends IComponent> clazz, List<ISortOrder> orderList, String defaultTableName, String defaultColumnSort) {
		StringBuilder sb = new StringBuilder();
		if (orderList != null && !orderList.isEmpty()) {
			int position = 0;
			for (ISortOrder so : orderList) {
				sb.append(buildSqlForASortOrder(defaultTableName, so, clazz, position++));
			}
		} else {
			if (defaultTableName != null) {
				sb.append(defaultTableName).append(".");
			}
			sb.append(defaultColumnSort);
		}
		return sb.toString();
	}

	private String getSqlColumnName(ComponentDescriptor cd, String propertyName) {
		PropertyDescriptor componentField = cd.getPropertyDescriptor(propertyName);

		if (componentField != null) {
			try {
				return ((DatabasePropertyExtensionDescriptor) componentField.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class)).getColumn().getSqlName();
			} catch (NullPointerException e) {
				System.out.println(propertyName);
				System.out.println(componentField.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class));
				System.out.println(((DatabasePropertyExtensionDescriptor) componentField.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class)).getColumn());
				System.out.println(((DatabasePropertyExtensionDescriptor) componentField.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class)).getColumn().getSqlName());
			}
		}
		return null;
	}

	public List<ISortOrder> importFromSortKeys(List<SortKey> keyList, String[] columnNames) {
		List<ISortOrder> orderList = new ArrayList<ISortOrder>();
		for (SortKey key : keyList) {
			ISortOrder so = ComponentFactory.getInstance().createInstance(ISortOrder.class);
			so.setPropertyName(columnNames[key.getColumn()]);
			so.setAscending((SortOrder.ASCENDING.equals(key.getSortOrder()) ? true : false));
			orderList.add(so);
		}
		return orderList;
	}

	public static boolean equalsSortOrder(List<ISortOrder> list1, List<ISortOrder> list2) {
		if (list1 == list2) {
			return true;
		}
		if (list1 == null || list2 == null || list1.size() != list2.size()) {
			return false;
		}

		Iterator<ISortOrder> it1 = list1.iterator();
		Iterator<ISortOrder> it2 = list2.iterator();
		ISortOrder obj1 = null;
		ISortOrder obj2 = null;

		while (it1.hasNext() && it2.hasNext()) {
			obj1 = it1.next();
			obj2 = it2.next();

			if (!(equalsSortOrder(obj1, obj2))) {
				return false;
			}
		}
		return !(it1.hasNext() || it2.hasNext());
	}

	private static boolean equalsSortOrder(ISortOrder obj1, ISortOrder obj2) {
		if (obj1 == obj2) {
			return true;
		}
		if (obj1 == null || obj2 == null) {
			return false;
		}
		if ((obj1.getPropertyName() != null) && (!obj1.getPropertyName().equals(obj2.getPropertyName()))) {
			return false;
		}
		if ((obj2.getPropertyName() != null) && (!obj2.getPropertyName().equals(obj1.getPropertyName()))) {
			return false;
		}
		if ((obj1.isAscending() != null) && (!obj1.isAscending().equals(obj2.isAscending()))) {
			return false;
		}
		if ((obj2.isAscending() != null) && (!obj2.isAscending().equals(obj1.isAscending()))) {
			return false;
		}
		return true;
	}

	/**
	 * Create a sort order list for property
	 * 
	 * @param propertyName
	 * @param ascending
	 * @return
	 */
	public static final List<ISortOrder> createSortOrders(String propertyName, boolean ascending) {
		ISortOrder sortOrder = ComponentFactory.getInstance().createInstance(ISortOrder.class);
		sortOrder.setPropertyName(propertyName);
		sortOrder.setAscending(ascending);
		return Arrays.asList(sortOrder);
	}

	/**
	 * Builder for SortOrder
	 * 
	 * @author Gaby
	 * 
	 */
	public static final class Builder {

		private List<ISortOrder> sortOrders;

		public Builder() {
			super();

			sortOrders = new ArrayList<ISortOrder>();
		}

		public Builder addProperty(String propertyName, boolean ascending) {
			assert propertyName != null;
			ISortOrder sortOrder = ComponentFactory.getInstance().createInstance(ISortOrder.class);
			sortOrder.setPropertyName(propertyName);
			sortOrder.setAscending(ascending);
			sortOrders.add(sortOrder);
			return this;
		}

		public List<ISortOrder> build() {
			return sortOrders;
		}
	}
}
