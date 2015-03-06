package com.synaptix.swing.other.pong;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.Timer;

import com.synaptix.swing.utils.GUIWindow;

public class Pong extends JDialog {

	private static final long serialVersionUID = 6457547469979659831L;

	static int width = 640, height = 480;

	// stuff for menus
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem exitItem;
	JMenu helpMenu;
	JMenuItem keysItem;
	JMenuItem aboutItem;

	// game stuff
	PongPane mainPane;
	PongGame mainGame;
	Timer aTimer;

	public Pong() {
		super(GUIWindow.getActiveWindow(), "Pong"); //$NON-NLS-1$
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		getContentPane().setLayout(null);
		setSize(width + 8, height + 56);

		// menus
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		fileMenu = new JMenu("File"); //$NON-NLS-1$
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		exitItem = new JMenuItem("Exit"); //$NON-NLS-1$
		exitItem.setMnemonic('x');
		exitItem.addActionListener(new ExitMenuItemListener());
		fileMenu.add(exitItem);

		helpMenu = new JMenu("Help"); //$NON-NLS-1$
		helpMenu.setMnemonic('H');
		menuBar.add(helpMenu);

		keysItem = new JMenuItem("Keys"); //$NON-NLS-1$
		keysItem.setMnemonic('K');
		keysItem.addActionListener(new KeysMenuItemListener());
		helpMenu.add(keysItem);

		JSeparator splitter = new JSeparator();
		helpMenu.add(splitter);

		aboutItem = new JMenuItem("About"); //$NON-NLS-1$
		aboutItem.setMnemonic('A');
		aboutItem.addActionListener(new AboutMenuItemListener());
		helpMenu.add(aboutItem);

		// game stuff
		mainGame = new PongGame(width, height);

		mainPane = new PongPane(width, height, mainGame);
		mainPane.setLocation(0, 0);
		mainPane.setSize(width, height);
		getContentPane().add(mainPane);

		aTimer = new Timer(10, new TimerRepaintListener());
		aTimer.start();

		// to make the game listen to the keyboard
		this.addKeyListener(new PaddleListener());
		
		this.setLocationRelativeTo(null);
	}

	private void update() {
		mainGame.update();
		repaint();
	}

	private class PaddleListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			char key = e.getKeyChar();
			if ((key == 'a') || (key == 'A'))
				mainGame.setP1Move(-1);
			else if ((key == 'q') || (key == 'Q'))
				mainGame.setP1Move(1);
			else if ((key == 'p') || (key == 'P'))
				mainGame.setP2Move(-1);
			else if ((key == 'm') || (key == 'M'))
				mainGame.setP2Move(1);
		}

		public void keyReleased(KeyEvent e) {
			char key = e.getKeyChar();
			if ((key == 'a') || (key == 'A') || (key == 'q') || (key == 'Q'))
				mainGame.setP1Move(0);
			else if ((key == 'p') || (key == 'P') || (key == 'm')
					|| (key == 'M'))
				mainGame.setP2Move(0);
		}

		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == ' ')
				mainGame.spaceBarPressed();
		}
	}

	private class TimerRepaintListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			update();
		}
	}

	private class KeysMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JOptionPane
					.showMessageDialog(
							getContentPane(),
							"Controls:\nPlayer1:\nUp: A  Down: Z\nPlayer2:\nUp: K  Down: M", //$NON-NLS-1$
							"Keys", JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
		}
	}

	private class AboutMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JOptionPane
					.showMessageDialog(
							getContentPane(),
							"Game created by Guillaume Couture-Levesque, just for fun, on April 19th, 2004", //$NON-NLS-1$
							"About Pong", JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
		}
	}

	private class ExitMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}
}