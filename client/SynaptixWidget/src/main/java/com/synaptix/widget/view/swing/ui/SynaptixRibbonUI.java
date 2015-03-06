package com.synaptix.widget.view.swing.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.internal.ui.ribbon.RibbonBandUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;
import org.pushingpixels.substance.flamingo.ribbon.ui.SubstanceRibbonUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SynaptixRibbonUI extends SubstanceRibbonUI {

	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SynaptixRibbonUI();
	}

	@Override
	protected void installComponents() {
		super.installComponents();

		bandScrollablePanel.setScrollOnRollover(true);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new RibbonLayout();
	}

	protected class RibbonLayout implements LayoutManager {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
		 */
		@Override
		public void addLayoutComponent(String name, Component c) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
		 */
		@Override
		public void removeLayoutComponent(Component c) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
		 */
		@Override
		public Dimension preferredLayoutSize(Container c) {
			Insets ins = c.getInsets();
			int maxPrefBandHeight = 0;
			boolean isRibbonMinimized = ribbon.isMinimized();
			if (!isRibbonMinimized) {
				if (ribbon.getTaskCount() > 0) {
					RibbonTask selectedTask = ribbon.getSelectedTask();
					for (AbstractRibbonBand<?> ribbonBand : selectedTask.getBands()) {
						int bandPrefHeight = ribbonBand.getPreferredSize().height;
						Insets bandInsets = ribbonBand.getInsets();
						maxPrefBandHeight = Math.max(maxPrefBandHeight, bandPrefHeight + bandInsets.top + bandInsets.bottom);
					}
				}
			}

			int extraHeight = getTaskToggleButtonHeight();
			if (!isUsingTitlePane()) {
				extraHeight += getTaskbarHeight();
			}
			int prefHeight = maxPrefBandHeight + extraHeight + ins.top + ins.bottom;
			// System.out.println("Ribbon pref = " + prefHeight);
			return new Dimension(c.getWidth(), prefHeight);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
		 */
		@Override
		public Dimension minimumLayoutSize(Container c) {
			// go over all ribbon bands and sum the width
			// of ribbon buttons (of collapsed state)
			Insets ins = c.getInsets();
			int width = 0;
			int maxMinBandHeight = 0;
			int gap = getBandGap();

			int extraHeight = getTaskToggleButtonHeight();
			if (!isUsingTitlePane()) {
				extraHeight += getTaskbarHeight();
			}

			if (ribbon.getTaskCount() > 0) {
				boolean isRibbonMinimized = ribbon.isMinimized();
				// minimum is when all the tasks are collapsed
				// for (int i = 0; i < ribbon.getTaskCount(); i++) {
				// RibbonTask selectedTask = ribbon.getTask(i);
				RibbonTask selectedTask = ribbon.getSelectedTask();
				for (AbstractRibbonBand<?> ribbonBand : selectedTask.getBands()) {
					int bandPrefHeight = ribbonBand.getMinimumSize().height;
					Insets bandInsets = ribbonBand.getInsets();
					RibbonBandUI bandUI = ribbonBand.getUI();
					width += bandUI.getPreferredCollapsedWidth();
					if (!isRibbonMinimized) {
						maxMinBandHeight = Math.max(maxMinBandHeight, bandPrefHeight + bandInsets.top + bandInsets.bottom);
					}
				}
				// add inter-band gaps
				width += gap * (selectedTask.getBandCount() - 1);
				// }
			} else {
				// fix for issue 44 (empty ribbon)
				width = 50;
			}
			return new Dimension(width, maxMinBandHeight + extraHeight + ins.top + ins.bottom);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
		 */
		@Override
		public void layoutContainer(Container c) {
			// System.out.println("Ribbon real = " + c.getHeight());

			Insets ins = c.getInsets();
			int tabButtonGap = getTabButtonGap();

			boolean ltr = ribbon.getComponentOrientation().isLeftToRight();

			// the top row - task bar components
			int width = c.getWidth();
			int taskbarHeight = getTaskbarHeight();
			int y = ins.top;

			boolean isUsingTitlePane = isUsingTitlePane();
			// handle taskbar only if it is not marked
			if (!isUsingTitlePane) {
				taskBarPanel.removeAll();
				for (Component regComp : ribbon.getTaskbarComponents()) {
					taskBarPanel.add(regComp);
				}
				// taskbar takes all available width
				taskBarPanel.setBounds(ins.left, ins.top, width - ins.left - ins.right, taskbarHeight);
				y += taskbarHeight;
			} else {
				taskBarPanel.setBounds(0, 0, 0, 0);
			}

			int taskToggleButtonHeight = getTaskToggleButtonHeight();

			int x = ltr ? ins.left : width - ins.right;
			// the application menu button
			int appMenuButtonSize = taskbarHeight + taskToggleButtonHeight;
			if (!isUsingTitlePane) {
				applicationMenuButton.setVisible(ribbon.getApplicationMenu() != null);
				if (ribbon.getApplicationMenu() != null) {
					if (ltr) {
						applicationMenuButton.setBounds(x, ins.top, appMenuButtonSize, appMenuButtonSize);
					} else {
						applicationMenuButton.setBounds(x - appMenuButtonSize, ins.top, appMenuButtonSize, appMenuButtonSize);
					}
				}
			} else {
				applicationMenuButton.setVisible(false);
			}
			x = ltr ? x + 2 : x - 2;
			if (FlamingoUtilities.getApplicationMenuButton(SwingUtilities.getWindowAncestor(ribbon)) != null) {
				x = ltr ? x + appMenuButtonSize : x - appMenuButtonSize;
			}

			// the help button
			if (helpPanel != null) {
				Dimension preferred = helpPanel.getPreferredSize();
				if (ltr) {
					helpPanel.setBounds(width - ins.right - preferred.width, y, preferred.width, preferred.height);
				} else {
					helpPanel.setBounds(ins.left, y, preferred.width, preferred.height);
				}
			}

			// task buttons
			if (ltr) {
				int taskButtonsWidth = (helpPanel != null) ? (helpPanel.getX() - tabButtonGap - x) : (c.getWidth() - ins.right - x);
				taskToggleButtonsScrollablePanel.setBounds(x, y, taskButtonsWidth, taskToggleButtonHeight);
			} else {
				int taskButtonsWidth = (helpPanel != null) ? (x - tabButtonGap - helpPanel.getX() - helpPanel.getWidth()) : (x - ins.left);
				taskToggleButtonsScrollablePanel.setBounds(x - taskButtonsWidth, y, taskButtonsWidth, taskToggleButtonHeight);
			}

			TaskToggleButtonsHostPanel taskToggleButtonsHostPanel = taskToggleButtonsScrollablePanel.getView();
			int taskToggleButtonsHostPanelMinWidth = taskToggleButtonsHostPanel.getMinimumSize().width;
			taskToggleButtonsHostPanel.setPreferredSize(new Dimension(taskToggleButtonsHostPanelMinWidth, taskToggleButtonsScrollablePanel.getBounds().height));
			taskToggleButtonsScrollablePanel.doLayout();

			y += taskToggleButtonHeight;

			int extraHeight = taskToggleButtonHeight;
			if (!isUsingTitlePane) {
				extraHeight += taskbarHeight;
			}

			if (bandScrollablePanel.getParent() == ribbon) {
				if (!ribbon.isMinimized() && (ribbon.getTaskCount() > 0)) {
					// y += ins.top;
					Insets bandInsets = (ribbon.getSelectedTask().getBandCount() == 0) ? new Insets(0, 0, 0, 0) : ribbon.getSelectedTask().getBand(0).getInsets();
					bandScrollablePanel.setBounds(1 + ins.left, y + bandInsets.top, c.getWidth() - 2 * ins.left - 2 * ins.right - 1, c.getHeight() - extraHeight - ins.top - ins.bottom
							- bandInsets.top - bandInsets.bottom);
					// System.out.println("Scrollable : "
					// + bandScrollablePanel.getBounds());
					BandHostPanel bandHostPanel = bandScrollablePanel.getView();

					int bandHostPanelMinWidth = 0;
					for (int i = 0; i < ribbon.getSelectedTask().getBandCount(); i++) {
						AbstractRibbonBand<?> band = ribbon.getSelectedTask().getBand(i);
						bandHostPanelMinWidth += band.getPreferredSize().width;
					}
					bandHostPanel.setPreferredSize(new Dimension(bandHostPanelMinWidth, bandScrollablePanel.getBounds().height));
					bandScrollablePanel.doLayout();
					bandHostPanel.doLayout();
				} else {
					bandScrollablePanel.setBounds(0, 0, 0, 0);
				}
			}
		}
	}
}
