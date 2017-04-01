package com.synaptix.widget.crud.controller;

import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.poi.ss.formula.eval.NotImplementedException;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.EntityFields;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IEntityService;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.ServiceException;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.component.util.SearchListener;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;
import com.synaptix.widget.viewworker.view.AbstractSavingViewWorker;

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
public abstract class AbstractCRUDManagementController<V extends ISynaptixViewFactory, E extends IEntity, G extends IEntity> extends AbstractComponentsManagementController<V, G>
		implements ICRUDManagementController<G>, ICRUDContext<E> {

	protected final Class<E> crudComponentClass;
	private final Class<? extends ICRUDEntityService<E>> crudEntityServiceClass;
	private int selectedTabIndex;

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
	 */
	protected abstract ICRUDManagementViewDescriptor<G> createCRUDManagementViewDescriptor();

	/**
	 * Get a CRUD manamgement view descriptor
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
	 */
	protected abstract ICRUDDialogController<E> newCRUDDialogController(DialogAction dialogAction, E entity);

	/**
	 * Get a CRUD Entity service
	 */
	protected final ICRUDEntityService<E> getCRUDEntityService() {
		return getServiceFactory().getService(crudEntityServiceClass);
	}

	/**
	 * Get a entity service
	 */
	protected final IEntityService getEntityService() {
		return getServiceFactory().getService(IEntityService.class);
	}

	/**
	 * Get a unicity error
	 */
	protected String getUnicityError(String description) {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().unicityConstraintException();
	}

	private IId addCRUDEntity(E entity) {
		if (crudEntityServiceClass != null) {
			return getCRUDEntityService().addCRUDEntity(entity);
		} else {
			return getEntityService().addEntity(entity);
		}
	}

	private IId editCRUDEntity(E entity) {
		if (crudEntityServiceClass != null) {
			return getCRUDEntityService().editCRUDEntity(entity);
		} else {
			return getEntityService().editEntity(entity);
		}
	}

	private IId removeCRUDEntity(E entity) {
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
					getViewFactory().waitFullComponentViewWorker(view, new AbstractLoadingViewWorker<IId>() {
						@Override
						protected IId doLoading() throws Exception {
							return addCRUDEntity(entity);
						}

						@Override
						public void success(IId e) {
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
									displayException(view, t);
									if (reopenIfException()) {
										_cloneEntity(entity);
									}
								}
							} else {
								displayException(view, t);
								if (reopenIfException()) {
									_cloneEntity(entity);
								}
							}
						}
					});
				}
			}
		});
	}

	protected void displayException(IView view, Throwable t) {
		getViewFactory().showErrorMessageDialog(view, t);
	}

	protected boolean reopenIfException() {
		return false;
	}

	protected void addEntitySuccess(IId idEntity) {
		loadPagination();
	}

	@Override
	public void showEntity(final G paginationEntity) {
		loadEntity(paginationEntity.getId(), new IResultCallback<E>() {
			@Override
			public void setResult(final E e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (e != null) {
							_showEntity(e);
						}
					}
				});
			}
		});
	}

	protected void _showEntity(final E crudEntity) {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController(DialogAction.SHOW, crudEntity);
		if (crudDialogController != null) {
			crudDialogController.setCRUDContext(this);
			crudDialogController.showEntity(getView(), crudEntity);
		}
	}

	/**
	 * Show a dialog for edit entity
	 */
	@Override
	@Deprecated
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

	// used to add only
	protected void _editEntity(final E crudEntity) {
		ICRUDDialogController<E> crudDialogController = newCRUDDialogController(DialogAction.EDIT, crudEntity);
		if (crudDialogController != null) {
			_editEntity(crudDialogController, getView(), crudEntity);
		}
	}

	// used to add only
	protected void _editEntity(ICRUDDialogController<E> crudDialogController, final IView view, final E crudEntity) {
		crudDialogController.setCRUDContext(this);
		crudDialogController.editEntity(view, crudEntity, new IResultCallback<E>() {
			@Override
			public void setResult(final E entity) {
				if (entity != null) {
					getViewFactory().waitFullComponentViewWorker(view, new AbstractLoadingViewWorker<IId>() {
						@Override
						protected IId doLoading() throws Exception {
							return editCRUDEntity(entity);
						}

						@Override
						public void success(IId e) {
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
									displayException(view, t);
									if (reopenIfException()) {
										_editEntity(entity);
									}
								}
							} else {
								displayException(view, t);
								if (reopenIfException()) {
									_editEntity(entity);
								}
							}
						}
					});
				}
			}
		});
	}

	protected void editEntitySuccess(IId idEntity) {
		loadPagination();
		// after the pagination (asynchronous), we could select the entity if it is still here
	}

	/**
	 * Show a dialog for confirmation delete entity
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

	@Override
	public void deleteEntities(List<G> paginationEntityList) {
		throw new NotImplementedException("Multi cancel is not implemented");
	}

	protected void _deleteEntity(final E crudEntity) {
		if (getViewFactory().showQuestionMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().validation(),
				StaticWidgetHelper.getSynaptixWidgetConstantsBundle().doYouWantToDeleteTheSelectedItem())) {
			_deleteEntity(getView(), crudEntity);
		}
	}

	protected void _deleteEntity(final IView view, final E crudEntity) {
		getViewFactory().waitFullComponentViewWorker(view, new AbstractLoadingViewWorker<IId>() {
			@Override
			protected IId doLoading() throws Exception {
				return removeCRUDEntity(crudEntity);
			}

			@Override
			public void success(IId e) {
				deleteEntitySuccess(e);
			}

			@Override
			public void fail(Throwable t) {
				displayException(view, t);
			}
		});
	}

	protected void deleteEntitySuccess(IId idEntity) {
		loadPagination();
	}

	/**
	 * Show a dialog for clone entity
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
					getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<IId>() {
						@Override
						protected IId doLoading() throws Exception {
							return addCRUDEntity(entity);
						}

						@Override
						public void success(IId e) {
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
									displayException(view, t);
									if (reopenIfException()) {
										_cloneEntity(entity);
									}
								}
							} else {
								displayException(view, t);
								if (reopenIfException()) {
									_cloneEntity(entity);
								}
							}
						}
					});
				}
			}
		});
	}

	protected void cloneEntitySuccess(IId idEntity) {
		loadPagination();
	}

	/**
	 * Method called for loading a entity with pagination entity
	 */
	protected final IWaitWorker loadEntity(final IId id, final IResultCallback<E> resultCallback) {
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
					displayException(getSearchComponentsView(), t);
					resultCallback.setResult(null);
				}
			});
		}
	}

	/**
	 * Load a full entity from database. This method is called within a view worker
	 */
	protected E loadFullEntity(Class<E> crudComponentClass, IId id) {
		return getEntityService().findEntityById(crudComponentClass, id);
	}

	/**
	 * Edit a entity, with other view
	 */
	public void editEntity(final IView view, IId id) {
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

	@Override
	public boolean hasPrevious(IId id) {
		List<G> componentList = getCRUDManagementViewDescriptor().getComponentList();
		List<IId> idList = ComponentHelper.extractValues(componentList, EntityFields.id().name());
		return idList.indexOf(id) + 1 > 1;
	}

	@Override
	public boolean hasNext(IId id) {
		List<G> componentList = getCRUDManagementViewDescriptor().getComponentList();
		List<IId> idList = ComponentHelper.extractValues(componentList, EntityFields.id().name());
		return idList.indexOf(id) + 1 < idList.size();
	}

	@Override
	public void showPrevious(IId id) {
		List<G> componentList = getCRUDManagementViewDescriptor().getComponentList();
		List<IId> idList = ComponentHelper.extractValues(componentList, EntityFields.id().name());
		int idx = idList.indexOf(id);
		if (idx > 0) {
			IId previousId = idList.get(idx - 1);
			G component = ComponentFactory.getInstance().createInstance(componentClass);
			component.setId(previousId);
			getCRUDManagementViewDescriptor().selectLine(idx - 1);
			showEntity(component);
		}
	}

	@Override
	public void showNext(IId id) {
		List<G> componentList = getCRUDManagementViewDescriptor().getComponentList();
		List<IId> idList = ComponentHelper.extractValues(componentList, EntityFields.id().name());
		int idx = idList.indexOf(id);
		if (idx < idList.size()) {
			IId nextId = idList.get(idx + 1);
			G component = ComponentFactory.getInstance().createInstance(componentClass);
			component.setId(nextId);
			getCRUDManagementViewDescriptor().selectLine(idx + 1);
			showEntity(component);
		}
	}

	@Override
	public void saveBean(final E entity, final IView parent, final AbstractCRUDDialogController.CloseAction closeAction) {
		getViewFactory().waitFullComponentViewWorker(parent != null ? parent : getView(), new AbstractSavingViewWorker<E>() {
			@Override
			protected E doSaving() throws Exception {
				IId id = editCRUDEntity(entity);
				if (id != null) {
					return loadFullEntity(crudComponentClass, id);
				}
				return entity;
			}

			@Override
			public void success(E newEntity) {
				entity.straightSetProperties(newEntity.straightGetProperties());

				if (closeAction != null) {
					SearchListener searchListener = new SearchListener() {

						@Override
						public boolean searchPerformed(Map<String, Object> valueFilterMap) {
							switch (closeAction) {
							case SHOW_PREVIOUS:
								showPrevious(entity.getId());
								break;
							case SHOW_NEXT:
								showNext(entity.getId());
								break;
							default:
								break;
							}

							return true;
						}
					};
					addSearchListener(searchListener);
				}
				editEntitySuccess(entity.getId()); // to confirm, we lose the scroll or might have issues with new lines or lines removed, not that great
			}

			@Override
			public void fail(Throwable t) {
				if (t.getCause() instanceof ServiceException) {
					ServiceException err = (ServiceException) t.getCause();
					if (ICRUDEntityService.UNICITY_CONSTRAINT.equals(err.getCode())) {
						getViewFactory().showErrorMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), getUnicityError(err.getDescription()));
						// _editEntity(entity); // we are in browse mode
					} else {
						displayException(parent, t);
					}
				} else {
					displayException(parent, t);
				}
				if (parent == null && reopenIfException()) { // if parent is null, it means the view has been closed
					_editEntity(entity);
				}
			}
		});
	}

	@Override
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	@Override
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	@Override
	public boolean askSaveChanges(IView parent) {
		return getViewFactory().showQuestionMessageDialog(parent, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().confirmation(),
				StaticWidgetHelper.getSynaptixWidgetConstantsBundle().thereAreChangesSaveThem());
	}

	public enum DialogAction {

		NEW, EDIT, SHOW, CLONE

	}
}
