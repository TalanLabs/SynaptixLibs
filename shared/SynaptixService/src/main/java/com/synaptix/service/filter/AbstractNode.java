package com.synaptix.service.filter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class AbstractNode implements Serializable {

	private static final long serialVersionUID = -7061683234485343509L;

	private AbstractNode parentNode;

	private List<AbstractNode> childNodes;

	public AbstractNode() {
		super();

		this.childNodes = null;
	}

	public AbstractNode(AbstractNode... childNodes) {
		super();

		assert childNodes != null && childNodes.length > 0;

		for (AbstractNode childNode : childNodes) {
			assert childNode != null;
			childNode.setParentNode(this);
		}
		this.childNodes = Collections.unmodifiableList(Arrays.asList(childNodes));
	}

	private void setParentNode(AbstractNode parentNode) {
		this.parentNode = parentNode;
	}

	public final AbstractNode getParentNode() {
		return parentNode;
	}

	public final boolean isLeaf() {
		return childNodes == null;
	}

	public final List<AbstractNode> getChildNodes() {
		return childNodes;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
