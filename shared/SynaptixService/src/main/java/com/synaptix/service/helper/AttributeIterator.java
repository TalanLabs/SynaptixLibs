package com.synaptix.service.helper;

import java.util.Iterator;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;

/**
 * Navigates into a String chain (entity attributes) with dot.
 * 
 * 
 * 
 * @author e368240
 * 
 */
public class AttributeIterator implements Iterator<String> {

	public String[] attributeArray;
	private int position = 0;
	private boolean isCurrentElmtAnEntity;
	private ComponentDescriptor componentDescriptor;

	/**
	 * Gets component descriptor of the current element
	 * 
	 * @return component Descriptor
	 */
	public ComponentDescriptor getComponentDescriptor() {
		return componentDescriptor;
	}

	/**
	 * Checks if current element is an Entity
	 * 
	 * @return true if current element is an entity
	 */
	public boolean isCurrentElmtAnEntity() {
		return isCurrentElmtAnEntity;
	}

	/* Constructor */
	/**
	 * Creates an AtributeIterator with String chain and class String chain sample : principal.iterator
	 * 
	 * @param attributeChain
	 * @param clazz
	 */
	public AttributeIterator(String attributeChain, Class<? extends IComponent> clazz) {
		super();
		attributeArray = attributeChain.split("\\.");
		componentDescriptor = ComponentFactory.getInstance().getDescriptor(clazz);
	}

	private boolean isEntity(String objectName) {
		if (IEntity.class.isAssignableFrom(componentDescriptor.getPropertyClass(objectName))) {
			isCurrentElmtAnEntity = true;
		} else {
			isCurrentElmtAnEntity = false;
		}
		return isCurrentElmtAnEntity;
	}

	/**
	 * Checks if iterator has next element
	 */
	@Override
	public boolean hasNext() {
		if (position >= attributeArray.length || attributeArray[position] == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns next element of the String chain and set Compoent descriptor of the current element
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String next() {
		if (isEntity(attributeArray[position])) {
			componentDescriptor = ComponentFactory.getInstance().getDescriptor((Class<IComponent>) componentDescriptor.getPropertyClass(attributeArray[position]));
		}
		return attributeArray[position++];
	}

	/**
	 * Remove is not supported
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
