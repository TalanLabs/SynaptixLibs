package titleheader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JLabel;

import com.synaptix.swing.utils.FontAwesomeHelper;

public class DefaultCardHeaderActionRenderer extends JLabel implements CardHeaderActionRenderer {

	private static final long serialVersionUID = -7373408196301142294L;

	private Color selectedColor = Color.BLACK;

	private Color defaultColor = Color.GRAY;

	public DefaultCardHeaderActionRenderer() {
		super();

		this.setFont(FontAwesomeHelper.deriveFont(10));
		this.setHorizontalAlignment(JLabel.CENTER);
	}

	@Override
	public Component getCardHeaderActionRendererComponent(JCardHeader cardHeader, boolean selected, Action action, boolean pressed, boolean over) {
		this.setPreferredSize(new Dimension(cardHeader.getCellHeight(), cardHeader.getCellHeight()));

		this.setText((String) action.getValue(Action.NAME));
		if (selected) {
			this.setForeground(selectedColor);
		} else {
			this.setForeground(defaultColor);
		}

		// this.setOpaque(true);
		// this.setBackground(Color.GRAY);
		// this.setForeground(Color.white);

		return this;
	}
}
