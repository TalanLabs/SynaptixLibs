package com.synaptix.swing.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;

import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;

public class JSyExportControl extends JButton {

	private static final long serialVersionUID = -6541355307417958411L;

	protected JSyTable table;

	public JSyExportControl() {
		super();
		this.setAction(new ControlButtonAction(new ExportControlIcon()));

		initializeLocalVars();
	}

	protected void initializeLocalVars() {
		this.setFocusPainted(false);
		this.setFocusable(false);
		table = null;
		
		this.setToolTipText(SwingMessages.getString("JSyExportControl.0")); //$NON-NLS-1$
	}

	public JSyTable getTable() {
		return table;
	}

	public void setTable(JSyTable table) {
		if (this.table != table) {
			JTable old = this.table;
			this.table = table;
			firePropertyChange("table", old, table); //$NON-NLS-1$
		}
	}

	private class ControlButtonAction extends AbstractAction {

		private static final long serialVersionUID = 3192787412586147481L;

		public ControlButtonAction(Icon icon) {
			super(null, icon);
		}

		public void actionPerformed(ActionEvent e) {
			table.exportTable();
		}
	}
}
