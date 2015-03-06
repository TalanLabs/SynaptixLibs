package com.synaptix.deployer.client.compare;

public class CompareNode {

	private final String name;
	private final int level;
	private final CompareNodeType compareNodeType;

	public CompareNode(int level, String name, CompareNodeType compareNodeType) {
		super();

		this.level = level;
		this.name = name;
		this.compareNodeType = compareNodeType;
	}

	public int getLevel() {
		return level;
	}

	public String getProperty() {
		return name;
	}

	public CompareNodeType getCompareNodeType() {
		return compareNodeType;
	}

	public enum CompareNodeType {

		TABLE, COLUMN, CONSTRAINT;

	}
}
