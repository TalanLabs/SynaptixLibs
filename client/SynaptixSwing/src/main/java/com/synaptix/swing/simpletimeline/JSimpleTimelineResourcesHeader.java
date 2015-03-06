package com.synaptix.swing.simpletimeline;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.plaf.UIResource;

import com.synaptix.swing.JSimpleTimeline;
import com.synaptix.swing.event.SimpleTimelineResourcesModelEvent;
import com.synaptix.swing.event.SimpleTimelineResourcesModelListener;
import com.synaptix.swing.plaf.SimpleTimelineResourceHeaderUI;

public class JSimpleTimelineResourcesHeader extends JComponent implements
		SimpleTimelineResourcesModelListener {

	private static final long serialVersionUID = 2253293907751629570L;

	private static final String uiClassID = "SimpleTimelineRessourceHeaderUI"; //$NON-NLS-1$

	protected JSimpleTimeline simpleTimeline;

	protected SimpleTimelineResourcesModel resourceModel;

	protected boolean reorderingAllowed;

	protected boolean resizingAllowed;

	transient protected SimpleTimelineResource resizingResource;

	transient protected SimpleTimelineResource draggedResource;

	transient protected int draggedDistance;

	private SimpleTimelineResourceRenderer defaultRenderer;

	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicSimpleTimelineResourceHeaderUI"); //$NON-NLS-1$
	}

	public JSimpleTimelineResourcesHeader(SimpleTimelineResourcesModel rm) {
		super();

		setResourceModel(rm);

		initializeLocalVars();

		updateUI();
	}

	public void setSimpleTimeline(JSimpleTimeline simpleTimeline) {
		JSimpleTimeline old = this.simpleTimeline;
		this.simpleTimeline = simpleTimeline;
		firePropertyChange("simpleTimeline", old, simpleTimeline); //$NON-NLS-1$
	}

	public JSimpleTimeline getSimpleTimeline() {
		return simpleTimeline;
	}

	public void setReorderingAllowed(boolean reorderingAllowed) {
		boolean old = this.reorderingAllowed;
		this.reorderingAllowed = reorderingAllowed;
		firePropertyChange("reorderingAllowed", old, reorderingAllowed); //$NON-NLS-1$
	}

	public boolean getReorderingAllowed() {
		return reorderingAllowed;
	}

	public void setResizingAllowed(boolean resizingAllowed) {
		boolean old = this.resizingAllowed;
		this.resizingAllowed = resizingAllowed;
		firePropertyChange("resizingAllowed", old, resizingAllowed); //$NON-NLS-1$
	}

	public boolean getResizingAllowed() {
		return resizingAllowed;
	}

	public SimpleTimelineResource getDraggedResource() {
		return draggedResource;
	}

	public int getDraggedDistance() {
		return draggedDistance;
	}

	public SimpleTimelineResource getResizingResource() {
		return resizingResource;
	}

	public void setDefaultRenderer(
			SimpleTimelineResourceRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	public SimpleTimelineResourceRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public int resourceAtPoint(Point point) {
		int y = point.y;
		return getResourceModel().getResourceIndexAtY(y);
	}

	public Rectangle getHeaderRect(int resource) {
		Rectangle r = new Rectangle();
		SimpleTimelineResourcesModel cm = getResourceModel();

		r.width = getWidth();

		if (resource < 0) {
		} else if (resource >= cm.getResourceCount()) {
		} else {
			for (int i = 0; i < resource; i++) {
				r.y += cm.getResource(i).getHeight();
			}

			r.height = cm.getResource(resource).getHeight();
		}
		return r;
	}

	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();
		int resource;

		if ((resource = resourceAtPoint(p)) != -1) {
			SimpleTimelineResource aResource = resourceModel
					.getResource(resource);
			SimpleTimelineResourceRenderer renderer = aResource
					.getHeaderRenderer();
			if (renderer == null) {
				renderer = defaultRenderer;
			}
			Component component = renderer
					.getSimpleTimelineResourceRendererComponent(
							getSimpleTimeline(), aResource, resource, false,
							false, false);

			if (component instanceof JComponent) {
				MouseEvent newEvent;
				Rectangle cellRect = getHeaderRect(resource);

				p.translate(-cellRect.x, -cellRect.y);
				newEvent = new MouseEvent(component, event.getID(), event
						.getWhen(), event.getModifiers(), p.x, p.y, event
						.getXOnScreen(), event.getYOnScreen(), event
						.getClickCount(), event.isPopupTrigger(),
						MouseEvent.NOBUTTON);

				tip = ((JComponent) component).getToolTipText(newEvent);
			}
		}

		if (tip == null)
			tip = getToolTipText();

		return tip;
	}

	public SimpleTimelineResourceHeaderUI getUI() {
		return (SimpleTimelineResourceHeaderUI) ui;
	}

	public void setUI(SimpleTimelineResourceHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((SimpleTimelineResourceHeaderUI) UIManager.getUI(this));

		SimpleTimelineResourceRenderer renderer = getDefaultRenderer();
		if (!(renderer instanceof UIResource) && renderer instanceof Component) {
			SwingUtilities.updateComponentTreeUI((Component) renderer);
		}
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setResourceModel(SimpleTimelineResourcesModel resourceModel) {
		if (resourceModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null ResourceModel"); //$NON-NLS-1$
		}
		SimpleTimelineResourcesModel old = this.resourceModel;
		if (resourceModel != old) {
			if (old != null) {
				old.removeResourcesModelListener(this);
			}
			this.resourceModel = resourceModel;
			resourceModel.addResourcesModelListener(this);

			firePropertyChange("resourceModel", old, resourceModel); //$NON-NLS-1$
			resizeAndRepaint();
		}
	}

	public SimpleTimelineResourcesModel getResourceModel() {
		return resourceModel;
	}

	public void resourceAdded(SimpleTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceMarginChanged(ChangeEvent e) {
		resizeAndRepaint();
	}

	public void resourceMoved(SimpleTimelineResourcesModelEvent e) {
		repaint();
	}

	public void resourceRemoved(SimpleTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceSelectionChanged(ListSelectionEvent e) {
	}

	protected SimpleTimelineResourcesModel createDefaultResourceModel() {
		return new DefaultSimpleTimelineResourcesModel();
	}

	protected SimpleTimelineResourceRenderer createDefaultRenderer() {
		return new DefaultSimpleTimelineResourceRenderer();
	}

	protected void initializeLocalVars() {
		setOpaque(true);
		simpleTimeline = null;
		reorderingAllowed = true;
		resizingAllowed = true;
		draggedResource = null;
		draggedDistance = 0;
		resizingResource = null;

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);
		setDefaultRenderer(createDefaultRenderer());
	}

	public void resizeAndRepaint(int resource) {
		Rectangle rect = getHeaderRect(resource);
		revalidate();
		repaint(rect.x, rect.y, rect.width, rect.height);
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public void setDraggedResource(SimpleTimelineResource aResource) {
		draggedResource = aResource;
	}

	public void setDraggedDistance(int distance) {
		draggedDistance = distance;
	}

	public void setResizingResource(SimpleTimelineResource aResource) {
		resizingResource = aResource;
	}
}
