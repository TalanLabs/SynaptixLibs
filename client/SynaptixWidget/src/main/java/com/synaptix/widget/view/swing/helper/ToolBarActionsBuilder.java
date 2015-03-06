package com.synaptix.widget.view.swing.helper;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;

import com.synaptix.swing.utils.ToolBarFactory;

public class ToolBarActionsBuilder implements IToolBarActionsBuilder {

	private List<JComponent> components;

	private boolean added;

	public ToolBarActionsBuilder() {
		super();
		components = new ArrayList<JComponent>();
	}

	@Override
	public IToolBarActionsBuilder addSeparator() {
		if (added) {
			components.add(null);
		}
		return this;
	}

	public IToolBarActionsBuilder addComponent(JComponent component) {
		components.add(component);
		added = true;
		return this;
	}

	@Override
	public IToolBarActionsBuilder addAction(Action action) {
		components.add(new JButton(action));
		added = true;
		return this;
	}

	public JComponent build() {
		if (!added) {
			return null;
		}
		return ToolBarFactory.buildToolBar(components.toArray(new JComponent[components.size()]));
	}
}
