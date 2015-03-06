package com.synaptix.widget.crud.controller;

import java.io.Serializable;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.entity.IEntity;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IEntityService;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.ServiceException;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

/**
 * A CRUD Controller, create a table and filter and action CRUD
 * 
 * @param <V>
 *            View factory
 * @param <E>
 *            CRUD Entity
 * @param <G>
 *            Pagination entity which
 */
public abstract class AbstractCRUDManagementController<V extends ISynaptixViewFactory, E extends IEntity, G extends IEntity> extends AbstractComponentsManagementController<V, G> implements
		ICRUDManagementController<G> {

	public enum DialogAction {

		NEW, EDIT, SHOW, CLONE

	}

	protected final Class<E> crudComponentClass;

	private final Class<? extends ICRUDEntityService<E>> crudEntityServiceClass;

	public AbstractCRUDManagementController(V viewFactory, Class<E> crudComponentClass, Class<G> paginationComponentClass) {
		this(viewFactory, crudComponentClass, paginationComponentClass, null, null);
	}

	public AbstractCRUDManagementController(V viewFactory, Class<E> crudComponentClass, Class<G> paginationComponentClass, Class<? extends ICRUDEntityService<E>> crudEntityServiceClass,
			Class<? extends IPaginationService<G>> paginationServiceClass) {
		super(viewFactory, paginationComponentClass, paginationServiceClass);

		this.crudComponentClass = crudComponentClass;
		this.crudEntityServiceClass = crudEntityServiceClass;
	}

	/**
	 * Create a CRUD management view descriptor. NOT CALL use getCRUDManagementViewDescriptor()
	 * 
	 * @return
	 */
	protected abstract ICRUDManagementViewDescriptor<G> createCRUDManagementViewDescriptor();

	/**
	 * Get a CRUD manamgement view descriptor
	 * 
	 * @return
	 */
	protected final ICRUDManagementViewDescriptor<G> getCRUDManagementViewDescriptor() {
		return (ICRUDManagementViewDescriptor<G>) getComponentsManagementViewDescriptor();
	}

	@Override
	protected final IComponentsManagementViewDescriptor<G> createComponentsManagementViewDescriptor() {
		return createCRUDManagementViewDescriptor();
	}

	/**
	 * Create a CRUD dialog controller
	 * 
	 * @param dialogAction
	 * @param entity
	 * @return
	 */
	protected abstract ICRUDDialogController<E> newCRUDDialogController(DialogAction dialogAction, E entity);

	/**
	 * Get a CRUD Entity service
	 * 
	 * @return
	 */
	protected final ICRUDEntityService<E> getCRUDEntityService() {
		return getServiceFactory().getService(crudEntityServiceClass);
	}

	/**
	 * Get a entity service
	 * 
	 * @return
	 */
	protected final IEntityService getEntityService() {
		return getServiceFactory().getService(IEntityService.class);
	}

	/**
	 * Get a unicity error
	 * 
	 * @param description
	 * @return
	 */
	protected String getUnicityError(String description) {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().unicityConstraintException();
	}

	private Serializable addCRUDEntity(E entity) {
		if (crudEntityServiceClass != null) {
			return getCRUDEntityService().addCRUDEntity(entity);
		} else {
			return getEntityService().addEntity(entity);
		}
	}

	private Serializable editCRUDEntity(E entity) {
		if (crudEntityServiceClass != null) {
			return getCRUDEntityService().editCRUDEntity(entity);
		} else {
			return getEntityService().editEntity(entity);
		}
	}

	private Serializable removeCRUDEntity(E entity) {
		if (crudEntityServiceClass != null) {
			return getCRUDEntityService().removeCRUDEntity(entity);
		} else {
			return getEntityService().removeEntity(entity);
		}
	}

	/**
	 * Show a dialog for add entity
	 */
	@Override
	public void addEntity() {
		_addEntity();
	}

	protected void _addEntity() {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController(DialogAction.NEW, null);
		if (crudDialogController != null) {
			_addEntity(crudDialogController, getView());
		}
	}

	protected void _addEntity(ICRUDDialogController<E> crudDialogController, final IView view) {
		crudDialogController.addEntity(view, new IResultCallback<E>() {
			@Override
			public void setResult(final E entity) {
				if (entity != null) {
					getViewFactory().waitFullComponentViewWorker(view, new AbstractLoadingViewWorker<Serializable>() {
						@Override
						protected Serializable doLoading() throws Exception {
							return addCRUDEntity(entity);
						}

						@Override
						public void success(Serializable e) {
							addEntitySuccess(e);
						}

						@Override
						public void fail(Throwable t) {
							if (t.getCause() instanceof ServiceException) {
								ServiceException err = (ServiceException) t.getCause();
								if (ICRUDEntityService.UNICITY_CONSTRAINT.equals(err.getCode())) {
									getViewFactory().showErrorMessageDialog(view, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), getUnicityError(err.getDescription()));

									_cloneEntity(entity);
								} else {
									getViewFactory().showErrorMessageDialog(view, t);
								}
							} else {
								getViewFactory().showErrorMessageDialog(view, t);
							}
						}
					});
				}
			}
		});
	}

	protected void addEntitySuccess(Serializable idEntity) {
		loadPagination();
	}

	@Override
	public void showEntity(final G paginationEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(E e) {
				if (e != null) {
					_showEntity(e);
				}
			}
		});
	}

	protected void _showEntity(final E crudEntity) {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController(DialogAction.SHOW, crudEntity);
		if (crudDialogController != null) {
			crudDialogController.showEntity(getView(), crudEntity);
		}
	}

	/**
	 * Show a dialog for edit entity
	 * 
	 * @param entity
	 */
	@Override
	public void editEntity(final G paginationEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(E e) {
				if (e != null) {
					_editEntity(e);
				}
			}
		});
	}

	protected void _editEntity(final E crudEntity) {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController(DialogAction.EDIT, crudEntity);
		if (crudDialogController != null) {
			_editEntity(crudDialogController, getView(), crudEntity);
		}
	}

	protected void _editEntity(ICRUDDialogController<E> crudDialogController, final IView view, final E crudEntity) {
		crudDialogController.editEntity(view, crudEntity, new IResultCallback<E>() {
			@Override
			public void setResult(final E entity) {
				if (entity != null) {
					getViewFactory().waitFullComponentViewWorker(view, new AbstractLoadingViewWorker<Serializable>() {
						@Override
						protected Serializable doLoading() throws Exception {
							return editCRUDEntity(entity);
						}

						@Override
						public void success(Serializable e) {
							editEntitySuccess(e);
						}

						@Override
						public void fail(Throwable t) {
							if (t.getCause() instanceof ServiceException) {
								ServiceException err = (ServiceException) t.getCause();
								if (ICRUDEntityService.UNICITY_CONSTRAINT.equals(err.getCode())) {
									getViewFactory().showErrorMessageDialog(view, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), getUnicityError(err.getDescription()));
									_editEntity(entity);
								} else {
									getViewFactory().showErrorMessageDialog(view, t);
								}
							} else {
								getViewFactory().showErrorMessageDialog(view, t);
							}
						}
					});
				}
			}
		});
	}

	protected void editEntitySuccess(Serializable idEntity) {
		loadPagination();
	}

	/**
	 * Show a dialog for confirmation delete entity
	 * 
	 * @param entity
	 */
	@Override
	public void deleteEntity(final G paginationEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(E e) {
				if (e != null) {
					_deleteEntity(e);
				}
			}
		});
	}

	protected void _deleteEntity(final E crudEntity) {
		if (getViewFactory().showQuestionMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().validation(),
				StaticWidgetHelper.getSynaptixWidgetConstantsBundle().doYouWantToDeleteTheSelectedItem())) {
			_deleteEntity(getView(), crudEntity);
		}
	}

	protected void _deleteEntity(final IView view, final E crudEntity) {
		getViewFactory().waitFullComponentViewWorker(view, new AbstractLoadingViewWorker<Serializable>() {
			@Override
			protected Serializable doLoading() throws Exception {
				return removeCRUDEntity(crudEntity);
			}

			@Override
			public void success(Serializable e) {
				deleteEntitySuccess(e);
			}

			@Override
			public void fail(Throwable t) {
				getViewFactory().showErrorMessageDialog(view, t);
			}
		});
	}

	protected void deleteEntitySuccess(Serializable idEntity) {
		loadPagination();
	}

	/**
	 * Show a dialog for clone entity
	 * 
	 * @param entity
	 */
	@Override
	public void cloneEntity(final G paginationEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(E e) {
				if (e != null) {
					_cloneEntity(e);
				}
			}
		});
	}

	protected void _cloneEntity(E crudEntity) {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController(DialogAction.CLONE, crudEntity);
		if (crudDialogController != null) {
			_cloneEntity(crudDialogController, getView(), crudEntity);
		}
	}

	protected void _cloneEntity(ICRUDDialogController<E> crudDialogController, final IView view, final E crudEntity) {
		crudDialogController.cloneEntity(getView(), crudEntity, new IResultCallback<E>() {
			@Override
			public void setResult(final E entity) {
				if (entity != null) {
					getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Serializable>() {
						@Override
						protected Serializable doLoading() throws Exception {
							return addCRUDEntity(entity);
						}

						@Override
						public void success(Serializable e) {
							cloneEntitySuccess(e);
						}

						@Override
						public void fail(Throwable t) {
							if (t.getCause() instanceof ServiceException) {
								ServiceException err = (ServiceException) t.getCause();
								if (ICRUDEntityService.UNICITY_CONSTRAINT.equals(err.getCode())) {
									getViewFactory().showErrorMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), getUnicityError(err.getDescription()));
									_cloneEntity(entity);
								} else {
									getViewFactory().showErrorMessageDialog(getView(), t);
								}
							} else {
								getViewFactory().showErrorMessageDialog(getView(), t);
							}
						}
					});
				}
			}
		});
	}

	protected void cloneEntitySuccess(Serializable idEntity) {
		loadPagination();
	}

	/**
	 * Method called for loading a entity with pagination entity
	 * 
	 * @param id
	 * @param resultCallback
	 * @return
	 */
	protected final IWaitWorker loadEntity(final Serializable id, final IResultCallback<E> resultCallback) {
		if (id == null) {
			resultCallback.setResult(null);
			return null;
		} else {
			return getViewFactory().waitFullComponentViewWorker(getSearchComponentsView(), new AbstractLoadingViewWorker<E>() {

				@Override
				protected E doLoading() throws Exception {
					return loadFullEntity(crudComponentClass, id);
				}

				@Override
				public void success(E e) {
					resultCallback.setResult(e);
				}

				@Override
				public void fail(Throwable t) {
					getViewFactory().showErrorMessageDialog(getSearchComponentsView(), t);
					resultCallback.setResult(null);
				}
			});
		}
	}

	/**
	 * Load a full entity from database. This method is called within a view worker
	 * 
	 * @param crudComponentClass
	 * @param id
	 * @return entity
	 */
	protected E loadFullEntity(Class<E> crudComponentClass, Serializable id) {
		return getEntityService().findEntityById(crudComponentClass, id);
	}

	/**
	 * Edit a entity, with other view
	 * 
	 * @param view
	 * @param id
	 */
	public void editEntity(final IView view, Serializable id) {
		loadEntity(id, new IResultCallback<E>() {
			@Override
			public void setResult(E e) {
				if (e != null) {
					ICRUDDialogController<E> crudDialogController = newCRUDDialogController(DialogAction.EDIT, e);
					if (crudDialogController != null) {
						_editEntity(crudDialogController, getView(), e);
					}
				}
			}
		});
	}
}
