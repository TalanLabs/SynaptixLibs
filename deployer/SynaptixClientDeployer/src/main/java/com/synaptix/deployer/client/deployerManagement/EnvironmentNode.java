package com.synaptix.deployer.client.deployerManagement;

import org.apache.commons.lang.StringUtils;

public class EnvironmentNode extends ManagementNode {

	private String name;

	public EnvironmentNode(String name) {
		super(NodeType.ENVIRONMENT, name, null);
	}

	@Override
	public String getName() {
		if (StringUtils.isNotBlank(name)) {
			return name;
		}
		return super.getName();
	}

	public void setName(String name) {
		this.name = name;
	}
}
