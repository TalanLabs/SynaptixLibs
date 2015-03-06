package com.synaptix.deployer.client.view.swing.compare;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import com.synaptix.component.IComponent;
import com.synaptix.deployer.client.compare.CompareNode;
import com.synaptix.deployer.client.controller.CompareController;

public class CompareTitle extends JLabel {

	private static final long serialVersionUID = -7729828916117966993L;

	private CompareNode node;

	public boolean explored;

	public boolean active;

	private Font normalFont;

	private Font boldFont;

	public <E extends IComponent> CompareTitle(final CompareNode node, final CompareController compareController) {
		super(node.getProperty());

		this.node = node;

		normalFont = getFont();
		boldFont = getFont().deriveFont(Font.BOLD);

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				compareController.exploreNode(node);
			}
		});
	}

	public int getRank() {
		return node.getLevel() + 1;
	}

	public void setExplored(boolean explored) {
		this.explored = explored;

		if (explored) {
			setForeground(Color.black);
		} else {
			setForeground(Color.lightGray);
		}
	}

	public void setActive(boolean active) {
		this.active = active;

		if (active) {
			setFont(boldFont);
		} else {
			setFont(normalFont);
		}
	}
}
