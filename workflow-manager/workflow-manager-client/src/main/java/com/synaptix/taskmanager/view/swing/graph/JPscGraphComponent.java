package com.synaptix.taskmanager.view.swing.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class JPscGraphComponent extends JComponent implements Scrollable {

	private static final long serialVersionUID = -8916603170766739124L;

	public static final int START_CELL_SIZE = 40;

	public static final int END_CELL_SIZE = 40;

	public static final int CELL_SIZE = 50;

	protected final mxGraph graph;

	protected final VertexGraphCanvas canvas;

	private Dimension preferredScrollableViewportSize = new Dimension(1, 1);

	public JPscGraphComponent() {
		super();

		this.graph = createGraph();
		this.canvas = createCanvas();

		this.graph.addListener(mxEvent.REPAINT, new mxEventSource.mxIEventListener() {
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				repaint();
			}
		});
		this.graph.getModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				updatePreferredSize();
			}
		});
	}

	protected mxGraph createGraph() {
		mxGraph graph = new mxGraph() {
			@Override
			public void drawState(mxICanvas canvas, mxCellState state, boolean drawLabel) {
				if (getModel().isVertex(state.getCell()) && canvas instanceof mxImageCanvas && ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof VertexGraphCanvas) {
					((VertexGraphCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas()).drawVertex(state);
				} else if (getModel().isVertex(state.getCell()) && canvas instanceof VertexGraphCanvas) {
					((VertexGraphCanvas) canvas).drawVertex(state);
				} else {
					super.drawState(canvas, state, drawLabel);
				}
			}
		};
		graph.setEnabled(false);
		graph.setCellsEditable(false);
		graph.setCellsLocked(true);
		graph.setVertexLabelsMovable(false);
		graph.setLabelsClipped(false);
		graph.setCellsSelectable(false);
		return graph;
	}

	public final mxGraph getGraph() {
		return graph;
	}

	protected VertexGraphCanvas createCanvas() {
		return new MyGraphCanvas();
	}

	/**
	 * Updates the preferred size for the given scale if the page size should be preferred or the page is visible.
	 */
	public void updatePreferredSize() {
		double scale = graph.getView().getScale();

		mxRectangle bounds = graph.getGraphBounds();
		int border = graph.getBorder();

		Dimension d = new Dimension((int) Math.round(bounds.getX() + bounds.getWidth()) + border + 1, (int) Math.round(bounds.getY() + bounds.getHeight()) + border + 1);
		mxRectangle min = graph.getMinimumGraphSize();

		if (min != null) {
			d.width = (int) Math.max(d.width, Math.round(min.getWidth() * scale));
			d.height = (int) Math.max(d.height, Math.round(min.getHeight() * scale));
		}

		if (!getPreferredSize().equals(d)) {
			setPreferredSize(d);
			setMinimumSize(d);
			revalidate();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		paintGrid(g);

		Graphics2D g2 = (Graphics2D) g.create();

		Dimension size = getSize();
		Dimension prefSize = getPreferredSize();
		boolean useTx = size.width > prefSize.width;
		int tx = useTx ? (size.width - prefSize.width) / 2 : 0;
		boolean useTy = size.height > prefSize.height;
		int ty = useTy ? (size.height - prefSize.height) / 2 : 0;
		g2.translate(tx, ty);

		mxUtils.setAntiAlias(g2, true, true);
		drawGraph(g2, true);

		g2.dispose();
	}

	/**
	 * 
	 */
	public void drawGraph(Graphics2D g, boolean drawLabels) {
		Graphics2D previousGraphics = canvas.getGraphics();
		boolean previousDrawLabels = canvas.isDrawLabels();
		Point previousTranslate = canvas.getTranslate();
		double previousScale = canvas.getScale();

		try {
			canvas.setScale(graph.getView().getScale());
			canvas.setDrawLabels(drawLabels);
			canvas.setTranslate(0, 0);
			canvas.setGraphics(g);

			// Draws the graph using the graphics canvas
			drawFromRootCell();
		} finally {
			canvas.setScale(previousScale);
			canvas.setTranslate(previousTranslate.x, previousTranslate.y);
			canvas.setDrawLabels(previousDrawLabels);
			canvas.setGraphics(previousGraphics);
		}
	}

	/**
	 * Hook to draw the root cell into the canvas.
	 */
	protected void drawFromRootCell() {
		drawCell(canvas, graph.getModel().getRoot());
	}

	/**
	 * 
	 */
	protected boolean hitClip(mxGraphics2DCanvas canvas, mxCellState state) {
		Rectangle rect = getExtendedCellBounds(state);

		return (rect == null || canvas.getGraphics().hitClip(rect.x, rect.y, rect.width, rect.height));
	}

	/**
	 * @param state
	 *            the cached state of the cell whose extended bounds are to be calculated
	 * @return the bounds of the cell, including the label and shadow and allowing for rotation
	 */
	protected Rectangle getExtendedCellBounds(mxCellState state) {
		Rectangle rect = null;

		// Takes rotation into account
		double rotation = mxUtils.getDouble(state.getStyle(), mxConstants.STYLE_ROTATION);
		mxRectangle tmp = mxUtils.getBoundingBox(new mxRectangle(state), rotation);

		// Adds scaled stroke width
		int border = (int) Math.ceil(mxUtils.getDouble(state.getStyle(), mxConstants.STYLE_STROKEWIDTH) * graph.getView().getScale()) + 1;
		tmp.grow(border);

		if (mxUtils.isTrue(state.getStyle(), mxConstants.STYLE_SHADOW)) {
			tmp.setWidth(tmp.getWidth() + mxConstants.SHADOW_OFFSETX);
			tmp.setHeight(tmp.getHeight() + mxConstants.SHADOW_OFFSETX);
		}

		// Adds the bounds of the label
		if (state.getLabelBounds() != null) {
			tmp.add(state.getLabelBounds());
		}

		rect = tmp.getRectangle();
		return rect;
	}

	/**
	 * Draws the given cell onto the specified canvas. This is a modified version of mxGraph.drawCell which paints the label only if the corresponding cell is not being edited and invokes the
	 * cellDrawn hook after all descendants have been painted.
	 * 
	 * @param canvas
	 *            Canvas onto which the cell should be drawn.
	 * @param cell
	 *            Cell that should be drawn onto the canvas.
	 */
	public void drawCell(mxICanvas canvas, Object cell) {
		mxCellState state = graph.getView().getState(cell);

		if (state != null && isCellDisplayable(state.getCell()) && (!(canvas instanceof mxGraphics2DCanvas) || hitClip((mxGraphics2DCanvas) canvas, state))) {
			graph.drawState(canvas, state, true);
		}

		// Handles special ordering for edges (all in foreground
		// or background) or draws all children in order
		boolean edgesFirst = graph.isKeepEdgesInBackground();
		boolean edgesLast = graph.isKeepEdgesInForeground();

		if (edgesFirst) {
			drawChildren(cell, true, false);
		}

		drawChildren(cell, !edgesFirst && !edgesLast, true);

		if (edgesLast) {
			drawChildren(cell, true, false);
		}
	}

	/**
	 * Draws the child edges and/or all other children in the given cell depending on the boolean arguments.
	 */
	protected void drawChildren(Object cell, boolean edges, boolean others) {
		mxIGraphModel model = graph.getModel();
		int childCount = model.getChildCount(cell);

		for (int i = 0; i < childCount; i++) {
			Object child = model.getChildAt(cell, i);
			boolean isEdge = model.isEdge(child);

			if ((others && !isEdge) || (edges && isEdge)) {
				drawCell(canvas, model.getChildAt(cell, i));
			}
		}
	}

	/**
	 * Returns true if the given cell is not the current root or the root in the model. This can be overridden to not render certain cells in the graph display.
	 */
	protected boolean isCellDisplayable(Object cell) {
		return cell != graph.getView().getCurrentRoot() && cell != graph.getModel().getRoot();
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return preferredScrollableViewportSize;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return getParent() instanceof JViewport && (((JViewport) getParent()).getWidth() > getPreferredSize().width);
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return getParent() instanceof JViewport && (((JViewport) getParent()).getHeight() > getPreferredSize().height);
	}

	protected void paintGrid(Graphics g) {
		g.setColor(new Color(192, 192, 192));
		Rectangle clip = g.getClipBounds();

		double left = clip.getX();
		double top = clip.getY();
		double right = left + clip.getWidth();
		double bottom = top + clip.getHeight();

		// Double the grid line spacing if smaller than half the gridsize
		int gridSize = graph.getGridSize();
		int minStepping = gridSize;

		// Smaller stepping for certain styles
		minStepping /= 2;

		// Fetches some global display state information
		mxPoint trans = graph.getView().getTranslate();
		double scale = graph.getView().getScale();
		double tx = trans.getX() * scale;
		double ty = trans.getY() * scale;

		// Sets the distance of the grid lines in pixels
		double stepping = gridSize * scale;

		if (stepping < minStepping) {
			int count = (int) Math.round(Math.ceil(minStepping / stepping) / 2) * 2;
			stepping = count * stepping;
		}

		double xs = Math.floor((left - tx) / stepping) * stepping + tx;
		double xe = Math.ceil(right / stepping) * stepping;
		double ys = Math.floor((top - ty) / stepping) * stepping + ty;
		double ye = Math.ceil(bottom / stepping) * stepping;

		for (double x = xs; x <= xe; x += stepping) {

			for (double y = ys; y <= ye; y += stepping) {
				// FIXME: Workaround for rounding errors when adding
				// stepping to
				// xs or ys multiple times (leads to double grid lines
				// when zoom
				// is set to eg. 121%)
				x = Math.round((x - tx) / stepping) * stepping + tx;
				y = Math.round((y - ty) / stepping) * stepping + ty;

				int ix = (int) Math.round(x);
				int iy = (int) Math.round(y);
				g.drawLine(ix, iy, ix, iy);
			}
		}
	}

	public abstract class VertexGraphCanvas extends mxInteractiveCanvas {

		public abstract void drawVertex(mxCellState state);

	}

	private class MyGraphCanvas extends VertexGraphCanvas {

		private CellRendererPane rendererPane;

		private JLabel vertexRenderer;

		private JRingNode startCellNode;

		private JRingNode endCellNode;

		private JRingNode cellNode;

		public MyGraphCanvas() {
			super();

			startCellNode = new JRingNode(START_CELL_SIZE);
			startCellNode.setColorNode(JRingNode.ColorNode.blue);

			endCellNode = new JRingNode(END_CELL_SIZE);
			endCellNode.setColorNode(JRingNode.ColorNode.blue);

			cellNode = new JRingNode(CELL_SIZE);

			vertexRenderer = new JLabel();
			vertexRenderer.setHorizontalAlignment(JLabel.CENTER);
			vertexRenderer.setOpaque(false);
			vertexRenderer.setFont(new Font("Dialog", Font.PLAIN, 9));

			rendererPane = new CellRendererPane();
		}

		@Override
		public void drawVertex(mxCellState state) {
			Component c;

			mxCell cell = (mxCell) state.getCell();
			if ("start".equals(cell.getId())) {
				c = startCellNode;
			} else if ("end".equals(cell.getId())) {
				c = endCellNode;
			} else {
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.white);
				cellNode.setTypeNode(null);
			}
			rendererPane.paintComponent(g, c, JPscGraphComponent.this, (int) state.getX() + translate.x, (int) state.getY() + translate.y, (int) state.getWidth(), (int) state.getHeight(), true);

			vertexRenderer.setText(state.getLabel());
			Dimension pref = vertexRenderer.getPreferredSize();
			int width = (int) Math.min(state.getWidth() * 2, pref.width);
			int x = (int) (state.getX() + translate.x + state.getWidth() / 2) - width / 2;
			rendererPane.paintComponent(g, vertexRenderer, JPscGraphComponent.this, x, (int) state.getY() + translate.y + (int) state.getHeight(), width, pref.height, true);
		}
	}
}