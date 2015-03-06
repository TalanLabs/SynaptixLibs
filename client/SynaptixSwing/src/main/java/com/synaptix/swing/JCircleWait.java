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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.Timer;

public class JCircleWait extends JComponent implements IAnimationComponent {

	private static final long serialVersionUID = -4216494310085850301L;

	private static final int FPS = 10;

	private static final int HEIGHT = 32;

	private Timer animatorTimer;

	private int nbCircle;

	private int currentCycle;

	private int circleSize;

	private int traineLenght;

	private int deltaEcartement;

	private Color traineColor;

	private Color[] traineColors;

	private Color[] userTraineColors;

	private int waitHeight;

	private boolean fullComponent;

	public JCircleWait() {
		this(FPS, false);
	}

	public JCircleWait(int fps, boolean fullComponent) {
		super();

		this.fullComponent = fullComponent;

		this.traineColor = Color.black;
		this.nbCircle = 8;
		this.traineLenght = 5;
		this.circleSize = 8;
		this.currentCycle = 0;
		this.deltaEcartement = 0;
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
	 * Nombre de cercle
	 * 
	 * @return
	 */
	public int getNbCircle() {
		return nbCircle;
	}

	/**
	 * Nombre de cercle
	 * 
	 * @param nbCircle
	 */
	public void setNbCircle(int nbCircle) {
		this.nbCircle = nbCircle;
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
	 * Taille d'un cercle
	 * 
	 * @return
	 */
	public int getCircleSize() {
		return circleSize;
	}

	/**
	 * Taille d'un cercle
	 * 
	 * @param circleSize
	 */
	public void setCircleSize(int circleSize) {
		this.circleSize = circleSize;
		repaint();
	}

	/**
	 * Ecartement entre le centre et le cercle
	 * 
	 * @return
	 */
	public int getDeltaEcartement() {
		return deltaEcartement;
	}

	/**
	 * Ecartement entre le centre et le cercle
	 * 
	 * @param deltaEcartement
	 */
	public void setDeltaEcartement(int deltaEcartement) {
		this.deltaEcartement = deltaEcartement;
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

		double delta = Math.PI * 2 / nbCircle;
		double angle = 0;
		for (int i = 0; i < nbCircle; i++) {
			if ((i <= currentCycle && i >= finCycle)
					|| (i > currentCycle && i - nbCircle >= finCycle)) {
				int current = i <= currentCycle ? currentCycle - i
						: (nbCircle - i) + currentCycle;
				current = current >= 0 && current < colors.length ? current : 0;
				Color c = colors[current];
				paintCircle(
						g2,
						c,
						(int) (circleSize * (double) (colors.length - current) / (double) colors.length),
						angle);
			}
			angle += delta;
		}
	}

	private void paintCircle(Graphics2D g2, Color color, int size, double angle) {
		int n = Math.min(this.getHeight(), this.getWidth());
		int dh = fullComponent ? n / 2 : waitHeight / 2;
		int dc = fullComponent ? n / nbCircle : circleSize / 2;

		int dx = fullComponent ? (this.getWidth() - n) / 2 : 0;
		int dy = fullComponent ? (this.getHeight() - n) / 2 : 0;

		Ellipse2D.Float e = new Ellipse2D.Float(dh - 2 * dc - deltaEcartement,
				-dc, size, size);

		AffineTransform af = new AffineTransform();
		af.translate(dx, dy);
		af.translate(dh, dh);
		af.rotate(angle);

		g2.setColor(color);
		g2.fill(new Area(e).createTransformedArea(af));
	}

	private class AnimatorTimer extends Timer implements ActionListener {

		private static final long serialVersionUID = 1L;

		public AnimatorTimer(int fps) {
			super((int) (1000 / fps), null);
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			currentCycle++;
			if (currentCycle >= nbCircle) {
				currentCycle = 0;
			}
			repaint();
		}
	}
}
