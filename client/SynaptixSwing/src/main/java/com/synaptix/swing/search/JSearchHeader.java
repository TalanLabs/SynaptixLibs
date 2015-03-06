package com.synaptix.swing.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.synaptix.swing.IconFeedbackComponentValidationResultPanel;
import com.synaptix.swing.SearchMessages;
import com.synaptix.swing.event.SearchFilterModelEvent;
import com.synaptix.swing.event.SearchFilterModelListener;
import com.synaptix.swing.search.Filter.TypeVisible;
import com.synaptix.swing.widget.JInformationPanel;

public class JSearchHeader extends JPanel implements SearchFilterModelListener, ISearchHeader {

	private static final long serialVersionUID = -3261871343274034667L;

	private SearchFilterModel filterModel;

	private JInformationPanel informationPanel;

	private JFiltersPanel filtersPanel;

	private JPopupMenu mPopupMenu;

	private JMenuItem mMenuItemHide;

	private JMenuItem mMenuItemDialogProperties;

	private JMenuItem mMenuItemValueDefault;

	private JMenuItem mMenuItemFiltersDefault;

	private SearchFilter mCurrentFilter;

	private FilterHeaderMouseListener mouseListener;

	private JScrollPane scrollPane;

	private SearchHeaderModel model;

	private SearchAction searchAction;

	private ValidationResultModel validationResultModel;

	public JSearchHeader(SearchHeaderModel model) {
		this(model, null);
	}

	public JSearchHeader(SearchHeaderModel model, String save) {
		super(new BorderLayout());

		this.model = model;

		initComponent();

		setFilterModel(new DefaultSearchFilterModel(save));

		while (filterModel.getFilterCount() > 0) {
			filterModel.removeFilter(filterModel.getFilter(0));
		}

		// Create new columns from the data model info
		for (int i = 0; i < model.getFilterCount(); i++) {
			Filter filter = model.getFilter(i);
			SearchFilter newFilter = new SearchFilter(i, filter);

			filterModel.addFilter(newFilter);
		}

		filterModel.load();
	}

	private void initComponent() {
		createPopupMenu();

		validationResultModel = new DefaultValidationResultModel();

		mouseListener = new FilterHeaderMouseListener();
		// this.addMouseListener(mouseListener);

		filtersPanel = new JFiltersPanel();
		scrollPane = new JScrollPane(filtersPanel);
		scrollPane.getViewport().addMouseListener(mouseListener);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.add(scrollPane, BorderLayout.CENTER);

		informationPanel = new JInformationPanel();
		informationPanel.setPreferredSize(new Dimension(10, 100));
		informationPanel.setVisible(false);
		this.add(informationPanel, BorderLayout.WEST);

		searchAction = new SearchAction();
		JButton queryButton = new JButton(searchAction);
		queryButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
					Enumeration<SearchFilter> filters = filterModel.getFilters();
					while (filters.hasMoreElements()) {
						SearchFilter searchFilter = filters.nextElement();
						if ((searchFilter.getComponent() != null) && (searchFilter.getComponent().isEnabled())) {
							searchFilter.setValue(null);
						}
					}
				}
			}
		});
		// queryButton.setPreferredSize(new Dimension(queryButton
		// .getPreferredSize().width, 55 + scrollPane
		// .getHorizontalScrollBar().getPreferredSize().height));
		this.add(queryButton, BorderLayout.EAST);
	}

	protected void createPopupMenu() {
		mPopupMenu = new JPopupMenu();

		mMenuItemHide = new JMenuItem();
		mMenuItemHide.setText(SearchMessages.getString("JSearchHeader.1")); //$NON-NLS-1$
		mMenuItemHide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hideFilter(mCurrentFilter);
			}
		});

		mMenuItemDialogProperties = new JMenuItem();
		mMenuItemDialogProperties.setText(SearchMessages.getString("JSearchHeader.2")); //$NON-NLS-1$
		mMenuItemDialogProperties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDialogProperties();
			}
		});

		mMenuItemValueDefault = new JMenuItem();
		mMenuItemValueDefault.setText(SearchMessages.getString("JSearchHeader.3")); //$NON-NLS-1$
		mMenuItemValueDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueByDefault(mCurrentFilter);
			}
		});

		mMenuItemFiltersDefault = new JMenuItem();
		mMenuItemFiltersDefault.setText(SearchMessages.getString("JSearchHeader.4")); //$NON-NLS-1$
		mMenuItemFiltersDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterModel.defaultFilters();
			}
		});

		mPopupMenu.add(mMenuItemHide);
		mPopupMenu.add(mMenuItemValueDefault);
		mPopupMenu.addSeparator();
		mPopupMenu.add(mMenuItemDialogProperties);
		mPopupMenu.add(mMenuItemFiltersDefault);

		mCurrentFilter = null;
	}

	public void load() {
		filterModel.load();
	}

	public void save() {
		filterModel.save();
	}

	public boolean isSave() {
		return filterModel.isSave();
	}

	public void defaultFilters() {
		filterModel.defaultFilters();
	}

	@Override
	public Map<String, Object> getValueFilters() {
		Map<String, Object> res = new HashMap<String, Object>();
		for (SearchFilter searchFilter : filterModel.getFilters(true, true)) {
			res.put(searchFilter.getIdentifier(), searchFilter.getValue());
		}
		return res;
	}

	public void setFilterValue(String name, Object value) {
		SearchFilter sf = filterModel.getFilter(filterModel.getFilterIndex(name, true), true);
		sf.setValue(value);
	}

	public void setFilterEnabled(String name, boolean enabled) {
		SearchFilter sf = filterModel.getFilter(filterModel.getFilterIndex(name, true), true);
		sf.getComponent().setEnabled(enabled);
	}

	public JComponent getFilterComponent(String name) {
		SearchFilter sf = filterModel.getFilter(filterModel.getFilterIndex(name, true), true);
		return sf.getComponent();
	}

	public ValidationResultModel getValidationResultModel() {
		return validationResultModel;
	}

	public boolean isEnabledSearchAction() {
		return searchAction.isEnabled();
	}

	public void setEnabledSearchAction(boolean enabled) {
		searchAction.setEnabled(isEnabled() && enabled);
	}

	private void showDialogProperties() {
		EditPropertiesFiltersDialog dialog = new EditPropertiesFiltersDialog(this);
		int result = dialog.showDialog(this);
		if (result == EditPropertiesFiltersDialog.CLOSE_OPTION) {
			filterModel.save();
		} else if (result == EditPropertiesFiltersDialog.DEFAULT_OPTION) {
			filterModel.defaultFilters();
		}
	}

	public void hideFilter(SearchFilter aFilter) {
		if (aFilter.isVisible()) {
			filterModel.invisibleFilter(aFilter);
		} else {
			filterModel.visibleFilter(aFilter);
		}
	}

	public void valueByDefault(SearchFilter aFilter) {
		aFilter.copyDefaultValue();
	}

	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	public SearchFilterModel getFilterModel() {
		return filterModel;
	}

	private void fireSearchActionPerformed() {
		if (model.validate(getValueFilters())) {
			ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
			ActionEvent e = new ActionEvent(this, 0, "search"); //$NON-NLS-1$

			for (ActionListener listener : listeners) {
				listener.actionPerformed(e);
			}
		}
	}

	public SearchHeaderModel getModel() {
		return model;
	}

	private void setFilterModel(SearchFilterModel filterModel) {
		if (filterModel == null) {
			throw new IllegalArgumentException("Cannot set a null FilterModel"); //$NON-NLS-1$
		}
		SearchFilterModel old = this.filterModel;
		if (filterModel != old) {
			if (old != null) {
				old.removeFilterModelListener(this);
			}
			this.filterModel = filterModel;
			filterModel.addFilterModelListener(this);

			firePropertyChange("filterModel", old, filterModel); //$NON-NLS-1$
			resizeAndRepaint();
		}
	}

	public void resizeAndRepaint() {
		filtersPanel.reconstructFiltersPanel();

		int h = filtersPanel.getPreferredSize().height + scrollPane.getHorizontalScrollBar().getPreferredSize().height + scrollPane.getInsets().bottom + scrollPane.getInsets().top;

		int nbInvisible = 0;
		for (SearchFilter sf : filterModel.getFilters(true, true)) {
			if (sf.getTypeVisible() == TypeVisible.Invisible) {
				nbInvisible++;
			}
		}

		int diff = filterModel.getFilterCount(true) - filterModel.getFilterCount(false) - nbInvisible;
		if (diff > 0) {
			if (diff == 1) {
				informationPanel.setText(SearchMessages.getString("JSearchHeader.16")); //$NON-NLS-1$
			} else {
				informationPanel.setText(SearchMessages.getString("JSearchHeader.17")); //$NON-NLS-1$
			}
			informationPanel.setVisible(true);
		} else {
			informationPanel.setText(null);
			informationPanel.setVisible(false);
		}

		informationPanel.setPreferredSize(new Dimension(10, h));

		this.setPreferredSize(new Dimension(10, h));

		revalidate();
		repaint();
	}

	@Override
	public void filterAdded(SearchFilterModelEvent e) {
		resizeAndRepaint();
	}

	@Override
	public void filterMoved(SearchFilterModelEvent e) {
		resizeAndRepaint();
	}

	@Override
	public void filterRemoved(SearchFilterModelEvent e) {
		resizeAndRepaint();
	}

	private int filterAtPoint(Point p) {
		return filterModel.getFilterIndexAtX(p.x);
	}

	private void mouseClickedAction(MouseEvent e) {
		if (isEnabled()) {
			int i = filterAtPoint(SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), filtersPanel));
			if (i != -1) {
				mCurrentFilter = filterModel.getFilter(i);
			} else {
				mCurrentFilter = null;
			}

			if (e.isMetaDown()) {
				mMenuItemHide.setText(SearchMessages.getString("JSearchHeader.1")); //$NON-NLS-1$
				mMenuItemHide.setEnabled(false);

				mMenuItemValueDefault.setEnabled(false);

				if (mCurrentFilter != null) {
					mMenuItemHide.setText(SearchMessages.getString("JSearchHeader.1") + " " //$NON-NLS-1$ //$NON-NLS-2$
							+ mCurrentFilter.getName());
					if (mCurrentFilter.getTypeVisible() == TypeVisible.Visible) {
						mMenuItemHide.setEnabled(true);
					} else {
						mMenuItemHide.setEnabled(false);
					}
					mMenuItemValueDefault.setEnabled(true);
				}

				mPopupMenu.pack();
				mPopupMenu.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		searchAction.setEnabled(enabled);
		filtersPanel.setEnabled(enabled);

		super.setEnabled(enabled);
	}

	private final class FilterHeaderMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			mouseClickedAction(e);
		}
	}

	private final class SearchAction extends AbstractAction {

		private static final long serialVersionUID = 8738203421103633956L;

		public SearchAction() {
			super(SearchMessages.getString("JSearchHeader.11")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireSearchActionPerformed();
		}
	}

	private final class JFiltersPanel extends JPanel implements Scrollable {

		private static final long serialVersionUID = 4196701342515990348L;

		private FiltreKeyListener filterKeyListener;

		private List<JComponent> components;

		public JFiltersPanel() {
			super(new BorderLayout());

			filterKeyListener = new FiltreKeyListener();

			components = new ArrayList<JComponent>();

			reconstructFiltersPanel();
		}

		@Override
		public void setEnabled(boolean enabled) {
			for (JComponent c : components) {
				c.setEnabled(enabled);
			}
			super.setEnabled(enabled);
		}

		public void reconstructFiltersPanel() {
			for (JComponent component : components) {
				component.removeKeyListener(filterKeyListener);
			}
			this.removeAll();

			if (filterModel != null) {
				List<ColumnSpec> columnSpecs = new ArrayList<ColumnSpec>();
				for (int i = 0; i < filterModel.getFilterCount(); i++) {
					columnSpecs.add(new ColumnSpec("3dlu")); //$NON-NLS-1$
					columnSpecs.add(new ColumnSpec("left:p")); //$NON-NLS-1$
				}
				columnSpecs.add(new ColumnSpec("3dlu")); //$NON-NLS-1$

				FormLayout layout = new FormLayout(columnSpecs.toArray(new ColumnSpec[columnSpecs.size()]), RowSpec.decodeSpecs("3dlu,pref,3dlu,pref,3dlu")); //$NON-NLS-1$
				PanelBuilder builder = new PanelBuilder(layout);
				CellConstraints cc = new CellConstraints();

				int x;
				for (int i = 0; i < filterModel.getFilterCount(); i++) {
					SearchFilter searchFilter = filterModel.getFilter(i);
					JComponent component = searchFilter.getComponent();
					component.addKeyListener(filterKeyListener);
					components.add(component);
					x = (i + 1) * 2;
					builder.addLabel(searchFilter.getName(), cc.xy(x, 2));
					builder.add(component, cc.xy(x, 4));
				}

				this.add(new IconFeedbackComponentValidationResultPanel(validationResultModel, builder.getPanel()), BorderLayout.CENTER);
			}
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			int height = this.getHeight();
			/*
			 * int width = this.getWidth(); if (scrollPane != null) { Insets insets = scrollPane.getInsets(); width = scrollPane.getWidth() - (insets.left + insets.right); }
			 */
			return new Dimension(0, height);
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			return getScrollableUnitIncrement(visibleRect, orientation, direction);
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		private JComponent componentAtPoint(Point point) {
			JComponent t = null;
			for (JComponent component : components) {
				Rectangle rect = component.getBounds();

				System.out.println(component.getSize() + " " //$NON-NLS-1$
						+ component.getPreferredSize());

				rect.width = this.getWidth() - rect.x;
				if (rect.contains(point)) {
					t = component;
				}
			}
			return t;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			// JComponent component = componentAtPoint(new Point(
			// visibleRect.x + 10, 30));
			// if (component != null) {
			// int x = component.getX();
			// if (x < visibleRect.x) {
			// // int d = visibleRect.x - x;
			// // if (direction > 0) {
			// // return component.getWidth() - d;
			// // } else {
			// // return d;
			// // }
			// } else {
			// return x + component.getWidth() - visibleRect.x;
			// }
			// }
			return 50;
		}

		private class FiltreKeyListener extends KeyAdapter {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					fireSearchActionPerformed();
				}
			}
		}
	}

}
