package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.Serializable;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.ChangePropertyName;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;

public class ValuePanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HierarchicalPanel<F, E, F, L> {

	private static final long serialVersionUID = 4230469501527160800L;

	private SimpleLayoutPanel headerPanel;
	private JViewport headerViewport;
	private ChangeListener headerChangeListener;

	private SimpleLayoutPanel contentPanel;
	private JViewport contentViewPort;
	private ChangeListener contentChangeListener;

	private SimpleLayoutPanel footerPanel;
	private JViewport footerViewPort;
	private ChangeListener footerChangeListener;

	public ValuePanel(final ConfigurationContext<E, F, L> configurationContext) {
		super(configurationContext);
	}

	@Override
	public HierarchicalPanelKind getPanelKind() {
		return HierarchicalPanelKind.VALUE;
	}

	@Override
	protected ChangePropertyName getKeyPropertyColumnTitleChanged() {
		return ChangePropertyName.VALUE_COLUMN;
	}

	@Override
	public String getColumnLabelAt(int columnIndex) {
		return getStringFromObject(getColumnDefinitionAt(columnIndex));
	}

	private int calculateMaxColumnWidth() {
		int maxColumnWidth = 0;
		for (int columnIndex = 0; columnIndex < getColumnDefinitionList().size(); columnIndex++) {
			final String columnLabel = getColumnLabelAt(columnIndex);
			final int columnLabelSize = SwingUtilities.computeStringWidth(getFontMetrics(getColumnTitleFont()), columnLabel);
			final int columnWidth = calculateColumnWidthWithContentSize(columnLabelSize);
			if (maxColumnWidth < columnWidth) {
				maxColumnWidth = columnWidth;
			}
		}
		return maxColumnWidth;
	}

	@Override
	protected final int computeColumnWidth(int columnIndex) {
		this.configurationContext.setCellWidth(getColumnWidth());
		return this.configurationContext.getCellWidth();
	}

	protected int getColumnWidth() {
		return calculateMaxColumnWidth();
	}

	@Override
	public Integer getColumnAbscissAt(final int columnIndex) {
		return super.configurationContext.getCellWidth() * columnIndex;
	}

	@Override
	protected void buildContents() {
		this.headerPanel = new SimpleLayoutPanel(getHeader().getPreferredSize().width, false, super.configurationContext.getHeaderHeight(), false);
		this.headerPanel.add(getHeader());
		this.headerViewport = new JViewport();
		this.headerViewport.setView(this.headerPanel);
		this.headerViewport.addMouseWheelListener(new MyHorizontalMouseWheelListener());
		this.headerChangeListener = new MyHeaderViewportChangeListener();

		this.contentPanel = new SimpleLayoutPanel(getContent().getPreferredSize().width, false, super.configurationContext.getCellHeight(), true);
		this.contentPanel.add(getContent());
		this.contentViewPort = new JViewport();
		this.contentViewPort.setView(this.contentPanel);
		this.contentViewPort.addMouseWheelListener(new MyVerticalMouseWheelListener());
		this.contentViewPort.setScrollMode(JViewport.BLIT_SCROLL_MODE);
		this.contentChangeListener = new MyResourceViewportChangeListener();

		this.footerPanel = new SimpleLayoutPanel(getFooter().getPreferredSize().width, false, super.configurationContext.getFooterHeight(), false);
		this.footerPanel.add(getFooter());
		this.footerViewPort = new JViewport();
		this.footerViewPort.setView(this.footerPanel);
		this.footerViewPort.addMouseWheelListener(new MyHorizontalMouseWheelListener());
		this.footerChangeListener = new MyFooterViewportChangeListener();
	}

	@Override
	public JComponent getDisplayableContent() {
		return this.contentViewPort;
	}

	@Override
	public JComponent getDisplayableHeader() {
		return this.headerViewport;
	}

	@Override
	public JComponent getDisplayableFooter() {
		return this.footerViewPort;
	}

	@Override
	public void resizeAndRepaint() {
		this.headerPanel.setWidth(getHeader().getPreferredSize().width);
		this.headerPanel.revalidate();
		this.headerPanel.repaint();
		this.contentPanel.setWidth(getContent().getPreferredSize().width);
		this.contentPanel.setNbRows(getModelSize());
		this.contentPanel.revalidate();
		this.contentPanel.repaint();
		this.footerPanel.setWidth(getFooter().getPreferredSize().width);
		this.footerPanel.revalidate();
		this.footerPanel.repaint();
	}

	private final class MyHeaderViewportChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			updateHorizontalPosition(headerViewport);
		}
	}

	private final class MyFooterViewportChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			updateHorizontalPosition(footerViewPort);
		}
	}

	private final class MyResourceViewportChangeListener implements ChangeListener {

		@Override
		public void stateChanged(final ChangeEvent e) {
			updateVerticalPosition(contentViewPort);
			updateHorizontalPosition(contentViewPort);
		}
	}

	@Override
	public Font getColumnTitleFont() {
		return super.configurationContext.getBigEmphaseFont();
	}

	@Override
	protected void updateVerticalView(final BoundedRangeModel rangeModel) {
		final Point p2 = this.contentViewPort.getViewPosition();
		p2.y = rangeModel.getValue();
		boolean wasEnabled = super.isEnabledListeners;
		setEnableListeners(false);
		this.contentViewPort.setViewPosition(p2);
		this.contentViewPort.repaint();
		setEnableListeners(wasEnabled);
	}

	@Override
	protected void updateHorizontalView(final BoundedRangeModel rangeModel) {
		final Point p1 = this.headerViewport.getViewPosition();
		final Point p2 = this.contentViewPort.getViewPosition();
		final Point p3 = this.footerViewPort.getViewPosition();

		p1.x = rangeModel.getValue();
		this.headerViewport.setViewPosition(p1);
		p2.x = rangeModel.getValue();
		this.contentViewPort.setViewPosition(p2);
		p3.x = rangeModel.getValue();
		this.footerViewPort.setViewPosition(p3);

		this.headerViewport.repaint();
		this.contentViewPort.repaint();
		this.footerViewPort.repaint();
	}

	@Override
	protected void setEnableListeners(final boolean isEnabled) {
		if (isEnabled && !super.isEnabledListeners) {
			this.headerViewport.addChangeListener(this.headerChangeListener);
			this.contentViewPort.addChangeListener(this.contentChangeListener);
			if (configurationContext.isShowSummaryLine()) {
				this.footerViewPort.addChangeListener(this.footerChangeListener);
			}
		} else if (this.isEnabledListeners && !isEnabled) {
			this.headerViewport.removeChangeListener(this.headerChangeListener);
			this.contentViewPort.removeChangeListener(this.contentChangeListener);
			this.footerViewPort.removeChangeListener(this.footerChangeListener);
		}
		super.setEnableListeners(isEnabled);
	}

	@Override
	public void recalculatePosition() {
		if (getColumnDefinitionList().size() > 0) {
			updateHorizontalPosition(headerViewport);
		}
		if (getModelSize() > 0) {
			updateVerticalPosition(contentViewPort);
		}
	}

	private final void updateVerticalPosition(JViewport viewport) {
		final Point viewPosition = viewport.getViewPosition();
		final int verticalExtent = viewport.getExtentSize().height;
		final int verticalMax = viewport.getViewSize().height;
		final int verticalValue = Math.max(0, Math.min(viewPosition.y, verticalMax - verticalExtent));
		if (verticalMax >= verticalExtent) {
			final BoundedRangeModel verticalRangeModel = new DefaultBoundedRangeModel(verticalValue, verticalExtent, 0, verticalMax);
			firePropertyChange(ChangePropertyName.VERTICAL_POSITION.name(), null, verticalRangeModel);
		}
	}

	private final void updateHorizontalPosition(JViewport viewport) {
		final Point viewPosition = viewport.getViewPosition();
		final int extent = viewport.getExtentSize().width;
		final int max = viewport.getViewSize().width;
		final int value = Math.max(0, Math.min(viewPosition.x, max - extent));
		if (max >= extent) {
			final BoundedRangeModel rangeModel = new DefaultBoundedRangeModel(value, extent, 0, max);
			firePropertyChange(ChangePropertyName.HORIZONTAL_POSITION.name(), null, rangeModel);
		}
	}

	private final class MyVerticalMouseWheelListener implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			firePropertyChange(ChangePropertyName.VERTICAL_WHEEL.name(), null, new Integer(e.getWheelRotation()));
		}
	}

	private final class MyHorizontalMouseWheelListener implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			firePropertyChange(ChangePropertyName.HORIZONTAL_WHEEL.name(), null, new Integer(e.getWheelRotation()));
		}
	}
}
