package com.synaptix.swing.foldertree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

class FolderTreeCellRenderer<E extends IFolder> extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = -3722014389420849021L;

	private final IFolderTreeDecorate<E> defaultFolderDecorate = new DefaultFolderDecorate();

	private IFolderTreeDecorate<E> folderDecorate;

	public FolderTreeCellRenderer() {
		super();
	}

	public void setFolderDecorate(IFolderTreeDecorate<E> folderDecorate) {
		this.folderDecorate = folderDecorate;
	}

	public IFolderTreeDecorate<E> getFolderDecorate() {
		return folderDecorate;
	}

	@SuppressWarnings("unchecked")
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component c = super.getTreeCellRendererComponent(tree, value, selected,
				expanded, leaf, row, hasFocus);
		JLabel label = (JLabel) c;

		if (value != null) {
			IFolderTreeDecorate<E> dd = folderDecorate != null ? folderDecorate
					: defaultFolderDecorate;

			FolderIHM<E> d = (FolderIHM<E>) value;

			Font font = dd.getFont(d.getFolder(), selected, expanded, d
					.getFolderTreeState());
			if (font != null) {
				label.setFont(font);
			}

			Color color = dd.getColor(d.getFolder(), selected, expanded, d
					.getFolderTreeState());
			if (!selected && color != null) {
				label.setForeground(color);
			}

			label.setIcon(dd.getIcon(d.getFolder(), selected, expanded, d
					.getFolderTreeState()));
			label.setText(dd.getText(d.getFolder(), selected, expanded, d
					.getFolderTreeState()));
		} else {
			label.setIcon(null);
			label.setText("");
		}
		return label;
	}

	private final class DefaultFolderDecorate implements IFolderTreeDecorate<E> {

		private Font normalFont = new Font("Arial", Font.PLAIN, 12);

		private Font italicFont = new Font("Arial", Font.ITALIC, 12);

		public Icon getIcon(E folder, boolean selected, boolean expanded,
				FolderTreeState state) {
			return null;
		}

		public Font getFont(E folder, boolean selected, boolean expanded,
				FolderTreeState state) {
			if (folder.isUpdateNameEnabled()) {
				return italicFont;
			}
			return normalFont;
		}

		public String getText(E folder, boolean selected, boolean expanded,
				FolderTreeState state) {
			StringBuilder sb = new StringBuilder();

			if (state.equals(FolderTreeState.LOADING)) {
				sb.append("LOAD ");
			} else if (state.equals(FolderTreeState.NO_LOAD)) {
				sb.append("* ");
			}

			sb.append(folder.getName());
			return sb.toString();
		}

		public Color getColor(E folder, boolean selected, boolean expanded,
				FolderTreeState state) {
			if (!folder.isUpdateNameEnabled()) {
				return Color.GRAY;
			}
			return Color.BLACK;
		}
	}
}
