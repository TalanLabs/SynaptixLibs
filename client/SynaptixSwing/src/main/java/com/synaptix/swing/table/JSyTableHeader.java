package com.synaptix.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.EtchedBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class JSyTableHeader extends JTableHeader {

	private static final long serialVersionUID = 5147209689736883544L;

	private boolean showToolTips;

	public JSyTableHeader() {
		this(null);
	}

	public JSyTableHeader(TableColumnModel cm) {
		super(cm);

		showToolTips = false;
	}

	public boolean isShowToolTips() {
		return showToolTips;
	}

	public void setShowToolTips(boolean showToolTips) {
		this.showToolTips = showToolTips;
	}

	@Override
	protected TableCellRenderer createDefaultRenderer() {
		return new UIResourceTableCellRenderer();
	}

	private final class UIResourceTableCellRenderer extends JLabel implements
			TableCellRenderer {

		private static final long serialVersionUID = 2978679154357427403L;

		private SortOrderIcon ascendingIcon = new AscendingIcon();

		private SortOrderIcon descendingIcon = new DescendingIcon();

		private SortOrderIcon nullIcon = new NullIcon();

		private boolean search;

		public UIResourceTableCellRenderer() {
			super();
			this.setOpaque(false);
			this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setHorizontalTextPosition(JLabel.LEFT);
		}

		public boolean isSearch() {
			return search;
		}

		public void setSearch(boolean search) {
			this.search = search;
			repaint();
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if (table != null) {
				SyTableColumn tc = (SyTableColumn) table.getColumnModel()
						.getColumn(column);

				StringBuilder sb = new StringBuilder();
				sb.append(value);

				JTableHeader header = table.getTableHeader();
				if (header != null) {
					if (tc.getSearch() != null) {
						String searchText = tc.getSearch().toString();
						setSearch(true);
						this.setForeground(table.getSelectionForeground());
						this.setBackground(table.getSelectionBackground());

						sb.append("("); //$NON-NLS-1$
						sb.append(searchText);
						sb.append(")"); //$NON-NLS-1$
					} else {
						setSearch(false);
						this.setForeground(header.getForeground());
						this.setBackground(header.getBackground());
					}

					Font font = new Font(header.getFont().getFontName(),
							Font.BOLD, header.getFont().getSize());
					this.setFont(font);
				}

				this.setText(sb.toString());

				if (showToolTips) {
					this.setToolTipText(sb.toString());
				}

				this.setIcon(nullIcon);
				int i = 1;
				if (table != null && table.getRowSorter() != null
						&& table.getRowSorter().getSortKeys() != null) {
					for (SortKey sortKey : table.getRowSorter().getSortKeys()) {
						if (sortKey.getColumn() == tc.getModelIndex()) {
							SortOrderIcon icon = null;
							switch (sortKey.getSortOrder()) {
							case ASCENDING:
								icon = ascendingIcon;
								break;
							case DESCENDING:
								icon = descendingIcon;
								break;
							case UNSORTED:
								icon = nullIcon;
								break;
							}
							icon.setNumberOrderColor(this.getForeground());
							icon.setNumberOrder(i);

							this.setIcon(icon);
						}
						i++;
					}
				}
			}
			return this;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			int w = this.getWidth();
			int h = this.getHeight();

			Color c1 = this.getBackground();
			Color c2 = c1.brighter().brighter();

			GradientPaint gp = new GradientPaint(0, 0, c1, 0, h * 3 / 4, c2);
			g2.setPaint(gp);
			g2.fillRect(0, 0, w, h * 3 / 4);

			gp = new GradientPaint(0, h * 3 / 4, c2, 0, h, c1);
			g2.setPaint(gp);
			g2.fillRect(0, h * 3 / 4, w, h);

			super.paintComponent(g);
		}
	}
}
