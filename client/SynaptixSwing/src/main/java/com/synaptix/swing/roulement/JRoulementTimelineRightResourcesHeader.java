package com.synaptix.swing.roulement;

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

import com.synaptix.swing.plaf.RoulementTimelineRightResourceHeaderUI;
import com.synaptix.swing.roulement.event.RoulementTimelineResourcesModelEvent;
import com.synaptix.swing.roulement.event.RoulementTimelineResourcesModelListener;

public class JRoulementTimelineRightResourcesHeader extends JComponent
		implements RoulementTimelineResourcesModelListener {

	private static final long serialVersionUID = 2253293907751629570L;

	private static final String uiClassID = "RoulementTimelineRightRessourceHeaderUI"; //$NON-NLS-1$

	protected JRoulementTimeline roulementTimeline;

	protected RoulementTimelineResourcesModel resourceModel;

	protected boolean reorderingAllowed;

	protected boolean resizingAllowed;

	transient protected RoulementTimelineResource resizingResource;

	transient protected RoulementTimelineResource draggedResource;

	transient protected int draggedDistance;

	private RoulementTimelineResourceRenderer defaultRenderer;

	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicRoulementTimelineRightResourceHeaderUI"); //$NON-NLS-1$
	}

	public JRoulementTimelineRightResourcesHeader(
			RoulementTimelineResourcesModel rm) {
		super();

		setResourceModel(rm);

		initializeLocalVars();

		updateUI();
	}

	public void setRoulementTimeline(JRoulementTimeline roulementTimeline) {
		JRoulementTimeline old = this.roulementTimeline;
		this.roulementTimeline = roulementTimeline;
		firePropertyChange("roulementTimeline", old, roulementTimeline); //$NON-NLS-1$
	}

	public JRoulementTimeline getRoulementTimeline() {
		return roulementTimeline;
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

	public RoulementTimelineResource getDraggedResource() {
		return draggedResource;
	}

	public int getDraggedDistance() {
		return draggedDistance;
	}

	public RoulementTimelineResource getResizingResource() {
		return resizingResource;
	}

	public void setDefaultRenderer(
			RoulementTimelineResourceRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	public RoulementTimelineResourceRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public int resourceAtPoint(Point point) {
		int y = point.y;
		return getResourceModel().getResourceIndexAtY(y);
	}

	public Rectangle getHeaderRect(int resource) {
		Rectangle r = new Rectangle();
		RoulementTimelineResourcesModel cm = getResourceModel();

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
			RoulementTimelineResource aResource = resourceModel
					.getResource(resource);
			RoulementTimelineResourceRenderer renderer = aResource
					.getHeaderRenderer();
			if (renderer == null) {
				renderer = defaultRenderer;
			}
			Component component = renderer
					.getRoulementTimelineLeftResourceRendererComponent(
							getRoulementTimeline(), aResource, resource, false,
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

	public RoulementTimelineRightResourceHeaderUI getUI() {
		return (RoulementTimelineRightResourceHeaderUI) ui;
	}

	public void setUI(RoulementTimelineRightResourceHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((RoulementTimelineRightResourceHeaderUI) UIManager.getUI(this));

		RoulementTimelineResourceRenderer renderer = getDefaultRenderer();
		if (!(renderer instanceof UIResource) && renderer instanceof Component) {
			SwingUtilities.updateComponentTreeUI((Component) renderer);
		}
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setResourceModel(RoulementTimelineResourcesModel resourceModel) {
		if (resourceModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null ResourceModel"); //$NON-NLS-1$
		}
		RoulementTimelineResourcesModel old = this.resourceModel;
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

	public RoulementTimelineResourcesModel getResourceModel() {
		return resourceModel;
	}

	public void resourceAdded(RoulementTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceMarginChanged(ChangeEvent e) {
		resizeAndRepaint();
	}

	public void resourceMoved(RoulementTimelineResourcesModelEvent e) {
		repaint();
	}

	public void resourceRemoved(RoulementTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceSelectionChanged(ListSelectionEvent e) {
	}

	protected RoulementTimelineResourcesModel createDefaultResourceModel() {
		return new DefaultRoulementTimelineResourcesModel();
	}

	protected RoulementTimelineResourceRenderer createDefaultRenderer() {
		return new DefaultRoulementTimelineResourceRenderer();
	}

	protected void initializeLocalVars() {
		setOpaque(true);
		roulementTimeline = null;
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

	public void setDraggedResource(RoulementTimelineResource aResource) {
		draggedResource = aResource;
	}

	public void setDraggedDistance(int distance) {
		draggedDistance = distance;
	}

	public void setResizingResource(RoulementTimelineResource aResource) {
		resizingResource = aResource;
	}
}
