package com.synaptix.swing.foldertree;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;

public interface IFolderTreeDecorate<E extends IFolder> {

	public abstract Icon getIcon(E folder, boolean selected, boolean expanded,
			FolderTreeState state);

	public abstract Font getFont(E folder, boolean selected, boolean expanded,
			FolderTreeState state);

	public abstract String getText(E folder, boolean selected,
			boolean expanded, FolderTreeState state);

	public abstract Color getColor(E folder, boolean selected,
			boolean expanded, FolderTreeState state);

}
