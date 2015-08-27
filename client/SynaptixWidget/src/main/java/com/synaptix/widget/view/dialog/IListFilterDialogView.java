package com.synaptix.widget.view.dialog;

import java.util.List;

import com.google.inject.assistedinject.Assisted;
import com.synaptix.client.view.IView;
import com.synaptix.swing.utils.GenericObjectToString;

/**
 * Install guice module for each generic using:
 *
 * <pre>
 * {@code
 * install(new FactoryModuleBuilder().implement(
 * 		new TypeLiteral<IListFilterDialogView<String>>() { },
 * 		new TypeLiteral<ListFilterDialog<String>>() { }).build(
 * 		new TypeLiteral<IListFilterDialogView.Factory<String>>() { })
 * 	);
 * }
 * </pre>
 *
 * @author NicolasP
 *
 * @param <E>
 */
public interface IListFilterDialogView<E> extends IView {

	public interface Factory<E> {

		public IListFilterDialogView<E> create(@Assisted GenericObjectToString<E> objectToString);

	}

	public int showDialog(IView parent, String title, List<E> list);

	public int showDialog(IView parent, String title, List<E> list, boolean isCaseSensitive, boolean isMultiSelection);

	public E getValue();

}
