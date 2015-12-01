package com.synaptix.widget.crud.controller;

import java.io.Serializable;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IEntityService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServiceException;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;
import com.synaptix.widget.viewworker.view.AbstractSavingViewWorker;

/**
 * A CRUD Controller, create instance and call initialize
 * 
 * @param <V>
 *            View factory
 * @param <E>
 *            CRUD Entity
 * @param <G>
 *            View entity which
 */
public abstract class AbstractCRUDController<V extends ISynaptixViewFactory, E extends IEntity, G extends IEntity> extends AbstractController {

	private final V viewFactory;

	private final Class<E> crudComponentClass;

	private final Class<? extends ICRUDEntityService<E>> crudEntityServiceClass;

	public AbstractCRUDController(V viewFactory, Class<E> crudComponentClass, Class<G> viewComponentClass) {
		this(viewFactory, crudComponentClass, viewComponentClass, null);
	}

	public AbstractCRUDController(V viewFactory, Class<E> crudComponentClass, Class<G> viewComponentClass, Class<? extends ICRUDEntityService<E>> crudEntityServiceClass) {
		super();

		this.viewFactory = viewFactory;
		this.crudComponentClass = crudComponentClass;
		this.crudEntityServiceClass = crudEntityServiceClass;
	}

	public final V getViewFactory() {
		return viewFactory;
	}

	public abstract boolean hasAuthWrite();

	/**
	 * Create a CRUD dialog controller
	 * 
	 * @return
	 */
	protected abstract ICRUDDialogController<E> newCRUDDialogController();

	/**
	 * Get service factory
	 * 
	 * @return
	 */
	protected abstract IServiceFactory getServiceFactory();

	/**
	 * Get a CRUD entity service
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
	 * Get unicity error
	 * 
	 * @param description
	 * @return
	 */
	protected String getUnicityError(String description) {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().unicityConstraintException();
	}

	protected abstract void loadEntities();

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
	public void addEntity() {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController();
		crudDialogController.addEntity(getView(), new IResultCallback<E>() {
			@Override
			public void setResult(final E entity) {
				if (entity != null) {
					getViewFactory().waitFullComponentViewWorker(getView(), new AbstractSavingViewWorker<Serializable>() {

						@Override
						protected Serializable doSaving() throws Exception {
							return addCRUDEntity(entity);
						}

						@Override
						public void success(Serializable e) {
							loadEntities();
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

	/**
	 * Show a dialog for edit entity
	 * 
	 * @param paginationEntity
	 */
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

	public void showEntity(final G paginationEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(E e) {
				if (e != null) {
					ICRUDDialogController<E> crudDialogController = newCRUDDialogController();
					crudDialogController.showEntity(getView(), e);
				}
			}
		});
	}

	protected final void _editEntity(final E crudEntity) {
		_editEntity(getView(), crudEntity);
	}

	private void _editEntity(final IView view, final E crudEntity) {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController();
		crudDialogController.editEntity(view, crudEntity, new IResultCallback<E>() {
			@Override
			public void setResult(final E entity) {
				if (entity != null) {
					getViewFactory().waitFullComponentViewWorker(view, new AbstractSavingViewWorker<Serializable>() {
						@Override
						protected Serializable doSaving() throws Exception {
							return editCRUDEntity(entity);
						}

						@Override
						public void success(Serializable e) {
							loadEntities();
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

	/**
	 * Show a dialog for confirmation delete entity
	 * 
	 * @param paginationEntity
	 */
	public void deleteEntity(final G paginationEntity) {
		if (getViewFactory().showQuestionMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().validation(),
				StaticWidgetHelper.getSynaptixWidgetConstantsBundle().doYouWantToDeleteTheSelectedItem())) {
			getViewFactory().waitFullComponentViewWorker(getView(), new AbstractSavingViewWorker<Serializable>() {
				@Override
				protected Serializable doSaving() throws Exception {
					return removeCRUDEntity(getEntityService().findEntityById(crudComponentClass, paginationEntity.getId()));
				}

				@Override
				public void success(Serializable e) {
					loadEntities();
				}

				@Override
				public void fail(Throwable t) {
					getViewFactory().showErrorMessageDialog(getView(), t);
				}
			});
		}
	}

	/**
	 * Show a dialog for clone entity
	 * 
	 * @param paginationEntity
	 */
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

	protected final void _cloneEntity(E crudEntity) {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController();
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
							loadEntities();
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

	/**
	 * Method called for loading a entity with pagination entity
	 * 
	 * @param id
	 * @param resultCallback
	 * @return
	 */
	protected final IWaitWorker loadEntity(final IId id, final IResultCallback<E> resultCallback) {
		if (id == null) {
			resultCallback.setResult(null);
			return null;
		} else {
			return getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<E>() {

				@Override
				protected E doLoading() throws Exception {
					return getEntityService().findEntityById(crudComponentClass, id);
				}

				@Override
				public void success(E e) {
					resultCallback.setResult(e);
				}

				@Override
				public void fail(Throwable t) {
					getViewFactory().showErrorMessageDialog(getView(), t);
					resultCallback.setResult(null);
				}
			});
		}
	}

	/**
	 * Edit a entity, with other view
	 * 
	 * @param view
	 * @param id
	 */
	public void editEntity(final IView view, IId id) {
		loadEntity(id, new IResultCallback<E>() {
			@Override
			public void setResult(E e) {
				if (e != null) {
					_editEntity(view, e);
				}
			}
		});
	}
}
