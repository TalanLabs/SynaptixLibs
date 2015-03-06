package com.synaptix.widget.perimeter.view.swing;

import javax.swing.SwingConstants;

public class PerimetersPanel extends AbstractPerimetersPanel {

	private static final long serialVersionUID = 8828107132674356505L;

	public PerimetersPanel(String id) {
		this(id, SwingConstants.LEFT, 0);
	}

	public PerimetersPanel(String id, int direction) {
		this(id, direction, 0);
	}

	public PerimetersPanel(String id, int direction, int gap) {
		super(id, direction, gap);

		initialize();
	}

	// @Override
	// protected JComponent buildCenter() {
	//		FormLayout layout = new FormLayout("FILL:PREF:GROW(1.0)", //$NON-NLS-1$
	//				"FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
	// PanelBuilder builder = new PanelBuilder(layout);
	// CellConstraints cc = new CellConstraints();
	// builder.add(new JArrowScrollPane(getPanel()), cc.xy(1, 1));
	// return builder.getPanel();
	// }

	@Override
	protected void createSubComponents() {
	}
}
