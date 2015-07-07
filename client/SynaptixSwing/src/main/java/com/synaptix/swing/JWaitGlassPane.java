package com.synaptix.swing;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class JWaitGlassPane extends JPanel {

	private static final long serialVersionUID = 832328991458849283L;

	public static final int TYPE_DIRECTION_LEFT_TO_RIGHT = 1;

	public static final int TYPE_DIRECTION_RIGHT_TO_LEFT = 2;

	public static final int TYPE_DIRECTION_PING_PONG = 3;

	public static final Image ICON_CLIENT;

	public static final Image ICON_DATABASE_SERVER;

	public static final Image ICON_SERVER;

	public static final Image ICON_PDF_SERVER;

	private static final float FPS = 5.0f;

	private static final String TEXT_DEFAULT = SwingMessages.getString("JWaitGlassPane.0"); //$NON-NLS-1$

	private static final AlphaComposite compositeHalf;

	private static final AlphaComposite compositeAll;

	private static final RenderingHints hints;

	public static final Color COLOR_DEFAULT_BIG;

	public static final Color COLOR_DEFAULT_HALF;

	public static final Color COLOR_DEFAULT_NORMAL;

	private int nbFigure;

	private int currentCycle;

	private int direction;

	private int typeDirection;

	private Image iconLeft;

	private Image iconRight;

	private Color colorBig;

	private Color colorHalf;

	private Color colorNormal;

	private String text;

	private boolean viewK2000;

	private Timer animatorTimer;

	static {
		ICON_CLIENT = new ImageIcon(JWaitGlassPane.class.getResource("/images/client.png")).getImage(); //$NON-NLS-1$
		ICON_DATABASE_SERVER = new ImageIcon(JWaitGlassPane.class.getResource("/images/database.png")).getImage(); //$NON-NLS-1$
		ICON_SERVER = new ImageIcon(JWaitGlassPane.class.getResource("/images/server.png")).getImage(); //$NON-NLS-1$
		ICON_PDF_SERVER = new ImageIcon(JWaitGlassPane.class.getResource("/images/mimepdf.png")).getImage(); //$NON-NLS-1$

		compositeHalf = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		compositeAll = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);

		hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		COLOR_DEFAULT_BIG = new Color(0, 0, 127);
		COLOR_DEFAULT_HALF = new Color(100, 100, 255);
		COLOR_DEFAULT_NORMAL = new Color(220, 220, 255);
	}

	public JWaitGlassPane() {
		this(TYPE_DIRECTION_PING_PONG, TEXT_DEFAULT, ICON_CLIENT, ICON_DATABASE_SERVER);
	}

	public JWaitGlassPane(final int type) {
		this(type, TEXT_DEFAULT, ICON_CLIENT, ICON_DATABASE_SERVER);
	}

	public JWaitGlassPane(final String text) {
		this(TYPE_DIRECTION_PING_PONG, text, ICON_CLIENT, ICON_DATABASE_SERVER);
	}

	public JWaitGlassPane(final Image iconLeft, final Image iconRight) {
		this(TYPE_DIRECTION_PING_PONG, "", iconLeft, iconRight); //$NON-NLS-1$
	}

	public JWaitGlassPane(final int typeDirection, final String text, final Image iconLeft, final Image iconRight) {
		super();
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		this.setFocusable(true);

		this.nbFigure = 5;
		this.typeDirection = typeDirection;
		switch (typeDirection) {
		case TYPE_DIRECTION_LEFT_TO_RIGHT:
			this.currentCycle = 0;
			this.direction = 1;
			break;
		case TYPE_DIRECTION_RIGHT_TO_LEFT:
			this.currentCycle = nbFigure - 1;
			this.direction = -1;
			break;
		case TYPE_DIRECTION_PING_PONG:
			this.currentCycle = 0;
			this.direction = 1;
			break;
		}

		this.text = text;
		this.iconLeft = iconLeft;
		this.iconRight = iconRight;

		this.colorBig = COLOR_DEFAULT_BIG;
		this.colorHalf = COLOR_DEFAULT_HALF;
		this.colorNormal = COLOR_DEFAULT_NORMAL;

		this.viewK2000 = true;

		this.animatorTimer = new AnimatorTimer();

		this.addMouseListener(new GlassPaneMouseListener());
		this.addMouseMotionListener(new GlassPaneMouseMotionListener());
		this.addFocusListener(new GlassPaneFocusListener());

		this.setBackground(Color.WHITE);

		this.setVisible(false);
	}

	public void setVisible(final boolean v) {
		if (v)
			this.requestFocus();
		super.setVisible(v);
	}

	public Image getIconLeft() {
		return iconLeft;
	}

	public void setIconLeft(final Image iconLeft) {
		this.iconLeft = iconLeft;
	}

	public Image getImageRight() {
		return iconRight;
	}

	public void setIconRight(final Image iconRight) {
		this.iconRight = iconRight;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setTypeDirection(final int typeDirection) {
		this.typeDirection = typeDirection;
		switch (typeDirection) {
		case TYPE_DIRECTION_LEFT_TO_RIGHT:
			this.currentCycle = 0;
			this.direction = 1;
			break;
		case TYPE_DIRECTION_RIGHT_TO_LEFT:
			this.currentCycle = nbFigure - 1;
			this.direction = -1;
			break;
		case TYPE_DIRECTION_PING_PONG:
			this.currentCycle = 0;
			this.direction = 1;
			break;
		}
	}

	public int getTypeDirection() {
		return typeDirection;
	}

	public boolean isViewK2000() {
		return viewK2000;
	}

	public void setViewK2000(final boolean viewK2000) {
		this.viewK2000 = viewK2000;
	}

	public void start() {
		start(true);
	}

	public void start(final boolean useAnimator) {
		if (useAnimator) {
			animatorTimer.start();
		}
		this.setVisible(true);
	}

	public void stop() {
		if (animatorTimer.isRunning())
			animatorTimer.stop();
		this.setVisible(false);
	}

	protected Area buildFigure(final int typeDirection, final int direction, final int currentCycle, final int currentPosition, final int width, final int height) {
		Area area = null;

		// Arrow right
		Polygon polyArrowRight = new Polygon();
		polyArrowRight.addPoint(0, 0);
		polyArrowRight.addPoint(width, height / 2);
		polyArrowRight.addPoint(0, height);
		polyArrowRight.addPoint(width / 4, height / 2);

		// Arrow left
		Polygon polyArrowLeft = new Polygon();
		polyArrowLeft.addPoint(width, 0);
		polyArrowLeft.addPoint(0, height / 2);
		polyArrowLeft.addPoint(width, height);
		polyArrowLeft.addPoint(width - width / 4, height / 2);

		switch (typeDirection) {
		case TYPE_DIRECTION_LEFT_TO_RIGHT:
			area = new Area(polyArrowRight);
			break;
		case TYPE_DIRECTION_RIGHT_TO_LEFT:
			area = new Area(polyArrowLeft);
			break;
		case TYPE_DIRECTION_PING_PONG:
			switch (direction) {
			case -1:
				if (currentPosition >= currentCycle)
					area = new Area(polyArrowLeft);
				else
					area = new Area(polyArrowRight);
				break;
			case 1:
				if (currentPosition <= currentCycle)
					area = new Area(polyArrowRight);
				else
					area = new Area(polyArrowLeft);
				break;
			}
			// Ellipse2D.Double eclipse = new Ellipse2D.Double(0, 0, width,
			// height);
			// area = new Area(eclipse);
			break;
		}
		return area;
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHints(hints);
		g2.setComposite(compositeHalf);

		int width = getWidth();
		int height = getHeight();

		float stroke = 3.0f;

		int widthHeightImage = 150;
		if (width < widthHeightImage * 3) {
			widthHeightImage = width / 3;
			stroke = 1.0f;
		}

		int betwenIcon = widthHeightImage * 2;

		int heightAnimation = widthHeightImage / 4;
		if (text == null || text.length() == 0)
			heightAnimation = 0;
		int betwenFigure = (betwenIcon - widthHeightImage) / nbFigure;

		g2.setPaint(this.getBackground());
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());

		g2.setComposite(compositeAll);

		int y1 = height / 2 - heightAnimation - widthHeightImage / 2;

		int x1 = width / 2 - betwenIcon / 2 - widthHeightImage / 2;
		int x2 = width / 2 + betwenIcon / 2 - widthHeightImage / 2;

		// Affichage des 2 icones
		if (iconLeft != null) {
			g2.drawImage(iconLeft, x1, y1, widthHeightImage, widthHeightImage, null);
		}
		if (iconRight != null) {
			g2.drawImage(iconRight, x2, y1, widthHeightImage, widthHeightImage, null);
		}

		// Affichage des fléches
		if (viewK2000) {
			int x4 = width / 2 - betwenIcon / 2 + widthHeightImage / 2 + betwenFigure / 2;
			for (int i = 0; i < nbFigure; i++) {
				int rayon = betwenFigure / 2;
				if (i == currentCycle) {
					g2.setPaint(colorBig);
					rayon += (rayon * 50) / 100;
				} else if ((i == currentCycle - 1) || (i == currentCycle + 1)) {
					g2.setPaint(colorHalf);
					rayon += (rayon * 25) / 100;
				} else if (typeDirection != TYPE_DIRECTION_PING_PONG && ((currentCycle == 0 && i == nbFigure - 1) || (currentCycle == nbFigure - 1 && i == 0))) {
					g2.setPaint(colorHalf);
					rayon += (rayon * 20) / 100;
				} else {
					g2.setPaint(colorNormal);
				}

				Area area = buildFigure(typeDirection, direction, currentCycle, i, rayon, rayon);

				double y2 = height / 2 - heightAnimation - rayon / 2;
				double x3 = x4 + i * betwenFigure - rayon / 2;
				AffineTransform at = AffineTransform.getTranslateInstance(x3, y2);
				area.transform(at);

				g2.fill(area);

				// g2.setStroke(new BasicStroke(stroke));
				// g2.setPaint(Color.BLACK);
				// g2.draw(area);
			}
		}

		// Affichage du text
		if (text != null && text.length() > 0) {
			FontRenderContext context = g2.getFontRenderContext();
			Font font = this.getFont().deriveFont(Font.BOLD, widthHeightImage / 5);
			FontMetrics fm = g2.getFontMetrics(font);
			String temp = new String(text);
			while (fm.stringWidth(temp) >= width) {
				temp = temp.substring(0, temp.length() - 1);
			}
			if (!temp.equals(text)) {
				temp = temp.substring(0, temp.length() - 3);
				temp += "..."; //$NON-NLS-1$
			}

			TextLayout layout = new TextLayout(temp, font, context);
			Rectangle2D bounds = layout.getBounds();
			g2.setColor(Color.BLACK);

			// TODO a modifier, le centre du text est au niveau du baseline
			float y3 = height / 2 + heightAnimation + layout.getAscent();
			layout.draw(g2, (float) (width - bounds.getWidth()) / 2, y3);
		}

		// Line milieu
		// g2.setStroke(new BasicStroke(2.0f));
		// g2.setColor(Color.RED);
		// g2.drawLine(0,height/2,width,height/2);
	}

	private class AnimatorTimer extends Timer implements ActionListener {
		private static final long serialVersionUID = 1L;

		public AnimatorTimer() {
			super((int) (1000 / FPS), null);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			switch (typeDirection) {
			case TYPE_DIRECTION_LEFT_TO_RIGHT:
				currentCycle++;
				if (currentCycle >= nbFigure)
					currentCycle = 0;
				break;
			case TYPE_DIRECTION_RIGHT_TO_LEFT:
				currentCycle--;
				if (currentCycle < 0)
					currentCycle = nbFigure - 1;
				break;
			case TYPE_DIRECTION_PING_PONG:
				currentCycle += direction;
				if (currentCycle == nbFigure - 1 || currentCycle == 0)
					direction = -direction;
				break;
			}
			repaint();
		}
	}

	private class GlassPaneMouseListener extends MouseAdapter {
	}

	private class GlassPaneMouseMotionListener extends MouseMotionAdapter {
	}

	private class GlassPaneFocusListener extends FocusAdapter {
		public void focusLost(FocusEvent e) {
			if (JWaitGlassPane.this.isVisible())
				JWaitGlassPane.this.requestFocus();
		}
	}
}
