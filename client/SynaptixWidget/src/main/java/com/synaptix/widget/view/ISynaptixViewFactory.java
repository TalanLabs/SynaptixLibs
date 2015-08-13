package com.synaptix.widget.view;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.synaptix.client.view.IDefaultViewFactory;
import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.IEntity;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.component.view.IComponentsManagementView;
import com.synaptix.widget.component.view.ISearchComponentsDialogView;
import com.synaptix.widget.component.view.ISearchTablePageComponentsView;
import com.synaptix.widget.filefilter.view.IFileFilter;
import com.synaptix.widget.view.dialog.IBeanDialogView;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.IBeanWizardDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;

public interface ISynaptixViewFactory extends IDefaultViewFactory {

	/**
	 * SHow error message type throwable
	 *
	 * @param parent
	 * @param t
	 */
	public void showErrorMessageDialog(IView parent, Throwable t);

	/**
	 * Show error message
	 *
	 * @param parent
	 * @param title
	 * @param message
	 */
	public void showErrorMessageDialog(IView parent, String title, String message);

	/**
	 * Show information message
	 *
	 * @param parent
	 * @param title
	 * @param message
	 */
	public void showInformationMessageDialog(IView parent, String title, String message);

	/**
	 * Ask a question
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @return true for YES or false for NO
	 */
	public boolean showQuestionMessageDialog(IView parent, String title, String message);

	/**
	 * Show a input dialog
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @return null for Cancel
	 */
	public String showInputDialog(IView parent, String title, Object message);

	/**
	 * Show a input dialog with initial value
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param initialValue
	 * @return
	 */
	public Object showInputDialog(IView parent, String title, Object message, Object initialValue);

	/**
	 * Show a question dialog with options and initialValue
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param options
	 * @param initialValue
	 * @return
	 */
	public int showOptionDialog(IView parent, String title, Object message, Object[] options, Object initialValue);

	/**
	 * Show html message
	 *
	 * @param parent
	 * @param title
	 * @param html
	 */
	public void showHtmlMessageDialog(IView parent, String title, String html);

	/**
	 * Show html message
	 *
	 * @param parent
	 * @param title
	 * @param subtitle
	 * @param html
	 */
	public void showHtmlMessageDialog(IView parent, String title, String subtitle, String html);

	/**
	 * Show a text message
	 *
	 * @param parent
	 * @param title
	 * @param text
	 */
	public void showTextMessageDialog(IView parent, String title, String text);

	/**
	 * Choose a file for open
	 *
	 * @param parent
	 * @param fileFilter
	 * @return
	 */
	public File chooseOpenFile(IView parent, IFileFilter fileFilter);

	/**
	 * Choose a file for save
	 *
	 * @param parent
	 * @param filename
	 * @param fileFilter
	 * @return
	 */
	public File chooseSaveFile(IView parent, String filename, IFileFilter fileFilter);

	/**
	 * Choose a directory
	 *
	 * @param parent
	 * @return
	 */
	public File chooseOpenDirectory(IView parent);

	/**
	 * Open a edit dialog for comments
	 *
	 * @param view
	 * @param title
	 * @param message
	 * @param commentsLengthMax
	 * @param readOnly
	 * @return
	 */
	public String addEditCommentaireDialog(IView view, String title, String message, int commentsLengthMax, boolean readOnly);

	/**
	 * Create a bean dialog
	 *
	 * @param beanExtensionDialogViews
	 * @return
	 */
	public <E, F extends IBeanExtensionDialogView<E>> IBeanDialogView<E> newBeanDialogView(F... beanExtensionDialogViews);

	/**
	 * Create a bean dialog
	 *
	 * @param hideListIfAlone
	 * @param beanExtensionDialogViews
	 * @return
	 */
	public <E, F extends IBeanExtensionDialogView<E>> IBeanDialogView<E> newBeanDialogView(boolean hideListIfAlone, F... beanExtensionDialogViews);

	public <E, F extends IBeanExtensionDialogView<E>> IBeanDialogView<E> newBeanDialogView(boolean acceptEnabled, String acceptActionLabel, String cancelActionLabel, String closeActionLabel,
			F... beanExtensionDialogViews);

	/**
	 * Create a CRUD bean dialog
	 */
	public <E extends IEntity, F extends IBeanExtensionDialogView<E>> ICRUDBeanDialogView<E> newCRUDBeanDialogView(F... beanExtensionDialogViews);

	public <E extends IEntity, F extends IBeanExtensionDialogView<E>> ICRUDBeanDialogView<E> newCRUDBeanDialogView(boolean acceptEnabled, String acceptActionLabel, String cancelActionLabel,
			String closeActionLabel, F... beanExtensionDialogViews);

	/**
	 * Open a dialog to select a page size between min & max
	 *
	 * @param parent
	 * @param min
	 * @param max
	 * @return
	 */
	public ISelectSizePageDialogView newSelectSizePageDialog(IView parent, int min, int max);

	/**
	 *
	 * @param parent
	 * @param title
	 * @param values
	 * @param map
	 * @return
	 */
	public <E extends Enum<E>> E chooseEnumDialog(IView parent, String title, E[] values, Map<String, String> map);

	/**
	 * Show a text message
	 *
	 * @param parent
	 * @param title
	 * @param text
	 */
	public void showTextMessageDialog(IView parent, String title, String subtitle, String text);

	/**
	 * Create a search dialog
	 *
	 * @param componentClass
	 * @param constantsBundle
	 * @param filterColumns
	 * @param tableColumns
	 * @return
	 */
	public <E extends IComponent> ISearchComponentsDialogView<E> newSearchComponentsDialogView(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns,
			String[] tableColumns);

	/**
	 * Create a search dialog
	 *
	 * @param componentClass
	 * @param viewDescriptor
	 * @return
	 */
	public <E extends IComponent> ISearchComponentsDialogView<E> newSearchComponentsDialogView(Class<E> componentClass, IViewDescriptor<E> viewDescriptor);

	/**
	 * Create a components management view
	 *
	 * @param componentClass
	 * @param viewDescriptor
	 * @return
	 */
	public <E extends IComponent> IComponentsManagementView<E> newComponentsManagementView(Class<E> componentClass, IViewDescriptor<E> viewDescriptor);

	/**
	 * Create a search panel
	 *
	 * @param componentClass
	 * @param constantsWithLookingBundle
	 * @param filterColumns
	 * @param tableColumns
	 * @return
	 */
	public <E extends IComponent> ISearchTablePageComponentsView<E> newSearchComponentsView(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns,
			String[] tableColumns);

	/**
	 * Create a search panel
	 *
	 * @param componentClass
	 * @param viewDescriptor
	 * @return
	 */
	public <E extends IComponent> ISearchTablePageComponentsView<E> newSearchComponentsView(Class<E> componentClass, IViewDescriptor<E> viewDescriptor);

	public <E, F extends IBeanExtensionDialogView<E>> IBeanWizardDialogView<E> newBeanWizardDialogView(F... beanExtensionDialogViews);

	/**
	 *
	 * @param parent
	 * @param title
	 * @param values
	 * @param caseSensitive
	 * @param mutliSelection
	 * @param objectToString
	 * @return
	 */
	public <E> List<E> chooseListDialog(IView parent, String title, List<E> values, boolean caseSensitive, boolean mutliSelection, GenericObjectToString<E> objectToString);
}
