package com.synaptix.widget.component.view.swing.dialog.edit;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.widget.component.controller.dialog.edit.ICRUDBeanExtensionDialogContext;

public class DefaultCRUDBeanExtensionDialog<E extends IEntity, F extends IEntity & ICancellable> extends AbstractCRUDBeanExtensionDialog<E, F> {

	private static final long serialVersionUID = 490552283185192897L;

	private final String propertyName;

	private List<F> cancelSubs;

	public DefaultCRUDBeanExtensionDialog(String title, String propertyName, Class<F> subEntityClass, ICRUDBeanExtensionDialogContext<F> crudBeanExtensionDialogContext,
			ConstantsWithLookingBundle constantsWithLookingBundle, String[] tableColumns) {
		super(title, subEntityClass, crudBeanExtensionDialogContext, constantsWithLookingBundle, tableColumns);

		this.propertyName = propertyName;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected List<F> loadList() {
		cancelSubs = new ArrayList<F>();
		List<F> res = new ArrayList<F>();
		List<F> list = (List<F>) bean.straightGetProperty(propertyName);
		if (list != null && !list.isEmpty()) {
			for (F i : list) {
				if (i.getCheckCancel()) {
					cancelSubs.add(i);
				} else {
					res.add(i);
				}
			}
		}
		return res;
	}

	@Override
	protected void saveList(List<F> list) {
		if (cancelSubs != null && !cancelSubs.isEmpty()) {
			list.addAll(cancelSubs);
		}
		bean.straightSetProperty(propertyName, list);
	}

	@Override
	protected void deleteComponent(F component) {
		if (component.getId() != null) {
			component.setCheckCancel(true);
			cancelSubs.add(component);
		}
	}
}
