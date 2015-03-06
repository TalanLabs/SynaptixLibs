package com.synaptix.swing.selection;

import java.util.EventListener;

public interface XYSelectionModelListener extends EventListener {

	public abstract void selectionChanged(XYSelectionModelEvent e);

}
