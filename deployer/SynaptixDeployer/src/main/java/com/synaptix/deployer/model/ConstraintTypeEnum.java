package com.synaptix.deployer.model;

public enum ConstraintTypeEnum {

	P("Primary Key"), R("Foreign Key"), C("Conditional"), U("Unique");

	private String meaning;

	private ConstraintTypeEnum(String meaning) {
		this.meaning = meaning;
	}

	public String getMeaning() {
		return meaning;
	}
}
