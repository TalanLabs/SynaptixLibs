package com.synaptix.widget.component.view;

import java.util.List;
import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;

public interface ISearchComponentsDialogView<E extends IComponent> extends ISearchTablePageComponentsView<E> {

	public static final int ACCEPT_OPTION = 0;

	public static final int CANCEL_OPTION = 1;

	public static final int CLOSE_OPTION = 1;

	/**
	 * Display dialog
	 * 
	 * @param parent
	 * @param valueFilterMap
	 * @param title
	 * @param subtitle
	 * @param multipleSelection
	 * @param readOnly
	 * @param startSearch
	 * @return <code>ISearchComponentsDialogView.ACCEPT_OPTION</code> if user clicks on "OK", <code>ISearchComponentsDialogView.CANCEL_OPTION</code> if user clicks on "cancel", and
	 *         <code>ISearchComponentsDialogView.CLOSE_OPTION</code> if user clicks on "Close" button.
	 */
	public int showDialog(IView parent, Map<String, Object> valueFilterMap, String title, String subtitle, boolean multipleSelection, boolean readOnly, boolean startSearch);

	/**
	 * Gets the components selected in the dialog after validation
	 * 
	 * @return List of IComponents. Returns an empty list if no component selected.
	 */
	public List<E> getSelectedComponents();
}
