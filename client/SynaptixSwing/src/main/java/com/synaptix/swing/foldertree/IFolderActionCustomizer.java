package com.synaptix.swing.foldertree;

import javax.swing.JPopupMenu;

public interface IFolderActionCustomizer<E extends IFolder> {

	public abstract String getAddChildrenFolderName(E folder);

	public abstract boolean isContinueDeleteFolder(E folder);

	public abstract String getDeleteFolderName(E folder);

	public abstract void visitFolderPopUp(JPopupMenu popupMenu, E folder);

}
