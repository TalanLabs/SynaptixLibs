package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.utils.GUIWindow;

public class JDialogModel {

	private static boolean activeSave;

	private static boolean multiScreen;

	private static boolean showTitle;

	private JSyDialog dialog;

	private String title;

	private String subTitle;

	private Action[] actions;

	private JTitle titlePanel;

	private boolean resizable;

	private boolean forbidenClose;

	private boolean centralizable;

	private ActionListener actionListenerOpen;

	private ActionListener actionListenerClose;

	private Component glassPane;

	private WindowFocusListener dialogWindowFocusListener;

	private Action dialogCloseActionListener;

	private Window parent;

	private JComponent contents;

	private String id;

	private Map<Action, JButton> buttonMap;

	static {
		activeSave = false;
		showTitle = true;
	}

	public JDialogModel(Component parent, String title, JComponent contents, Action[] actions) {
		this(parent, title, null, contents, actions, null);
	}

	public JDialogModel(Component parent, String title, String subTitle, JComponent contents, Action[] actions) {
		this(parent, title, subTitle, contents, actions, null);
	}

	public JDialogModel(Component parent, String title, JComponent contents, Action[] actions, ActionListener actionListenerClose) {
		this(parent, title, null, contents, actions, actionListenerClose);
	}

	public JDialogModel(Component parent, String title, JComponent contents, Action[] actions, ActionListener actionListenerOpen, ActionListener actionListenerClose) {
		this(parent, title, null, contents, actions, actionListenerOpen, actionListenerClose);
	}

	public JDialogModel(Component parent, String title, boolean showTitle, JComponent contents, Action[] actions, ActionListener actionListenerClose) {
		this(parent, title, null, showTitle, contents, actions, actionListenerClose);
	}

	public JDialogModel(Component parent, String title, boolean showTitle, JComponent contents, Action[] actions, ActionListener actionListenerOpen, ActionListener actionListenerClose) {
		this(parent, title, null, showTitle, contents, actions, actionListenerOpen, actionListenerClose);
	}

	public JDialogModel(Component parent, String title, String subTitle, JComponent contents, Action[] actions, ActionListener actionListenerClose) {
		this(parent, title, subTitle, showTitle, contents, actions, actionListenerClose);
	}

	public JDialogModel(Component parent, String title, String subTitle, JComponent contents, Action[] actions, ActionListener actionListenerOpen, ActionListener actionListenerClose) {
		this(parent, title, subTitle, showTitle, contents, actions, actionListenerOpen, actionListenerClose);
	}

	public JDialogModel(Component parent, String title, String subTitle, boolean showTitle, JComponent contents, Action[] actions, ActionListener actionListenerClose) {
		this(parent, title, subTitle, showTitle, contents, actions, null, actionListenerClose);
	}

	public JDialogModel(Component parent, String title, String subTitle, boolean showTitle, JComponent contents, Action[] actions, ActionListener actionListenerOpen, ActionListener actionListenerClose) {
		this.title = title;
		this.subTitle = subTitle;
		this.actions = actions;

		this.contents = contents;

		this.actionListenerOpen = actionListenerOpen;
		this.actionListenerClose = actionListenerClose;

		this.glassPane = null;

		this.resizable = true;
		this.forbidenClose = false;
		this.centralizable = true;

		buttonMap = new HashMap<Action, JButton>();

		if (showTitle) {
			if (subTitle != null) {
				titlePanel = new JTitle(title, subTitle, 64);
			} else {
				titlePanel = new JTitle(title);
			}
		}

		if (parent == null) {
			this.parent = GUIWindow.getActiveWindow();
		} else {
			this.parent = getWindow(parent);
		}

		dialog = new JSyDialog(this.parent, title);
		dialog.setModal(true);

		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		dialog.addWindowListener(new DialogWindowListener());

		if (getDefaultImage() != null) {
			dialog.setIconImage(getDefaultImage());
		}

		dialogWindowFocusListener = new DialogWindowFocusListener();

		Container container = dialog.getContentPane();
		container.setLayout(new BorderLayout());
		if (showTitle) {
			container.add(titlePanel, BorderLayout.NORTH);
		}
		container.add(contents, BorderLayout.CENTER);
		if (actions != null && actions.length > 0) {
			container.add(buildButtons(), BorderLayout.SOUTH);
		}

		dialogCloseActionListener = new DialogCloseActionListener();
		dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		dialog.getRootPane().getActionMap().put("close", dialogCloseActionListener);

		dialog.pack();
		dialog.setLocationRelativeTo(parent);
	}

	private Window getWindow(Component component) {
		if (component != null) {
			if (component instanceof Window) {
				return (Window) component;
			}
			return getWindow(component.getParent());
		}
		return null;
	}

	public JTitle getTitleComponent() {
		return titlePanel;
	}

	private JPanel buildButtons() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10, 5, 15, 5));

		JComponent separator = DefaultComponentFactory.getInstance().createSeparator(""); //$NON-NLS-1$
		panel.add(separator, BorderLayout.NORTH);

		buttonMap.clear();

		JButton[] buttons = new JButton[actions.length];
		for (int i = 0; i < buttons.length; i++) {
			JButton b = new JButton(actions[i]);
			buttons[i] = b;
			buttonMap.put(actions[i], b);
		}

		JPanel buttonsPanel;
		if (actions.length > 1) {
			buttonsPanel = ButtonBarFactory.buildRightAlignedBar(buttons);
		} else {
			buttonsPanel = ButtonBarFactory.buildCenteredBar(buttons);
		}
		buttonsPanel.setBorder(new EmptyBorder(15, 0, 0, 10));
		panel.add(buttonsPanel, BorderLayout.CENTER);

		return panel;
	}

	public void setSize(int width, int height) {
		dialog.setSize(width, height);
	}

	public void setSize(Dimension d) {
		dialog.setSize(d);
	}

	public void centerToScreen() {
		dialog.setLocationRelativeTo(null);
	}

	public void centerToParent() {
		dialog.setLocationRelativeTo(parent);
	}

	public void pack() {
		dialog.pack();
	}

	public void setModal(boolean modal) {
		dialog.setModal(modal);
		dialog.setSeparatedWindow(!modal);
	}

	public boolean isModal(boolean modal) {
		return dialog.isModal();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void showDialog() {
		if (resizable) {
			Dimension d = dialog.getPreferredSize();

			if (activeSave) {
				SyPreferences prefs = SyPreferences.getPreferences();
				String name = id != null ? id : contents.getClass().getName();

				int ow = prefs.getInt(name + "_origine_w", -1); //$NON-NLS-1$
				int oh = prefs.getInt(name + "_origine_h", -1); //$NON-NLS-1$
				if (ow == d.width && oh == d.height) {
					int w = prefs.getInt(name + "_current_w", d.width); //$NON-NLS-1$
					int h = prefs.getInt(name + "_current_h", d.height); //$NON-NLS-1$
					d.setSize(w, h);
					dialog.setSize(d);
				} else {
					prefs.putInt(name + "_origine_w", d.width); //$NON-NLS-1$
					prefs.putInt(name + "_origine_h", d.height); //$NON-NLS-1$

					prefs.remove(name + "_current_w"); //$NON-NLS-1$
					prefs.remove(name + "_current_h"); //$NON-NLS-1$
				}
				prefs.getInt(name + "_origine_h", -1); //$NON-NLS-1$
			}

			Dimension dScreen = Toolkit.getDefaultToolkit().getScreenSize();

			if (d.width > dScreen.width && d.height > dScreen.height) {
				dialog.setSize(dScreen.width - 50, dScreen.height - 50);
			} else if (d.width > dScreen.width) {
				dialog.setSize(dScreen.width - 50, d.height);
			} else if (d.height > dScreen.height) {
				dialog.setSize(d.height, dScreen.height - 50);
			}

			if (centralizable) {
				if (multiScreen) {
					centerToParent();
				} else {
					centerToScreen();
				}
			}

			dialog.setResizable(true);
		} else {
			dialog.setResizable(false);
		}

		if (glassPane != null) {
			dialog.setGlassPane(glassPane);
		}

		dialog.addWindowFocusListener(dialogWindowFocusListener);

		if (titlePanel != null) {
			titlePanel.startAnimation();
		}

		dialog.setVisible(true);
	}

	public void closeDialog() {
		if (resizable && activeSave) {
			SyPreferences prefs = SyPreferences.getPreferences();
			String name = id != null ? id : contents.getClass().getName();

			Dimension d = dialog.getSize();

			prefs.putInt(name + "_current_w", d.width); //$NON-NLS-1$
			prefs.putInt(name + "_current_h", d.height); //$NON-NLS-1$
		}

		dialog.setVisible(false);

		if (titlePanel != null) {
			titlePanel.stopAnimation();
		}

		dialog.removeWindowFocusListener(dialogWindowFocusListener);

		dialog.getRootPane().getActionMap().remove("close");
	}

	public void dispose() {
		dialog.dispose();
		dialog = null;
	}

	public boolean isCloseDialog() {
		return dialog == null || !dialog.isShowing();
	}

	public void requestFocus() {
		dialog.requestFocus();
	}

	public static Image getDefaultImage() {
		return JTitle.getDefaultImage();
	}

	public static void setDefaultImage(Image defaultImage) {
		JTitle.setDefaultImage(defaultImage);
	}

	public static boolean isActiveSave() {
		return activeSave;
	}

	public static void setActiveSave(boolean activeSave) {
		JDialogModel.activeSave = activeSave;
	}

	public static boolean isMultiScreen() {
		return multiScreen;
	}

	public static void setMultiScreen(boolean multiScreen) {
		JDialogModel.multiScreen = multiScreen;
	}

	public static void setShowTitle(boolean showTitle) {
		JDialogModel.showTitle = showTitle;
	}

	public static boolean isShowTitle() {
		return showTitle;
	}

	public void setGlassPane(Component glassPane) {
		this.glassPane = glassPane;
		if (dialog != null && glassPane != null) {
			dialog.setGlassPane(glassPane);
		}
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
		if (dialog != null) {
			dialog.setResizable(resizable);
		}
	}

	public boolean isCentralizable() {
		return centralizable;
	}

	public void setCentralizable(boolean centralizable) {
		this.centralizable = centralizable;
	}

	public boolean isForbidenClose() {
		return forbidenClose;
	}

	public void setForbidenClose(boolean forbidenClose) {
		this.forbidenClose = forbidenClose;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		if (titlePanel != null) {
			titlePanel.setTitle(title);
		}
		if (dialog != null) {
			dialog.setTitle(title);
		}
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
		if (titlePanel != null) {
			titlePanel.setSubTitle(subTitle);
		}
	}

	public ActionListener getActionListenerOpen() {
		return actionListenerOpen;
	}

	public void setActionListenerOpen(ActionListener actionListenerOpen) {
		this.actionListenerOpen = actionListenerOpen;
	}

	public ActionListener getActionListenerClose() {
		return actionListenerClose;
	}

	public void setActionListenerClose(ActionListener actionListenerClose) {
		this.actionListenerClose = actionListenerClose;
	}

	public void addWindowListener(WindowListener l) {
		dialog.addWindowListener(l);
	}

	public void removeWindowListener(WindowListener l) {
		dialog.removeWindowListener(l);
	}

	/**
	 * Donne access au button selon l'action
	 * 
	 * @param action
	 * @return
	 */
	public JButton getButton(Action action) {
		return buttonMap.get(action);
	}

	private final class DialogWindowListener extends WindowAdapter {
		@Override
		public void windowOpened(WindowEvent e) {
			if (actionListenerOpen != null) {
				actionListenerOpen.actionPerformed(new ActionEvent(JDialogModel.this, 0, "opened")); //$NON-NLS-1$
			}
			dialog.invalidate();
			dialog.validate();
		}

		@Override
		public void windowClosing(WindowEvent e) {
			if (actionListenerClose != null) {
				actionListenerClose.actionPerformed(new ActionEvent(JDialogModel.this, 0, "closed")); //$NON-NLS-1$
			}
			if (!forbidenClose && dialog != null && dialog.isVisible()) {
				dialog.setVisible(false);
			}
		}
	}

	private final class DialogWindowFocusListener implements WindowFocusListener {

		@Override
		public void windowGainedFocus(WindowEvent e) {
			if (titlePanel != null && !titlePanel.isAnimation()) {
				titlePanel.startAnimation();
			}
		}

		@Override
		public void windowLostFocus(WindowEvent e) {
			if (titlePanel != null && titlePanel.isAnimation()) {
				titlePanel.stopAnimation();
			}
		}
	}

	private final class DialogCloseActionListener extends AbstractAction {

		private static final long serialVersionUID = -5972543929894613111L;

		@Override
		public void actionPerformed(ActionEvent e) {
			closeDialog();
		}
	}
}
