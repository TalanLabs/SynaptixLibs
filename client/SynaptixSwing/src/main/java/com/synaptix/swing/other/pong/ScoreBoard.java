package com.synaptix.swing.other.pong;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

public class ScoreBoard {
	
	private int player1, player2;

	public ScoreBoard() {
		player1 = 0;
		player2 = 0;
	}

	public void clearScore() {
		player1 = 0;
		player2 = 0;
	}

	public void player1Score() {
		player1 += 1;
	}

	public void player2Score() {
		player2 += 1;
	}

	public void paint(Graphics aPen, Point pos) {
		String score = "Player 1: " + player1 + "  Player 2: " + player2; //$NON-NLS-1$ //$NON-NLS-2$
		aPen.setColor(new Color(255, 255, 255));
		aPen.setFont(new Font("Chicago", Font.PLAIN, 12)); //$NON-NLS-1$
		aPen.drawString(score, pos.x, pos.y);
	}
}