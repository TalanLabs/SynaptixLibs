package com.synaptix.mybatis.delegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import com.google.inject.Inject;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.EntityFields;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.extension.BusinessPropertyExtensionDescriptor;
import com.synaptix.entity.extension.IBusinessComponentExtension;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.dao.mapper.BusinessMapper;
import com.synaptix.service.IEntityService;
import com.synaptix.service.ServiceException;
import com.synaptix.service.model.ICancellableEntity;

public class EntityServiceDelegate {

	private final IDaoSession daoSession;

	private ComponentServiceDelegate componentServiceDelegate;


	@Inject
	private IParamsServiceDelegate paramsServiceDelegate;

	@Inject
	public EntityServiceDelegate(IDaoSession daoSession) {
		super();
		this.daoSession = daoSession;
	}

	@Inject
	public void setComponentServiceDelegate(ComponentServiceDelegate componentServiceDelegate) {
		this.componentServiceDelegate = componentServiceDelegate;
	}

	private BusinessMapper getBusinessMapper() {
		return daoSession.getMapper(BusinessMapper.class);
	}

	private <E extends IEntity> boolean isExistBusinessKey(E entity) {
		boolean res = false;
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(entity);
		Iterator<PropertyDescriptor> it = cd.getPropertyDescriptors().iterator();
		while (it.hasNext() && !res) {
			PropertyDescriptor pd = it.next();
			BusinessPropertyExtensionDescriptor bp = (BusinessPropertyExtensionDescriptor) pd.getPropertyExtensionDescriptor(IBusinessComponentExtension.class);
			res = bp != null;
		}
		return res;
	}

	/**
	 * Check unicit constraint
	 */
	public <E extends IEntity> void checkUnicityConstraint(E entity) throws ServiceException {
		if (!isExistBusinessKey(entity)) {
			return;
		}
		int ret = 0;
		try {
			if (entity != null) {
				ret = getBusinessMapper().checkUnicityConstraint(entity);

				if (ret > 0) {
					throw new ServiceException(IEntityService.UNICITY_CONSTRAINT, null);
				}
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException("", e.getMessage(), e);
		}
	}

	private String validateAndGetCanceledReference(IEntity entity) throws InvocationTargetException, IllegalAccessException {
		for(Method method:entity.getClass().getDeclaredMethods()){
			if(ICancellable.class.isAssignableFrom(method.getReturnType()) ){
				ICancellable cancelable = (ICancellable) method.invoke(entity);
				if(cancelable != null && cancelable.getCheckCancel()) {
					return method.getReturnType().getSimpleName();
				}
			}
		}

		return null;
	}

	public <E extends IEntity> void checkCancelConstraint(E entity) throws ServiceException {
		String ret = null;
		try {
			if (entity != null) {
				ret = validateAndGetCanceledReference(entity);
				if (ret != null) {
					throw new ServiceException(IEntityService.CHECK_CANCEL_CONSTRAINT, ret, null);
				}
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException("", e.getMessage(), e);
		}
	}

	/**
	 * Add entity and check unicity
	 */
	public <E extends IEntity> int addEntity(E entity, boolean checkUnicityConstraint) throws ServiceException {
		if(paramsServiceDelegate != null && paramsServiceDelegate.getBooleanParam("ALLOW_CHECK_CANCEL_VALIDATION", false)) {
			checkCancelConstraint(entity);
		}

		if (checkUnicityConstraint) {
			checkUnicityConstraint(entity);
		}
		return daoSession.saveEntity(entity);
	}

	/**
	 * Edit entity and check unicity
	 */
	public <E extends IEntity> int editEntity(E entity, boolean checkUnicityConstraint) throws ServiceException {
		if(paramsServiceDelegate != null && paramsServiceDelegate.getBooleanParam("ALLOW_CHECK_CANCEL_VALIDATION", false)) {
			checkCancelConstraint(entity);
		}

		if (checkUnicityConstraint) {
			checkUnicityConstraint(entity);
		}
		return daoSession.saveOrUpdateEntity(entity);
	}

	/**
	 * Remove entity, delete or cancel
	 */
	public <E extends IEntity> int removeEntity(E entity) throws ServiceException {
		int ret;
		if (ICancellableEntity.class.isAssignableFrom(entity.getClass())) {
			ret = cancelEntity((ICancellableEntity) entity);
		} else {
			ret = daoSession.deleteEntity(entity);
		}
		return ret;
	}

	private <E extends IEntity & ICancellable> int cancelEntity(E entity) throws ServiceException {
		return daoSession.cancelEntity(entity);
	}

	/**
	 * Find a entity by id
	 */
	public <E extends IEntity> E findEntityById(Class<E> entityClass, IId id) throws ServiceException {
		return componentServiceDelegate.findComponentByPropertyName(entityClass, EntityFields.id().name(), id);
	}
}
