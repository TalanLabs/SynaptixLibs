import helper.MainHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import model.CountryBuilder;
import model.ICountry;

import org.pushingpixels.substance.api.renderers.SubstanceRenderer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.widget.renderer.view.swing.IValueComponent;
import com.synaptix.widget.renderer.view.swing.ValueComponentSubstanceListCellRenderer;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				DefaultListModel listModel = new DefaultListModel();
				for (int i = 0; i < 100; i++) {
					listModel.addElement(new CountryBuilder().build());
				}

				JList list = new JList(listModel);
				list.setFixedCellHeight(100);

				list.setCellRenderer(new ValueComponentSubstanceListCellRenderer<ICountry>(new MyRenderer()));

				list.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

				JFrame frame = MainHelper.createFrame();
				frame.getContentPane().add(new JScrollPane(list));
				frame.setVisible(true);
			}
		});
	}

	@SubstanceRenderer
	private static final class MyRenderer extends JPanel implements ListCellRenderer, IValueComponent<ICountry> {

		private final Color selectedBorderColor = new Color(39, 90, 124);
		private final Color selectedBackColor = new Color(210, 235, 252);
		private final Color deselectedBorderColor = new Color(121, 123, 124);
		private final Color deselectedBackColor = new Color(232, 237, 240);

		private Color borderColor;

		private JLabel label;

		private JLabel label2;

		private JLabel label3;

		private Color backColor;

		public MyRenderer() {
			borderColor = deselectedBorderColor;
			backColor = deselectedBackColor;

			label = new JLabel("Coucou" + " " + "no");
			label2 = new JLabel("Default");
			label3 = new JLabel("01/01/1970");

			setOpaque(false);

			FormLayout layout = new FormLayout("LEFT:DEFAULT:GROW(0.5),3DLU,RIGHT:DEFAULT:GROW(0.5)");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
			builder.append(label);
			builder.nextLine(1);
			builder.append(label2);
			builder.append(label3);

			this.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 5));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Rectangle rect = this.getBounds();

			g.setColor(backColor);
			// g.fillRect(0, 0, rect.width, rect.height - 10);

			g.setColor(borderColor);
			g.drawRect(0, 0, rect.width - 1, rect.height - 10);

			g.setColor(borderColor);
			g.fillRect(0, 0, 6, rect.height - 10);
		}

		public void setSelected(boolean selected) {
			if (selected) {
				borderColor = selectedBorderColor;
				backColor = selectedBackColor;
			} else {
				borderColor = deselectedBorderColor;
				backColor = deselectedBackColor;
			}
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			setSelected(isSelected);
			return this;
		}

		@Override
		public void setValue(JLabel label, ICountry value) {
		}

		@Override
		public Component getComponent() {
			return this;
		}
	}
}
