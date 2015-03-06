package com.synaptix.swing.other.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Ball {
	
	private Point pos;
	
	private double x, y, xvel, yvel;
	
	private int r;

	public Ball() {
		pos = new Point(0, 0);
		x = 0;
		y = 0;
		xvel = 0.0;
		yvel = 0.0;
		r = 10;
	}

	public void setPosition(Point position) {
		pos = position;
		x = pos.x;
		y = pos.y;
	}

	public void setPosition(double xpos, double ypos) {
		x = xpos;
		y = ypos;
		pos.setLocation((int) (x + 0.5), (int) (y + 0.5));
	}

	public void paint(Graphics aPen) {
		aPen.setColor(new Color(255, 255, 255));
		aPen.fillRect(pos.x - r, pos.y - r, 2 * r, 2 * r);
	}

	public void setVelocity(double xv, double yv) {
		xvel = xv;
		yvel = yv;
	}

	public double getXVelocity() {
		return xvel;
	}

	public double getYVelocity() {
		return yvel;
	}

	public void move() {
		x += xvel;
		y += yvel;
		pos.setLocation(x, y);
	}

	public Point getPosition() {
		return pos;
	}

	public int getRadius() {
		return r;
	}
}