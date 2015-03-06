package com.synaptix.entity.extension;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.component.extension.IPropertyExtensionDescriptor;

public class BusinessPropertyExtensionDescriptor implements IPropertyExtensionDescriptor {

	private final int order;

	public BusinessPropertyExtensionDescriptor(int order) {
		super();
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
