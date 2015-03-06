package com.synaptix.widget.view.swing;

import java.text.ParseException;

import javax.swing.JFormattedTextField;

public class JSyFormattedTextField<E> extends JFormattedTextField {

	private static final long serialVersionUID = 174227602386476873L;

	@SuppressWarnings("unchecked")
	public E getCommitValue() {
		try {
			this.commitEdit();
			return (E) getValue();
		} catch (ParseException e) {
			return null;
		}
	}
}
