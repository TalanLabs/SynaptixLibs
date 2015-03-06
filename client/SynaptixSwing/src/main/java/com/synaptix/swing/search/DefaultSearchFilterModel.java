package com.synaptix.swing.search;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.event.SearchFilterModelEvent;
import com.synaptix.swing.event.SearchFilterModelListener;
import com.synaptix.swing.search.Filter.TypeVisible;
import com.synaptix.swing.utils.Utils;

public class DefaultSearchFilterModel implements SearchFilterModel,
		Serializable {

	private static final long serialVersionUID = 7877262316375514001L;

	private Vector<SearchFilter> searchFilters;

	private List<SearchFilter> initialFilters;

	private List<SearchFilter> currentFilters;

	private Set<SearchFilter> invisibleFilters;

	protected int totalFilterWidth;

	protected EventListenerList listenerList;

	private boolean activeSave;

	private String saveName;

	public DefaultSearchFilterModel() {
		this(null);
	}

	public DefaultSearchFilterModel(String saveName) {
		this.saveName = saveName;
		initializeLocalVars();
	}

	protected void initializeLocalVars() {
		listenerList = new EventListenerList();

		searchFilters = new Vector<SearchFilter>();

		initialFilters = new ArrayList<SearchFilter>();
		currentFilters = new ArrayList<SearchFilter>();
		invisibleFilters = new HashSet<SearchFilter>();

		activeSave = true;
	}

	public void addFilter(SearchFilter aFilter) {
		if (aFilter == null) {
			throw new IllegalArgumentException("Object is null"); //$NON-NLS-1$
		}

		initialFilters.add(aFilter);
		currentFilters.add(aFilter);
		searchFilters.add(aFilter);
		invalidateWidthCache();

		fireFilterAdded(new SearchFilterModelEvent(this, 0,
				getFilterCount(false) - 1));
	}

	public void moveFilter(int filterIndex, int newIndex) {
		if ((filterIndex < 0) || (filterIndex >= getFilterCount())
				|| (newIndex < 0) || (newIndex >= getFilterCount())) {
			throw new IllegalArgumentException(
					"moveFilter() - Index out of range"); //$NON-NLS-1$
		}
		if (filterIndex != newIndex) {
			updateCurrentFilters(filterIndex, newIndex);

			SearchFilter aFilter = searchFilters.elementAt(filterIndex);
			searchFilters.removeElementAt(filterIndex);
			searchFilters.insertElementAt(aFilter, newIndex);
		}

		save();

		fireFilterMoved(new SearchFilterModelEvent(this, filterIndex, newIndex));
	}

	private void updateCurrentFilters(int oldIndex, int newIndex) {
		SearchFilter movedFilter = searchFilters.elementAt(oldIndex);
		int oldPosition = currentFilters.indexOf(movedFilter);
		SearchFilter targetColumn = searchFilters.elementAt(newIndex);
		int newPosition = currentFilters.indexOf(targetColumn);
		currentFilters.remove(oldPosition);
		currentFilters.add(newPosition, movedFilter);
	}

	public void removeFilter(SearchFilter aFilter) {
		int filterIndex = searchFilters.indexOf(aFilter);

		initialFilters.remove(aFilter);
		currentFilters.remove(aFilter);
		invisibleFilters.remove(aFilter);
		searchFilters.remove(aFilter);

		invalidateWidthCache();
		fireFilterRemoved(new SearchFilterModelEvent(this, filterIndex, 0));
	}

	public void visibleFilter(SearchFilter aFilter) {
		aFilter.setVisible(true);

		invisibleFilters.remove(aFilter);

		searchFilters.add(aFilter);
		invalidateWidthCache();
		fireFilterAdded(new SearchFilterModelEvent(this, 0,
				getFilterCount(false) - 1));

		int addIndex = currentFilters.indexOf(aFilter);
		for (int i = 0; i < (getFilterCount() - 1); i++) {
			SearchFilter tableCol = getFilter(i);
			int actualPosition = currentFilters.indexOf(tableCol);
			if (actualPosition > addIndex) {
				int filterIndex = getFilterCount() - 1;
				int newIndex = i;
				SearchFilter f = searchFilters.elementAt(filterIndex);
				searchFilters.removeElementAt(filterIndex);
				searchFilters.insertElementAt(f, newIndex);
				fireFilterMoved(new SearchFilterModelEvent(this, filterIndex,
						newIndex));
				break;
			}
		}

		save();
	}

	public void invisibleFilter(SearchFilter aFilter) {
		aFilter.setVisible(false);

		invisibleFilters.add(aFilter);

		int filterIndex = searchFilters.indexOf(aFilter);
		searchFilters.remove(aFilter);
		invalidateWidthCache();
		fireFilterRemoved(new SearchFilterModelEvent(this, filterIndex, 0));

		save();
	}

	public void addFilterModelListener(SearchFilterModelListener x) {
		listenerList.add(SearchFilterModelListener.class, x);
	}

	public void removeFilterModelListener(SearchFilterModelListener x) {
		listenerList.remove(SearchFilterModelListener.class, x);
	}

	public SearchFilterModelListener[] getFilterModelListeners() {
		return (SearchFilterModelListener[]) listenerList
				.getListeners(SearchFilterModelListener.class);
	}

	public SearchFilter getFilter(int filterIndex, boolean includeHidden) {
		if (includeHidden) {
			return initialFilters.get(filterIndex);
		}
		return getFilter(filterIndex);
	}

	public SearchFilter getFilter(int filterIndex) {
		return searchFilters.elementAt(filterIndex);
	}

	public int getFilterCount() {
		return searchFilters.size();
	}

	public int getFilterCount(boolean includeHidden) {
		if (includeHidden) {
			return initialFilters.size();
		}
		return getFilterCount();
	}

	public int getFilterIndex(Object filterIdentifier, boolean includeHidden) {
		if (filterIdentifier == null) {
			throw new IllegalArgumentException("Identifier is null"); //$NON-NLS-1$
		}

		List<SearchFilter> enumeration = getFilters(includeHidden, true);
		int index = 0;
		for (SearchFilter aFilter : enumeration) {
			if (filterIdentifier.equals(aFilter.getIdentifier()))
				return index;
			index++;
		}
		throw new IllegalArgumentException("Identifier not found " //$NON-NLS-1$
				+ filterIdentifier);
	}

	public int getFilterIndexAtX(int xPosition) {
		Enumeration<SearchFilter> enumeration = getFilters();
		SearchFilter aFilter;
		int index = 0;
		while (enumeration.hasMoreElements()) {
			aFilter = enumeration.nextElement();

			JComponent component = aFilter.getComponent();
			Rectangle rect = component.getBounds();
			if (rect.contains(new Point(xPosition, rect.y))) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public List<SearchFilter> getFilters(boolean includeHidden, boolean initial) {
		if (includeHidden) {
			if (initial) {
				return new ArrayList<SearchFilter>(initialFilters);
			} else {
				return new ArrayList<SearchFilter>(currentFilters);
			}
		}
		return Collections.list(getFilters());
	}

	public Enumeration<SearchFilter> getFilters() {
		return searchFilters.elements();
	}

	public int getTotalFilterWidth() {
		if (totalFilterWidth == -1) {
			recalcWidthCache();
		}
		return totalFilterWidth;
	}

	public boolean isSave() {
		boolean res = false;
		String save = saveName;
		if (save != null) {
			save += "Filter"; //$NON-NLS-1$
			SyPreferences prefs = SyPreferences.getPreferences();
			int l = Integer.parseInt(prefs.get(save + "_lenght", "-1")); //$NON-NLS-1$ //$NON-NLS-2$
			if (l != -1 && l == initialFilters.size()) {
				res = true;
			}
		}
		return res;
	}

	public void load() {
		activeSave = false;
		try {
			String save = saveName;
			if (save != null) {
				save += "Filter"; //$NON-NLS-1$
				SyPreferences prefs = SyPreferences.getPreferences();
				int l = Integer.parseInt(prefs.get(save + "_lenght", "-1")); //$NON-NLS-1$ //$NON-NLS-2$
				if (l != -1) {
					if (l != initialFilters.size()) {
						prefs.remove(save + "_lenght"); //$NON-NLS-1$
						for (int j = 0; j < l; j++) {
							prefs.remove(save + "_visible_" + j); //$NON-NLS-1$
							prefs.remove(save + "_position_" + j); //$NON-NLS-1$
							prefs.remove(save + "_value_" + j); //$NON-NLS-1$
						}
					} else {
						int i = 0;
						for (SearchFilter searchFilter : initialFilters) {
							String p = prefs.get(save + "_position_" + i, null); //$NON-NLS-1$

							if (p != null) {
								int newIndex = Integer.parseInt(p);
								int filterIndex = currentFilters
										.indexOf(searchFilter);

								moveFilter(filterIndex, newIndex);
							}

							i++;
						}

						i = 0;
						for (SearchFilter searchFilter : initialFilters) {
							if (searchFilter.getTypeVisible() == TypeVisible.Visible) {
								String v = prefs.get(save + "_visible_" + i, //$NON-NLS-1$
										null);
								if (v != null) {
									boolean visible = Boolean.parseBoolean(v);
									if (!visible) {
										invisibleFilter(searchFilter);
									}
								}
							} else if (searchFilter.getTypeVisible() == TypeVisible.Invisible) {
								invisibleFilter(searchFilter);
							}

							String value = prefs
									.get(save + "_value_" + i, null); //$NON-NLS-1$
							if (value != null) {
								byte[] bs = Utils
										.convertStringToByteArray(value);
								Object o = Utils.convertByteArrayToObject(bs);
								searchFilter.setDefaultValue(o);
							} else {
								searchFilter.setDefaultValue(null);
							}
							searchFilter.copyDefaultValue();

							i++;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		activeSave = true;
	}

	public void save() {
		if (activeSave) {
			String save = saveName;
			if (save != null) {
				save += "Filter"; //$NON-NLS-1$

				SyPreferences prefs = SyPreferences.getPreferences();
				int l = Integer.parseInt(prefs.get(save + "_lenght", "-1")); //$NON-NLS-1$ //$NON-NLS-2$
				if (l != -1 && l != initialFilters.size()) {
					prefs.remove(save + "_lenght"); //$NON-NLS-1$
					for (int j = 0; j < l; j++) {
						prefs.remove(save + "_visible_" + j); //$NON-NLS-1$
						prefs.remove(save + "_position_" + j); //$NON-NLS-1$
						prefs.remove(save + "_value_" + j); //$NON-NLS-1$
					}
				}

				prefs.put(save + "_lenght", String.valueOf(initialFilters //$NON-NLS-1$
						.size()));
				int i = 0;
				for (SearchFilter searchFilter : initialFilters) {
					prefs.put(save + "_visible_" + i, String //$NON-NLS-1$
							.valueOf(searchFilter.isVisible()));
					prefs.put(save + "_position_" + i, String //$NON-NLS-1$
							.valueOf(currentFilters.indexOf(searchFilter)));

					byte[] bs = Utils.convertObjectToByteArray(searchFilter
							.getDefaultValue());
					String s = Utils.convertByteArrayToString(bs);
					prefs.put(save + "_value_" + i, s); //$NON-NLS-1$

					i++;
				}
			}
		}
	}

	public void defaultFilters() {
		activeSave = false;

		for (SearchFilter searchFilter : initialFilters) {
			searchFilter.copyFirstDefaultValue();
			searchFilter.copyDefaultValue();
		}

		SearchFilter[] sfs = initialFilters
				.toArray(new SearchFilter[initialFilters.size()]);

		for (int i = this.getFilterCount() - 1; i >= 0; i--) {
			SearchFilter searchFilter = this.getFilter(i);
			searchFilters.remove(searchFilter);
			invalidateWidthCache();
		}
		currentFilters.clear();
		invisibleFilters.clear();
		initialFilters.clear();
		for (SearchFilter searchFilter : sfs) {
			addFilter(searchFilter);
			if (searchFilter.getTypeVisible() == TypeVisible.Visible) {
				searchFilter.setVisible(searchFilter.isDefaultVisible());
				if (!searchFilter.isDefaultVisible()) {
					invisibleFilter(searchFilter);
				}
			} else if (searchFilter.getTypeVisible() == TypeVisible.Invisible) {
				invisibleFilter(searchFilter);
			}
		}

		activeSave = true;
		save();
	}

	protected void fireFilterAdded(SearchFilterModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SearchFilterModelListener.class) {
				((SearchFilterModelListener) listeners[i + 1]).filterAdded(e);
			}
		}
	}

	protected void fireFilterRemoved(SearchFilterModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SearchFilterModelListener.class) {
				((SearchFilterModelListener) listeners[i + 1]).filterRemoved(e);
			}
		}
	}

	protected void fireFilterMoved(SearchFilterModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SearchFilterModelListener.class) {
				((SearchFilterModelListener) listeners[i + 1]).filterMoved(e);
			}
		}
	}

	protected void recalcWidthCache() {

	}

	private void invalidateWidthCache() {
		totalFilterWidth = -1;
	}
}
