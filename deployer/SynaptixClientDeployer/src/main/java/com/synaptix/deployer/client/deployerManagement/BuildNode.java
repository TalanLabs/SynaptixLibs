package com.synaptix.deployer.client.deployerManagement;

public class BuildNode extends ManagementNode {

	public BuildNode(String name, ManagementNode parentNode) {
		super(NodeType.BUILD, name, parentNode);
	}
}
