package com.synaptix.swing.foldertree;

import java.util.List;

public interface IFolderTreeContext<E extends IFolder> {

	public abstract E createEmptyFolder();

	public abstract List<E> findFolderChildrensByFolder(E parent)
			throws Exception;

	public abstract void addFolder(E parent, E folder) throws Exception;

	public abstract void updateNameFolder(E folder, String name)
			throws Exception;

	public abstract void moveFolder(E origine, E destination, E folder)
			throws Exception;

	public abstract void deleteFolder(E parent, E folder) throws Exception;

}
