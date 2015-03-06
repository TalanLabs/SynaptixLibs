package com.synaptix.widget.vldocking.view.swing.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.watermark.SubstanceWatermark;
import org.pushingpixels.substance.internal.painter.DecorationPainterUtils;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.vlsolutions.swing.docking.DockViewTitleBar;
import com.vlsolutions.swing.docking.ui.DockViewTitleBarUI;

public class SubstanceDockViewTitleBarUI extends DockViewTitleBarUI {

	public static ComponentUI createUI(JComponent c) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(c);
		return new SubstanceDockViewTitleBarUI((DockViewTitleBar) c);
	}

	public SubstanceDockViewTitleBarUI(DockViewTitleBar tb) {
		super(tb);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		SubstanceLookAndFeel.setDecorationType(this.titleBar, DecorationAreaType.GENERAL);

		Color backgr = this.titleBar.getBackground();
		if (backgr == null || backgr instanceof UIResource) {
			Color toSet = SubstanceColorSchemeUtilities.getColorScheme(this.titleBar, ComponentState.ENABLED).getBackgroundFillColor();
			this.titleBar.setBackground(new ColorUIResource(toSet));
		}

		SubstanceSkin skin = SubstanceCoreUtilities.getSkin(this.titleBar);
		SubstanceColorScheme bgColorScheme = skin.getBackgroundColorScheme(DecorationAreaType.HEADER);
		Color bgFillColor = bgColorScheme.getBackgroundFillColor();
		Color fgColor = bgColorScheme.getForegroundColor();
		fgColor = SubstanceColorUtilities.getInterpolatedColor(fgColor, bgFillColor, 0.2f);
		titleBar.getTitleLabel().setForeground(fgColor);
	}

	@Override
	public void uninstallUI(JComponent c) {
		DecorationPainterUtils.clearDecorationType(this.titleBar);

		super.uninstallUI(c);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		super.propertyChange(e);

		String pName = e.getPropertyName();
		if (pName.equals("active")) {
			SubstanceSkin skin = SubstanceCoreUtilities.getSkin(this.titleBar);
			SubstanceColorScheme bgColorScheme = skin.getBackgroundColorScheme(DecorationAreaType.HEADER);
			Color bgFillColor = bgColorScheme.getBackgroundFillColor();
			Color fgColor = bgColorScheme.getForegroundColor();

			boolean isActive = ((Boolean) e.getNewValue()).booleanValue();
			if (isActive) {
				titleBar.getTitleLabel().setForeground(fgColor);
			} else {
				fgColor = SubstanceColorUtilities.getInterpolatedColor(fgColor, bgFillColor, 0.2f);
				titleBar.getTitleLabel().setForeground(fgColor);
			}
			titleBar.getTitleLabel().repaint();
		}
	}

	@Override
	public void paint(Graphics graphics, JComponent comp) {
		super.paint(graphics, comp);

		// System.out.println("paint");

		SubstanceSkin skin = SubstanceCoreUtilities.getSkin(comp);
		// SubstanceColorScheme bgScheme =
		// skin.getBackgroundColorScheme(SubstanceLookAndFeel.getDecorationType(comp));
		SubstanceColorScheme bgScheme = skin.getBackgroundColorScheme(DecorationAreaType.HEADER);

		int offset = comp.getHeight() / 2;
		float bp = (float) offset / (float) comp.getHeight();

		float rolloverAmount = titleBar.isActive() ? 1 : 0;

		Color c1 = bgScheme.getUltraLightColor();
		Color c2 = SubstanceColorUtilities.getInterpolatedColor(bgScheme.getUltraLightColor(), bgScheme.getExtraLightColor(), rolloverAmount);
		Color c3 = SubstanceColorUtilities.getInterpolatedColor(bgScheme.getExtraLightColor(), bgScheme.getLightColor(), rolloverAmount);
		Color c4 = SubstanceColorUtilities.getInterpolatedColor(bgScheme.getUltraLightColor(), bgScheme.getExtraLightColor(), rolloverAmount);

		LinearGradientPaint fillPaint = new LinearGradientPaint(0, 0, 0, comp.getHeight(), new float[] { 0.0f, bp - 0.00001f, bp, 1.0f }, new Color[] { c1, c2, c3, c4 });

		Graphics2D g2d = (Graphics2D) graphics.create();
		g2d.setPaint(fillPaint);
		g2d.fillRect(0, 0, comp.getWidth(), comp.getHeight());

		// stamp watermark
		SubstanceWatermark watermark = skin.getWatermark();
		if ((watermark != null) && SubstanceCoreUtilities.toDrawWatermark(comp)) {
			watermark.drawWatermarkImage(g2d, comp, 0, 0, comp.getWidth(), comp.getHeight());
		}
		g2d.dispose();
	}
}
