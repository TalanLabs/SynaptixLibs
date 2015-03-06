package com.synaptix.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.synaptix.swing.event.TabbedEvent;
import com.synaptix.swing.event.TabbedListener;
import com.synaptix.swing.plaf.SyTabbedPaneUI;

public class JSyTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 6763953253854014435L;

	private static final String uiClassID = "SyTabbedPaneUI"; //$NON-NLS-1$
	
	private static final String TEXT_RESTORE = "Restore"; //$NON-NLS-1$

	private static final String TEXT_MAXIMIZE = "Maximize"; //$NON-NLS-1$

	private static final String TEXT_CLOSE = "Close"; //$NON-NLS-1$

	private static final String TEXT_CLOSE_OTHERS = "Close others"; //$NON-NLS-1$

	private static final String TEXT_CLOSE_ALL = "Close all"; //$NON-NLS-1$

	protected boolean showMaximize = true;

	protected boolean maximized = false;

	protected boolean showAllClose = true;

	protected boolean showClose = true;

	protected boolean showUnselectedClose = true;

	protected boolean showUnselectedImage = true;

	protected boolean showMenu = true;

	protected EventListenerList listenerList;

	protected JPopupMenu popupMenu;

	protected JMenuItem restoreMenuItem;

	protected JMenuItem maximizeMenuItem;

	protected JMenuItem closeMenuItem;

	protected JMenuItem closeOthersMenuItem;

	protected JMenuItem closeAllMenuItem;

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicSyTabbedPaneUI"); //$NON-NLS-1$
	}
	
	public JSyTabbedPane() {
		super();
		listenerList = new EventListenerList();
		initializePopup();
		this.setComponentPopupMenu(popupMenu);
		
		updateUI();
	}

	public SyTabbedPaneUI getUI() {
		return (SyTabbedPaneUI) ui;
	}

	public void setUI(SyTabbedPaneUI ui) {
		super.setUI(ui);
	}

	public void updateUI() {
		setUI((SyTabbedPaneUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}
	
	private void initializePopup() {
		popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuPopupMenuListener());

		PopupMenuActionListener pmal = new PopupMenuActionListener();

		restoreMenuItem = new JMenuItem(TEXT_RESTORE);
		restoreMenuItem.addActionListener(pmal);
		popupMenu.add(restoreMenuItem);

		maximizeMenuItem = new JMenuItem(TEXT_MAXIMIZE);
		maximizeMenuItem.addActionListener(pmal);
		popupMenu.add(maximizeMenuItem);

		popupMenu.addSeparator();

		closeMenuItem = new JMenuItem(TEXT_CLOSE);
		closeMenuItem.addActionListener(pmal);
		popupMenu.add(closeMenuItem);

		closeOthersMenuItem = new JMenuItem(TEXT_CLOSE_OTHERS);
		closeOthersMenuItem.addActionListener(pmal);
		popupMenu.add(closeOthersMenuItem);

		closeAllMenuItem = new JMenuItem(TEXT_CLOSE_ALL);
		closeAllMenuItem.addActionListener(pmal);
		popupMenu.add(closeAllMenuItem);
	}

	public void addTabbedListener(TabbedListener tabbedListener) {
		listenerList.add(TabbedListener.class, tabbedListener);
	}

	public void removeTabbedListener(TabbedListener tabbedListener) {
		listenerList.remove(TabbedListener.class, tabbedListener);
	}

	protected void fireMaximizedTabbedListener() {
		TabbedListener[] list = (TabbedListener[]) listenerList
				.getListeners(TabbedListener.class);
		TabbedEvent te = new TabbedEvent(this);
		for (TabbedListener tl : list)
			tl.maximized(te);
	}

	protected void fireRestoreTabbedListener() {
		TabbedListener[] list = (TabbedListener[]) listenerList
				.getListeners(TabbedListener.class);
		TabbedEvent te = new TabbedEvent(this);
		for (TabbedListener tl : list)
			tl.restore(te);
	}

	protected void fireAllCloseTabbedListener() {
		TabbedListener[] list = (TabbedListener[]) listenerList
				.getListeners(TabbedListener.class);
		TabbedEvent te = new TabbedEvent(this);
		for (TabbedListener tl : list)
			tl.allClose(te);
	}

	protected void fireCloseTabbedListener(int index) {
		TabbedListener[] list = (TabbedListener[]) listenerList
				.getListeners(TabbedListener.class);
		TabbedEvent te = new TabbedEvent(this, index);
		for (TabbedListener tl : list)
			tl.close(te);
	}

	public boolean isShowClose() {
		return showClose;
	}

	public void setShowClose(boolean showClose) {
		this.showClose = showClose;
		repaint();
	}

	public boolean isShowUnselectedClose() {
		return showUnselectedClose;
	}

	public void setShowUnselectedClose(boolean showUnselectedClose) {
		this.showUnselectedClose = showUnselectedClose;
		repaint();
	}

	public boolean isShowUnselectedImage() {
		return showUnselectedImage;
	}

	public void setShowUnselectedImage(boolean showUnselectedImage) {
		this.showUnselectedImage = showUnselectedImage;
		repaint();
	}

	public boolean isShowMaximize() {
		return showMaximize;
	}

	public void setShowMaximize(boolean showMaximize) {
		this.showMaximize = showMaximize;
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		if (maximized)
			fireMaximizedTabbedListener();
		else
			fireRestoreTabbedListener();
		this.maximized = maximized;
	}

	public boolean isShowAllClose() {
		return showAllClose;
	}

	public void setShowAllClose(boolean showAllClose) {
		this.showAllClose = showAllClose;

	}

	public boolean isShowMenu() {
		return showMenu;
	}

	public void setShowMenu(boolean showMenu) {
		this.showMenu = showMenu;
		if (showMenu)
			this.setComponentPopupMenu(popupMenu);
		else
			this.setComponentPopupMenu(null);
	}

	public void closeAll() {
		fireAllCloseTabbedListener();
		//this.removeAll();
	}

	public void closeAt(int index) {
		fireCloseTabbedListener(index);
		//this.removeTabAt(index);
	}

	private class PopupMenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == restoreMenuItem) {
				setMaximized(!maximized);
			} else if (e.getSource() == maximizeMenuItem) {
				setMaximized(!maximized);
			} else if (e.getSource() == closeMenuItem) {
				closeAt(getSelectedIndex());
			} else if (e.getSource() == closeOthersMenuItem) {

			} else if (e.getSource() == closeAllMenuItem) {
				closeAll();
			}
		}
	}

	private class PopupMenuPopupMenuListener implements PopupMenuListener {
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			if (showMaximize) {
				if (maximized) {
					restoreMenuItem.setEnabled(true);
					maximizeMenuItem.setEnabled(false);
				} else {
					restoreMenuItem.setEnabled(false);
					maximizeMenuItem.setEnabled(true);
				}
			} else {
				restoreMenuItem.setEnabled(false);
				maximizeMenuItem.setEnabled(false);
			}
			if (showClose && getSelectedIndex() != -1) {
				closeMenuItem.setEnabled(true);
				if (getTabCount() > 1)
					closeOthersMenuItem.setEnabled(true);
				else
					closeOthersMenuItem.setEnabled(false);
			} else {
				closeMenuItem.setEnabled(false);
				closeOthersMenuItem.setEnabled(false);
			}
			if (showAllClose)
				closeAllMenuItem.setEnabled(true);
			else
				closeAllMenuItem.setEnabled(false);
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}


}
