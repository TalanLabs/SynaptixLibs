package com.synaptix.swing.path;

import java.util.EventListener;

public interface PathModelListener extends EventListener {

	public abstract void pathModelChanged(PathModelEvent e);

}
