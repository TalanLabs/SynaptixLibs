package com.synaptix.taskmanager.view.swing.graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.CellRendererPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

public abstract class AbstractGraphWithDetailsComponent extends JPanel {

	public static final int START_CELL_SIZE = 40;

	public static final int END_CELL_SIZE = 40;

	public static final int CELL_SIZE = 50;

	public static final int LABEL_SIZE = 15;

	private static final long serialVersionUID = -8916603170766739124L;
	protected final mxGraph graph;
	protected final mxHierarchicalLayout graphLayout;

	private mxGraphComponent mxGraphComponent;

	public AbstractGraphWithDetailsComponent() {
		super(new BorderLayout());

		this.graph = createGraph();
		this.graphLayout = new mxHierarchicalLayout(graph, SwingConstants.WEST);

		this.mxGraphComponent = new mxGraphComponent(graph) {
			@Override
			public mxInteractiveCanvas createCanvas() {
				return createGraphCanvas();
			}
		};
		mxGraphComponent.setToolTips(true);
		mxGraphComponent.setConnectable(false);
		mxGraphComponent.setCenterPage(true);
		mxGraphComponent.setGridVisible(true);
		mxGraphComponent.setCenterPage(true);
		mxGraphComponent.getVerticalScrollBar().setUnitIncrement(16);

		add(mxGraphComponent, BorderLayout.CENTER);

		initalizeDetailsPanel();

		JPanel detailsPanel = createDetailsPanel();
		add(detailsPanel, BorderLayout.SOUTH);

		this.graph.addListener(mxEvent.REPAINT, new mxEventSource.mxIEventListener() {
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				repaint();
			}
		});

		mxGraphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent event) {

				if (event.getButton() == 1) {
					Object selectedValue = getSelectedValue(event.getX(), event.getY());

					if (selectedValue != null) {
						fillDetails(selectedValue);
					} else {
						clearDetails();
					}
				}
			}

		});
	}

	private Object getSelectedValue(int x, int y) {
		Object selectedCell = null;

		mxGraphComponent.getCanvas().setScale(graph.getView().getScale());
		mxGraphComponent.getCanvas().setTranslate(0, 0);
		mxIGraphModel model = graph.getModel();
		Object parent = graph.getDefaultParent();
		int childCount = model.getChildCount(parent);
		for (int i = 0; i < childCount; i++) {
			Object cell = model.getChildAt(parent, i);
			mxGraphView view = graph.getView();
			Rectangle hit = new Rectangle(x, y, 1, 1);
			mxCellState state = view.getState(cell);
			boolean intersects = mxGraphComponent.getCanvas().intersects(mxGraphComponent, hit, state);
			if (intersects) {
				selectedCell = cell;
				break;
			}
		}

		Object value = null;
		if (selectedCell != null && selectedCell instanceof mxCell) {
			mxCell cell = (mxCell) selectedCell;
			if (cell.isVertex()) {
				value = cell.getValue();
			}
		}
		return value;
	}

	/**
	 * Called when no object is selected: clear all detail information.
	 */
	protected abstract void clearDetails();

	protected abstract void initalizeDetailsPanel();

	protected JLabel buildTitleLabel(final String text) {
		JLabel label = new JLabel(text + " :");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		return label;
	}

	/**
	 * Fills the details panel with clicked element information
	 *
	 * @param value
	 *            Object behind the clicked graph element
	 */
	protected abstract void fillDetails(Object value);

	abstract protected JPanel createDetailsPanel();

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

	protected VertexGraphCanvas createGraphCanvas() {
		return new MyGraphCanvas();
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
			rendererPane.paintComponent(g, c, AbstractGraphWithDetailsComponent.this, (int) state.getX() + translate.x, (int) state.getY() + translate.y, (int) state.getWidth(),
					(int) state.getWidth(), true);

			vertexRenderer.setText(state.getLabel());
			Dimension pref = vertexRenderer.getPreferredSize();
			int width = (int) Math.min(state.getWidth() * 2, pref.width);
			int x = (int) (state.getX() + translate.x + state.getWidth() / 2) - width / 2;
			rendererPane.paintComponent(g, vertexRenderer, AbstractGraphWithDetailsComponent.this, x, (int) state.getY() + translate.y + (int) state.getWidth(), width, pref.height, true);
		}
	}
}
