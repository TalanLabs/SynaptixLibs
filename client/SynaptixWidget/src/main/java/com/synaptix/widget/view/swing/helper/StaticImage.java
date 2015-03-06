package com.synaptix.widget.view.swing.helper;

import java.awt.Image;

import javax.swing.ImageIcon;

public final class StaticImage {

	public static ImageIcon getImageScale(ImageIcon imageIcon, int height) {
		if (imageIcon != null) {
			int width = imageIcon.getIconWidth() * height / imageIcon.getIconHeight();
			Image scaledImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
			return new ImageIcon(scaledImage);
		}
		return null;
	}
}
