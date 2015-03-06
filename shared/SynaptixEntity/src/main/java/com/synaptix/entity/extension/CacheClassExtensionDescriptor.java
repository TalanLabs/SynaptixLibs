package com.synaptix.entity.extension;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.component.IComponent;
import com.synaptix.component.extension.IClassExtensionDescriptor;

public class CacheClassExtensionDescriptor implements IClassExtensionDescriptor {

	private final boolean readOnly;

	private final int size;

	private final long clearInterval;

	private final Class<? extends IComponent>[] links;

	public CacheClassExtensionDescriptor(boolean readOnly, int size, long clearInterval, Class<? extends IComponent>[] links) {
		super();
		this.readOnly = readOnly;
		this.size = size;
		this.clearInterval = clearInterval;
		this.links = links;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public int getSize() {
		return size;
	}

	public long getClearInterval() {
		return clearInterval;
	}

	public Class<? extends IComponent>[] getLinks() {
		return links;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
