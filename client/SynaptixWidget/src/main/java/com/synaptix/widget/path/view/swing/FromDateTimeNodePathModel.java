package com.synaptix.widget.path.view.swing;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalTime;

import com.synaptix.swing.path.AbstractPathModel;
import com.synaptix.widget.util.StaticWidgetHelper;

/**
 * 
 * @author Gaby
 * 
 */
public class FromDateTimeNodePathModel extends AbstractPathModel {

	private List<DateTimeNode> dateTimeNodes = new ArrayList<DateTimeNode>();

	private List<String> nodeNames = new ArrayList<String>();

	private List<String> lineNames = new ArrayList<String>();

	public FromDateTimeNodePathModel() {
		super();
	}

	public void setDateTimeNodes(List<DateTimeNode> dateTimeNodes, List<String> lineNames) {
		this.dateTimeNodes.clear();
		this.lineNames.clear();
		if (dateTimeNodes != null) {
			this.dateTimeNodes.addAll(dateTimeNodes);
		}
		if (lineNames != null) {
			this.lineNames.addAll(lineNames);
		}

		buildNodeNames();

		firePathChanged();
	}

	public DateTimeNode getNode(int index) {
		return dateTimeNodes.get(index);
	}

	public int getNodeCount() {
		return dateTimeNodes.size();
	}

	public String getNodeName(int index) {
		return nodeNames.get(index);
	}

	public String getLineName(int index1, int index2) {
		if (index1 < lineNames.size()) {
			return lineNames.get(index1);
		}
		return null;
	}

	public boolean isSelectedLine(int index1, int index2) {
		return false;
	}

	public boolean isSelectedNode(int index) {
		return false;
	}

	protected void buildNodeNames() {
		this.nodeNames.clear();
		if (dateTimeNodes != null && !dateTimeNodes.isEmpty()) {
			for (int i = 0; i < dateTimeNodes.size(); i++) {
				DateTimeNode node = dateTimeNodes.get(i);

				StringBuilder sb = new StringBuilder(node.getName());
				sb.append("\n");

				boolean arrive = i > 0;
				boolean depart = i < dateTimeNodes.size() - 1;
				boolean passage = node.getPause().getMillis() == 0;
				if (arrive || passage) {
					if (node.getTime() != null) {
						sb.append("J + ");
						sb.append(node.getTime().getDayOfYear() - 1);
						sb.append(" - ");
						sb.append(new LocalTime(node.getTime()).toString(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayTimeFormat()));
					}
				}
				if (arrive && depart && !passage) {
					sb.append(" - ");
				}
				if (depart && !passage) {
					if (node.getTime() != null) {
						sb.append(node.getTime().plusMillis((int) node.getPause().getMillis()).toString(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayDateTimeFormat()));
					}

				}
				nodeNames.add(sb.toString());
			}
		}
	}
}