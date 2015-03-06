package com.synaptix.widget.view.swing.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.pushingpixels.lafwidget.LafWidgetUtilities;
import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker;
import org.pushingpixels.substance.internal.ui.SubstanceSliderUI;
import org.pushingpixels.substance.internal.utils.HashMapKey;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceOutlineUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

import com.synaptix.widget.view.swing.JSyAreaSlider;

public class SyAreaSliderUI extends SubstanceSliderUI {

	private JSyAreaSlider areaSlider;

	private Rectangle thumbRect1;
	private Rectangle thumbRect2;

	public SyAreaSliderUI(final JSyAreaSlider areaSlider) {
		super(areaSlider);

		this.areaSlider = areaSlider;
		this.thumbRect1 = new Rectangle();
		this.thumbRect2 = new Rectangle();

		areaSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// Clicked in the Thumb area?
				if (thumbRect1.contains(e.getX(), e.getY())) {
					areaSlider.setSelectedThumb(0, false);
				} else if (thumbRect2.contains(e.getX(), e.getY())) {
					areaSlider.setSelectedThumb(1, false);
				}
				super.mousePressed(e);
			}
		});
	}

	@Override
	public void paintThumb(Graphics g) {

		Graphics2D graphics = (Graphics2D) g.create();
		// graphics.setComposite(TransitionLayout.getAlphaComposite(slider));
		Rectangle knobBounds = this.thumbRect1;

		graphics.translate(knobBounds.x, knobBounds.y);

		Icon icon = this.getIcon();
		if (this.areaSlider.getOrientation() == JSlider.HORIZONTAL) {
			if (icon != null)
				icon.paintIcon(this.areaSlider, graphics, -1, 0);
		} else {
			if (this.slider.getComponentOrientation().isLeftToRight()) {
				if (icon != null)
					icon.paintIcon(this.areaSlider, graphics, 0, -1);
			} else {
				if (icon != null)
					icon.paintIcon(this.areaSlider, graphics, 0, 1);
			}
		}
		graphics.translate(-knobBounds.x, -knobBounds.y);

		knobBounds = this.thumbRect2;
		graphics.translate(knobBounds.x, knobBounds.y);

		if (this.areaSlider.getOrientation() == JSlider.HORIZONTAL) {
			if (icon != null)
				icon.paintIcon(this.areaSlider, graphics, -1, 0);
		} else {
			if (this.slider.getComponentOrientation().isLeftToRight()) {
				if (icon != null)
					icon.paintIcon(this.areaSlider, graphics, 0, -1);
			} else {
				if (icon != null)
					icon.paintIcon(this.areaSlider, graphics, 0, 1);
			}
		}

		graphics.dispose();
	}

	@Override
	protected void calculateThumbLocation() {
		super.calculateThumbLocation();

		Rectangle trackRect = this.getPaintTrackRect();

		thumbRect1.setBounds(thumbRect);
		thumbRect2.setBounds(thumbRect);

		if (slider.getOrientation() == JSlider.HORIZONTAL) {
			int valuePosition1 = xPositionForValue(areaSlider.getValue1());

			double centerY1 = trackRect.y + trackRect.height / 2.0;
			thumbRect1.y = (int) (centerY1 - thumbRect.height / 2.0) + 1;

			thumbRect1.x = valuePosition1 - thumbRect.width / 2;

			int valuePosition2 = xPositionForValue(areaSlider.getValue2());

			double centerY2 = trackRect.y + trackRect.height / 2.0;
			thumbRect2.y = (int) (centerY2 - thumbRect.height / 2.0) + 1;

			thumbRect2.x = valuePosition2 - thumbRect.width / 2;

		} else {
			int valuePosition1 = yPositionForValue(areaSlider.getValue1());

			double centerX1 = trackRect.x + trackRect.width / 2.0;
			thumbRect1.x = (int) (centerX1 - thumbRect.width / 2.0) + 1;

			thumbRect1.y = valuePosition1 - (thumbRect.height / 2);

			int valuePosition2 = yPositionForValue(areaSlider.getValue2());

			double centerX2 = trackRect.x + trackRect.width / 2.0;
			thumbRect2.x = (int) (centerX2 - thumbRect.width / 2.0) + 1;

			thumbRect2.y = valuePosition2 - (thumbRect.height / 2);
		}
	}

	private Rectangle getPaintTrackRect() {
		int trackLeft = 0;
		int trackRight;
		int trackTop = 0;
		int trackBottom;
		int trackWidth = this.getTrackWidth();
		if (this.slider.getOrientation() == SwingConstants.HORIZONTAL) {
			trackTop = 3 + this.insetCache.top + 2 * this.focusInsets.top;
			trackBottom = trackTop + trackWidth - 1;
			trackRight = this.trackRect.width;
			return new Rectangle(this.trackRect.x + trackLeft, trackTop, trackRight - trackLeft, trackBottom - trackTop);
		} else {
			if (this.slider.getPaintLabels() || this.slider.getPaintTicks()) {
				if (this.slider.getComponentOrientation().isLeftToRight()) {
					trackLeft = trackRect.x + this.insetCache.left + this.focusInsets.left;
					trackRight = trackLeft + trackWidth - 1;
				} else {
					trackRight = trackRect.x + trackRect.width - this.insetCache.right - this.focusInsets.right;
					trackLeft = trackRight - trackWidth - 1;
				}
			} else {
				// horizontally center the track
				if (this.slider.getComponentOrientation().isLeftToRight()) {
					trackLeft = (this.insetCache.left + this.focusInsets.left + this.slider.getWidth() - this.insetCache.right - this.focusInsets.right) / 2 - trackWidth / 2;
					trackRight = trackLeft + trackWidth - 1;
				} else {
					trackRight = (this.insetCache.left + this.focusInsets.left + this.slider.getWidth() - this.insetCache.right - this.focusInsets.right) / 2 + trackWidth / 2;
					trackLeft = trackRight - trackWidth - 1;
				}
			}
			trackBottom = this.trackRect.height - 1;
			return new Rectangle(trackLeft, this.trackRect.y + trackTop, trackRight - trackLeft, trackBottom - trackTop);
		}
	}

	@Override
	public void setThumbLocation(int x, int y) {
		switch (areaSlider.getSelectedThumb()) {
		case 0:
			if (x > thumbRect2.x) {
				Rectangle r = new Rectangle(thumbRect2);
				thumbRect2.setLocation(x, y);
				thumbRect1.setBounds(r);
				areaSlider.setSelectedThumb(1, true);
			} else {
				thumbRect1.setLocation(x, y);
			}
			break;
		case 1:
			if (x < thumbRect1.x) {
				Rectangle r = new Rectangle(thumbRect1);
				thumbRect1.setLocation(x, y);
				thumbRect2.setBounds(r);
				areaSlider.setSelectedThumb(0, true);
			} else {
				thumbRect2.setLocation(x, y);
			}
			break;
		default:
			break;
		}
		super.setThumbLocation(x, y);
	}

	@Override
	public void paintTrack(Graphics g) {
		Graphics2D graphics = (Graphics2D) g.create();

		boolean drawInverted = this.drawInverted();

		Rectangle paintRect = this.getPaintTrackRect();

		// Width and height of the painting rectangle.
		int width = paintRect.width;
		int height = paintRect.height;

		if (this.slider.getOrientation() == JSlider.VERTICAL) {
			// apply rotation / translate transformation on vertical
			// slider tracks
			int temp = width;
			// noinspection SuspiciousNameCombination
			width = height;
			height = temp;
			AffineTransform at = graphics.getTransform();
			at.translate(paintRect.x, width + paintRect.y);
			at.rotate(-Math.PI / 2);
			graphics.setTransform(at);
		} else {
			graphics.translate(paintRect.x, paintRect.y);
		}

		StateTransitionTracker.ModelStateInfo modelStateInfo = this.stateTransitionTracker.getModelStateInfo();

		SubstanceColorScheme trackSchemeUnselected = SubstanceColorSchemeUtilities.getColorScheme(this.slider, this.slider.isEnabled() ? ComponentState.ENABLED : ComponentState.DISABLED_UNSELECTED);
		SubstanceColorScheme trackBorderSchemeUnselected = SubstanceColorSchemeUtilities.getColorScheme(this.slider, ColorSchemeAssociationKind.BORDER,
				this.slider.isEnabled() ? ComponentState.ENABLED : ComponentState.DISABLED_UNSELECTED);
		paintSliderTrack(graphics, drawInverted, trackSchemeUnselected, trackBorderSchemeUnselected, width, height);

		Map<ComponentState, StateTransitionTracker.StateContributionInfo> activeStates = modelStateInfo.getStateContributionMap();
		for (Map.Entry<ComponentState, StateTransitionTracker.StateContributionInfo> activeEntry : activeStates.entrySet()) {
			ComponentState activeState = activeEntry.getKey();
			if (!activeState.isActive())
				continue;

			float contribution = activeEntry.getValue().getContribution();
			if (contribution == 0.0f)
				continue;

			graphics.setComposite(LafWidgetUtilities.getAlphaComposite(this.slider, contribution, g));

			SubstanceColorScheme activeFillScheme = SubstanceColorSchemeUtilities.getColorScheme(this.slider, activeState);
			SubstanceColorScheme activeBorderScheme = SubstanceColorSchemeUtilities.getColorScheme(this.slider, ColorSchemeAssociationKind.BORDER, activeState);
			this.paintSliderTrackSelected(graphics, drawInverted, paintRect, activeFillScheme, activeBorderScheme, width, height);
		}

		graphics.dispose();
	}

	/**
	 * Paints the slider track.
	 * 
	 * @param graphics
	 *            Graphics.
	 * @param drawInverted
	 *            Indicates whether the value-range shown for the slider is reversed.
	 * @param fillColorScheme
	 *            Fill color scheme.
	 * @param borderScheme
	 *            Border color scheme.
	 * @param width
	 *            Track width.
	 * @param height
	 *            Track height.
	 */
	private void paintSliderTrack(Graphics2D graphics, boolean drawInverted, SubstanceColorScheme fillColorScheme, SubstanceColorScheme borderScheme, int width, int height) {
		Graphics2D g2d = (Graphics2D) graphics.create();

		SubstanceFillPainter fillPainter = ClassicFillPainter.INSTANCE;
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities.getBorderPainter(this.slider);

		int componentFontSize = SubstanceSizeUtils.getComponentFontSize(this.slider);
		int borderDelta = (int) Math.floor(SubstanceSizeUtils.getBorderStrokeWidth(componentFontSize) / 2.0);
		float radius = SubstanceSizeUtils.getClassicButtonCornerRadius(componentFontSize) / 2.0f;
		int borderThickness = (int) SubstanceSizeUtils.getBorderStrokeWidth(componentFontSize);

		HashMapKey key = SubstanceCoreUtilities.getHashKey(width, height, radius, borderDelta, borderThickness, fillColorScheme.getDisplayName(), borderScheme.getDisplayName());

		BufferedImage trackImage = trackCache.get(key);
		if (trackImage == null) {
			trackImage = SubstanceCoreUtilities.getBlankImage(width + 1, height + 1);
			Graphics2D cacheGraphics = trackImage.createGraphics();

			Shape contour = SubstanceOutlineUtilities.getBaseOutline(width + 1, height + 1, radius, null, borderDelta);

			fillPainter.paintContourBackground(cacheGraphics, slider, width, height, contour, false, fillColorScheme, false);

			GeneralPath contourInner = SubstanceOutlineUtilities.getBaseOutline(width + 1, height + 1, radius - borderThickness, null, borderThickness + borderDelta);
			borderPainter.paintBorder(cacheGraphics, slider, width + 1, height + 1, contour, contourInner, borderScheme);

			trackCache.put(key, trackImage);
			cacheGraphics.dispose();
		}

		g2d.drawImage(trackImage, 0, 0, null);

		g2d.dispose();
	}

	private void paintSliderTrackSelected(Graphics2D graphics, boolean drawInverted, Rectangle paintRect, SubstanceColorScheme fillScheme, SubstanceColorScheme borderScheme, int width, int height) {

		Graphics2D g2d = (Graphics2D) graphics.create();
		Insets insets = this.slider.getInsets();
		insets.top /= 2;
		insets.left /= 2;
		insets.bottom /= 2;
		insets.right /= 2;

		SubstanceFillPainter fillPainter = SubstanceCoreUtilities.getFillPainter(this.slider);
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities.getBorderPainter(this.slider);
		float radius = SubstanceSizeUtils.getClassicButtonCornerRadius(SubstanceSizeUtils.getComponentFontSize(slider)) / 2.0f;
		int borderDelta = (int) Math.floor(SubstanceSizeUtils.getBorderStrokeWidth(SubstanceSizeUtils.getComponentFontSize(slider)) / 2.0);

		// fill selected portion
		if (this.slider.isEnabled()) {
			if (this.slider.getOrientation() == SwingConstants.HORIZONTAL) {
				int fillWidth = (int) (thumbRect2.getCenterX() - thumbRect1.getCenterX());

				int fillHeight = height + 1;
				if ((fillWidth > 0) && (fillHeight > 0)) {
					g2d.translate(thumbRect1.getX() - insets.left, 0);
					Shape contour = SubstanceOutlineUtilities.getBaseOutline(fillWidth, fillHeight, radius, null, borderDelta);
					fillPainter.paintContourBackground(g2d, this.slider, fillWidth, fillHeight, contour, false, fillScheme, false);

					borderPainter.paintBorder(g2d, this.slider, fillWidth, fillHeight, contour, null, borderScheme);
				}
			} else {
				int middleOfThumb = this.thumbRect.y + (this.thumbRect.height / 2) - paintRect.y;
				int fillMin;
				int fillMax;

				if (this.drawInverted()) {
					fillMin = 0;
					fillMax = middleOfThumb;
					// fix for issue 368 - inverted vertical sliders
					g2d.translate(width + 2 - middleOfThumb, 0);
				} else {
					fillMin = middleOfThumb;
					fillMax = width + 1;
				}

				int fillWidth = fillMax - fillMin;
				int fillHeight = height + 1;
				if ((fillWidth > 0) && (fillHeight > 0)) {
					Shape contour = SubstanceOutlineUtilities.getBaseOutline(fillWidth, fillHeight, radius, null, borderDelta);

					fillPainter.paintContourBackground(g2d, this.slider, fillWidth, fillHeight, contour, false, fillScheme, false);
					borderPainter.paintBorder(g2d, this.slider, fillWidth, fillHeight, contour, null, borderScheme);
				}
			}
		}
		g2d.dispose();
	}

	@Override
	public boolean isInside(MouseEvent me) {
		Rectangle thumbB1 = this.thumbRect1;
		if (thumbB1 != null && thumbB1.contains(me.getX(), me.getY())) {
			return true;
		}
		Rectangle thumbB2 = this.thumbRect2;
		if (thumbB2 != null && thumbB2.contains(me.getX(), me.getY())) {
			return true;
		}
		return false;
	}
}
