package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.Serializable;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.ChangePropertyName;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.rule.HierarchicalSummaryRule;

public class SummaryPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HierarchicalPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> {

	private static final long serialVersionUID = 2250666583081022422L;

	private SimpleLayoutPanel contentPanel;
	private JViewport contentViewPort;
	private ChangeListener contentChangeListener;

	public SummaryPanel(final ConfigurationContext<E, F, L> configurationContext) {
		super(configurationContext);
	}

	@Override
	public HierarchicalPanelKind getPanelKind() {
		return HierarchicalPanelKind.SUMMARY;
	}

	@Override
	protected ChangePropertyName getKeyPropertyColumnTitleChanged() {
		return ChangePropertyName.SUMMARY_COLUM;
	}

	@Override
	public String getColumnLabelAt(final int columnIndex) {
		final HierarchicalSummaryRule<E, F, L> rule = getColumnDefinitionAt(columnIndex);
		return rule.getRuleName();
	}

	@Override
	public Font getColumnTitleFont() {
		return super.configurationContext.getBigEmphaseFont();
	}

	@Override
	public JComponent getDisplayableContent() {
		return this.contentViewPort;
	}

	@Override
	protected void buildContents() {
		this.contentPanel = new SimpleLayoutPanel(getContent().getPreferredSize().width, false, configurationContext.getCellHeight(), true);
		this.contentPanel.add(getContent());
		this.contentViewPort = new JViewport();
		this.contentViewPort.setView(this.contentPanel);
		this.contentChangeListener = new MyResourceViewportChangeListener();
		this.contentViewPort.addMouseWheelListener(new MyMouseWheelListener());
	}

	@Override
	protected void setColumnsDefinitionAndCalculateColumnWidthIfRequired(final List<HierarchicalSummaryRule<E, F, L>> columnsDefinition) {
		super.setColumnsDefinitionAndCalculateColumnWidthIfRequired(columnsDefinition);
		computeContents();
	}

	@Override
	public void resizeAndRepaint() {
		this.contentPanel.setNbRows(getModelSize());
		this.contentPanel.revalidate();
		this.contentPanel.repaint();
	}

	@Override
	protected void updateVerticalView(BoundedRangeModel rangeModel) {
		final Point p2 = this.contentViewPort.getViewPosition();
		p2.y = rangeModel.getValue();
		boolean wasEnabled = super.isEnabledListeners;
		setEnableListeners(false);
		this.contentViewPort.setViewPosition(p2);
		setEnableListeners(wasEnabled);
	}

	@Override
	protected void updateHorizontalView(final BoundedRangeModel rangeModel) {
	}

	@Override
	protected void setEnableListeners(final boolean isEnabled) {
		if (isEnabled && !super.isEnabledListeners) {
			this.contentViewPort.addChangeListener(this.contentChangeListener);
		} else if (this.isEnabledListeners && !isEnabled) {
			this.contentViewPort.removeChangeListener(this.contentChangeListener);
		}
		super.setEnableListeners(isEnabled);
	}

	@Override
	public void recalculatePosition() {
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

	private final class MyResourceViewportChangeListener implements ChangeListener {

		@Override
		public void stateChanged(final ChangeEvent e) {
			updateVerticalPosition(contentViewPort);
		}
	}

	private final class MyMouseWheelListener implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			firePropertyChange(ChangePropertyName.VERTICAL_WHEEL.name(), null, new Integer(e.getWheelRotation()));
		}
	}
}
