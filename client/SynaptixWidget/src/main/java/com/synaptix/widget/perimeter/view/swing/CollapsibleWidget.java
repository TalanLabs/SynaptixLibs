package com.synaptix.widget.perimeter.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXCollapsiblePane;

import com.synaptix.widget.view.swing.helper.StaticImage;

public class CollapsibleWidget extends JPanel {

	private static final long serialVersionUID = 4662778944431513435L;

	private final static Color NORMAL_PANEL_COLOR = new Color(224, 224, 224);

	private final static Color ROLLOVER_PANEL_COLOR = new Color(240, 240, 240);

	private final static Color NORMAL_VALUE_COLOR = new Color(224, 128, 128);

	private final static Color ROLLOVER_VALUE_COLOR = new Color(240, 128, 128);

	private static final ImageIcon PLUS_ICON;

	private static final ImageIcon MOINS_ICON;

	static {
		PLUS_ICON = StaticImage.getImageScale(new ImageIcon(CollapsibleWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconMiniPlus.png")), 8); //$NON-NLS-1$
		MOINS_ICON = StaticImage.getImageScale(new ImageIcon(CollapsibleWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconMiniMoins.png")), 8); //$NON-NLS-1$
	}

	private JXCollapsiblePane collapsible;

	private MyLabel titleLabel;

	private IPerimeterWidget perimetreWidget;

	public CollapsibleWidget(IPerimeterWidget perimetreWidget) {
		super(new BorderLayout());

		this.perimetreWidget = perimetreWidget;

		initComponents();

		updateColor();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void createComponents() {
		titleLabel = new MyLabel();
		collapsible = new JXCollapsiblePane();
	}

	private void initComponents() {
		createComponents();

		titleLabel.setFont(new Font("arial", Font.BOLD, 12));
		titleLabel.setText(perimetreWidget.getTitle());
		titleLabel.addMouseListener(new MyMouseListener());
		titleLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		collapsible.setCollapsed(false);
		collapsible.setAnimated(false);

		collapsible.add(perimetreWidget.getView());

		perimetreWidget.addPerimetreWidgetListener(new MyPerimetreWidgetListener());

		updateIcon();
	}

	private JComponent buildContents() {
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(titleLabel, BorderLayout.CENTER);
		p2.setBorder(BorderFactory.createEtchedBorder());

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(p2, BorderLayout.NORTH);
		panel.add(collapsible, BorderLayout.CENTER);
		return panel;
	}

	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	protected void fireActionListener(String action) {
		ActionListener[] ls = listenerList.getListeners(ActionListener.class);
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action);
		for (ActionListener l : ls) {
			l.actionPerformed(e);
		}
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsible.setCollapsed(collapsed);
		updateIcon();
	}

	public boolean isCollapsed() {
		return this.collapsible.isCollapsed();
	}

	private void updateIcon() {
		if (isCollapsed()) {
			this.titleLabel.setIcon(PLUS_ICON);
		} else {
			this.titleLabel.setIcon(MOINS_ICON);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		titleLabel.setEnabled(enabled);
		setCollapsed(!enabled);
	}

	private void updateColor() {
		if (titleLabel.rollover) {
			titleLabel.setBackground(perimetreWidget.getValue() != null ? ROLLOVER_VALUE_COLOR : ROLLOVER_PANEL_COLOR);
		} else {
			titleLabel.setBackground(perimetreWidget.getValue() != null ? NORMAL_VALUE_COLOR : NORMAL_PANEL_COLOR);
		}
	}

	private final class MyPerimetreWidgetListener implements PerimeterWidgetListener {

		@Override
		public void titleChanged(IPerimeterWidget source) {
			titleLabel.setText(perimetreWidget.getTitle());
		}

		@Override
		public void valuesChanged(IPerimeterWidget source) {
			updateColor();
		}
	}

	private class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (isEnabled()) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					setCollapsed(!isCollapsed());
					fireActionListener("collasped");

					updateIcon();
				}
				if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
					perimetreWidget.setValue(null);
				}
			}
		}
	}

	private final class MyLabel extends JLabel {

		private static final long serialVersionUID = 7650131542621702239L;

		private boolean rollover;

		public MyLabel() {
			super(PLUS_ICON, JLabel.LEFT);

			this.rollover = false;

			this.setOpaque(true);
			// this.setBackground(NORMAL_PANEL_COLOR);

			this.addMouseListener(new MyMouseListener());
		}

		private final class MyMouseListener extends MouseAdapter {

			@Override
			public void mouseEntered(MouseEvent e) {
				rollover = true;
				updateColor();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				rollover = false;
				updateColor();
			}
		}
	}
}
