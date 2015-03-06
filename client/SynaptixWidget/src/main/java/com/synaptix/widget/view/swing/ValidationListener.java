package com.synaptix.widget.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ValidationListener implements DocumentListener, ActionListener, ChangeListener, PropertyChangeListener, ListSelectionListener {

	private IValidationView validationView;

	private boolean enabled;

	public ValidationListener(IValidationView validationView) {
		super();

		this.validationView = validationView;
		this.enabled = true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (enabled) {
			validationView.updateValidation();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (enabled) {
			validationView.updateValidation();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (enabled) {
			validationView.updateValidation();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (enabled) {
			validationView.updateValidation();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (enabled) {
			validationView.updateValidation();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (enabled) {
			validationView.updateValidation();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (enabled) {
			// On teste si l'ancienne valeur et la nouvelle différente
			if ((evt.getOldValue() == null && evt.getNewValue() != null) || (evt.getOldValue() != null && evt.getNewValue() == null)
					|| (evt.getOldValue() != null && evt.getNewValue() != null && !evt.getOldValue().equals(evt.getNewValue()))) {
				validationView.updateValidation();
			}
		}
	}
}