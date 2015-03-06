package com.synaptix.swing.foldertree;

import java.util.ArrayList;
import java.util.List;

class FolderIHM<E extends IFolder> {

	private E folder;

	private List<FolderIHM<E>> filsList;

	private FolderTreeState state;

	private String name;

	public FolderIHM(E folder) {
		this.folder = folder;
		this.filsList = new ArrayList<FolderIHM<E>>();
		this.name = folder.getName();

		if (folder.isLeaf()) {
			this.state = FolderTreeState.LOAD;
		} else {
			this.state = FolderTreeState.NO_LOAD;
		}
	}

	public E getFolder() {
		return folder;
	}

	public List<FolderIHM<E>> getFilsList() {
		return filsList;
	}

	public void setFilsList(List<FolderIHM<E>> filsList) {
		this.filsList = filsList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FolderTreeState getFolderTreeState() {
		return state;
	}

	public void setFolderTreeState(FolderTreeState state) {
		this.state = state;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("( FolderIHM ");
		sb.append(hashCode());
		sb.append(" ->");
		sb.append(" name ");
		sb.append(getName());
		sb.append(" E ");
		sb.append(getFolder());
		sb.append(" )");
		return sb.toString();
	}
}
