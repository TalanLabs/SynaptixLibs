package com.synaptix.service.filter.builder;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.service.filter.AbstractNode;
import com.synaptix.service.filter.branch.AndOperator;
import com.synaptix.service.filter.branch.NotOperator;
import com.synaptix.service.filter.branch.OrOperator;
import com.synaptix.service.filter.leaf.AssossiationLeaf;
import com.synaptix.service.filter.leaf.AssossiationPropertyValue;
import com.synaptix.service.filter.leaf.ComparePropertyValue;
import com.synaptix.service.filter.leaf.EqualsPropertyValue;
import com.synaptix.service.filter.leaf.InPropertyValue;
import com.synaptix.service.filter.leaf.LikePropertyValue;
import com.synaptix.service.filter.leaf.NullProperty;

public abstract class AbstractOperatorBuilder<F extends AbstractNode, U extends AbstractOperatorBuilder<F, U>> {

	private final List<AbstractNode> childNodes;

	public AbstractOperatorBuilder() {
		super();

		childNodes = new ArrayList<AbstractNode>();
	}

	public boolean isEmpty() {
		return childNodes.isEmpty();
	}

	public void addNode(AbstractNode node) {
		childNodes.add(node);
	}

	protected AbstractNode[] getChildNodes() {
		return childNodes.toArray(new AbstractNode[childNodes.size()]);
	}

	protected abstract U get();

	/**
	 * Add and operator
	 * 
	 * @param andOperator
	 * @return
	 */
	public U addAndOperator(AndOperator andOperator) {
		childNodes.add(andOperator);
		return get();
	}

	/**
	 * Add or operator
	 * 
	 * @param orOperator
	 * @return
	 */
	public U addOrOperator(OrOperator orOperator) {
		childNodes.add(orOperator);
		return get();
	}

	public U addNotOperator(NotOperator notOperator) {
		childNodes.add(notOperator);
		return get();
	}

	/**
	 * Add like property value
	 * 
	 * @param type
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public U addLikePropertyValue(LikePropertyValue.Type type, String propertyName, String value) {
		childNodes.add(new LikePropertyValue(type, propertyName, value));
		return get();
	}

	/**
	 * Add equals property value
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public <E> U addEqualsPropertyValue(String propertyName, E value) {
		childNodes.add(new EqualsPropertyValue<E>(propertyName, value));
		return get();
	}

	/**
	 * Add compare property value
	 * 
	 * @param type
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public <E> U addComparePropertyValue(ComparePropertyValue.Type type, String propertyName, E value) {
		childNodes.add(new ComparePropertyValue<E>(type, propertyName, value));
		return get();
	}

	/**
	 * Add in property value
	 * 
	 * @param type
	 * @param propertyName
	 * @param values
	 * @return
	 */
	public <E> U addInPropertyValue(InPropertyValue.Type type, String propertyName, E... values) {
		childNodes.add(new InPropertyValue<E>(type, propertyName, values));
		return get();
	}

	/**
	 * Add assossiation property value
	 * 
	 * @param type
	 * @param propertyName
	 * @param assossiationSqlTable
	 * @param assossiationSqlColumn
	 * @param value
	 * @return
	 */
	public <E> U addAssossiationPropertyValue(AssossiationPropertyValue.Type type, String propertyName, String assossiationSqlTable, String assossiationSqlColumn, E value) {
		childNodes.add(new AssossiationPropertyValue<E>(type, propertyName, assossiationSqlTable, assossiationSqlColumn, value));
		return get();
	}

	/**
	 * Add leaf property value
	 * 
	 * @param type
	 * @param propertyName
	 * @param assossiationSqlTable
	 * @param assossiationSqlColumn1
	 * @param assossiationSqlColumn2
	 * @param value
	 * @return
	 */
	public <E> U addAssossiationLeaf(AssossiationLeaf.Type type, String propertyName, String assossiationSqlTable, String assossiationSqlColumn1, String assossiationSqlColumn2, E value) {
		childNodes.add(new AssossiationLeaf<E>(type, propertyName, assossiationSqlTable, assossiationSqlColumn1, assossiationSqlColumn2, value));
		return get();
	}

	/**
	 * Add null property
	 * 
	 * @param type
	 * @param propertyName
	 * @return
	 */
	public U addNullProperty(NullProperty.Type type, String propertyName) {
		childNodes.add(new NullProperty(type, propertyName));
		return get();
	}

	public abstract F build();

}