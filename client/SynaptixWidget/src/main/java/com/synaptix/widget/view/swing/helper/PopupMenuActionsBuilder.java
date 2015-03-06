package com.synaptix.widget.view.swing.helper;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class PopupMenuActionsBuilder implements IPopupMenuActionsBuilder {

	private JPopupMenu popupMenu;

	private boolean added;

	public PopupMenuActionsBuilder() {
		super();
		this.popupMenu = new JPopupMenu();
	}

	@Override
	public IPopupMenuActionsBuilder addSeparator() {
		if (added) {
			popupMenu.addSeparator();
		}
		return this;
	}

	@Override
	public IPopupMenuActionsBuilder addAction(Action action) {
		popupMenu.add(action);
		added = true;
		return this;
	}

	@Override
	public IPopupMenuActionsBuilder addSubPopupMenu(Icon icon, String name) {
		JMenu menu = new JMenu(name);
		menu.setIcon(icon);
		popupMenu.add(menu);
		return new SubPopupMenuActionsBuilder(menu);
	}

	public JPopupMenu build() {
		if (!added) {
			return null;
		}
		return popupMenu;
	}

	private class SubPopupMenuActionsBuilder implements IPopupMenuActionsBuilder {

		private final JMenu menu;

		private boolean added;

		public SubPopupMenuActionsBuilder(JMenu menu) {
			super();
			this.menu = menu;
		}

		@Override
		public IPopupMenuActionsBuilder addSeparator() {
			if (added) {
				menu.addSeparator();
			}
			return this;
		}

		@Override
		public IPopupMenuActionsBuilder addAction(Action action) {
			menu.add(action);
			added = true;
			return this;
		}

		@Override
		public IPopupMenuActionsBuilder addSubPopupMenu(Icon icon, String name) {
			JMenu menu = new JMenu(name);
			menu.setIcon(icon);
			menu.add(menu);
			return new SubPopupMenuActionsBuilder(menu);
		}
	}
}
