import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.FontAwesomeHelper;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.view.swing.RoundedBorder;

import helper.MainHelper;

public class MainCard {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JSplitPane split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, createPanel("Titre de la fenêtre 1"), createPanel("Titre de la fenêtre 2"));
				split1.setDividerLocation(400);

				JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, createPanel("Titre de la fenêtre 3"), createPanel("Titre de la fenêtre 4"));
				split2.setDividerLocation(400);

				JSplitPane split3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, split1, split2);
				split3.setDividerLocation(300);
				split3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

				frame.getContentPane().add(createContent(), BorderLayout.CENTER);

				frame.setSize(800, 600);
				frame.setVisible(true);
			}
		});
	}

	private static JComponent createContent() {
		JTable table = new JTable(new DefaultTableModel(100, 10) {
			@Override
			public Object getValueAt(int row, int column) {
				return row + " " + column;
			}
		});
		table.setPreferredScrollableViewportSize(new Dimension(0, 200));

		FormLayout layout = new FormLayout("FILL:PREF:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append(ButtonBarFactory.buildLeftAlignedBar(new JButton(new MyAction()), new JButton(new MyAction()), new JButton(new MyAction())));
		builder.appendRow(builder.getLineGapSpec());
		builder.appendRow("FILL:PREF:GROW(1.0)");
		builder.nextLine(2);
		builder.append(new JScrollPane(table));
		JButton test = new JButton("Test");
		test.setEnabled(false);
		builder.append(ButtonBarFactory.buildLeftAlignedBar(test));
		return builder.getPanel();
	}

	private static class MyAction extends AbstractAddAction {
		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private static JComponent createPanel(String title) {
		return createCard(title, createContent());
	}

	private static JComponent createCard(String title, Component c) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(createCardHeader(title), BorderLayout.NORTH);
		panel.add(createCardContent(c), BorderLayout.CENTER);
		return panel;
	}

	private static JComponent selectedComponent = null;

	private static JComponent createCardHeader(String title) {
		// final JCardHeader panelHeader = new JCardHeader(title);
		// panelHeader.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mouseClicked(MouseEvent e) {
		// super.mouseClicked(e);
		// panelHeader.setSelected(!panelHeader.isSelected());
		// }
		// });
		// panelHeader.setBorder(new RoundedBorder(RoundedBorder.Mode.ROUNDED, RoundedBorder.Mode.LINE, 4));
		//
		// panelHeader.setRightActions(new Action[] { new MyFontAction("fa-compress"), new MyFontAction("fa-expand"), new MyFontAction("fa-close") });

		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.setOpaque(true);
		panel.setBackground(Color.DARK_GRAY);

		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		titleLabel.setHorizontalAlignment(JLabel.CENTER);

		panel.add(Box.createHorizontalStrut(4));
		panel.add(Box.createHorizontalGlue());
		panel.add(titleLabel);
		panel.add(Box.createHorizontalGlue());
		panel.add(Box.createRigidArea(new Dimension(4, 20)));
		panel.add(createButton("fa-compress", "Close"));
		panel.add(createButton("fa-expand", "Close"));
		panel.add(createButton("fa-close", "Close"));

		panel.setBorder(new RoundedBorder(RoundedBorder.Mode.ROUNDED, RoundedBorder.Mode.LINE, 4));

		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (selectedComponent != null && selectedComponent != panel) {
					selectedComponent.setBackground(Color.DARK_GRAY);
				}
				selectedComponent = panel;
				panel.setBackground(Color.LIGHT_GRAY);
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

		return panel;
	}

	private static JButton createButton(String name, String toolTipText) {
		JButton res = new JButton();
		res.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		res.setMargin(new Insets(0, 2, 0, 2));
		res.setRolloverEnabled(true);
		res.setBorderPainted(false);
		res.setFocusable(false);
		res.setContentAreaFilled(false);
		res.setIcon(FontAwesomeHelper.getIcon(name, 10, Color.BLACK));
		res.setRolloverIcon(FontAwesomeHelper.getIcon(name, 10, Color.GRAY));
		res.setPressedIcon(FontAwesomeHelper.getIcon(name, 10, Color.LIGHT_GRAY));
		res.setRolloverSelectedIcon(FontAwesomeHelper.getIcon(name, 10, Color.GRAY));
		res.setToolTipText(toolTipText);
		return res;
	}

	private static JComponent createCardContent(Component c) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(c, BorderLayout.CENTER);
		panel.setBorder(new RoundedBorder(RoundedBorder.Mode.NONE, RoundedBorder.Mode.ROUNDED, 4));
		return panel;
	}

	private static class MyFontAction extends AbstractAction {

		private static final long serialVersionUID = 667749814720334325L;

		public MyFontAction(String text) {
			super(FontAwesomeHelper.getCharacter(text).toString());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
}
