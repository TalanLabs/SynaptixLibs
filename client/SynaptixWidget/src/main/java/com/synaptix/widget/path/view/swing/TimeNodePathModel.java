package com.synaptix.widget.path.view.swing;

import com.synaptix.widget.util.StaticWidgetHelper;

/**
 * 
 * @author Gaby
 * 
 */
public class TimeNodePathModel extends AbstractNodePathModel<TimeNode> {

	@Override
	protected void buildNodeNames() {
		this.nodeNames.clear();
		if (nodes != null && !nodes.isEmpty()) {
			for (int i = 0; i < nodes.size(); i++) {
				TimeNode node = nodes.get(i);

				StringBuilder sb = new StringBuilder(node.getName());
				sb.append("\n");

				boolean arrive = i > 0;
				boolean depart = i < nodes.size() - 1;
				boolean passage = node.getPause() == null || node.getPause().getMillis() == 0;
				if (arrive || passage) {
					sb.append(node.getTime().toString(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayTimeFormat()));
				}
				if (arrive && depart && !passage) {
					sb.append(" - ");
				}
				if (depart && !passage) {
					sb.append(node.getTime().plusMillis((int) node.getPause().getMillis()).toString(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayTimeFormat()));
				}
				nodeNames.add(sb.toString());
			}
		}
	}
}