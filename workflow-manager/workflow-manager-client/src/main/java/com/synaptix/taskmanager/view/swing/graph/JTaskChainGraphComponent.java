package com.synaptix.taskmanager.view.swing.graph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.CellRendererPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.component.IComponent;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.taskmanager.antlr.AbstractGraphNode;
import com.synaptix.taskmanager.antlr.GraphCalcHelper;
import com.synaptix.taskmanager.antlr.IdGraphNode;
import com.synaptix.taskmanager.antlr.NextGraphNode;
import com.synaptix.taskmanager.antlr.ParallelGraphNode;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.TaskChainFields;
import com.synaptix.taskmanager.model.TaskTypeFields;

public class JTaskChainGraphComponent extends com.synaptix.taskmanager.view.swing.graph.AbstractGraphWithDetailsComponent {

	private static final long serialVersionUID = 120081943081039182L;
	private JLabel meaningLabel;
	private JLabel codeLabel;
	private JLabel descriptionLabel;

	public JTaskChainGraphComponent() {
		super();

	}

	@Override
	protected void clearDetails() {
		meaningLabel.setText("");
		codeLabel.setText("");
		descriptionLabel.setText("");
	}

	@Override
	protected void initalizeDetailsPanel() {
		meaningLabel = new JLabel(" ");
		codeLabel = new JLabel(" ");
		descriptionLabel = new JLabel(" ");
	}

	@Override
	protected void fillDetails(Object value) {
		ITaskType taskType;
		if (value instanceof ITaskType) {
			taskType = (ITaskType) value;
			meaningLabel.setText(taskType.getMeaning());
			codeLabel.setText(taskType.getCode());
			descriptionLabel.setText(taskType.getDescription());
		} else if (value instanceof ITaskChain) {
			ITaskChain taskChain = (ITaskChain) value;
			meaningLabel.setText(taskChain.getCode());
			codeLabel.setText(taskChain.getCode());
			descriptionLabel.setText(taskChain.getDescription());
		} else {
			clearDetails();
		}
	}

	protected JPanel createDetailsPanel(/* mxCell cell */) {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE,FILL:PREF:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().meaning()), meaningLabel);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().serviceCode()), codeLabel);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().description()));
		builder.append(descriptionLabel, 2);
		return builder.getPanel();
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
					if (value instanceof ITaskChain) {
						return ((ITaskChain) value).getCode();
					} else if (value instanceof ITaskType) {
						ITaskType taskType = (ITaskType) value;
						return taskType.getMeaning() != null ? taskType.getMeaning() : taskType.getCode();
					}
				}
				return super.convertValueToString(cell);
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
	protected VertexGraphCanvas createGraphCanvas() {
		return new MyGraphCanvas();
	}

	public void setGraphRule(String graphRule, final List<ITaskChain> taskChains, boolean global) {
		if (global) {
			graphRule = GraphCalcHelper.replaceId(graphRule, new GraphCalcHelper.IReplaceId() {
				@Override
				public String getOtherId(String id) {
					ITaskChain taskChain = ComponentHelper.findComponentBy(taskChains, TaskChainFields.code().name(), id);
					return taskChain != null ? "(" + taskChain.getGraphRuleReadable() + ")" : id;
				}
			});

			List<ITaskType> taskTypes = new ArrayList<ITaskType>();
			if (taskChains != null && !taskChains.isEmpty()) {
				for (ITaskChain taskChain : taskChains) {
					if (taskChain.getTaskTypes() != null && !taskChain.getTaskTypes().isEmpty()) {
						taskTypes.addAll(taskChain.getTaskTypes());
					}
				}
			}

			updateGraph(graphRule, taskTypes, TaskTypeFields.code().name());
		} else {
			updateGraph(graphRule, taskChains, TaskChainFields.code().name());
		}
	}

	public void setGraphRule(String graphRule, final List<ITaskType> taskTypes) {
		updateGraph(graphRule, taskTypes, TaskTypeFields.code().name());
	}

	public void setGraphRule(String graphRule) {
		updateGraph(graphRule, null, null);
	}

	private <E extends IComponent> void updateGraph(String graphRule, List<E> components, String propertyName) {
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));

			Object startVertex = graph.insertVertex(parent, "start", StaticCommonHelper.getCommonConstantsBundle().start(), 0, 0, START_CELL_SIZE, START_CELL_SIZE);
			Object endVertex = graph.insertVertex(parent, "end", StaticCommonHelper.getCommonConstantsBundle().end(), 0, 0, END_CELL_SIZE, END_CELL_SIZE);

			if (graphRule != null && GraphCalcHelper.isValidGraphRule(graphRule)) {
				AbstractGraphNode node = GraphCalcHelper.buildGraphRule(graphRule);
				CreateVertexResult cr = createVertexs(graph, parent, node, components, propertyName);
				if (cr != null) {
					if (cr.firstVertexs != null && !cr.firstVertexs.isEmpty()) {
						for (Object v3 : cr.firstVertexs) {
							graph.insertEdge(parent, null, "", startVertex, v3);
						}
					}

					if (cr.lastVertexs != null && !cr.lastVertexs.isEmpty()) {
						for (Object v3 : cr.lastVertexs) {
							graph.insertEdge(parent, null, "", v3, endVertex);
						}
					}
				}
			} else {
				graph.insertEdge(parent, null, "", startVertex, endVertex);
			}

			graphLayout.execute(parent);
		} finally {
			graph.getModel().endUpdate();
		}
	}

	private <E extends IComponent> CreateVertexResult createVertexs(mxGraph graph, Object parent, AbstractGraphNode node, List<E> components, String propertyName) {
		CreateVertexResult res = null;
		if (node != null) {
			res = new CreateVertexResult();
			if (node instanceof IdGraphNode) {
				IdGraphNode ign = (IdGraphNode) node;
				E component = ComponentHelper.findComponentBy(components, propertyName, ign.getId());

				Object v1 = graph.insertVertex(parent, null, component != null ? component : ign.getId(), 0, 0, CELL_SIZE, CELL_SIZE + LABEL_SIZE);
				res.firstVertexs = Arrays.asList(v1);
				res.lastVertexs = Arrays.asList(v1);
			} else if (node instanceof ParallelGraphNode) {
				ParallelGraphNode pgn = (ParallelGraphNode) node;
				res.firstVertexs = new ArrayList<Object>();
				res.lastVertexs = new ArrayList<Object>();
				for (AbstractGraphNode subNode : pgn.getNodes()) {
					CreateVertexResult cr = createVertexs(graph, parent, subNode, components, propertyName);
					if (cr != null) {
						res.firstVertexs.addAll(cr.firstVertexs);
						res.lastVertexs.addAll(cr.lastVertexs);
					}
				}
			} else if (node instanceof NextGraphNode) {
				NextGraphNode ngn = (NextGraphNode) node;

				CreateVertexResult firstCr = createVertexs(graph, parent, ngn.getFirstNode(), components, propertyName);
				CreateVertexResult nextCr = createVertexs(graph, parent, ngn.getNextNode(), components, propertyName);

				if (firstCr != null && nextCr != null) {
					if (firstCr.lastVertexs != null && !firstCr.lastVertexs.isEmpty() && nextCr.firstVertexs != null && !nextCr.firstVertexs.isEmpty()) {
						for (Object v1 : firstCr.lastVertexs) {
							for (Object v2 : nextCr.firstVertexs) {
								graph.insertEdge(parent, null, "", v1, v2);
							}
						}
					}
					res.firstVertexs = firstCr.firstVertexs;
					res.lastVertexs = nextCr.lastVertexs;
				}
			}
		}
		return res;
	}

	private class CreateVertexResult {

		List<Object> firstVertexs;

		List<Object> lastVertexs;

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
			} else if (cell.getValue() instanceof ITaskChain) {
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.yellow);
				cellNode.setTypeNode(null);
				text = state.getLabel();
			} else if (cell.getValue() instanceof ITaskType) {
				ITaskType taskType = (ITaskType) cell.getValue();
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.white);
				if (taskType.getNature() != null) {
					switch (taskType.getNature()) {
					case DATA_CHECK:
						cellNode.setTypeNode(JRingNode.TypeNode.dataCheck);
						break;
					case ENRICHMENT:
						cellNode.setTypeNode(JRingNode.TypeNode.enrichment);
						break;
					case EXTERNAL_PROCESS:
						cellNode.setTypeNode(JRingNode.TypeNode.externalProcess);
						break;
					case MANUAL_ENRICHMENT:
						cellNode.setTypeNode(JRingNode.TypeNode.manualEnrichment);
						break;
					case UPDATE_STATUS:
						cellNode.setTypeNode(JRingNode.TypeNode.updateStatus);
						break;
					}
				} else {
					cellNode.setTypeNode(null);
				}

				text = state.getLabel();
			} else {
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.white);
				cellNode.setTypeNode(null);
				text = state.getLabel();
			}
			rendererPane.paintComponent(g, c, JTaskChainGraphComponent.this, (int) state.getX() + translate.x, (int) state.getY() + translate.y, (int) state.getWidth(), (int) state.getWidth(), true);

			vertexRenderer.setText(text);
			Dimension pref = vertexRenderer.getPreferredSize();
			int width = (int) Math.min(state.getWidth() * 2, pref.width);
			int x = (int) (state.getX() + translate.x + state.getWidth() / 2) - width / 2;
			rendererPane.paintComponent(g, vertexRenderer, JTaskChainGraphComponent.this, x, (int) state.getY() + translate.y + (int) state.getWidth(), width, pref.height, true);
		}
	}
}
