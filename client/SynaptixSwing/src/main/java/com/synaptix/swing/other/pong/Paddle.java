package com.synaptix.swing.other.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Paddle {
	private int x, y, vel, width, height;

	private Point pos;

	public Paddle() {
		x = 0;
		y = 0;
		vel = 0;
		width = 10;
		height = 110;
		pos = new Point(0, 0);
	}

	public void setPosition(Point position) {
		pos = position;
		x = pos.x;
		y = pos.y;
	}

	public void setPosition(int xpos, int ypos) {
		x = xpos;
		y = ypos;
		pos.setLocation(x, y);
	}

	public void setVelocity(int v) {
		vel = v;
	}

	public int getVelocity() {
		return vel;
	}

	public Point getPosition() {
		return pos;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void paint(Graphics aPen) {
		aPen.setColor(new Color(255, 255, 255));
		aPen.fillRect(x - width / 2, y - height / 2, width, height);
	}
}