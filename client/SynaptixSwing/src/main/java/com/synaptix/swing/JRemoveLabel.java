package com.synaptix.swing;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.EventListenerList;

public class JRemoveLabel extends JLabel {

	private static final long serialVersionUID = 8403189487699243366L;

	private final static ImageIcon ICON_VALIDATE;

	private final static ImageIcon ICON_ERROR;

	static {
		ICON_VALIDATE = new ImageIcon(getHeightImageScale(new ImageIcon(
				JRemoveLabel.class.getResource("/images/iconValidate.png")) //$NON-NLS-1$
				.getImage(), 20));
		ICON_ERROR = new ImageIcon(getHeightImageScale(new ImageIcon(
				JRemoveLabel.class.getResource("/images/iconError.png")) //$NON-NLS-1$
				.getImage(), 20));
	}

	public static Image getHeightImageScale(Image image, int height) {
		if (image != null) {
			int width = image.getWidth(null) * height / image.getHeight(null);
			Image scaledImage = image.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);
			return scaledImage;
		}
		return null;
	}

	private EventListenerList listeners;

	public JRemoveLabel() {
		super(ICON_ERROR);

		listeners = new EventListenerList();

		this.addMouseListener(new RemoveMouseListener());
	}

	public void showErrorIcon() {
		showErrorIcon(null);
	}

	public void showErrorIcon(String toolTips) {
		this.setIcon(ICON_ERROR);
		this.setToolTipText(toolTips);
	}

	public void showValidateIcon() {
		showValidateIcon(null);
	}

	public void showValidateIcon(String toolTips) {
		this.setIcon(ICON_VALIDATE);
		this.setToolTipText(toolTips);
	}

	public void addActionListener(ActionListener l) {
		listeners.add(ActionListener.class, l);
	}

	public void removeActionListener(ActionListener l) {
		listeners.remove(ActionListener.class, l);
	}

	protected void fireActionListener() {
		ActionEvent e = new ActionEvent(this, 0, "changed"); //$NON-NLS-1$
		for (ActionListener l : listeners.getListeners(ActionListener.class)) {
			l.actionPerformed(e);
		}
	}

	private final class RemoveMouseListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if (isEnabled() && e.getButton() == MouseEvent.BUTTON1
					&& e.getClickCount() == 2) {
				fireActionListener();
			}
		}
	}
}
