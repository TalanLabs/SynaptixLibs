package com.synaptix.widget.renderer.view.swing;

import java.awt.Component;

import javax.swing.JLabel;

public interface IValueComponent<E> {

	public void setValue(JLabel label, E value);

	public Component getComponent();

}
