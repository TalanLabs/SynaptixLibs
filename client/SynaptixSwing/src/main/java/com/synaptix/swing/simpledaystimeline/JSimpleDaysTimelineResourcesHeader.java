package com.synaptix.swing.simpledaystimeline;

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

import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.event.SimpleDaysTimelineResourcesModelEvent;
import com.synaptix.swing.event.SimpleDaysTimelineResourcesModelListener;
import com.synaptix.swing.plaf.SimpleDaysTimelineResourceHeaderUI;

public class JSimpleDaysTimelineResourcesHeader extends JComponent implements
		SimpleDaysTimelineResourcesModelListener {

	private static final long serialVersionUID = 2253293907751629570L;

	private static final String uiClassID = "SimpleDaysTimelineRessourceHeaderUI"; //$NON-NLS-1$

	protected JSimpleDaysTimeline simpleDaysTimeline;

	protected SimpleDaysTimelineResourcesModel resourceModel;

	protected boolean reorderingAllowed;

	protected boolean resizingAllowed;

	transient protected SimpleDaysTimelineResource resizingResource;

	transient protected SimpleDaysTimelineResource draggedResource;

	transient protected int draggedDistance;

	private SimpleDaysTimelineResourceRenderer defaultRenderer;

	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicSimpleDaysTimelineResourceHeaderUI"); //$NON-NLS-1$
	}

	public JSimpleDaysTimelineResourcesHeader(
			SimpleDaysTimelineResourcesModel rm) {
		super();
		
		setResourceModel(rm);

		initializeLocalVars();

		updateUI();
	}

	public void setSimpleDaysTimeline(JSimpleDaysTimeline simpleDaysTimeline) {
		JSimpleDaysTimeline old = this.simpleDaysTimeline;
		this.simpleDaysTimeline = simpleDaysTimeline;
		firePropertyChange("simpleDaysTimeline", old, simpleDaysTimeline); //$NON-NLS-1$
	}

	public JSimpleDaysTimeline getSimpleDaysTimeline() {
		return simpleDaysTimeline;
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

	public SimpleDaysTimelineResource getDraggedResource() {
		return draggedResource;
	}

	public int getDraggedDistance() {
		return draggedDistance;
	}

	public SimpleDaysTimelineResource getResizingResource() {
		return resizingResource;
	}

	public void setDefaultRenderer(
			SimpleDaysTimelineResourceRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	public SimpleDaysTimelineResourceRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public int resourceAtPoint(Point point) {
		int y = point.y;
		return getResourceModel().getResourceIndexAtY(y);
	}

	public Rectangle getHeaderRect(int resource) {
		Rectangle r = new Rectangle();
		SimpleDaysTimelineResourcesModel cm = getResourceModel();

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
			SimpleDaysTimelineResource aResource = resourceModel
					.getResource(resource);
			SimpleDaysTimelineResourceRenderer renderer = aResource
					.getHeaderRenderer();
			if (renderer == null) {
				renderer = defaultRenderer;
			}
			Component component = renderer
					.getSimpleDaysTimelineResourceRendererComponent(
							getSimpleDaysTimeline(), aResource, resource,
							false, false, false);

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

	public SimpleDaysTimelineResourceHeaderUI getUI() {
		return (SimpleDaysTimelineResourceHeaderUI) ui;
	}

	public void setUI(SimpleDaysTimelineResourceHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((SimpleDaysTimelineResourceHeaderUI) UIManager.getUI(this));

		SimpleDaysTimelineResourceRenderer renderer = getDefaultRenderer();
		if (!(renderer instanceof UIResource) && renderer instanceof Component) {
			SwingUtilities.updateComponentTreeUI((Component) renderer);
		}
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setResourceModel(SimpleDaysTimelineResourcesModel resourceModel) {
		if (resourceModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null ResourceModel"); //$NON-NLS-1$
		}
		SimpleDaysTimelineResourcesModel old = this.resourceModel;
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

	public SimpleDaysTimelineResourcesModel getResourceModel() {
		return resourceModel;
	}

	public void resourceAdded(SimpleDaysTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceMarginChanged(ChangeEvent e) {
		resizeAndRepaint();
	}

	public void resourceMoved(SimpleDaysTimelineResourcesModelEvent e) {
		repaint();
	}

	public void resourceRemoved(SimpleDaysTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceSelectionChanged(ListSelectionEvent e) {
	}

	protected SimpleDaysTimelineResourcesModel createDefaultResourceModel() {
		return new DefaultSimpleDaysTimelineResourcesModel();
	}

	protected SimpleDaysTimelineResourceRenderer createDefaultRenderer() {
		return new DefaultSimpleDaysTimelineResourceRenderer();
	}

	protected void initializeLocalVars() {
		setOpaque(true);
		simpleDaysTimeline = null;
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

	public void setDraggedResource(SimpleDaysTimelineResource aResource) {
		draggedResource = aResource;
	}

	public void setDraggedDistance(int distance) {
		draggedDistance = distance;
	}

	public void setResizingResource(SimpleDaysTimelineResource aResource) {
		resizingResource = aResource;
	}
}
