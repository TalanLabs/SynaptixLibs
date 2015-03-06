package com.synaptix.swing.foldertree;

import java.util.EventListener;

public interface FolderTreeSelectionListener<E extends IFolder> extends
		EventListener {

	public abstract void selectedFolderChanged(E[] folderPath);

}
