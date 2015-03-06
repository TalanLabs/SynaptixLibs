package com.synaptix.deployer.delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.model.Binome;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper.PropertyArrayBuilder;
import com.synaptix.deployer.model.ConstraintFields;
import com.synaptix.deployer.model.ICompareConstraintResult;
import com.synaptix.deployer.model.ICompareStructureResult;
import com.synaptix.deployer.model.IConstraint;
import com.synaptix.deployer.model.IDifference;
import com.synaptix.deployer.model.IDifferenceAttribute;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.deployer.model.ITableColumn;
import com.synaptix.deployer.model.TableColumnFields;

public class DeployerDelegate {

	private static final Comparator<ITableColumn> tableColumnComparator = new Comparator<ITableColumn>() {

		@Override
		public int compare(ITableColumn o1, ITableColumn o2) {
			String t1 = o1.getTableName();
			String t2 = o2.getTableName();
			if (t1.equals(t2)) {
				return o1.getColumnName().compareTo(o2.getColumnName());
			}
			return t1.compareTo(t2);
		}
	};

	private static final Comparator<IConstraint> constraintComparator = new Comparator<IConstraint>() {

		@Override
		public int compare(IConstraint o1, IConstraint o2) {
			String t1 = o1.getTableName();
			String t2 = o2.getTableName();
			if (t1.equals(t2)) {
				return o1.getConstraintName().compareTo(o2.getConstraintName());
			}
			return t1.compareTo(t2);
		}
	};

	public ICompareStructureResult compileStructureResult(ISynaptixDatabaseSchema database1, List<ITableColumn> database1TableColumnList, ISynaptixDatabaseSchema database2,
			List<ITableColumn> database2TableColumnList, List<String> ignoreTableList) {
		ICompareStructureResult compareResult = ComponentFactory.getInstance().createInstance(ICompareStructureResult.class);

		List<Binome<ISynaptixDatabaseSchema, String>> binomeMissingTableList = new ArrayList<Binome<ISynaptixDatabaseSchema, String>>();
		Map<String, List<ITableColumn>> differenceMapDb1 = new HashMap<String, List<ITableColumn>>();
		Map<String, List<ITableColumn>> differenceMapDb2 = new HashMap<String, List<ITableColumn>>();
		// List<IDifference> differenceList = new ArrayList<IDifference>();
		compareResult.setMissingTableList(binomeMissingTableList);
		compareResult.setDifferenceMapDb1(differenceMapDb1);
		compareResult.setDifferenceMapDb2(differenceMapDb2);
		// compareResult.setDifferenceList(differenceList);
		compareResult.setTableColumnDb1(database1TableColumnList);
		compareResult.setTableColumnDb2(database2TableColumnList);

		Set<String> db1TableSet = new HashSet<String>();
		Set<String> db2TableSet = new HashSet<String>();

		int idx1 = 0;
		int idx2 = 0;
		int max1 = CollectionHelper.size(database1TableColumnList);
		int max2 = CollectionHelper.size(database2TableColumnList);

		Collections.sort(database1TableColumnList, tableColumnComparator);
		Collections.sort(database2TableColumnList, tableColumnComparator);

		String[] fields = PropertyArrayBuilder.build(TableColumnFields.dataDefault(), TableColumnFields.dataScale(), TableColumnFields.nullable(), TableColumnFields.precision(),
				TableColumnFields.type(), TableColumnFields.dataLength());

		while ((idx1 < max1) && (idx2 < max2)) {
			boolean ignore = false;
			ITableColumn env1 = null;
			do {
				env1 = database1TableColumnList.get(idx1);
				if (ignoreTableList.contains(env1.getTableName())) {
					ignore = true;
					idx1++;
				} else {
					ignore = false;
				}
				if (idx1 > max1) {
					ignore = false;
					env1 = null;
				}
			} while (ignore);

			ITableColumn env2 = null;
			do {
				env2 = database2TableColumnList.get(idx2);
				if (ignoreTableList.contains(env2.getTableName())) {
					ignore = true;
					idx2++;
				} else {
					ignore = false;
				}
				if (idx2 > max2) {
					ignore = false;
					env2 = null;
				}
			} while (ignore);

			if ((env1 != null) && (env2 != null)) {

				db1TableSet.add(env1.getTableName());
				db2TableSet.add(env2.getTableName());

				int compare = tableColumnComparator.compare(env1, env2);
				if (compare == 0) {
					IDifference difference = getDifferentFields(env1, env2, fields);
					if (difference != null) {
						// difference.setTableName(env1.getTableName());
						// difference.setObjectName(env1.getColumnName());
						// differenceList.add(difference);
						List<ITableColumn> list1 = differenceMapDb1.get(env1.getTableName());
						if (list1 == null) {
							list1 = new ArrayList<ITableColumn>();
							differenceMapDb1.put(env1.getTableName(), list1);
						}
						list1.add(env1);
						List<ITableColumn> list2 = differenceMapDb1.get(env1.getTableName());
						if (list2 == null) {
							list2 = new ArrayList<ITableColumn>();
							differenceMapDb2.put(env1.getTableName(), list2);
						}
						list2.add(env2);
					}
					idx1++;
					idx2++;
				} else {
					if (compare > 0) {
						List<ITableColumn> list = differenceMapDb2.get(env2.getTableName());
						if (list == null) {
							list = new ArrayList<ITableColumn>();
							differenceMapDb2.put(env2.getTableName(), list);
						}
						list.add(env2);
						idx2++;
					} else if (compare < 0) {
						List<ITableColumn> list = differenceMapDb1.get(env1.getTableName());
						if (list == null) {
							list = new ArrayList<ITableColumn>();
							differenceMapDb1.put(env1.getTableName(), list);
						}
						list.add(env1);
						idx1++;
					}
				}
			}
		}
		for (int i = idx1; i < max1; i++) {
			ITableColumn env1 = database1TableColumnList.get(i);
			List<ITableColumn> list = differenceMapDb1.get(env1.getTableName());
			if (list == null) {
				list = new ArrayList<ITableColumn>();
				differenceMapDb1.put(env1.getTableName(), list);
			}
		}
		for (int i = idx2; i < max2; i++) {
			ITableColumn env2 = database2TableColumnList.get(i);
			List<ITableColumn> list = differenceMapDb2.get(env2.getTableName());
			if (list == null) {
				list = new ArrayList<ITableColumn>();
				differenceMapDb2.put(env2.getTableName(), list);
			}
			list.add(env2);
		}

		for (String table : db1TableSet) {
			if (!db2TableSet.contains(table)) {
				binomeMissingTableList.add(new Binome<ISynaptixDatabaseSchema, String>(database2, table));
			}
		}

		for (String table : db2TableSet) {
			if (!db1TableSet.contains(table)) {
				binomeMissingTableList.add(new Binome<ISynaptixDatabaseSchema, String>(database1, table));
			}
		}

		return compareResult;
	}

	/**
	 * 
	 * @param env1
	 * @param env2
	 * @param fields
	 * @return
	 */
	private <E extends IComponent> IDifference getDifferentFields(E env1, E env2, String[] fields) {
		IDifference difference = null;
		for (String field : fields) {
			Object env1Value = env1.straightGetProperty(field);
			Object env2Value = env2.straightGetProperty(field);
			if ((env1Value != null) && (String.class.isAssignableFrom(env1Value.getClass()))) {
				env1Value = StringUtils.trim((String) env1Value);
			}
			if ((env2Value != null) && (String.class.isAssignableFrom(env2Value.getClass()))) {
				env2Value = StringUtils.trim((String) env2Value);
			}
			if ((env1Value != null) && (!env1Value.equals(env2Value))) {
				if (difference == null) {
					difference = createDifference();
				}
				IDifferenceAttribute attribute = createDifferenceAttribute(field, env1Value, env2Value);
				difference.getDifferenceAttributeList().add(attribute);
			} else if ((env2Value != null) && (!env2Value.equals(env1Value))) {
				if (difference == null) {
					difference = createDifference();
				}
				IDifferenceAttribute attribute = createDifferenceAttribute(field, env1Value, env2Value);
				difference.getDifferenceAttributeList().add(attribute);
			}
		}
		return difference;
	}

	private IDifference createDifference() {
		IDifference difference = ComponentFactory.getInstance().createInstance(IDifference.class);
		difference.setDifferenceAttributeList(new ArrayList<IDifferenceAttribute>());
		return difference;
	}

	private IDifferenceAttribute createDifferenceAttribute(String field, Object env1Value, Object env2Value) {
		IDifferenceAttribute attribute = ComponentFactory.getInstance().createInstance(IDifferenceAttribute.class);
		attribute.setAttribute(field);
		attribute.setValue1(env1Value);
		attribute.setValue2(env2Value);
		return attribute;
	}

	public ICompareConstraintResult compileConstraintResult(ISynaptixDatabaseSchema database1, List<IConstraint> database1ConstraintList, ISynaptixDatabaseSchema database2,
			List<IConstraint> database2ConstraintList, List<String> ignoreTableList) {
		ICompareConstraintResult compareResult = ComponentFactory.getInstance().createInstance(ICompareConstraintResult.class);

		List<Binome<ISynaptixDatabaseSchema, IConstraint>> binomeMissingList = new ArrayList<Binome<ISynaptixDatabaseSchema, IConstraint>>();
		List<IDifference> differenceList = new ArrayList<IDifference>();
		compareResult.setMissingConstraintList(binomeMissingList);
		compareResult.setDifferenceList(differenceList);

		int idx1 = 0;
		int idx2 = 0;
		int max1 = CollectionHelper.size(database1ConstraintList);
		int max2 = CollectionHelper.size(database2ConstraintList);

		Collections.sort(database1ConstraintList, constraintComparator);
		Collections.sort(database2ConstraintList, constraintComparator);

		String[] fields = PropertyArrayBuilder.build(ConstraintFields.constraintType(), ConstraintFields.searchCondition(), ConstraintFields.columns());

		while ((idx1 < max1) && (idx2 < max2)) {
			boolean ignore = false;
			IConstraint env1 = null;
			do {
				env1 = database1ConstraintList.get(idx1);
				if (ignoreTableList.contains(env1.getTableName())) {
					ignore = true;
					idx1++;
				} else {
					ignore = false;
				}
				if (idx1 > max1) {
					ignore = false;
					env1 = null;
				}
			} while (ignore);

			IConstraint env2 = null;
			do {
				env2 = database2ConstraintList.get(idx2);
				if (ignoreTableList.contains(env2.getTableName())) {
					ignore = true;
					idx2++;
				} else {
					ignore = false;
				}
				if (idx2 > max2) {
					ignore = false;
					env2 = null;
				}
			} while (ignore);

			if ((env1 != null) && (env2 != null)) {
				int compare = constraintComparator.compare(env1, env2);
				if (compare == 0) {
					IDifference difference = getDifferentFields(env1, env2, fields);
					if (difference != null) {
						difference.setTableName(env1.getTableName());
						difference.setObjectName(env1.getConstraintName());
						differenceList.add(difference);
					}
					idx1++;
					idx2++;
				} else {
					if (compare > 0) {
						binomeMissingList.add(new Binome<ISynaptixDatabaseSchema, IConstraint>(database1, env2));
						idx2++;
					} else if (compare < 0) {
						binomeMissingList.add(new Binome<ISynaptixDatabaseSchema, IConstraint>(database2, env1));
						idx1++;
					}
				}
			}
		}
		for (int i = idx1; i < max1; i++) {
			IConstraint env1 = database1ConstraintList.get(i);
			binomeMissingList.add(new Binome<ISynaptixDatabaseSchema, IConstraint>(database2, env1));
		}
		for (int i = idx2; i < max2; i++) {
			IConstraint env2 = database2ConstraintList.get(i);
			binomeMissingList.add(new Binome<ISynaptixDatabaseSchema, IConstraint>(database1, env2));
		}

		return compareResult;
	}
}
