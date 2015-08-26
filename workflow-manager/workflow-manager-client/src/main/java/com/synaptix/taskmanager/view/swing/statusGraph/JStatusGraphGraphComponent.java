package com.synaptix.taskmanager.view.swing.statusGraph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.CellRendererPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.view.swing.graph.JPscGraphComponent;
import com.synaptix.taskmanager.view.swing.graph.JRingNode;

public class JStatusGraphGraphComponent extends JPscGraphComponent {

	private static final long serialVersionUID = 120081943081039182L;

	private final mxHierarchicalLayout graphLayout;

	public JStatusGraphGraphComponent() {
		super();

		this.graphLayout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
	}

	@Override
	protected mxGraph createGraph() {
		mxGraph graph = new mxGraph() {
			public void drawState(mxICanvas canvas, mxCellState state, boolean drawLabel) {
				if (getModel().isVertex(state.getCell()) && canvas instanceof mxImageCanvas && ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof VertexGraphCanvas) {
					((VertexGraphCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas()).drawVertex(state);
				} else if (getModel().isVertex(state.getCell()) && canvas instanceof VertexGraphCanvas) {
					((VertexGraphCanvas) canvas).drawVertex(state);
				} else {
					super.drawState(canvas, state, drawLabel);
				}
			}

			@Override
			public String convertValueToString(Object cell) {
				if (cell instanceof mxCell) {
					Object value = ((mxCell) cell).getValue();
					if (value instanceof IStatusGraph) {
						IStatusGraph statusGraph = (IStatusGraph) value;
						IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(statusGraph.getObjectType());
						if (objectType != null) {
							return getText(objectType, statusGraph.getNextStatus());
						}
					}
				}
				return super.convertValueToString(cell);
			}

			private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> String getText(
					IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
				if (objectType != null) {
					return objectType.getStatusMeaning(Enum.valueOf(objectType.getStatusClass(), value));
				} else {
					return value;
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

	@Override
	protected VertexGraphCanvas createCanvas() {
		return new MyGraphCanvas();
	}

	public void setStatusGraphs(List<IStatusGraph> statusGraphs) {
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));

			Object startVertex = graph.insertVertex(parent, "start", StaticCommonHelper.getCommonConstantsBundle().start(), 0, 0, START_CELL_SIZE, START_CELL_SIZE);
			Object endVertex = graph.insertVertex(parent, "end", StaticCommonHelper.getCommonConstantsBundle().end(), 0, 0, END_CELL_SIZE, END_CELL_SIZE);

			if (statusGraphs != null && !statusGraphs.isEmpty()) {
				boolean start = false;
				boolean end = false;

				Map<IStatusGraph, Object> cellMap = new HashMap<IStatusGraph, Object>();
				for (IStatusGraph statusGraph : statusGraphs) {
					Object v2 = graph.insertVertex(parent, null, statusGraph, 0, 0, CELL_SIZE, CELL_SIZE);
					cellMap.put(statusGraph, v2);

					if (statusGraph.getCurrentStatus() == null) {
						start = true;
						graph.insertEdge(parent, null, "", startVertex, v2);
					}
				}

				for (IStatusGraph statusGraph : statusGraphs) {
					Object v2 = cellMap.get(statusGraph);

					boolean last = true;
					for (IStatusGraph nextStatusGraph : statusGraphs) {
						if (statusGraph.getNextStatus().equals(nextStatusGraph.getCurrentStatus())) {
							Object v3 = cellMap.get(nextStatusGraph);
							graph.insertEdge(parent, null, "", v2, v3);
							last = false;
						}
					}
					if (last) {
						graph.insertEdge(parent, null, "", v2, endVertex);
						end = true;
					}
				}

				if (!start || !end) {
					graph.insertEdge(parent, null, "", startVertex, endVertex);
				}
			} else {
				graph.insertEdge(parent, null, "", startVertex, endVertex);
			}

			graphLayout.execute(parent);
		} finally {
			graph.getModel().endUpdate();
		}
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

		public void drawVertex(mxCellState state) {
			Component c;
			String text;
			mxCell cell = (mxCell) state.getCell();
			if ("start".equals(cell.getId())) {
				c = startCellNode;
				text = state.getLabel();
			} else if ("end".equals(cell.getId())) {
				c = endCellNode;
				text = state.getLabel();
			} else if (cell.getValue() instanceof IStatusGraph) {
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.white);
				cellNode.setTypeNode(JRingNode.TypeNode.updateStatus);

				text = state.getLabel();
			} else {
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.white);
				cellNode.setTypeNode(null);
				text = state.getLabel();
			}
			rendererPane.paintComponent(g, c, JStatusGraphGraphComponent.this, (int) state.getX() + translate.x, (int) state.getY() + translate.y, (int) state.getWidth(), (int) state.getHeight(),
					true);

			vertexRenderer.setText(text);
			Dimension pref = vertexRenderer.getPreferredSize();
			int width = (int) Math.min(state.getWidth() * 2, pref.width);
			int x = (int) (state.getX() + translate.x + state.getWidth() / 2) - width / 2;
			rendererPane.paintComponent(g, vertexRenderer, JStatusGraphGraphComponent.this, x, (int) state.getY() + translate.y + (int) state.getHeight(), width, pref.height, true);
		}
	}
}
