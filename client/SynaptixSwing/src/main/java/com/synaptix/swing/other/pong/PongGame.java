package com.synaptix.swing.other.pong;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;

public class PongGame {

	private int width, height;

	private Ball ball;

	private Paddle player1, player2;

	private ScoreBoard score;

	private boolean paused, hasStarted;

	public PongGame(int w, int h) {
		paused = false;
		hasStarted = false;
		width = w - 40;
		height = h - 40;
		ball = new Ball();
		ball.setPosition(new Point(width / 2, height / 2));
		randomizeBall();
		player1 = new Paddle();
		player2 = new Paddle();
		resetPaddles();
		score = new ScoreBoard();
	}

	public void update() {
		if ((!paused) && (hasStarted)) {
			ball.move();
			movePaddles();
			checkCollisions();
		}
	}

	private void randomizeBall() {
		Random rnd = new Random();
		boolean vert = rnd.nextBoolean();
		boolean horiz = rnd.nextBoolean();
		double xvel, yvel;

		if (vert)
			yvel = 1;
		else
			yvel = -1;

		if (horiz)
			xvel = 1;
		else
			xvel = -1;

		ball.setVelocity(xvel, yvel);
	}

	private void resetPaddles() {
		player1.setPosition(15, height / 2);
		player2.setPosition(width - 15, height / 2);
	}

	private void checkCollisions() {
		Point pos = ball.getPosition();
		double xvel = ball.getXVelocity();
		double yvel = ball.getYVelocity();
		int r = ball.getRadius();

		// wall collisions
		if ((pos.x <= r) || (pos.x >= width - r)) {
			hasStarted = false;
			if (pos.x <= r)
				score.player2Score();
			else
				score.player1Score();
			ball.setPosition(new Point(width / 2, height / 2));
			randomizeBall();
			return;
		}
		if ((pos.y <= r) || (pos.y >= height - r))
			yvel *= -1;

		// paddle collisions
		// player 1
		Point p1pos = player1.getPosition();
		int w = player1.getWidth();
		int h = player1.getHeight();
		int xposdiff = (pos.x - r) - (p1pos.x + w / 2);
		if ((xposdiff <= 0) && (xposdiff >= -5)
				&& (pos.y + r >= p1pos.y - h / 2)
				&& (pos.y - r <= p1pos.y + h / 2)) {
			xvel *= -1;
			double vel = Math.sqrt((xvel * xvel) + (yvel * yvel));
			double angle = Math.atan2(yvel, xvel);
			vel = vel + 0.2;
			xvel = vel * Math.cos(angle);
			yvel = vel * Math.sin(angle);
		}

		// player 2
		Point p2pos = player2.getPosition();
		w = player2.getWidth();
		h = player2.getHeight();
		xposdiff = (pos.x + r) - (p2pos.x - w / 2);
		if ((xposdiff >= 0) && (xposdiff <= 5)
				&& (pos.y + r >= p2pos.y - h / 2)
				&& (pos.y - r <= p2pos.y + h / 2)) {
			xvel *= -1;
			double vel = Math.sqrt((xvel * xvel) + (yvel * yvel));
			double angle = Math.atan2(yvel, xvel);
			vel = vel + 0.5;
			xvel = vel * Math.cos(angle);
			yvel = vel * Math.sin(angle);
		}

		// set the velocity back
		ball.setVelocity(xvel, yvel);
	}

	private void movePaddles() {
		// player 1
		Point pos = player1.getPosition();
		int vel = player1.getVelocity();
		int h = player1.getHeight();

		if ((vel > 0) && (pos.y + h / 2 < height))
			pos.y += vel;
		if ((vel < 0) && (pos.y - h / 2 > 0))
			pos.y += vel;

		player1.setPosition(pos);

		// player 2
		pos = player2.getPosition();
		vel = player2.getVelocity();
		h = player2.getHeight();

		if ((vel > 0) && (pos.y + h / 2 < height))
			pos.y += vel;
		if ((vel < 0) && (pos.y - h / 2 > 0))
			pos.y += vel;

		player2.setPosition(pos);
	}

	public void setP1Move(int dir) {
		if (dir < 0)
			player1.setVelocity(-2);
		else if (dir > 0)
			player1.setVelocity(2);
		else
			player1.setVelocity(0);
	}

	public void setP2Move(int dir) {
		if (dir < 0)
			player2.setVelocity(-2);
		else if (dir > 0)
			player2.setVelocity(2);
		else
			player2.setVelocity(0);
	}

	private void togglePaused() {
		if (paused)
			paused = false;
		else
			paused = true;
	}

	private void toggleHasStarted() {
		if (hasStarted)
			hasStarted = false;
		else
			hasStarted = true;
	}

	public void spaceBarPressed() {
		if (hasStarted)
			togglePaused();
		else
			toggleHasStarted();
	}

	public void paint(Graphics aPen) {
		score.paint(aPen, new Point(20, 15));
		aPen.translate(20, 20);
		aPen.setColor(new Color(255, 255, 255));
		aPen.drawRect(-1, -1, width + 1, height + 1);
		ball.paint(aPen);
		player1.paint(aPen);
		player2.paint(aPen);

		if (paused) {
			aPen.setColor(new Color(255, 255, 255));
			aPen.fillRect(width / 2 - 50, height / 2 - 20, 101, 41);
			aPen.setColor(new Color(0, 0, 0));
			aPen.drawRect(width / 2 - 46, height / 2 - 16, 92, 32);
			aPen.setFont(new Font("Chicago", Font.PLAIN, 24)); //$NON-NLS-1$
			aPen.drawString("Paused", width / 2 - 41, height / 2 + 8); //$NON-NLS-1$
		}

		if (!hasStarted) {
			aPen.setColor(new Color(255, 255, 255));
			aPen.fillRect(width / 2 - 124, height / 2 - 20, 249, 41);
			aPen.setColor(new Color(0, 0, 0));
			aPen.drawRect(width / 2 - 120, height / 2 - 16, 240, 32);
			aPen.setFont(new Font("Chicago", Font.PLAIN, 24)); //$NON-NLS-1$
			aPen.drawString("Press Space To Start", width / 2 - 114, //$NON-NLS-1$
					height / 2 + 8);
		}
	}
}