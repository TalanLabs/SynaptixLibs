package com.synaptix.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JComponent;
import javax.swing.Timer;

import com.synaptix.swing.utils.Toolkit;
import com.synaptix.swing.utils.Utils;

public class JTitle extends JComponent {

	private static final long serialVersionUID = -7527319037138135055L;

	public static final Color COLOR_DEFAULT_BACKGROUND_LOW;

	public static final Color COLOR_DEFAULT_BACKGROUND_HIGH;

	public static final Color COLOR_DEFAULT_SUBTITLE;

	public static final Color COLOR_DEFAULT_TITLE;

	public static final Color COLOR_DEFAULT_TITLE_SHADOW;

	public static final Color COLOR_DEFAULT_GLOSS_VERY_LOW;

	public static final Color COLOR_DEFAULT_GLOSS_LOW;

	public static final Color COLOR_DEFAULT_GLOSS_HIGH;

	public static final Color COLOR_DEFAULT_DOWN_LINE;

	public static final int DEFAULT_GLOSS_PRECISION;

	public static final double DEFAULT_NUMBER_WAVE;

	public static final double DEFAULT_SPEED_ANGLE;

	public static final double DEFAULT_SPEED_WAVE;

	private static final int DEFAULT_START_TITLE_X;

	private static final int DEFAULT_START_SUBTITLE_X;

	private static final RenderingHints hints;

	private static final ConvolveOp blurOp;

	private static final Font titleFont;

	private static final Font subTitleFont;

	private static final Stroke glossStrock;

	private static final Stroke lineDownStrock;

	private static Image defaultImage;

	private String title;

	private String subTitle;

	private Image imageTitle;

	private Color colorBackgroundLow;

	private Color colorBackgroundHigh;

	private Color colorGlossVeryLow;

	private Color colorGlossLow;

	private Color colorGlossHigh;

	private Color colorSubTitle;

	private Color colorTitle;

	private Color colorTitleShadow;

	private Color colorDownLine;

	private double animationAngle;

	private double animationSpeedAngle;

	private double animationHeight;

	private double animationSpeedHeight;

	private boolean animationUp;

	private boolean playAnimation;

	private Timer animationTimer;

	private Image stripImage;

	private Image textShadowImage;

	private int maxImageLength;

	private float startSubTitleY;

	private double stripeWidth;

	private Stroke stripeWidthStroke;

	private Paint glossPaintCache;

	private Paint glossPaint2Cache;

	private AffineTransform inverseAffineTransforme;

	private Paint pinstripPaintCache;

	private TextLayout titleTextLayoutCache;

	private TextLayout subTitleTextLayoutCache;

	static {
		COLOR_DEFAULT_BACKGROUND_LOW = new Color(2, 97, 192, 255);
		COLOR_DEFAULT_BACKGROUND_HIGH = new Color(124, 173, 223, 255);
		COLOR_DEFAULT_SUBTITLE = new Color(255, 255, 255, 255);
		COLOR_DEFAULT_TITLE_SHADOW = new Color(0, 0, 0, 192);
		COLOR_DEFAULT_TITLE = new Color(255, 255, 255, 255);
		COLOR_DEFAULT_GLOSS_VERY_LOW = new Color(1.0f, 1.0f, 1.0f, 0.125f);
		COLOR_DEFAULT_GLOSS_LOW = new Color(1.0f, 1.0f, 1.0f, 0.25f);
		COLOR_DEFAULT_GLOSS_HIGH = new Color(1.0f, 1.0f, 1.0f, 0.5f);
		COLOR_DEFAULT_DOWN_LINE = new Color(0.25f, 0.25f, 0.25f, 0.75f);

		DEFAULT_GLOSS_PRECISION = 10;
		DEFAULT_NUMBER_WAVE = 2.5;
		DEFAULT_SPEED_ANGLE = 1.0;
		DEFAULT_SPEED_WAVE = 1.0 / 256.0;

		DEFAULT_START_TITLE_X = 10;
		DEFAULT_START_SUBTITLE_X = 30;

		hints = new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		blurOp = new ConvolveOp(getBlurKernel(5), ConvolveOp.EDGE_NO_OP, hints);

		titleFont = new Font("Arial", Font.BOLD, 25); //$NON-NLS-1$

		subTitleFont = new Font("Dialog", Font.BOLD, 12); //$NON-NLS-1$

		glossStrock = new BasicStroke(2.0f);
		lineDownStrock = new BasicStroke(2.0f);

		defaultImage = null;
	}

	public JTitle() {
		this(""); //$NON-NLS-1$
	}

	public JTitle(String title) {
		this(title, ""); //$NON-NLS-1$
	}

	public JTitle(String title, String subTitle) {
		this(title, subTitle, 48);
	}

	public JTitle(String title, String subTitle, int height) {
		super();
		this.title = title;
		this.subTitle = subTitle;

		this.setOpaque(false);

		this.colorBackgroundLow = COLOR_DEFAULT_BACKGROUND_LOW;
		this.colorBackgroundHigh = COLOR_DEFAULT_BACKGROUND_HIGH;
		this.colorSubTitle = COLOR_DEFAULT_SUBTITLE;
		this.colorTitle = COLOR_DEFAULT_TITLE;
		this.colorTitleShadow = COLOR_DEFAULT_TITLE_SHADOW;
		this.colorGlossVeryLow = COLOR_DEFAULT_GLOSS_VERY_LOW;
		this.colorGlossLow = COLOR_DEFAULT_GLOSS_LOW;
		this.colorGlossHigh = COLOR_DEFAULT_GLOSS_HIGH;
		this.colorDownLine = COLOR_DEFAULT_DOWN_LINE;

		this.imageTitle = defaultImage;

		this.animationAngle = 0.0;
		this.animationSpeedAngle = DEFAULT_SPEED_ANGLE;
		this.animationHeight = 0.0;
		this.animationSpeedHeight = DEFAULT_SPEED_WAVE;
		this.animationUp = false;

		this.playAnimation = true;

		this.stripImage = null;
		this.textShadowImage = null;

		this.stripeWidth = 1.0;
		this.stripeWidthStroke = new BasicStroke((float) stripeWidth);

		this.glossPaintCache = null;
		this.glossPaint2Cache = null;
		this.inverseAffineTransforme = null;

		this.pinstripPaintCache = null;

		this.titleTextLayoutCache = null;
		this.subTitleTextLayoutCache = null;

		calculateSize(height);

		this.animationTimer = new Timer(40, new TimerActionListener());

		this.addComponentListener(new TitleComponentListener());
	}

	private void calculateSize(int height) {
		int width = 0;

		maxImageLength = height - 4;

		if (imageTitle != null) {
			Rectangle2D rect = Toolkit.getImageScale(imageTitle, imageTitle
					.getWidth(null), height - 4);
			maxImageLength = (int) rect.getWidth();
			width += maxImageLength + 8;
		}

		int max = DEFAULT_START_TITLE_X;

		if (title != null && title.length() > 0) {
			TextLayout layout = new TextLayout(title, titleFont,
					new FontRenderContext(null, false, false));
			max = (int) (layout.getBounds().getWidth()) + DEFAULT_START_TITLE_X
					+ DEFAULT_START_TITLE_X;
			startSubTitleY = (float) layout.getBounds().getHeight() / 2.0f
					+ layout.getLeading() + layout.getDescent() + 2;
		}
		if (subTitle != null && subTitle.length() > 0) {
			TextLayout layout = new TextLayout(subTitle, subTitleFont,
					new FontRenderContext(null, false, false));
			max = Math.max(max, (int) (layout.getBounds().getWidth())
					+ DEFAULT_START_SUBTITLE_X + DEFAULT_START_TITLE_X);
		}
		width += max;

		Dimension d = new Dimension(width, height);
		this.setMinimumSize(d);
		this.setPreferredSize(d);
		this.setSize(d);
	}

	public boolean isPlayAnimation() {
		return playAnimation;
	}

	public void setPlayAnimation(boolean playAnimation) {
		this.playAnimation = playAnimation;
		if (animationTimer.isRunning()) {
			animationTimer.stop();
		}
		this.animationAngle = 0.0;
		this.animationHeight = 0.0;
		repaint();
	}

	public void startAnimation() {
		if (playAnimation) {
			animationTimer.start();
		}
	}

	public void stopAnimation() {
		if (playAnimation) {
			animationTimer.stop();
		}
	}

	public boolean isAnimation() {
		return animationTimer.isRunning();
	}

	private void paintGloss(Graphics2D g2) {
		int w = this.getWidth();
		int h = this.getHeight();

		int d = w / DEFAULT_GLOSS_PRECISION;

		if (!playAnimation) {
			animationAngle = 0.0;
			animationHeight = h / 4.0;
		}

		Polygon polygon = new Polygon();
		polygon.addPoint(0, 0);

		int x = 0;
		double h2 = h / 2.0;
		double angle = animationAngle;
		double deltaAngle = 180.0 * DEFAULT_NUMBER_WAVE / d;
		for (int i = 0; i < d; i++) {
			int y = (int) (Utils.cos((int) angle) * animationHeight + h2);

			polygon.addPoint(x, y);

			x += DEFAULT_GLOSS_PRECISION;
			angle += deltaAngle;
			if (angle >= 360.0) {
				angle -= 360.0;
			}
		}
		polygon.addPoint(w - 1,
				(int) (Utils.cos((int) angle) * animationHeight + h2));
		polygon.addPoint(w - 1, 0);

		if (glossPaintCache == null) {
			glossPaintCache = new GradientPaint(0, 0, colorGlossLow, w / 2, 0,
					colorGlossHigh, true);
		}
		g2.setPaint(glossPaintCache);
		g2.fillPolygon(polygon);

		Area areaPolygon = new Area(new Rectangle(0, 0, w - 1, h - 2));
		areaPolygon.subtract(new Area(polygon));

		g2.setStroke(glossStrock);
		g2.setPaint(colorGlossHigh);
		g2.draw(areaPolygon);

		if (inverseAffineTransforme == null) {
			inverseAffineTransforme = new AffineTransform();
			inverseAffineTransforme.translate(0, h);
			inverseAffineTransforme.scale(1.0, -1.0);
		}

		if (glossPaint2Cache == null) {
			glossPaint2Cache = new GradientPaint(0, 0, colorGlossVeryLow,
					w / 2, 0, colorGlossLow, true);
		}
		g2.setPaint(glossPaint2Cache);
		g2.fill(areaPolygon.createTransformedArea(inverseAffineTransforme));
	}

	private void paintPinstrip(Graphics2D g2) {
		int h = this.getHeight();

		g2.setColor(colorBackgroundHigh);
		g2.fillRect(0, 0, this.getWidth(), h);

		double spacing = 3.0;

		if (pinstripPaintCache == null) {
			pinstripPaintCache = new GradientPaint(0, h / 2,
					colorBackgroundLow, 0, h, colorBackgroundHigh, true);
		}
		g2.setPaint(pinstripPaintCache);
		g2.setStroke(stripeWidthStroke);

		spacing += stripeWidth;
		int numLines = (int) ((this.getWidth() + h) / spacing);

		double x = -h;
		for (int i = 0; i < numLines; i++) {
			g2.drawLine((int) x, h, (int) (x + h), 0);
			x += spacing;
		}
	}

	private void paintShadow(Graphics2D g2) {
		int w = this.getWidth();
		int h = this.getHeight();

		// Shadow
		BufferedImage buffer = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2buffer = (Graphics2D) buffer.createGraphics();
		g2buffer.setRenderingHints(hints);

		// Shadow title
		if (title != null && title.length() > 0) {
			float x = DEFAULT_START_TITLE_X;
			float y = (float) h / 2.0f + titleTextLayoutCache.getLeading()
					+ titleTextLayoutCache.getDescent();

			g2buffer.setColor(colorTitleShadow);
			titleTextLayoutCache.draw(g2buffer, x + 2, y + 2);
		}

		// Shadow subtitle
		if (subTitle != null && subTitle.length() > 0) {
			g2buffer.setColor(colorTitleShadow);

			float x = DEFAULT_START_SUBTITLE_X;
			float y = (float) h / 2.0f + startSubTitleY
					+ subTitleTextLayoutCache.getLeading()
					+ subTitleTextLayoutCache.getDescent();
			subTitleTextLayoutCache.draw(g2buffer, x + 2, y + 2);
		}

		g2buffer.dispose();

		g2.drawImage(buffer, blurOp, 0, 0);
	}

	private void paintTitle(Graphics2D g2) {
		if (title != null && title.length() > 0) {
			float x = DEFAULT_START_TITLE_X;
			float y = (float) this.getHeight() / 2.0f
					+ titleTextLayoutCache.getLeading()
					+ titleTextLayoutCache.getDescent();
			g2.setColor(colorTitle);
			titleTextLayoutCache.draw(g2, x, y);
		}
	}

	private void paintSubTitle(Graphics2D g2) {
		if (subTitle != null && subTitle.length() > 0) {
			g2.setColor(colorSubTitle);

			float x = DEFAULT_START_SUBTITLE_X;
			float y = (float) this.getHeight() / 2.0f + startSubTitleY
					+ subTitleTextLayoutCache.getLeading()
					+ subTitleTextLayoutCache.getDescent();
			subTitleTextLayoutCache.draw(g2, x, y);
		}
	}

	private void paintDownLine(Graphics2D g2) {
		int h = this.getHeight();

		g2.setColor(colorDownLine);
		g2.setStroke(lineDownStrock);
		g2.drawLine(0, h - 1, this.getWidth(), h - 1);
	}

	private void paintImageTitle(Graphics2D g2) {
		if (imageTitle != null) {
			Rectangle2D rect = Toolkit.getImageScale(imageTitle,
					maxImageLength, this.getHeight() - 4);

			g2.drawImage(imageTitle, (int) (this.getWidth() - maxImageLength
					+ rect.getX() - 4), (int) (rect.getY() + 2), (int) rect
					.getWidth(), (int) rect.getHeight(), null);
		}
	}

	private Image createStripImage() {
		BufferedImage buffer = new BufferedImage(this.getWidth(), this
				.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2buffer = (Graphics2D) buffer.createGraphics();
		g2buffer.setRenderingHints(hints);
		paintPinstrip(g2buffer);
		g2buffer.dispose();

		return buffer;
	}

	private Image createTextShadowImage() {
		BufferedImage buffer = new BufferedImage(this.getWidth(), this
				.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2buffer = (Graphics2D) buffer.createGraphics();
		g2buffer.setRenderingHints(hints);

		FontRenderContext context = g2buffer.getFontRenderContext();
		if (title != null && title.length() > 0 && titleTextLayoutCache == null) {
			titleTextLayoutCache = new TextLayout(title, titleFont, context);
		}
		if (subTitle != null && subTitle.length() > 0
				&& subTitleTextLayoutCache == null) {
			subTitleTextLayoutCache = new TextLayout(subTitle, subTitleFont,
					context);
		}

		paintShadow(g2buffer);
		paintTitle(g2buffer);
		paintSubTitle(g2buffer);
		paintImageTitle(g2buffer);
		paintDownLine(g2buffer);
		g2buffer.dispose();

		return buffer;
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHints(hints);

		if (stripImage == null) {
			stripImage = createStripImage();
		}
		g2.drawImage(stripImage, 0, 0, null);

		paintGloss(g2);

		if (textShadowImage == null) {
			textShadowImage = createTextShadowImage();
		}
		g2.drawImage(textShadowImage, 0, 0, null);
	}

	private static Kernel getBlurKernel(int blurSize) {
		if (blurSize <= 0)
			return null;

		int size = blurSize * blurSize;
		float coeff = 1.0f / size;
		float[] kernelData = new float[size];

		for (int i = 0; i < size; i++)
			kernelData[i] = coeff;

		return new Kernel(blurSize, blurSize, kernelData);
	}

	public Image getImageTitle() {
		return imageTitle;
	}

	public void setImageTitle(Image imageTitle) {
		this.imageTitle = imageTitle;
		textShadowImage = null;

		calculateSize(this.getPreferredSize().height);

		cleanCache();

		revalidate();
		repaint();
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
		textShadowImage = null;
		calculateSize(this.getPreferredSize().height);

		cleanCache();

		revalidate();
		repaint();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		textShadowImage = null;
		calculateSize(this.getPreferredSize().height);

		cleanCache();

		revalidate();
		repaint();
	}

	public void setColorBackgroundHigh(Color colorBackgroundHigh) {
		this.colorBackgroundHigh = colorBackgroundHigh;
		cleanCache();
		repaint();
	}

	public void setColorBackgroundLow(Color colorBackgroundLow) {
		this.colorBackgroundLow = colorBackgroundLow;
		cleanCache();
		repaint();
	}

	public Color getColorDownLine() {
		return colorDownLine;
	}

	public void setColorDownLine(Color colorDownLine) {
		this.colorDownLine = colorDownLine;
		cleanCache();
		repaint();
	}

	public Color getColorGlossHigh() {
		return colorGlossHigh;
	}

	public void setColorGlossHigh(Color colorGlossHigh) {
		this.colorGlossHigh = colorGlossHigh;
		cleanCache();
		repaint();
	}

	public Color getColorGlossLow() {
		return colorGlossLow;
	}

	public void setColorGlossLow(Color colorGlossLow) {
		this.colorGlossLow = colorGlossLow;
		cleanCache();
		repaint();
	}

	public Color getColorGlossVeryLow() {
		return colorGlossVeryLow;
	}

	public void setColorGlossVeryLow(Color colorGlossVeryLow) {
		this.colorGlossVeryLow = colorGlossVeryLow;
		cleanCache();
		repaint();
	}

	public Color getColorSubTitle() {
		return colorSubTitle;
	}

	public void setColorSubTitle(Color colorSubTitle) {
		this.colorSubTitle = colorSubTitle;
		cleanCache();
		repaint();
	}

	public Color getColorTitle() {
		return colorTitle;
	}

	public void setColorTitle(Color colorTitle) {
		this.colorTitle = colorTitle;
	}

	public Color getColorTitleShadow() {
		return colorTitleShadow;
	}

	public void setColorTitleShadow(Color colorTitleShadow) {
		this.colorTitleShadow = colorTitleShadow;
		cleanCache();
		repaint();
	}

	public Color getColorBackgroundHigh() {
		return colorBackgroundHigh;
	}

	public Color getColorBackgroundLow() {
		return colorBackgroundLow;
	}

	public static Image getDefaultImage() {
		return defaultImage;
	}

	public static void setDefaultImage(Image defaultImage) {
		JTitle.defaultImage = defaultImage;
	}

	private void cleanCache() {
		glossPaintCache = null;
		glossPaint2Cache = null;
		inverseAffineTransforme = null;
		pinstripPaintCache = null;

		titleTextLayoutCache = null;
		subTitleTextLayoutCache = null;

		stripImage = null;
		textShadowImage = null;
	}

	private final class TimerActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int h = getHeight();

			animationAngle += animationSpeedAngle;
			if (animationAngle >= 360.0) {
				animationAngle -= 360.0;
			}

			if (animationUp)
				animationHeight += h * animationSpeedHeight;
			else
				animationHeight -= h * animationSpeedHeight;
			if (animationHeight > h / 4.0 || animationHeight < -h / 4.0) {
				animationUp = !animationUp;
			}
			repaint();
		}
	}

	private final class TitleComponentListener extends ComponentAdapter {

		public void componentResized(ComponentEvent e) {
			cleanCache();
		}
	}
}
