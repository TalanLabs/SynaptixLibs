package titleheader;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

public class DefaultCardHeaderTitleRenderer extends JLabel implements CardHeaderTitleRenderer {

	private static final long serialVersionUID = 1436875989662108523L;

	private Font selectedFont;

	private Font defaultFont;

	public DefaultCardHeaderTitleRenderer() {
		super();

		this.setHorizontalAlignment(JLabel.CENTER);
	}

	@Override
	public Component getCardHeaderTitleRendererComponent(JCardHeader cardHeader, String title, boolean selected) {
		if (selectedFont == null) {
			defaultFont = this.getFont().deriveFont(Font.BOLD, this.getFont().getSize() + 2.0f);
			selectedFont = this.getFont().deriveFont(Font.BOLD, this.getFont().getSize() + 2.0f);
		}
		this.setForeground(cardHeader.getForeground());
		if (selected) {
			this.setFont(selectedFont);
		} else {
			this.setFont(defaultFont);
		}
		this.setText(title);
		return this;
	}
}
