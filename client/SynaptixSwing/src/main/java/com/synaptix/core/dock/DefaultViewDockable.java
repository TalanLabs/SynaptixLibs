package com.synaptix.core.dock;

import javax.swing.JComponent;

public class DefaultViewDockable extends AbstractViewDockable {

	protected String categorie;

	protected String id;

	protected String name;

	protected JComponent component;

	public DefaultViewDockable(String id, String categorie, String name,
			JComponent component) {
		super();
		this.categorie = categorie;
		this.id = id;
		this.name = name;
		this.component = component;
	}

	public String getCategorie() {
		return categorie;
	}

	public JComponent getView() {
		return component;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
