package com.synaptix.widget.path.view.swing;

import com.synaptix.widget.util.StaticWidgetHelper;

/**
 * 
 * @author Gaby
 * 
 */
public class DateTimeNodePathModel extends AbstractNodePathModel<DateTimeNode> {

	public DateTimeNodePathModel() {
		super();
	}

	@Override
	protected void buildNodeNames() {
		this.nodeNames.clear();
		if (nodes != null && !nodes.isEmpty()) {
			for (int i = 0; i < nodes.size(); i++) {
				DateTimeNode node = nodes.get(i);

				StringBuilder sb = new StringBuilder(node.getName() != null ? node.getName() : "");
				sb.append("\n");

				boolean arrive = i > 0;
				boolean depart = i < nodes.size() - 1;
				boolean passage = node.getPause() == null || node.getPause().getMillis() == 0;
				if (arrive || passage) {
					sb.append(node.getTime().toString(getDateTimeFormat()));

				}
				if (arrive && depart && !passage) {
					sb.append(" - ");
				}
				if (depart && !passage) {
					sb.append(node.getTime().plusMillis((int) node.getPause().getMillis()).toString(getDateTimeFormat()));

				}
				nodeNames.add(sb.toString());
			}
		}
	}

	protected String getDateTimeFormat() {
		return StaticWidgetHelper.getSynaptixDateConstantsBundle().displayDateTimeFormat();
	}
}