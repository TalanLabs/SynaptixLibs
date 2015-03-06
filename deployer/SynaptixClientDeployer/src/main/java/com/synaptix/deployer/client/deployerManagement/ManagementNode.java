package com.synaptix.deployer.client.deployerManagement;

public abstract class ManagementNode {

	public enum NodeType {

		ENVIRONMENT, INSTRUCTIONS, BUILD, SCRIPTS, CONFIRMATION

	}

	private final NodeType type;
	private final String name;
	private final ManagementNode parentNode;
	private final String key;

	public ManagementNode(NodeType type, String name, ManagementNode parentNode) {
		super();

		this.type = type;
		this.name = name;
		this.parentNode = parentNode;
		this.key = parentNode != null ? parentNode.getName() + " - " + getName() : getName();
	}

	public NodeType getType() {
		return type;
	}

	public final int getLevel() {
		return parentNode == null ? 0 : parentNode.getLevel() + 1;
	}

	public final String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public final ManagementNode getParentNode() {
		return parentNode;
	}
}
