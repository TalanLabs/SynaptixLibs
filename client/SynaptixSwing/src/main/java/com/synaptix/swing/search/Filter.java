package com.synaptix.swing.search;

import java.io.Serializable;

import javax.swing.JComponent;

public interface Filter {

	public enum TypeVisible {
		Visible, Unvisible, Invisible
	};

	public String getId();

	public String getName();

	public JComponent getComponent();

	public JComponent getDefaultComponent();

	public Serializable getValue();

	public void setValue(Object o);

	public Serializable getDefaultValue();

	public void setDefaultValue(Object o);

	public void copyDefaultValue();

	public TypeVisible getTypeVisible();

	public boolean isDefaultVisible();

}
