package com.synaptix.client.view;

public class Perspective implements Cloneable {

	private String name;

	private String workspaceXml;

	private String workspaceStatsXml;

	private String binding;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWorkspaceXml() {
		return workspaceXml;
	}

	public void setWorkspaceXml(String workspaceXml) {
		this.workspaceXml = workspaceXml;
	}

	public String getWorkspaceStatsXml() {
		return workspaceStatsXml;
	}

	public void setWorkspaceStatsXml(String workspaceStatsXml) {
		this.workspaceStatsXml = workspaceStatsXml;
	}

	public String getBinding() {
		return binding;
	}

	public void setBinding(String binding) {
		this.binding = binding;
	}

	public Perspective clone() {
		Perspective clone = new Perspective();
		clone.setName(getName());
		clone.setWorkspaceXml(getWorkspaceXml());
		clone.setBinding(getBinding());
		clone.setWorkspaceStatsXml(getWorkspaceStatsXml());
		return clone;
	}
}
