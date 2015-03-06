package com.synaptix.swing.timeline;

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

import com.synaptix.swing.JTimeline;
import com.synaptix.swing.event.TimelineRessourceModelEvent;
import com.synaptix.swing.event.TimelineRessourceModelListener;
import com.synaptix.swing.plaf.TimelineRessourceHeaderUI;

public class JTimelineRessourceHeader extends JComponent implements
		TimelineRessourceModelListener {

	private static final long serialVersionUID = 2253293907751629570L;

	private static final String uiClassID = "TimelineRessourceHeaderUI"; //$NON-NLS-1$

	protected JTimeline timeline;

	protected TimelineRessourceModel ressourceModel;

	protected boolean reorderingAllowed;

	protected boolean resizingAllowed;

	transient protected TimelineRessource resizingRessource;

	transient protected TimelineRessource draggedRessource;

	transient protected int draggedDistance;

	private TimelineRessourceRenderer defaultRenderer;

	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicTimelineRessourceHeaderUI"); //$NON-NLS-1$
	}

	public JTimelineRessourceHeader() {
		this(null);
	}

	public JTimelineRessourceHeader(TimelineRessourceModel rm) {
		super();

		if (rm == null)
			rm = createDefaultRessourceModel();
		setRessourceModel(rm);

		initializeLocalVars();

		updateUI();
	}

	public void setTimeline(JTimeline timeline) {
		JTimeline old = this.timeline;
		this.timeline = timeline;
		firePropertyChange("timeline", old, timeline); //$NON-NLS-1$
	}

	public JTimeline getTimeline() {
		return timeline;
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

	public TimelineRessource getDraggedRessource() {
		return draggedRessource;
	}

	public int getDraggedDistance() {
		return draggedDistance;
	}

	public TimelineRessource getResizingRessource() {
		return resizingRessource;
	}

	public void setDefaultRenderer(TimelineRessourceRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	public TimelineRessourceRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public int ressourceAtPoint(Point point) {
		int y = point.y;
		return getRessourceModel().getRessourceIndexAtY(y);
	}

	public Rectangle getHeaderRect(int ressource) {
		Rectangle r = new Rectangle();
		TimelineRessourceModel cm = getRessourceModel();

		r.width = getWidth();

		if (ressource < 0) {
		} else if (ressource >= cm.getRessourceCount()) {
		} else {
			for (int i = 0; i < ressource; i++) {
				r.y += cm.getRessource(i).getHeight();
			}

			r.height = cm.getRessource(ressource).getHeight();
		}
		return r;
	}

	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();
		int ressource;

		if ((ressource = ressourceAtPoint(p)) != -1) {
			TimelineRessource aRessource = ressourceModel
					.getRessource(ressource);
			TimelineRessourceRenderer renderer = aRessource.getHeaderRenderer();
			if (renderer == null) {
				renderer = defaultRenderer;
			}
			Component component = renderer
					.getTimelineRessourceRendererComponent(getTimeline(),
							aRessource, ressource,false,false);

			if (component instanceof JComponent) {
				MouseEvent newEvent;
				Rectangle cellRect = getHeaderRect(ressource);

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

	public TimelineRessourceHeaderUI getUI() {
		return (TimelineRessourceHeaderUI) ui;
	}

	public void setUI(TimelineRessourceHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((TimelineRessourceHeaderUI) UIManager.getUI(this));

		TimelineRessourceRenderer renderer = getDefaultRenderer();
		if (!(renderer instanceof UIResource) && renderer instanceof Component) {
			SwingUtilities.updateComponentTreeUI((Component) renderer);
		}
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setRessourceModel(TimelineRessourceModel ressourceModel) {
		if (ressourceModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null RessourceModel"); //$NON-NLS-1$
		}
		TimelineRessourceModel old = this.ressourceModel;
		if (ressourceModel != old) {
			if (old != null) {
				old.removeRessourceModelListener(this);
			}
			this.ressourceModel = ressourceModel;
			ressourceModel.addRessourceModelListener(this);

			firePropertyChange("ressourceModel", old, ressourceModel); //$NON-NLS-1$
			resizeAndRepaint();
		}
	}

	public TimelineRessourceModel getRessourceModel() {
		return ressourceModel;
	}

	public void ressourceAdded(TimelineRessourceModelEvent e) {
		resizeAndRepaint();
	}

	public void ressourceMarginChanged(ChangeEvent e) {
		resizeAndRepaint();
	}

	public void ressourceMoved(TimelineRessourceModelEvent e) {
		repaint();
	}

	public void ressourceRemoved(TimelineRessourceModelEvent e) {
		resizeAndRepaint();
	}

	public void ressourceSelectionChanged(ListSelectionEvent e) {
	}

	protected TimelineRessourceModel createDefaultRessourceModel() {
		return new DefaultTimelineRessourceModel();
	}

	protected TimelineRessourceRenderer createDefaultRenderer() {
		return new DefaultTimelineRessourceRenderer();
	}

	protected void initializeLocalVars() {
		setOpaque(true);
		timeline = null;
		reorderingAllowed = true;
		resizingAllowed = true;
		draggedRessource = null;
		draggedDistance = 0;
		resizingRessource = null;

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);
		setDefaultRenderer(createDefaultRenderer());
	}

	public void resizeAndRepaint(int ressource) {
		Rectangle rect = getHeaderRect(ressource);
		revalidate();
		repaint(rect.x, rect.y, rect.width, rect.height);
	}
	
	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public void setDraggedRessource(TimelineRessource aRessource) {
		draggedRessource = aRessource;
	}

	public void setDraggedDistance(int distance) {
		draggedDistance = distance;
	}

	public void setResizingRessource(TimelineRessource aRessource) {
		resizingRessource = aRessource;
	}
}
