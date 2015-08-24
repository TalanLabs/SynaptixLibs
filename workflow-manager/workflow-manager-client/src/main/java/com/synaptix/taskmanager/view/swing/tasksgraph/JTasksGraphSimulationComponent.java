package com.synaptix.taskmanager.view.swing.tasksgraph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskChainCriteria;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.TaskTypeFields;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.view.swing.graph.AbstractGraphWithDetailsComponent;
import com.synaptix.taskmanager.view.swing.graph.JRingNode;

public class JTasksGraphSimulationComponent extends AbstractGraphWithDetailsComponent {

	private static final long serialVersionUID = 120081943081039182L;

	private JLabel meaningLabel;
	private JLabel codeLabel;
	private JLabel descriptionLabel;

	public JTasksGraphSimulationComponent() {
		super();

	}

	@Override
	protected void clearDetails() {
		meaningLabel.setText(null);
		codeLabel.setText(null);
		descriptionLabel.setText(null);
	}

	@Override
	protected void initalizeDetailsPanel() {
		meaningLabel = new JLabel();
		codeLabel = new JLabel();
		descriptionLabel = new JLabel();
	}

	@Override
	protected void fillDetails(Object value) {
		ITaskType taskType = null;
		if (value instanceof IStatusGraph) {
			IStatusGraph statusGraph = (IStatusGraph) value;
			taskType = statusGraph.getTaskType();
		} else if (value instanceof ITaskType) {
			taskType = (ITaskType) value;
		}

		if (taskType != null) {
			meaningLabel.setText(taskType.getMeaning());
			codeLabel.setText(taskType.getCode());
			descriptionLabel.setText(taskType.getDescription());
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
						ITaskType taskType = ((ITaskType) value);
						return taskType.getMeaning() != null ? taskType.getMeaning() : taskType.getCode();
					} else if (value instanceof IStatusGraph) {
						IStatusGraph statusGraph = (IStatusGraph) value;
						if (statusGraph.getTaskType() != null) {
							return statusGraph.getTaskType().getMeaning() != null ? statusGraph.getTaskType().getMeaning() : statusGraph.getTaskType().getCode();
						} else {
							IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(statusGraph.getObjectType());
							if (objectType != null) {
								return getStatusText(objectType, statusGraph.getNextStatus());
							}
						}
					} else if (value instanceof Class<?>) {
						@SuppressWarnings("unchecked")
						Class<? extends ITaskObject<?>> objectTypeClass = (Class<? extends ITaskObject<?>>) value;
						IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(objectTypeClass);
						return getText(objectType, objectTypeClass);
					}
				}
				return super.convertValueToString(cell);
			}

			private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> String getStatusText(
					IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
				if (objectType != null) {
					return objectType.getStatusMeaning(Enum.valueOf(objectType.getStatusClass(), value));
				} else {
					return value;
				}
			}

			private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> String getText(
					IObjectType<E, F, ManagerRole, ExecutantRole> objectType, Class<? extends ITaskObject<?>> objectTypeClass) {
				if (objectType != null) {
					return objectType.getName();
				} else {
					return objectTypeClass.getName();
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
	protected VertexGraphCanvas createGraphCanvas() {
		return new MyGraphCanvas();
	}

	public void setGraphRule(List<IStatusGraph> statusGraphs, List<ITaskChainCriteria<? extends Enum<?>>> taskChainCriterias, Class<? extends ITaskObject<?>> objectType) {
		updateGraph(statusGraphs, taskChainCriterias, objectType);
	}

	private void updateGraph(List<IStatusGraph> statusGraphs, List<ITaskChainCriteria<? extends Enum<?>>> taskChainCriterias, Class<? extends ITaskObject<?>> objectType) {
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try {
			CellMapping firstCellMapping = new CellMapping();
			Map<IStatusGraph, Object> lastCellMap = new HashMap<IStatusGraph, Object>();
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));

			Object startVertex = graph.insertVertex(parent, "start", objectType, 0, 0, START_CELL_SIZE, START_CELL_SIZE);
			boolean startVertexNoLinked = true;
			if (statusGraphs != null) {
				for (IStatusGraph statusGraph : statusGraphs) {
					ITaskChainCriteria<? extends Enum<?>> taskChainCriteria = selectTaskChainCriteria(statusGraph, taskChainCriterias);
					Object statusUpdateVertex = graph.insertVertex(parent, null, statusGraph, 0, 0, CELL_SIZE, CELL_SIZE);
					lastCellMap.put(statusGraph, statusUpdateVertex);

					if (taskChainCriteria != null && taskChainCriteria.getGraphRule() != null && GraphCalcHelper.isValidGraphRule(taskChainCriteria.getGraphRule())) {
						if (taskChainCriteria.getGraphRule() != null && GraphCalcHelper.isValidGraphRule(taskChainCriteria.getGraphRule())) {
							AbstractGraphNode node = GraphCalcHelper.buildGraphRule(taskChainCriteria.getGraphRule());
							CreateVertexResult cr = createVertexs(graph, parent, node, taskChainCriteria.getTaskChains(), TaskTypeFields.id().name());
							if (cr != null) {
								if (cr.firstVertexs != null && !cr.firstVertexs.isEmpty()) {
									for (Object v3 : cr.firstVertexs) {
										firstCellMapping.add(statusGraph, v3);
										if (statusGraph.getCurrentStatus() == null) {
											graph.insertEdge(parent, null, "", startVertex, v3);
											startVertexNoLinked = false;
										}
									}
								}

								if (cr.lastVertexs != null && !cr.lastVertexs.isEmpty()) {
									for (Object v3 : cr.lastVertexs) {
										graph.insertEdge(parent, null, "", v3, statusUpdateVertex);
									}
								}
							}
						}
					} else {
						firstCellMapping.add(statusGraph, statusUpdateVertex);
					}
				}
				for (IStatusGraph statusGraph : statusGraphs) {
					Object v2 = lastCellMap.get(statusGraph);

					boolean first = true;
					for (IStatusGraph prevStatusGraph : statusGraphs) {
						if (prevStatusGraph.getNextStatus().equals(statusGraph.getCurrentStatus())) {
							first = false;
						}
					}
					if (first && startVertexNoLinked) {
						graph.insertEdge(parent, null, "", startVertex, v2);
					}
				}
				for (IStatusGraph statusGraph : statusGraphs) {
					Object v2 = lastCellMap.get(statusGraph);

					for (IStatusGraph nextStatusGraph : statusGraphs) {
						if (statusGraph.getNextStatus().equals(nextStatusGraph.getCurrentStatus())) {
							List<Object> vertexes = firstCellMapping.getValues(nextStatusGraph);
							for (Object v3 : vertexes) {
								graph.insertEdge(parent, null, "", v2, v3);
							}
						}
					}
				}
			}

			graphLayout.execute(parent);
		} finally {
			graph.getModel().endUpdate();
		}
	}

	private ITaskChainCriteria<? extends Enum<?>> selectTaskChainCriteria(IStatusGraph statusGraph, List<ITaskChainCriteria<? extends Enum<?>>> taskChainCriterias) {
		ITaskChainCriteria<? extends Enum<?>> taskChainCriteriaToReturn = null;
		for (ITaskChainCriteria<? extends Enum<?>> taskChainCriteria : taskChainCriterias) {
			if (statusGraph != null
					&& taskChainCriteria != null
					&& (statusGraph.getCurrentStatus() != null && taskChainCriteria.getCurrentStatus() != null
							&& statusGraph.getCurrentStatus().equals(taskChainCriteria.getCurrentStatus().toString()) || (statusGraph.getCurrentStatus() == null && taskChainCriteria
							.getCurrentStatus() == null))
					&& (statusGraph.getNextStatus() != null && taskChainCriteria.getNextStatus() != null && statusGraph.getNextStatus().equals(taskChainCriteria.getNextStatus().toString()) || (statusGraph
							.getNextStatus() == null && taskChainCriteria.getNextStatus() == null))) {
				taskChainCriteriaToReturn = taskChainCriteria;
			}
		}
		return taskChainCriteriaToReturn;
	}

	private <E extends IComponent> CreateVertexResult createVertexs(mxGraph graph, Object parent, AbstractGraphNode node, List<E> components, String propertyName) {
		CreateVertexResult res = null;
		if (node != null) {
			res = new CreateVertexResult();
			if (node instanceof IdGraphNode) {
				IdGraphNode ign = (IdGraphNode) node;
				E component = ComponentHelper.findComponentBy(components, propertyName, ign.getId());

				if (component == null) {
					if (ign.getId() != null && components != null && !components.isEmpty()) {
						Iterator<E> it = components.iterator();
						while (it.hasNext() && component == null) {
							E findComponent = it.next();
							Object key = findComponent.straightGetProperty(propertyName);
							if (key != null && key.toString().equals(ign.getId())) {
								component = findComponent;
							}
						}
					}
				}
				if (component instanceof ITaskChain) {
					ITaskChain taskChain = (ITaskChain) component;
					if (taskChain.getGraphRule() != null && GraphCalcHelper.isValidGraphRule(taskChain.getGraphRule())) {
						AbstractGraphNode nodeChain = GraphCalcHelper.buildGraphRule(taskChain.getGraphRule());
						CreateVertexResult cr = createVertexs(graph, parent, nodeChain, taskChain.getTaskTypes(), TaskTypeFields.id().name());
						res.firstVertexs = cr.firstVertexs;
						res.lastVertexs = cr.lastVertexs;
					}

				} else {
					Object v1 = graph.insertVertex(parent, null, component != null ? component : ign.getId(), 0, 0, CELL_SIZE, CELL_SIZE + LABEL_SIZE);
					res.firstVertexs = Arrays.asList(v1);
					res.lastVertexs = Arrays.asList(v1);
				}
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

	private class CellMapping {

		List<IStatusGraph> statusGraphs;

		List<Object> linkedVertexs;

		public CellMapping() {
			statusGraphs = new ArrayList<IStatusGraph>();
			linkedVertexs = new ArrayList<Object>();
		}

		public void add(IStatusGraph status, Object vertex) {
			statusGraphs.add(status);
			linkedVertexs.add(vertex);
		}

		public List<Object> getValues(IStatusGraph statusToFind) {
			List<Object> values = new ArrayList<Object>();
			int i = 0;
			for (IStatusGraph status : statusGraphs) {
				if (status.equals(statusToFind)) {
					values.add(linkedVertexs.get(i));
				}
				i++;
			}
			return values;
		}
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
			} else if (cell.getValue() instanceof IStatusGraph) {
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.white);
				cellNode.setTypeNode(JRingNode.TypeNode.updateStatus);

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
			rendererPane.paintComponent(g, c, JTasksGraphSimulationComponent.this, (int) state.getX() + translate.x, (int) state.getY() + translate.y, (int) state.getWidth(), (int) state.getWidth(),
					true);

			vertexRenderer.setText(text);
			Dimension pref = vertexRenderer.getPreferredSize();
			int width = (int) Math.min(state.getWidth() * 2, pref.width);
			int x = (int) (state.getX() + translate.x + state.getWidth() / 2) - width / 2;
			rendererPane.paintComponent(g, vertexRenderer, JTasksGraphSimulationComponent.this, x, (int) state.getY() + translate.y + (int) state.getWidth(), width, pref.height, true);
		}
	}
}
