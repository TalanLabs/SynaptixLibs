package com.synaptix.swing.wizard;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JSyDialog;
import com.synaptix.swing.JTitle;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.wizard.event.WizardAdapter;
import com.synaptix.swing.wizard.event.WizardListener;

public abstract class AbstractWizardDialog<E> extends JPanel implements
		Wizard<E> {

	private static final long serialVersionUID = -5088559956184556719L;

	public static final int FINISH_OPTION = 0;

	public static final int CANCEL_OPTION = 1;

	protected JSyDialog dialog;

	protected JTitle titlePanel;

	protected int returnValue;

	protected transient WindowFocusListener dialogWindowFocusListener;

	protected transient WindowListener dialogWindowListener;

	protected String title;

	protected JWizard<E> wizard;
	
	private E bean;

	public AbstractWizardDialog(String title) {
		super(new BorderLayout());

		this.title = title;

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
	}

	private void createComponents() {
		titlePanel = new JTitle(title, "Coucou", 64); //$NON-NLS-1$
		dialogWindowFocusListener = new DialogWindowFocusListener();
		dialogWindowListener = new DialogWindowListener();

		wizard = new JWizard<E>();
	}

	private void initComponents() {
		createComponents();

		wizard.addWizardListener(new MyWizardListener());
	}

	protected JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:400DLU:GROW(1.0)", //$NON-NLS-1$
				"FILL:PREF:NONE,FILL:250DLU:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(titlePanel, cc.xy(1, 1));
		builder.add(buildAllPanel(), cc.xy(1, 2));
		return builder.getPanel();
	}

	protected JComponent buildTitlePanel() {
		return titlePanel;
	}

	/**
	 * Panel central de la dialogue, on doit ajouter le wizard
	 * 
	 * @return
	 */
	protected abstract JComponent buildAllPanel();

	public int showWizard(E bean) {
		returnValue = CANCEL_OPTION;
		
		this.bean = bean;

		dialog = new JSyDialog(GUIWindow.getActiveWindow(), title);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(dialogWindowListener);
		dialog.addWindowFocusListener(dialogWindowFocusListener);
		dialog.setResizable(true);

		Container container = dialog.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this, BorderLayout.CENTER);

		dialog.pack();
		dialog.setLocationRelativeTo(GUIWindow.getActiveWindow());

		titlePanel.startAnimation();

		dialog.setVisible(true);

		return returnValue;
	}

	private void closeDialog() {
		dialog.setVisible(false);
		dialog.dispose();

		titlePanel.stopAnimation();

		dialog.removeWindowFocusListener(dialogWindowFocusListener);
		dialog.removeWindowListener(dialogWindowListener);
	}

	public void addWizardListener(WizardListener<E> l) {
		wizard.addWizardListener(l);
	}

	public void removeWizardListener(WizardListener<E> l) {
		wizard.removeWizardListener(l);
	}

	public void addWizardPage(WizardPage<E> wizardPage) {
		wizard.addWizardPage(wizardPage);
	}

	public void removeWizardPage(WizardPage<E> wizardPage) {
		wizard.removeWizardPage(wizardPage);
	}

	public E getBean() {
		return wizard.getBean();
	}

	public WizardPage<E>[] getWizardPages() {
		return wizard.getWizardPages();
	}

	public void startWizard(E bean) {
		wizard.startWizard(bean);
	}

	public void helpWizardPage() {
		wizard.helpWizardPage();
	}

	public void nextWizardPage() {
		wizard.nextWizardPage();
	}

	public void previousWizardPage() {
		wizard.previousWizardPage();
	}

	public void finishWizardPage() {
		wizard.finishWizardPage();
	}

	public void cancelWizard() {
		wizard.cancelWizard();
	}

	public WizardPage<E> findWizardPageById(String id) {
		return wizard.findWizardPageById(id);
	}

	private final class MyWizardListener extends WizardAdapter<E> {

		public void selectedWizardPage(Wizard<E> wizard,
				WizardPage<E> wizardPage) {
			titlePanel.setTitle(title + " - " + wizardPage.getTitle()); //$NON-NLS-1$
			titlePanel.setSubTitle(wizardPage.getDescription());
		}

		public void stopedWizard(Wizard<E> wizard, boolean finished) {
			returnValue = finished ? FINISH_OPTION : CANCEL_OPTION;
			closeDialog();
		}
	}

	private final class DialogWindowListener extends WindowAdapter {

		public void windowOpened(WindowEvent e) {
			wizard.startWizard(bean);
			
			dialog.invalidate();
			dialog.validate();
		}

		public void windowClosing(WindowEvent e) {
			wizard.cancelWizard();
		}
	}

	private final class DialogWindowFocusListener implements
			WindowFocusListener {

		public void windowGainedFocus(WindowEvent e) {
			if (!titlePanel.isAnimation()) {
				titlePanel.startAnimation();
			}
		}

		public void windowLostFocus(WindowEvent e) {
			if (titlePanel.isAnimation()) {
				titlePanel.stopAnimation();
			}
		}
	}
}
