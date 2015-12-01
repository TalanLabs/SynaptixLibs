package com.synaptix.taskmanager.service;

import java.util.List;

import com.google.inject.Inject;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IId;
import com.synaptix.mybatis.delegate.ComponentServiceDelegate;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;
import com.synaptix.mybatis.service.Transactional;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.dao.mapper.TaskChainMapper;
import com.synaptix.taskmanager.model.AssoTaskChainTypeFields;
import com.synaptix.taskmanager.model.IAssoTaskChainType;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskType;

public class TaskChainServerService extends AbstractSimpleService implements ITaskChainService {

	private final ComponentServiceDelegate componentServiceDelegate;

	private final EntityServiceDelegate entityServiceDelegate;

	@Inject
	public TaskChainServerService(ComponentServiceDelegate componentServiceDelegate, EntityServiceDelegate entityServiceDelegate) {
		super();
		this.componentServiceDelegate = componentServiceDelegate;
		this.entityServiceDelegate = entityServiceDelegate;
	}

	private final TaskChainMapper getTaskChainMapper() {
		return getDaoSession().getMapper(TaskChainMapper.class);
	}

	@Override
	public IId addCRUDEntity(ITaskChain entity) throws ServiceException {
		IId res = null;
		try {
			getDaoSession().begin();
			int count = entityServiceDelegate.addEntity(entity, true);
			if (count == 1) {
				List<ITaskType> taskTypes = entity.getTaskTypes();
				if (taskTypes != null && !taskTypes.isEmpty()) {
					for (ITaskType taskType : taskTypes) {
						IAssoTaskChainType assoTaskChainType = ComponentFactory.getInstance().createInstance(IAssoTaskChainType.class);
						assoTaskChainType.setIdTaskChain(entity.getId());
						assoTaskChainType.setIdTaskType(taskType.getId());
						getDaoSession().saveEntity(assoTaskChainType);
					}
				}

				getDaoSession().commit();

				res = entity.getId();
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
		return res;
	}

	@Override
	public IId editCRUDEntity(ITaskChain entity) throws ServiceException {
		IId res = null;
		try {
			getDaoSession().begin();

			int count = entityServiceDelegate.editEntity(entity, true);
			if (count == 1) {
				List<IAssoTaskChainType> drs = componentServiceDelegate.findComponentsByIdParent(IAssoTaskChainType.class, AssoTaskChainTypeFields.idTaskChain().name(), entity.getId());
				if (drs != null && !drs.isEmpty()) {
					for (IAssoTaskChainType dr : drs) {
						getDaoSession().deleteEntity(dr);
					}
				}

				List<ITaskType> taskTypes = entity.getTaskTypes();
				if (taskTypes != null && !taskTypes.isEmpty()) {
					for (ITaskType taskType : taskTypes) {
						IAssoTaskChainType assoTaskChainType = ComponentFactory.getInstance().createInstance(IAssoTaskChainType.class);
						assoTaskChainType.setIdTaskChain(entity.getId());
						assoTaskChainType.setIdTaskType(taskType.getId());
						getDaoSession().saveEntity(assoTaskChainType);
					}
				}

				getDaoSession().commit();

				res = entity.getId();
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
		return res;
	}

	@Override
	public IId removeCRUDEntity(ITaskChain entity) throws ServiceException {
		IId res = null;
		try {
			getDaoSession().begin();
			int count = entityServiceDelegate.removeEntity(entity);
			if (count == 1) {
				getTaskChainMapper().deleteAssoTaskChainTaskType(entity.getId());
				getDaoSession().commit();
				res = entity.getId();
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
		return res;
	}

	@Override
	@Transactional
	public boolean getTaskChainsFromTaskType(ITaskType taskType) {
		return !getTaskChainMapper().findTaskChainsByTaskType(taskType.getId()).isEmpty();
	}

	@Override
	@Transactional
	public boolean getTaskChainCriteriasFromTaskChain(ITaskChain taskChain) {
		return !getTaskChainMapper().findTaskChainsByTaskType(taskChain.getId()).isEmpty();
	}

	// @Override
	// @Transactional
	// public int countPagination(Map<String, Object> valueFilterMap) throws ServiceException {
	// consolidateValueFilterMap(valueFilterMap);
	// return componentServiceDelegate.countPaginationOld(ITaskChain.class, valueFilterMap);
	// }
	//
	// @Override
	// @Transactional
	// public List<ITaskChain> selectPagination(Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> orders, Set<String> columns) throws ServiceException {
	// consolidateValueFilterMap(valueFilterMap);
	// return componentServiceDelegate.selectPaginationOld(ITaskChain.class, valueFilterMap, from, to, orders, columns, false);
	// }
	//
	// private void consolidateValueFilterMap(Map<String, Object> valueFilterMap) {
	// {
	// final Object taskTypes = valueFilterMap.get(TaskChainFields.taskTypes().name());
	// if (taskTypes != null) {
	//
	// valueFilterMap.put(TaskChainFields.taskTypes().name(), new IRequestBuilder() {
	//
	// @Override
	// public String getSqlChunk(String sqlName, String prefix) {
	// // sb.append("exists (select 1 from t_member_role a inner join t_zip b on a.id_zip = b.id where a.id = t.").append(idMemberRoleColumnName)
	// StringBuilder sb = new StringBuilder("exists(SELECT 1 FROM T_ASSO_TASK_CHAIN_TYPE a WHERE a.ID_TASK_TYPE = ? AND a.ID_TASK_CHAIN = t.id(");
	//
	// sb.append("))");
	// return sb.toString();
	// }
	// });
	// }
	// }
	// }
	// }
}
