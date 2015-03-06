package com.synaptix.swing.wizard;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class JDefaultWizardDialog<E> extends AbstractWizardDialog<E> {

	private static final long serialVersionUID = 6099989217776051353L;

	public JDefaultWizardDialog(String title) {
		super(title);
	}

	protected JComponent buildAllPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		Component preview = buildPreviewContents();
		if (preview != null) {
			panel.add(preview, BorderLayout.WEST);
		}
		panel.add(wizard, BorderLayout.CENTER);
		Component navigator = buildNavigatorContents();
		if (navigator != null) {
			panel.add(navigator, BorderLayout.SOUTH);
		}
		return panel;
	}

	protected Component buildPreviewContents() {
		return new DefaultPreviewWizard<E>(wizard);
	}

	protected Component buildNavigatorContents() {
		return new DefaultNavigatorWizard<E>(wizard);
	}
}
