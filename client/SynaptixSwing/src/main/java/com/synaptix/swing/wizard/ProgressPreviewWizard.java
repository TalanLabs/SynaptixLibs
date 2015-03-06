package com.synaptix.swing.wizard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.synaptix.swing.wizard.event.WizardAdapter;

public class ProgressPreviewWizard<E> extends JPanel {

	private static final long serialVersionUID = 1285853622897877165L;

	private JProgressBar progressBar;

	private List<WizardPage<E>> pageList;

	private int max;

	private int orientation;

	private boolean viewOnlyMax;

	private Wizard<E> wizard;

	public ProgressPreviewWizard(Wizard<E> wizard) {
		this(wizard, false, SwingConstants.HORIZONTAL);
	}

	public ProgressPreviewWizard(Wizard<E> wizard, boolean viewOnlyMax,
			int orientation) {
		super(new BorderLayout());

		this.wizard = wizard;
		this.viewOnlyMax = viewOnlyMax;
		this.orientation = orientation;
		max = 0;
		pageList = new ArrayList<WizardPage<E>>();

		initActions();
		initComponents();

		this.setBorder(BorderFactory.createEtchedBorder());

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
	}

	private void createComponents() {
		progressBar = new JProgressBar();
	}

	private void initComponents() {
		createComponents();

		progressBar.setOrientation(orientation);
		wizard.addWizardListener(new MyWizardListener());
	}

	private JComponent buildContents() {
		return progressBar;
	}

	private final class MyWizardListener extends WizardAdapter<E> {

		public void selectedWizardPage(Wizard<E> wizard,
				WizardPage<E> wizardPage) {
			if (pageList.contains(wizardPage)) {
				int index = pageList.indexOf(wizardPage);
				for (int i = pageList.size() - 1; i > index; i--) {
					pageList.remove(i);
				}
			} else {
				pageList.add(wizardPage);
			}

			max = Math.max(max, pageList.size());

			progressBar.setMinimum(1);
			progressBar.setMaximum(wizard.getWizardPages().length);
			progressBar.setValue(viewOnlyMax ? max : pageList.size());
		}
	}
}
