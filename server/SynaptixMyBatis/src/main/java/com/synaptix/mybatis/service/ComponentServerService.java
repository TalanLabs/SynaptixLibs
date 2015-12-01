package com.synaptix.mybatis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.synaptix.component.IComponent;
import com.synaptix.entity.IId;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.delegate.ComponentServiceDelegate;
import com.synaptix.service.IComponentService;
import com.synaptix.service.ServiceException;
import com.synaptix.service.filter.RootNode;
import com.synaptix.service.model.ISortOrder;

public class ComponentServerService implements IComponentService {

	private final IDaoSession daoSession;

	private final ComponentServiceDelegate componentServiceDelegate;

	@Inject
	public ComponentServerService(IDaoSession daoSession, ComponentServiceDelegate componentServiceDelegate) {
		super();
		this.daoSession = daoSession;
		this.componentServiceDelegate = componentServiceDelegate;
	}

	@Override
	public <E extends IComponent> int countPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap) throws ServiceException {
		try {
			daoSession.begin();
			return componentServiceDelegate.countPaginationOld(componentClass, valueFilterMap);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}

	@Override
	public <E extends IComponent> List<E> selectPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> sortOrders, Set<String> columns)
			throws ServiceException {
		try {
			daoSession.begin();
			return componentServiceDelegate.selectPaginationOld(componentClass, valueFilterMap, from, to, sortOrders, columns);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}

	@Override
	public <E extends IComponent> List<E> findComponentsByIdParent(Class<E> componentClass, String idParentPropertyName, IId idParent) throws ServiceException {
		try {
			daoSession.begin();
			return componentServiceDelegate.findComponentsByIdParent(componentClass, idParentPropertyName, idParent);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}

	@Override
	public <E extends IComponent> List<E> selectSuggest(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders, Set<String> columns) throws ServiceException {
		try {
			daoSession.begin();
			return componentServiceDelegate.selectSuggest(componentClass, filterRootNode, sortOrders, columns);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}

	@Override
	public <E extends IComponent> List<E> selectList(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) throws ServiceException {
		try {
			daoSession.begin();
			return componentServiceDelegate.selectList(componentClass, filterRootNode, sortOrders);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}

	@Override
	public <E extends IComponent> E selectOne(Class<E> componentClass, RootNode filterRootNode) throws ServiceException {
		try {
			daoSession.begin();
			return componentServiceDelegate.selectOne(componentClass, filterRootNode);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}

	@Override
	public <E extends IComponent> int countPagination(Class<E> componentClass, RootNode filterRootNode) throws ServiceException {
		try {
			daoSession.begin();
			return componentServiceDelegate.countPagination(componentClass, filterRootNode);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}

	@Override
	public <E extends IComponent> List<E> selectPagination(Class<E> componentClass, RootNode filterRootNode, int from, int to, List<ISortOrder> sortOrders, Set<String> columns)
			throws ServiceException {
		try {
			daoSession.begin();
			return componentServiceDelegate.selectPagination(componentClass, filterRootNode, from, to, sortOrders, columns);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}
}
