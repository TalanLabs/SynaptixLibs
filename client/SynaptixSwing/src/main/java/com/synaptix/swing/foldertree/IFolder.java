package com.synaptix.swing.foldertree;

public interface IFolder {

	public abstract String getName();

	public abstract boolean isLeaf();

	public abstract boolean isAddChildrenEnabled();

	public abstract boolean isDeleteEnabled();

	public abstract boolean isUpdateNameEnabled();

	public abstract boolean isDragEnabled();

	public abstract boolean isDropEnabled();

}
