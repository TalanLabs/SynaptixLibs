package com.synaptix.deployer.service.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.deployer.dao.IDeployerDaoSession;
import com.synaptix.deployer.delegate.DeployerDelegate;
import com.synaptix.deployer.mapper.DeployerMapper;
import com.synaptix.deployer.model.ICompareConstraintResult;
import com.synaptix.deployer.model.ICompareStructureResult;
import com.synaptix.deployer.model.IConstraint;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.deployer.model.ITableColumn;
import com.synaptix.deployer.model.SqlObject;
import com.synaptix.deployer.service.IDeployerService;
import com.synaptix.service.ServiceException;

public class DeployerServerService implements IDeployerService {

	@Inject
	private IDeployerDaoSession daoSession;

	@Inject
	private DeployerDelegate deployerDelegate;

	@Override
	public String getDbName(ISynaptixDatabaseSchema database) {
		try {
			daoSession.begin(database);
			return getDeployerMapper().getDbName();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}
	}

	@Override
	public ICompareStructureResult compareStructure(ISynaptixDatabaseSchema database1, ISynaptixDatabaseSchema database2, List<String> ignoreTableList) {
		List<ITableColumn> database1TableColumnList = null;

		try {
			daoSession.begin(database1);
			database1TableColumnList = getDeployerMapper().getTableAndColumns(database1.getTablespaceName());
		} catch (Exception e) {
			LogFactory.getLog(DeployerServerService.class).error("", e);
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}
		List<ITableColumn> database2TableColumnList = null;
		try {
			daoSession.begin(database2);
			database2TableColumnList = getDeployerMapper().getTableAndColumns(database2.getTablespaceName());
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}

		return deployerDelegate.compileStructureResult(database1, database1TableColumnList, database2, database2TableColumnList, ignoreTableList);
	}

	@Override
	public ICompareConstraintResult compareConstraint(ISynaptixDatabaseSchema database1, ISynaptixDatabaseSchema database2, List<String> ignoreTableList) {
		List<IConstraint> database1ConstraintList = null;
		try {
			daoSession.begin(database1);
			database1ConstraintList = getDeployerMapper().getConstraints(database1.getTablespaceName());
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}
		List<IConstraint> database2ConstraintList = null;
		try {
			daoSession.begin(database2);
			database2ConstraintList = getDeployerMapper().getConstraints(database2.getTablespaceName());
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}

		return deployerDelegate.compileConstraintResult(database1, database1ConstraintList, database2, database2ConstraintList, ignoreTableList);
	}

	@Override
	public List<String> getDDL(ISynaptixDatabaseSchema database, String login, char[] password) {
		try {
			daoSession.begin(database, login, password);
			List<String> ddlList = new ArrayList<String>();
			getDeployerMapper().callDeactivateConstraint();
			for (SqlObject sqlObject : SqlObject.values()) {
				List<String> ddls = getDeployerMapper().getDDL(database.getTablespaceName(), sqlObject);
				if (ddls != null) {
					ddlList.addAll(ddls);
				}
			}

			List<String> ddls = getDeployerMapper().getConstraintDDL(database.getTablespaceName());
			if (ddls != null) {
				ddlList.addAll(ddls);
			}
			return ddlList;
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}
	}

	private DeployerMapper getDeployerMapper() {
		return daoSession.getMapper(DeployerMapper.class);
	}
}
