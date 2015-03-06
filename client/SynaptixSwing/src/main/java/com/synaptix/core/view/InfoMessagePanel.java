package com.synaptix.core.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXCollapsiblePane;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.core.controller.InfoMessageListener;

public class InfoMessagePanel extends JPanel {

	private static final long serialVersionUID = 4373544206467111456L;

	private static final ImageIcon closeNormalIcon;

	private static final ImageIcon closeRolloverIcon;

	private JXCollapsiblePane collapsiblePane;

	private JLabel infoMessageLabel;

	private Action closeInfoMessageAction;

	private InfoMessageListener actionListener;

	private JButton closeButton;

	static {
		closeNormalIcon = new ImageIcon(InfoMessagePanel.class
				.getResource("closeNormal.png"));
		closeRolloverIcon = new ImageIcon(InfoMessagePanel.class
				.getResource("closeRollover.png"));
	}

	public InfoMessagePanel() {
		super(new BorderLayout());

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		closeInfoMessageAction = new CloseInfoMessageAction();
	}

	private void createComponents() {
		collapsiblePane = new JXCollapsiblePane();
		infoMessageLabel = new JLabel();
		closeButton = new JButton(closeInfoMessageAction);
	}

	private void initComponents() {
		createComponents();

		collapsiblePane.setAnimated(true);
		collapsiblePane.setCollapsed(true);

		infoMessageLabel.setOpaque(false);
		infoMessageLabel.setForeground(Color.BLACK);
		infoMessageLabel.addMouseListener(new MyMouseListener());

		closeButton.setOpaque(false);
		closeButton.setBorderPainted(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setFocusable(false);
		closeButton.setIcon(closeNormalIcon);
		closeButton.setRolloverEnabled(true);
		closeButton.setRolloverIcon(closeRolloverIcon);

		JComponent c = buildInfoMessagePanel();
		c.setOpaque(true);
		c.setBackground(new Color(0xFFFFE1));
		c.setBorder(BorderFactory.createEtchedBorder());

		collapsiblePane.add(c, BorderLayout.CENTER);
	}

	private JComponent buildInfoMessagePanel() {
		FormLayout layout = new FormLayout(
				"FILL:PREF:GROW(1.0),FILL:4DLU:NONE,FILL:15PX:NONE",
				"FILL:PREF:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(infoMessageLabel, cc.xy(1, 1));
		builder.add(closeButton, cc.xy(3, 1));
		return builder.getPanel();
	}

	private JComponent buildContents() {
		return collapsiblePane;
	}

	public void showInfoMessage(Icon icon, String message,
			boolean authorizeClose, InfoMessageListener l) {
		infoMessageLabel.setIcon(icon);
		infoMessageLabel.setText(message);
		this.actionListener = l;
		closeButton.setVisible(authorizeClose);
		collapsiblePane.setCollapsed(false);
	}

	public void hideInfoMessage() {
		collapsiblePane.setCollapsed(true);
	}

	private final class MyMouseListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && actionListener != null) {
				actionListener.labelClicked();
			}
		}
	}

	private final class CloseInfoMessageAction extends AbstractAction {

		private static final long serialVersionUID = 8486039449608655938L;

		public CloseInfoMessageAction() {
			super(null, closeNormalIcon);
		}

		public void actionPerformed(ActionEvent e) {
			if (actionListener != null) {
				actionListener.closeClicked();
			}
			collapsiblePane.setCollapsed(true);
		}
	}
}
