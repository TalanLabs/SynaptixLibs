package com.synaptix.deployer.client.view.swing;

import java.awt.Component;

import com.synaptix.client.view.IView;
import com.synaptix.swing.WaitComponentFeedbackPanel;

public class MailPanel extends WaitComponentFeedbackPanel implements IView {

	public MailPanel() {
		super();

		initComponents();

		this.addContent(buildContent());
	}

	private void initComponents() {

	}

	private Component buildContent() {
		return null;
	}
}
