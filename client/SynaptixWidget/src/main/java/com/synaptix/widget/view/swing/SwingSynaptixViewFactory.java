package com.synaptix.widget.view.swing;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.IEntity;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.view.swing.SwingDefaultViewFactory;
import com.synaptix.view.swing.error.ErrorViewManager;
import com.synaptix.widget.component.view.IComponentsManagementView;
import com.synaptix.widget.component.view.ISearchComponentsDialogView;
import com.synaptix.widget.component.view.ISearchTablePageComponentsView;
import com.synaptix.widget.component.view.swing.DefaultComponentsManagementPanel;
import com.synaptix.widget.component.view.swing.DefaultSearchTablePageComponentsPanel;
import com.synaptix.widget.component.view.swing.dialog.DefaultSearchTablePageComponentDialog;
import com.synaptix.widget.filefilter.view.IFileFilter;
import com.synaptix.widget.helper.FileHelper;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISelectSizePageDialogView;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.IViewDescriptor;
import com.synaptix.widget.view.dialog.IBeanDialogView;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.IBeanWizardDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;
import com.synaptix.widget.view.dialog.TextDialog;
import com.synaptix.widget.view.swing.descriptor.AbstractSearchViewDescriptor;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;

public class SwingSynaptixViewFactory extends SwingDefaultViewFactory implements ISynaptixViewFactory {

	/**
	 * Get a component for IView
	 *
	 * @param parent
	 * @return
	 */
	protected Component getComponent(IView parent) {
		if (parent instanceof Component) {
			return (Component) parent;
		}
		return GUIWindow.getActiveWindow();
	}

	@Override
	public void showErrorMessageDialog(IView parent, Throwable t) {
		ErrorViewManager.getInstance().showErrorDialog(getComponent(parent), t);
	}

	@Override
	public void showErrorMessageDialog(IView parent, String title, String message) {
		JOptionPane.showMessageDialog(getComponent(parent), message, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void showInformationMessageDialog(IView parent, String title, String message) {
		JOptionPane.showMessageDialog(getComponent(parent), message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public boolean showQuestionMessageDialog(IView parent, String title, String message) {
		return JOptionPane.showConfirmDialog(getComponent(parent), message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	@Override
	public String showInputDialog(IView parent, String title, Object message) {
		return (String) showInputDialog(parent, title, message, null);
	}

	@Override
	public Object showInputDialog(IView parent, String title, Object message, Object initialValue) {
		return JOptionPane.showInputDialog(getComponent(parent), message, title, JOptionPane.QUESTION_MESSAGE, null, null, initialValue);
	}

	@Override
	public int showOptionDialog(IView parent, String title, Object message, Object[] options, Object initialValue) {
		return JOptionPane.showOptionDialog(getComponent(parent), message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, initialValue);
	}

	@Override
	public void showHtmlMessageDialog(IView parent, String title, String html) {
		new HelpDialog().showDialog(getComponent(parent), title, html);
	}

	@Override
	public void showHtmlMessageDialog(IView parent, String title, String subtitle, String html) {
		new HelpDialog(subtitle).showDialog(getComponent(parent), title, html);
	}

	@Override
	public void showTextMessageDialog(IView parent, String title, String text) {
		new TextDialog().showDialog(getComponent(parent), title, text);
	}

	@Override
	public void showTextMessageDialog(IView parent, String title, String subtitle, String text) {
		new TextDialog(subtitle).showDialog(getComponent(parent), title, text);
	}

	@Override
	public File chooseOpenFile(IView parent, final IFileFilter fileFilter) {
		File res = null;
		JFileChooser dialog = new JFileChooser(FileHelper.loadLastDirectoryOpen());
		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		dialog.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return fileFilter.getDescription();
			}

			@Override
			public boolean accept(File f) {
				return fileFilter.accept(f);
			}
		});
		if (dialog.showOpenDialog(getComponent(parent)) == JFileChooser.APPROVE_OPTION) {
			File newFile = dialog.getSelectedFile();

			FileHelper.saveLastDirectoryOpen(newFile.getParent());

			res = newFile;
		}
		return res;
	}

	@Override
	public File chooseSaveFile(IView parent, String filename, final IFileFilter fileFilter) {
		File res = null;
		JFileChooser dialog = new JFileChooser(FileHelper.loadLastDirectorySave());
		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (filename != null) {
			dialog.setSelectedFile(new File(filename));
		}
		dialog.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return fileFilter.getDescription();
			}

			@Override
			public boolean accept(File f) {
				return fileFilter.accept(f);
			}
		});
		if (dialog.showSaveDialog(getComponent(parent)) == JFileChooser.APPROVE_OPTION) {
			File newFile = fileFilter.format(dialog.getSelectedFile());
			FileHelper.saveLastDirectorySave(newFile.getParent());

			if (newFile.exists()) {
				if (JOptionPane.showConfirmDialog(GUIWindow.getActiveWindow(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().doYouWantToOverwriteTheSelectedFile(), StaticWidgetHelper
						.getSynaptixWidgetConstantsBundle().file(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					res = newFile;
				} else {
					res = chooseSaveFile(parent, filename, fileFilter);
				}
			} else {
				res = newFile;
			}
		}
		return res;
	}

	@Override
	public File chooseOpenDirectory(IView parent) {
		File res = null;
		JFileChooser dialog = new JFileChooser(FileHelper.loadLastDirectoryOpen());
		dialog.setDialogTitle(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().chooseFolder());
		dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (dialog.showOpenDialog(getComponent(parent)) == JFileChooser.APPROVE_OPTION) {
			File newFile = dialog.getSelectedFile();

			FileHelper.saveLastDirectoryOpen(newFile.getParent());

			res = newFile;
		}
		return res;
	}

	@Override
	public String addEditCommentaireDialog(IView view, String title, String message, int commentsLengthMax, boolean readOnly) {
		String res = null;
		AddEditCommentaireDialog dialog = new AddEditCommentaireDialog();
		if (dialog.showDialog(getComponent(view), title, message, commentsLengthMax, !readOnly) == AddEditCommentaireDialog.ACCEPT_OPTION) {
			res = dialog.getCommentaire();
		}
		return res;
	}

	@Override
	public <E, F extends IBeanExtensionDialogView<E>> IBeanDialogView<E> newBeanDialogView(F... beanExtensionDialogViews) {
		return newBeanDialogView(true, beanExtensionDialogViews);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E, F extends IBeanExtensionDialogView<E>> IBeanDialogView<E> newBeanDialogView(boolean hideListIfAlone, F... beanExtensionDialogViews) {
		List<AbstractBeanExtensionDialog<E>> list = new ArrayList<AbstractBeanExtensionDialog<E>>();
		for (IBeanExtensionDialogView<E> e : beanExtensionDialogViews) {
			list.add((AbstractBeanExtensionDialog<E>) e);
		}
		return new DefaultBeanDialog<E>(hideListIfAlone, list.toArray(new IBeanExtensionDialogView[list.size()]));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E, F extends IBeanExtensionDialogView<E>> IBeanDialogView<E> newBeanDialogView(boolean acceptEnabled, String acceptActionLabel, String cancelActionLabel, String closeActionLabel,
			F... beanExtensionDialogViews) {
		List<AbstractBeanExtensionDialog<E>> list = new ArrayList<AbstractBeanExtensionDialog<E>>();
		for (IBeanExtensionDialogView<E> e : beanExtensionDialogViews) {
			list.add((AbstractBeanExtensionDialog<E>) e);
		}
		return new DefaultBeanDialog<E>(true, acceptEnabled, acceptActionLabel, cancelActionLabel, closeActionLabel, list.toArray(new IBeanExtensionDialogView[list.size()]));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends IEntity, F extends IBeanExtensionDialogView<E>> ICRUDBeanDialogView<E> newCRUDBeanDialogView(F... beanExtensionDialogViews) {
		List<AbstractBeanExtensionDialog<E>> list = new ArrayList<AbstractBeanExtensionDialog<E>>();
		for (IBeanExtensionDialogView<E> e : beanExtensionDialogViews) {
			list.add((AbstractBeanExtensionDialog<E>) e);
		}
		return new DefaultCRUDBeanDialog<E>(list.toArray(new IBeanExtensionDialogView[list.size()]));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends IEntity, F extends IBeanExtensionDialogView<E>> ICRUDBeanDialogView<E> newCRUDBeanDialogView(boolean acceptEnabled, String acceptActionLabel, String cancelActionLabel,
			String closeActionLabel, F... beanExtensionDialogViews) {
		List<AbstractBeanExtensionDialog<E>> list = new ArrayList<AbstractBeanExtensionDialog<E>>();
		for (IBeanExtensionDialogView<E> e : beanExtensionDialogViews) {
			list.add((AbstractBeanExtensionDialog<E>) e);
		}
		return new DefaultCRUDBeanDialog<E>(true, acceptEnabled, acceptActionLabel, cancelActionLabel, closeActionLabel, list.toArray(new IBeanExtensionDialogView[list.size()]));
	}

	@Override
	public ISelectSizePageDialogView newSelectSizePageDialog(IView parent, int min, int max) {
		return new SelectSizePageDialogPanel(parent, min, max);
	}

	@Override
	public <E extends Enum<E>> E chooseEnumDialog(IView parent, String title, E[] values, Map<String, String> map) {
		E res = null;
		AddEditEnumDialog<E> dialog = new AddEditEnumDialog<E>(values, map);
		if (dialog.showDialog(parent, title, null) == AddEditEnumDialog.ACCEPT_OPTION) {
			res = dialog.getValue();
		}
		return res;
	}

	@Override
	public <E extends IComponent> ISearchComponentsDialogView<E> newSearchComponentsDialogView(Class<E> componentClass, IViewDescriptor<E> viewDescriptor) {
		return new DefaultSearchTablePageComponentDialog<E>(componentClass, (AbstractSearchViewDescriptor<E>) viewDescriptor);
	}

	@Override
	public <E extends IComponent> ISearchComponentsDialogView<E> newSearchComponentsDialogView(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns,
			String[] tableColumns) {
		return new DefaultSearchTablePageComponentDialog<E>(componentClass, constantsWithLookingBundle, filterColumns, tableColumns);
	}

	@Override
	public <E extends IComponent> ISearchTablePageComponentsView<E> newSearchComponentsView(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns,
			String[] tableColumns) {
		return new DefaultSearchTablePageComponentsPanel<E>(componentClass, constantsWithLookingBundle, filterColumns, tableColumns);
	}

	@Override
	public <E extends IComponent> ISearchTablePageComponentsView<E> newSearchComponentsView(Class<E> componentClass, IViewDescriptor<E> viewDescriptor) {
		return new DefaultSearchTablePageComponentsPanel<E>(componentClass, (AbstractSearchViewDescriptor<E>) viewDescriptor);
	}

	@Override
	public <E extends IComponent> IComponentsManagementView<E> newComponentsManagementView(Class<E> componentClass, IViewDescriptor<E> viewDescriptor) {
		return new DefaultComponentsManagementPanel<E>(componentClass, (AbstractSearchViewDescriptor<E>) viewDescriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWizardDialogView<E> newBeanWizardDialogView(F... beanExtensionDialogViews) {
		List<AbstractBeanExtensionDialog<E>> list = new ArrayList<AbstractBeanExtensionDialog<E>>();
		for (IBeanExtensionDialogView<E> e : beanExtensionDialogViews) {
			list.add((AbstractBeanExtensionDialog<E>) e);
		}
		return new DefaultBeanWizardDialog<E>(list.toArray(new IBeanExtensionDialogView[list.size()]));
	}

	@Override
	public <E> List<E> chooseListDialog(IView parent, String title, List<E> values, boolean caseSensitive, boolean mutliSelection, GenericObjectToString<E> objectToString) {
		ListFilterDialog<E> dialog = new ListFilterDialog<E>(objectToString);
		if (dialog.showDialog(parent, title, values, caseSensitive, mutliSelection) == ListFilterDialog.ACCEPT_OPTION) {
			return dialog.getValues();
		}
		return null;
	}
}
