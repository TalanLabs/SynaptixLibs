package com.synaptix.widget.view.swing.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;

import com.jgoodies.validation.ValidationResult;
import com.synaptix.swing.widget.JBigLetters;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.swing.DefaultBeanDialog;

public class DefaultBeanExtensionDialogCellRenderer<T> extends JBigLetters implements DefaultBeanDialog.BeanExtensionDialogCellRenderer<T> {

	private static final long serialVersionUID = -3686539449199414414L;

	private final Color contourColor = new Color(192, 192, 192);

	private final Color fondColor = new Color(224, 224, 224);

	private final Color textColor = new Color(0, 0, 0);

	private final Color contourErreurColor = new Color(192, 96, 96);

	private final Color fondErreurColor = new Color(192, 56, 56);

	private final Color textErreurColor = new Color(255, 255, 255);

	private final Color fondSelectionColor = new Color(200, 210, 240);

	private final Color contourSelectionColor = new Color(0, 128, 255);

	private final Color contourAttentionColor = new Color(224, 156, 0);

	private final Color fondAttentionColor = new Color(224, 112, 0);

	private final Color textAttentionColor = new Color(255, 255, 255);

	private final Image erreurImage = new ImageIcon(DefaultBeanExtensionDialogCellRenderer.class.getResource("/com/synaptix/widget/view/swing/erreur/erreurRouge.png")) //$NON-NLS-1$
			.getImage();

	private final Image attentionImage = new ImageIcon(DefaultBeanExtensionDialogCellRenderer.class.getResource("/com/synaptix/widget/view/swing/warningOrange.png")) //$NON-NLS-1$
			.getImage();

	public DefaultBeanExtensionDialogCellRenderer() {
		super();
		this.setOpaque(true);
		// this.setBackground(Color.WHITE);
	}

	@Override
	public Component getCellRendererComponent(DefaultBeanDialog<T> beanDialog, IBeanExtensionDialogView<T> led, ValidationResult result, boolean isSelected) {
		this.setText(led.getTitle());

		boolean erreur = false;
		boolean attention = false;
		if (result != null) {
			if (result.hasErrors()) {
				erreur = true;
				this.setLogoImage(erreurImage);
			} else if (result.hasWarnings()) {
				attention = true;
				this.setLogoImage(attentionImage);
			} else {
				this.setLogoImage(null);
			}
		} else {
			this.setLogoImage(null);
		}

		if (isSelected) {
			this.setContourColor(contourSelectionColor);
			this.setFondColor(fondSelectionColor);
			this.setTextColor(textColor);
		} else {
			if (erreur) {
				this.setContourColor(contourErreurColor);
				this.setFondColor(fondErreurColor);
				this.setTextColor(textErreurColor);
			} else if (attention) {
				this.setContourColor(contourAttentionColor);
				this.setFondColor(fondAttentionColor);
				this.setTextColor(textAttentionColor);
			} else {
				this.setContourColor(contourColor);
				this.setFondColor(fondColor);
				this.setTextColor(textColor);
			}
		}

		return this;
	}

}
