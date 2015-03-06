package com.synaptix.swing.utils;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class Toolkit {

	public static Rectangle2D getImageScale(Image image, int width, int height) {
		return getImageScale(image, width, height, false);
	}

	public static Rectangle2D getImageScale(Image image, int width, int height,
			boolean upscale) {
		return getImageScale(image, 0, 0, width, height, false);
	}

	public static Rectangle2D getImageScale(Image image, int x, int y,
			int width, int height) {
		return getImageScale(image, x, y, width, height, false);
	}

	public static Rectangle2D getImageScale(Image image, int x, int y,
			int width, int height, boolean upscale) {
		double widthComponent = width;
		double heightComponent = height;
		double diffComponent = widthComponent / heightComponent;

		// System.out.println("Component w = " + widthComponent + " h = "
		// + heightComponent + " diff = " + diffComponent);

		double widthImage = image.getWidth(null);
		double heightImage = image.getHeight(null);
		double diffImage = widthImage / heightImage;

		// System.out.println("Image w = " + widthImage + " h = " + heightImage
		// + " diff = " + diffImage);

		double diff = diffImage / diffComponent;

		// System.out.println("diff = " + diff);

		double w = widthComponent;
		double h = heightComponent;
		if (diff >= 1.0) {
			h = h / diff;
		} else {
			w = w * diff;
		}

		if (!upscale) {
			w = Math.min(w, widthImage);
			h = Math.min(h, heightImage);
		}

		double sx = (widthComponent - w) / 2.0 + x;
		double sy = (heightComponent - h) / 2.0 + y;

		return new Rectangle2D.Double(sx, sy, w, h);
	}

	public static Rectangle2D getImageScaleForComponent(Image image,
			JComponent component) {
		return getImageScaleForComponent(image, component, false);
	}

	public static Rectangle2D getImageScaleForComponent(Image image,
			JComponent component, boolean upscale) {
		Insets insets = component.getInsets();
		return getImageScale(image, insets.left, insets.top, component
				.getWidth()
				- (insets.right + insets.left), component.getHeight()
				- (insets.top + insets.bottom), upscale);
	}

	public static ImageIcon getImageIcon(Image image) {
		return new ImageIcon(image);
	}

	public static ImageIcon getImageIconScale(Image image, int width, int height) {
		return new ImageIcon(getImageToScale(image, width, height));
	}

	public static ImageIcon getWidthImageIconScale(Image image, int width) {
		return new ImageIcon(getWidthImageScale(image, width));
	}

	public static ImageIcon getHeightImageIconScale(Image image, int height) {
		return new ImageIcon(getHeightImageScale(image, height));
	}

	public static Image getImageToScale(Image image, int width, int height) {
		if (image != null) {
			Image scaledImage = image.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);
			return scaledImage;
		}
		return null;
	}

	public static Image getWidthImageScale(Image image, int width) {
		if (image != null) {
			int height = image.getHeight(null) * width / image.getWidth(null);
			Image scaledImage = image.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);
			return scaledImage;
		}
		return null;
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

	private static final BufferedImage resize(Image image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(image, 0, 0, width, height, null);
		g2.dispose();
		return resizedImage;
	}

	/**
	 * Retaille une image avec un effet de blur
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static final Image resizeTrick(Image image, int width, int height) {
		BufferedImage res = resize(image, width * 2, height * 2);
		res = blurImage(res);
		return resize(res, width, height);
	}

	private static final BufferedImage blurImage(BufferedImage image) {
		float ninth = 1.0f / 9.0f;
		float[] blurKernel = { ninth, ninth, ninth, ninth, ninth, ninth, ninth,
				ninth, ninth };

		Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
		map.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		map.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		map.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		RenderingHints hints = new RenderingHints(map);
		BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel),
				ConvolveOp.EDGE_NO_OP, hints);
		return op.filter(image, null);
	}

	/**
	 * Autre methode de retaille avec système recursif
	 * 
	 * @param img
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
	public static final Image getScaledInstance(Image img, int targetWidth,
			int targetHeight) {
		Image ret = img;
		int w = img.getWidth(null);
		int h = img.getHeight(null);

		do {
			if (w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = tmp.createGraphics();
			g2.setComposite(AlphaComposite.Src);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	public static final Image getImageWithAlpha(Image image, float alpha) {
		return getImageWithComposite(image, AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, alpha));
	}

	public static final Image getImageWithComposite(Image image,
			Composite composite) {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.setComposite(composite);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return bufferedImage;
	}
}
