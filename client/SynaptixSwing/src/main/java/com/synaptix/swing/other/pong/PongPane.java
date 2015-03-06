package com.synaptix.swing.other.pong;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class PongPane extends JPanel {

	private static final long serialVersionUID = -2747987662422814121L;

	private int width, height;
	
	private PongGame game;

	public PongPane(int w, int h, PongGame g) {
		super();
		width = w;
		height = h;
		game = g;
		super.setSize(width, height);
	}

	public void setSize(int w, int h) {
		super.setSize(w, h);
		width = w;
		height = h;
	}

	public void paintComponent(Graphics aPen) {
		aPen.setColor(new Color(0, 0, 0));
		aPen.fillRect(0, 0, width, height);
		game.paint(aPen);
	}
}