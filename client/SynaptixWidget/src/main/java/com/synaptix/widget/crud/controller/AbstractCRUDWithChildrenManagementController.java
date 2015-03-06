package com.synaptix.widget.crud.controller;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CancellationException;

import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.entity.IEntity;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.ServiceException;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.crud.view.descriptor.ICRUDWithChildrenManagementViewDescriptor;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

/**
 * A CRUD and children Controller, create a table and filter and action CRUD and table for children and action CRUD
 * 
 * @param <V>
 *            View factory
 * @param <E>
 *            CRUD Entity
 * @param <G>
 *            Pagination entity which
 * @param <C>
 *            CRUD Child Entity
 */
public abstract class AbstractCRUDWithChildrenManagementController<V extends ISynaptixViewFactory, E extends IEntity, G extends IEntity, C extends IEntity> extends
		AbstractCRUDManagementController<V, E, G> implements ICRUDWithChildrenManagementController<G, C> {

	protected final Class<C> childClass;

	protected final String idParentPropertyName;

	protected final Class<? extends ICRUDEntityService<C>> crudChildEntityServiceClass;

	private IWaitWorker loadChildrenWaitWorker;

	public AbstractCRUDWithChildrenManagementController(V viewFactory, Class<E> crudComponentClass, Class<G> paginationComponentClass, Class<C> childClass, String idParentPropertyName) {
		this(viewFactory, crudComponentClass, paginationComponentClass, childClass, idParentPropertyName, null, null, null);
	}

	public AbstractCRUDWithChildrenManagementController(V viewFactory, Class<E> crudComponentClass, Class<G> paginationComponentClass, Class<C> childClass, String idParentPropertyName,
			Class<? extends ICRUDEntityService<E>> crudEntityServiceClass, Class<? extends IPaginationService<G>> paginationServiceClass,
			Class<? extends ICRUDEntityService<C>> crudChildEntityServiceClass) {
		super(viewFactory, crudComponentClass, paginationComponentClass, crudEntityServiceClass, paginationServiceClass);

		this.childClass = childClass;
		this.idParentPropertyName = idParentPropertyName;
		this.crudChildEntityServiceClass = crudChildEntityServiceClass;
	}

	/**
	 * Get a CRUD child entity service
	 * 
	 * @return
	 */
	protected final ICRUDEntityService<C> getCRUDChildEntityService() {
		return getServiceFactory().getService(crudChildEntityServiceClass);
	}

	/**
	 * Create a CRUD child dialog controller
	 * 
	 * @param dialogAction
	 * @param parentEntity
	 * @param childEntity
	 * @return
	 */
	protected abstract ICRUDDialogController<C> newCRUDChildDialogController(DialogAction dialogAction, E parentEntity, C childEntity);

	/**
	 * Create a CRUD wiith children management view descriptor. NOT CALL use getCRUDWithChildrenManagementViewDescriptor()
	 * 
	 * @return
	 */
	protected abstract ICRUDWithChildrenManagementViewDescriptor<G, C> createCRUDWithChildrenManagementViewDescriptor();

	@Override
	protected final ICRUDManagementViewDescriptor<G> createCRUDManagementViewDescriptor() {
		return createCRUDWithChildrenManagementViewDescriptor();
	}

	/**
	 * Get a CRUD with children management view descriptor
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final ICRUDWithChildrenManagementViewDescriptor<G, C> getCRUDWithChildrenManagementViewDescriptor() {
		return (ICRUDWithChildrenManagementViewDescriptor<G, C>) getCRUDManagementViewDescriptor();
	}

	/**
	 * Load a childrens
	 * 
	 * @param vat
	 */
	@Override
	public void loadChildren(final G paginationEntity) {
		if (loadChildrenWaitWorker != null && !loadChildrenWaitWorker.isDone()) {
			loadChildrenWaitWorker.cancel(false);
			loadChildrenWaitWorker = null;
		}
		if (paginationEntity != null) {
			loadChildrenWaitWorker = getViewFactory().waitComponentViewWorker(getView(), new AbstractLoadingViewWorker<List<C>>() {
				@Override
				protected List<C> doLoading() throws Exception {
					return getComponentService().findComponentsByIdParent(childClass, idParentPropertyName, paginationEntity.getId());
				}

				@Override
				public void success(List<C> e) {
					getCRUDWithChildrenManagementViewDescriptor().setChildComponents(paginationEntity, e);
				}

				@Override
				public void fail(Throwable t) {
					if (!(t instanceof CancellationException)) {
						getViewFactory().showErrorMessageDialog(getView(), t);
						getCRUDWithChildrenManagementViewDescriptor().setChildComponents(paginationEntity, null);
					}
				}
			});
		} else {
			getCRUDWithChildrenManagementViewDescriptor().setChildComponents(null, null);
		}
	}

	private Serializable addCRUDChildEntity(C entity) {
		if (crudChildEntityServiceClass != null) {
			return getCRUDChildEntityService().addCRUDEntity(entity);
		} else {
			return getEntityService().addEntity(entity);
		}
	}

	private Serializable editCRUDChildEntity(C entity) {
		if (crudChildEntityServiceClass != null) {
			return getCRUDChildEntityService().editCRUDEntity(entity);
		} else {
			return getEntityService().editEntity(entity);
		}
	}

	private Serializable removeCRUDChildEntity(C entity) {
		if (crudChildEntityServiceClass != null) {
			return getCRUDChildEntityService().removeCRUDEntity(entity);
		} else {
			return getEntityService().removeEntity(entity);
		}
	}

	@Override
	public void addChildEntity(final G paginationEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(E parentEntity) {
				ICRUDDialogController<C> crudDialogController = newCRUDChildDialogController(DialogAction.NEW, parentEntity, null);
				if (crudDialogController != null) {
					crudDialogController.addEntity(getView(), new IResultCallback<C>() {
						@Override
						public void setResult(final C entity) {
							if (entity != null) {
								getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Serializable>() {
									@Override
									protected Serializable doLoading() throws Exception {
										copyParentFields(entity, paginationEntity);
										entity.straightSetProperty(idParentPropertyName, paginationEntity.getId());
										return addCRUDChildEntity(entity);
									}

									@Override
									public void success(Serializable e) {
										loadChildren(paginationEntity);
									}

									@Override
									public void fail(Throwable t) {
										if (t.getCause() instanceof ServiceException) {
											ServiceException err = (ServiceException) t.getCause();
											if (ICRUDEntityService.UNICITY_CONSTRAINT.equals(err.getCode())) {
												getViewFactory()
														.showErrorMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), getUnicityError(err.getDescription()));

												cloneChildEntity(paginationEntity, entity);
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
			}
		});
	}

	/**
	 * Used to copy some particular fields from the parentEntity to the child
	 * 
	 * @param entity
	 * @param paginationEntity
	 */
	protected void copyParentFields(C childEntity, G parentEntity) {
	}

	@Override
	public void editChildEntity(final G paginationEntity, final C originalChildEntity) {
		loadEntitiesForChild(paginationEntity.getId(), originalChildEntity.getId(), new IResultCallback<ChildResult>() {
			@Override
			public void setResult(ChildResult childResult) {
				E parentEntity = childResult.parentEntity;
				C childEntity = childResult.childEntity;
				ICRUDDialogController<C> crudDialogController = newCRUDChildDialogController(DialogAction.EDIT, parentEntity, childEntity);
				if (crudDialogController != null) {
					crudDialogController.editEntity(getView(), childEntity, new IResultCallback<C>() {
						@Override
						public void setResult(final C entity) {
							if (entity != null) {
								getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Serializable>() {
									@Override
									protected Serializable doLoading() throws Exception {
										return editCRUDChildEntity(entity);
									}

									@Override
									public void success(Serializable e) {
										loadChildren(paginationEntity);
									}

									@Override
									public void fail(Throwable t) {
										if (t.getCause() instanceof ServiceException) {
											ServiceException err = (ServiceException) t.getCause();
											if (ICRUDEntityService.UNICITY_CONSTRAINT.equals(err.getCode())) {
												getViewFactory()
														.showErrorMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), getUnicityError(err.getDescription()));
												editChildEntity(paginationEntity, entity);
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
			}
		});
	}

	@Override
	public void showChildEntity(G paginationEntity, final C childEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(E parentEntity) {
				ICRUDDialogController<C> crudDialogController = newCRUDChildDialogController(DialogAction.SHOW, parentEntity, childEntity);
				if (crudDialogController != null) {
					crudDialogController.showEntity(getView(), childEntity);
				}
			}
		});
	}

	@Override
	public void deleteChildEntity(final G paginationEntity, final C childEntity) {
		if (getViewFactory().showQuestionMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().validation(),
				StaticWidgetHelper.getSynaptixWidgetConstantsBundle().doYouWantToDeleteTheSelectedItem())) {
			getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Serializable>() {
				@Override
				protected Serializable doLoading() throws Exception {
					return removeCRUDChildEntity(childEntity);
				}

				@Override
				public void success(Serializable e) {
					loadChildren(paginationEntity);
				}

				@Override
				public void fail(Throwable t) {
					getViewFactory().showErrorMessageDialog(getView(), t);
				}
			});
		}
	}

	@Override
	public void cloneChildEntity(final G paginationEntity, final C childEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(E parentEntity) {
				ICRUDDialogController<C> crudDialogController = newCRUDChildDialogController(DialogAction.CLONE, parentEntity, childEntity);
				if (crudDialogController != null) {
					crudDialogController.cloneEntity(getView(), childEntity, new IResultCallback<C>() {
						@Override
						public void setResult(final C entity) {
							if (entity != null) {
								getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Serializable>() {
									@Override
									protected Serializable doLoading() throws Exception {
										entity.straightSetProperty(idParentPropertyName, paginationEntity.getId());
										return addCRUDChildEntity(entity);
									}

									@Override
									public void success(Serializable e) {
										loadChildren(paginationEntity);
									}

									@Override
									public void fail(Throwable t) {
										if (t.getCause() instanceof ServiceException) {
											ServiceException err = (ServiceException) t.getCause();
											if (ICRUDEntityService.UNICITY_CONSTRAINT.equals(err.getCode())) {
												getViewFactory()
														.showErrorMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), getUnicityError(err.getDescription()));
												cloneChildEntity(paginationEntity, entity);
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
			}
		});
	}

	/**
	 * Method called for loading a child entity and its parent<br/>
	 * Very useful in case of an exception after commit() has been called from the dialog view
	 * 
	 * @param id
	 * @param resultCallback
	 * @return
	 */
	private IWaitWorker loadEntitiesForChild(final Serializable id, final Serializable idChild, final IResultCallback<ChildResult> resultCallback) {
		if (id == null) {
			resultCallback.setResult(null);
			return null;
		} else {
			return getViewFactory().waitFullComponentViewWorker(getSearchComponentsView(), new AbstractLoadingViewWorker<ChildResult>() {

				@Override
				protected ChildResult doLoading() throws Exception {
					ChildResult childResult = new ChildResult();
					childResult.parentEntity = loadFullEntity(crudComponentClass, id);
					childResult.childEntity = getEntityService().findEntityById(childClass, idChild);
					return childResult;
				}

				@Override
				public void success(ChildResult e) {
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

	private class ChildResult {
		E parentEntity;
		C childEntity;
	}
}
