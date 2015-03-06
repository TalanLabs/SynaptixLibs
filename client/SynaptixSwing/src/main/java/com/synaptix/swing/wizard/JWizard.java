package com.synaptix.swing.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.synaptix.swing.wizard.action.WizardAction;
import com.synaptix.swing.wizard.event.WizardListener;

public class JWizard<E> extends JPanel implements Wizard<E> {

	private static final long serialVersionUID = -4507464989487514899L;

	protected CardLayout cardLayout;

	protected JPanel cardPanel;

	protected WizardPage<E> currentWizardPage;

	protected Map<String, WizardPage<E>> map;

	protected List<WizardPage<E>> list;

	protected E bean;

	public JWizard() {
		super(new BorderLayout());

		map = new HashMap<String, WizardPage<E>>();
		list = new ArrayList<WizardPage<E>>();

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
	}

	private void createComponents() {
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
	}

	private void initComponents() {
		createComponents();
	}

	private JComponent buildContents() {
		return cardPanel;
	}

	public void addWizardListener(WizardListener<E> l) {
		listenerList.add(WizardListener.class, l);
	}

	public void removeWizardListener(WizardListener<E> l) {
		listenerList.remove(WizardListener.class, l);
	}

	@SuppressWarnings("unchecked")
	protected void fireAddedWizardPage(WizardPage<E> wizardPage) {
		WizardListener<E>[] ls = (WizardListener<E>[]) listenerList
				.getListeners(WizardListener.class);
		for (WizardListener<E> l : ls) {
			l.addedWizardPage(this, wizardPage);
		}
	}

	@SuppressWarnings("unchecked")
	protected void fireRemovedWizardPage(WizardPage<E> wizardPage) {
		WizardListener<E>[] ls = (WizardListener<E>[]) listenerList
				.getListeners(WizardListener.class);
		for (WizardListener<E> l : ls) {
			l.removedWizardPage(this, wizardPage);
		}
	}

	@SuppressWarnings("unchecked")
	protected void fireSelectedWizardPage(WizardPage<E> wizardPage) {
		WizardListener<E>[] ls = (WizardListener<E>[]) listenerList
				.getListeners(WizardListener.class);
		for (WizardListener<E> l : ls) {
			l.selectedWizardPage(this, wizardPage);
		}
	}

	@SuppressWarnings("unchecked")
	protected void fireStartedWizardPage() {
		WizardListener<E>[] ls = (WizardListener<E>[]) listenerList
				.getListeners(WizardListener.class);
		for (WizardListener<E> l : ls) {
			l.startedWizard(this);
		}
	}

	@SuppressWarnings("unchecked")
	protected void fireStopedWizardPage(boolean finish) {
		WizardListener<E>[] ls = (WizardListener<E>[]) listenerList
				.getListeners(WizardListener.class);
		for (WizardListener<E> l : ls) {
			l.stopedWizard(this, finish);
		}
	}

	public void addWizardPage(WizardPage<E> wizardPage) {
		cardPanel.add(wizardPage.getView(), wizardPage.getId());

		map.put(wizardPage.getId(), wizardPage);
		list.add(wizardPage);

		if (currentWizardPage == null) {
			currentWizardPage = wizardPage;
		}

		fireAddedWizardPage(wizardPage);
	}

	public void removeWizardPage(WizardPage<E> wizardPage) {
		cardPanel.remove(wizardPage.getView());

		map.remove(wizardPage.getId());
		if (currentWizardPage.equals(wizardPage)) {
			int index = list.indexOf(wizardPage);
			if (list.size() > 1) {
				currentWizardPage = list.get(index > 0 ? index - 1 : index + 1);
			} else {
				currentWizardPage = null;
			}
		}
		list.remove(wizardPage);

		fireRemovedWizardPage(wizardPage);
	}

	@SuppressWarnings("unchecked")
	public WizardPage<E>[] getWizardPages() {
		return (WizardPage<E>[]) map.values().toArray(
				new WizardPage[map.size()]);
	}

	public void nextWizardPage() {
		WizardAction<E> a = currentWizardPage.getNextAction();
		if (a != null && a.isEnabled()) {
			nextWizardPage(a);
		}
	}

	public void previousWizardPage() {
		WizardAction<E> a = currentWizardPage.getPreviousAction();
		if (a != null && a.isEnabled()) {
			previousWizardPage(a);
		}
	}

	public void helpWizardPage() {
		WizardAction<E> a = currentWizardPage.getHelpAction();
		if (a != null && a.isEnabled()) {
			a.actionPerformed(this, currentWizardPage, WizardAction.Type.Help,
					bean);
		}
	}

	public void finishWizardPage() {
		WizardAction<E> a = currentWizardPage.getFinishAction();
		if (a != null && a.isEnabled()) {
			currentWizardPage.commit(bean);
			
			a.actionPerformed(JWizard.this, currentWizardPage,
					WizardAction.Type.Finish, bean);

			fireStopedWizardPage(true);
		}
	}

	public void cancelWizard() {
		fireStopedWizardPage(false);
	}

	/**
	 * On commit les données avant de revenir à la page d'avant
	 * 
	 * @param a
	 */
	private void previousWizardPage(WizardAction<E> a) {
		WizardPage<E> oldWizardPage = this.currentWizardPage;

		oldWizardPage.commit(bean);

		WizardPage<E> newWizardPage = a.actionPerformed(JWizard.this,
				currentWizardPage, WizardAction.Type.Previous, bean);
		if (newWizardPage != null) {
			showWizardPage(newWizardPage);
		}
	}

	/**
	 * On commit les données avant d'aller à la page suivante
	 * 
	 * @param a
	 */
	private void nextWizardPage(WizardAction<E> a) {
		WizardPage<E> oldWizardPage = this.currentWizardPage;

		oldWizardPage.commit(bean);
		WizardPage<E> newWizardPage = a.actionPerformed(JWizard.this,
				currentWizardPage, WizardAction.Type.Previous, bean);
		if (newWizardPage != null) {
			showWizardPage(newWizardPage);
		}
	}

	private void showWizardPage(WizardPage<E> wizardPage) {
		cardLayout.show(cardPanel, wizardPage.getId());

		this.currentWizardPage = wizardPage;
		wizardPage.load(bean);

		fireSelectedWizardPage(wizardPage);
	}

	public WizardPage<E> findWizardPageById(String id) {
		return map.get(id);
	}

	public void startWizard(E bean) {
		this.bean = bean;

		fireStartedWizardPage();

		showWizardPage(currentWizardPage);
	}

	public E getBean() {
		return bean;
	}
}
