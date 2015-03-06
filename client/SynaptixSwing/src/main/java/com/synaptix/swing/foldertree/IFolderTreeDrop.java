package com.synaptix.swing.foldertree;

import java.awt.datatransfer.Transferable;

public interface IFolderTreeDrop<E extends IFolder> {

	public boolean canDrop(Transferable transferable, E folder);

	public void done(Transferable transferable, E folder);

}
