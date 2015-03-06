package com.synaptix.deployer.client.view.swing.deployerManagement;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import com.synaptix.component.IComponent;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.deployerManagement.ManagementNode;

public class ManagementTitle extends JLabel {

	private static final long serialVersionUID = -7729828916117966993L;

	private ManagementNode node;

	public boolean explored;

	public boolean active;

	private Font normalFont;

	private Font boldFont;

	public <E extends IComponent> ManagementTitle(final ManagementNode node, final DeployerManagementController deployerManagementController) {
		super();

		this.node = node;

		updateName();

		normalFont = getFont();
		boldFont = getFont().deriveFont(Font.BOLD);

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				deployerManagementController.exploreNode(node);
			}
		});
	}

	public void updateName() {
		setText((node.getLevel() + 1) + " : " + node.getName());
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
