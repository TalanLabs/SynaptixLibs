package com.synaptix.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.Timer;

public class JTriangleWait extends JComponent implements IAnimationComponent {

	private static final long serialVersionUID = -4216494310085850301L;

	private static final int FPS = 10;

	private static final int HEIGHT = 32;

	private Timer animatorTimer;

	private int nbTriangle;

	private int currentCycle;

	private int traineLenght;

	private Color traineColor;

	private Color[] traineColors;

	private Color[] userTraineColors;

	private int waitHeight;

	private boolean fullComponent;

	public JTriangleWait() {
		this(FPS, false);
	}

	public JTriangleWait(int fps, boolean fullComponent) {
		super();

		this.fullComponent = fullComponent;

		this.traineColor = Color.black;
		this.nbTriangle = 8;
		this.traineLenght = 5;
		this.currentCycle = 0;
		this.waitHeight = HEIGHT;
		this.userTraineColors = null;
		this.animatorTimer = new AnimatorTimer(fps);

		buildTraineColors();
	}

	private void buildTraineColors() {
		traineColors = new Color[this.traineLenght];
		for (int i = 0; i < traineLenght; i++) {
			int alpha = (int) (traineColor.getAlpha() * ((double) (traineLenght - i) / (double) traineLenght));
			traineColors[i] = new Color(traineColor.getRed(), traineColor
					.getGreen(), traineColor.getBlue(), alpha);
		}
	}

	/**
	 * Couleur du premiere cercle
	 * 
	 * @return
	 */
	public Color getTraineColor() {
		return traineColor;
	}

	/**
	 * Couleur du premiere cercle
	 * 
	 * @param traineColor
	 */
	public void setTraineColor(Color traineColor) {
		this.traineColor = traineColor;
		buildTraineColors();
		repaint();
	}

	/**
	 * Nombre de triangle
	 * 
	 * @return
	 */
	public int getNbTriangle() {
		return nbTriangle;
	}

	/**
	 * Nombre de triangle
	 * 
	 * @param nbTriangle
	 */
	public void setNbTriangle(int nbTriangle) {
		this.nbTriangle = nbTriangle;
		repaint();
	}

	/**
	 * Longueur de la traine en nombre de cercle
	 * 
	 * @return
	 */
	public int getTraineLenght() {
		return traineLenght;
	}

	/**
	 * Longueur de la traine en nombre de cercle
	 * 
	 * @param traineLenght
	 */
	public void setTraineLenght(int traineLenght) {
		this.traineLenght = traineLenght;
		buildTraineColors();
		repaint();
	}

	/**
	 * Trainé de couleur fournit
	 * 
	 * @return
	 */
	public Color[] getUserTraineColors() {
		return userTraineColors;
	}

	/**
	 * Trainé de couleur fournit
	 * 
	 * @param userTraineColors
	 */
	public void setUserTraineColors(Color[] userTraineColors) {
		this.userTraineColors = userTraineColors;
	}

	public int getWaitHeight() {
		return waitHeight;
	}

	public void setWaitHeight(int waitHeight) {
		this.waitHeight = waitHeight;
		repaint();
		revalidate();
	}

	public Component getComponent() {
		return this;
	}

	/**
	 * Demarre l'animation
	 */
	public void start() {
		animatorTimer.start();
	}

	/**
	 * Arrete l'animation
	 */
	public void stop() {
		if (animatorTimer.isRunning()) {
			animatorTimer.stop();
		}
	}

	public Dimension getPreferredSize() {
		Dimension d = new Dimension(waitHeight, waitHeight);
		return d;
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Color[] colors = userTraineColors != null
				&& userTraineColors.length > 0 ? userTraineColors
				: traineColors;

		int finCycle = (currentCycle - (traineLenght - 1));
		int rayon = Math.min(this.getHeight(), this.getWidth()) / 2;

		double delta = Math.PI * 2 / nbTriangle;
		double angle = 0;
		for (int i = 0; i < nbTriangle; i++) {
			if ((i <= currentCycle && i >= finCycle)
					|| (i > currentCycle && i - nbTriangle >= finCycle)) {
				int current = i <= currentCycle ? currentCycle - i
						: (nbTriangle - i) + currentCycle;
				current = current >= 0 && current < colors.length ? current : 0;
				Color c = colors[current];
				paintTriangle(g2, c, rayon, angle, delta);
			}
			angle += delta;
		}
	}

	private void paintTriangle(Graphics2D g2, Color color, int rayon,
			double angle, double deltaAngle) {
		int dh = fullComponent ? rayon : waitHeight / 2;

		int dx = fullComponent ? this.getWidth() / 2 - rayon : 0;
		int dy = fullComponent ? this.getHeight() / 2 - rayon : 0;

		double yy = Math.tan(deltaAngle / 2.0) * rayon;

		double rr = rayon - yy * 2;

		Arc2D grand = new Arc2D.Double(-rayon, -rayon, rayon * 2, rayon * 2,
				180 - Math.toDegrees(deltaAngle) / 2 + 2, Math
						.toDegrees(deltaAngle) - 2, Arc2D.PIE);
		Ellipse2D petit = new Ellipse2D.Double(-rr, -rr, rr * 2, rr * 2);

		Area a = new Area(grand);
		a.subtract(new Area(petit));

		AffineTransform af = new AffineTransform();
		af.translate(dx, dy);
		af.translate(dh, dh);
		af.rotate(angle);

		g2.setColor(color);
		g2.fill(a.createTransformedArea(af));
	}

	private class AnimatorTimer extends Timer implements ActionListener {

		private static final long serialVersionUID = 1L;

		public AnimatorTimer(int fps) {
			super((int) (1000 / fps), null);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			currentCycle++;
			if (currentCycle >= nbTriangle) {
				currentCycle = 0;
			}
			repaint();
		}
	}
}
