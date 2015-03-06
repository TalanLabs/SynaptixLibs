package com.synaptix.constants;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ConstantInformation {

	private String bundleName;

	private String key;

	private String description;

	private String meaning;

	public ConstantInformation(String bundleName, String key, String description, String meaning) {
		super();
		this.bundleName = bundleName;
		this.key = key;
		this.description = description;
		this.meaning = meaning;
	}

	public String getBundleName() {
		return bundleName;
	}

	public String getKey() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	public String getMeaning() {
		return meaning;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
