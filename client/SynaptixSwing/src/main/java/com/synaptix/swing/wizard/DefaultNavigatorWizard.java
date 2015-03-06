package com.synaptix.swing.wizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.synaptix.swing.widget.AbstractCancelAction;
import com.synaptix.swing.widget.AbstractFinishAction;
import com.synaptix.swing.widget.AbstractHelpAction;
import com.synaptix.swing.widget.AbstractNextAction;
import com.synaptix.swing.widget.AbstractPreviousAction;
import com.synaptix.swing.wizard.action.WizardAction;
import com.synaptix.swing.wizard.event.WizardAdapter;

public class DefaultNavigatorWizard<E> extends JPanel {

	private static final long serialVersionUID = 2732025319850688115L;

	private JButton nextButton;

	private JButton previousButton;

	private JButton finishButton;

	private Action cancelAction;

	private JButton helpButton;

	private Wizard<E> wizard;

	private WizardPage<E> selectedWizardPage;

	private PropertyChangeListener previousPropertyChangeListener;

	private PropertyChangeListener nextPropertyChangeListener;

	private PropertyChangeListener finishPropertyChangeListener;

	private PropertyChangeListener helpPropertyChangeListener;

	public DefaultNavigatorWizard(Wizard<E> wizard) {
		super(new BorderLayout());

		this.wizard = wizard;

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		cancelAction = new CancelAction();
	}

	private void createComponents() {
		finishButton = new JButton(new FinishAction());
		nextButton = new JButton(new NextAction());
		previousButton = new JButton(new PreviousAction());
		helpButton = new JButton(new HelpAction());
	}

	private void initComponents() {
		createComponents();

		previousPropertyChangeListener = new MyPropertyChangeListener(
				previousButton);
		nextPropertyChangeListener = new MyPropertyChangeListener(nextButton);
		finishPropertyChangeListener = new MyPropertyChangeListener(
				finishButton);
		helpPropertyChangeListener = new MyPropertyChangeListener(helpButton);

		wizard.addWizardListener(new MyWizardListener());
	}

	private JComponent buildContents() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 15, 5));
		JComponent separator = DefaultComponentFactory.getInstance()
				.createSeparator(""); //$NON-NLS-1$
		panel.add(separator, BorderLayout.NORTH);

		JPanel rightPanel = ButtonBarFactory.buildRightAlignedBar(
				previousButton, nextButton, finishButton, new JButton(
						cancelAction));
		rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 10));

		JPanel leftPanel = ButtonBarFactory.buildRightAlignedBar(helpButton);
		leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 10));

		panel.add(leftPanel, BorderLayout.WEST);
		panel.add(rightPanel, BorderLayout.EAST);
		return panel;
	}

	private void desynchWizardAction(WizardAction<E> wizardAction,
			PropertyChangeListener propertyChangeListener) {
		if (wizardAction != null) {
			wizardAction.removePropertyChangeListener(propertyChangeListener);
		}
	}

	private void synchWizardAction(WizardAction<E> wizardAction,
			JButton button, PropertyChangeListener propertyChangeListener) {
		if (wizardAction != null) {
			button.setEnabled(wizardAction.isEnabled());
			wizardAction.addPropertyChangeListener(propertyChangeListener);
		} else {
			button.setEnabled(false);
		}
	}

	private void installWizardPageListener(WizardPage<E> wizardPage) {
		synchWizardAction(wizardPage.getPreviousAction(), previousButton,
				previousPropertyChangeListener);
		synchWizardAction(wizardPage.getNextAction(), nextButton,
				nextPropertyChangeListener);
		synchWizardAction(wizardPage.getFinishAction(), finishButton,
				finishPropertyChangeListener);
		synchWizardAction(wizardPage.getHelpAction(), helpButton,
				helpPropertyChangeListener);
	}

	private void uninstallWizardPageListener(WizardPage<E> wizardPage) {
		desynchWizardAction(wizardPage.getPreviousAction(),
				previousPropertyChangeListener);
		desynchWizardAction(wizardPage.getNextAction(),
				nextPropertyChangeListener);
		desynchWizardAction(wizardPage.getFinishAction(),
				finishPropertyChangeListener);
		desynchWizardAction(wizardPage.getHelpAction(),
				helpPropertyChangeListener);
	}

	private final class MyWizardListener extends WizardAdapter<E> {

		public void selectedWizardPage(Wizard<E> wizard,
				WizardPage<E> wizardPage) {
			WizardPage<E> oldWizardPage = selectedWizardPage;

			if (oldWizardPage != null) {
				uninstallWizardPageListener(oldWizardPage);
			}

			selectedWizardPage = wizardPage;
			installWizardPageListener(wizardPage);

			finishButton.setVisible(wizardPage.getFinishAction() != null);
			helpButton.setVisible(wizardPage.getHelpAction() != null);
		}

		public void stopedWizard(Wizard<E> wizard, boolean finished) {
			uninstallWizardPageListener(selectedWizardPage);
		}
	}

	private final class MyPropertyChangeListener implements
			PropertyChangeListener {

		private JButton button;

		public MyPropertyChangeListener(JButton button) {
			super();

			this.button = button;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if ("enabled".equals(evt.getPropertyName())) {
				button.setEnabled((Boolean) evt.getNewValue());
			}
		}
	}

	private final class PreviousAction extends AbstractPreviousAction {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			wizard.previousWizardPage();
		}
	}

	private final class NextAction extends AbstractNextAction {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			wizard.nextWizardPage();
		}
	}

	private final class FinishAction extends AbstractFinishAction {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			wizard.finishWizardPage();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			wizard.cancelWizard();
		}
	}

	private final class HelpAction extends AbstractHelpAction {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			wizard.helpWizardPage();
		}
	}
}
