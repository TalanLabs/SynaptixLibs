package com.synaptix.swing.utils;

import com.synaptix.client.view.Perspective;

public class DefaultPerspectivesBuilder {

	public DefaultPerspectivesBuilder() {
	}

	public static Perspective buildPerspective(String name, String nameFile) {
		return buildPerspective(name, nameFile, null);
	}

	public static Perspective buildPerspective(String name, String nameFile,
			String binding) {
		Perspective perspective = new Perspective();
		perspective.setName(name);
		perspective.setWorkspaceXml(Utils.getResourceAsString(nameFile));
		perspective.setBinding(binding);
		return perspective;
	}
}
