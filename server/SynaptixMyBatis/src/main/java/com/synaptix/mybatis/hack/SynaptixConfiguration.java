package com.synaptix.mybatis.hack;

import java.sql.SQLException;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.transaction.Transaction;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.cache.SynaptixCacheManager.CacheResult;
import com.synaptix.mybatis.helper.ComponentResultMapHelper;
import com.synaptix.mybatis.helper.FindMappedStatement;

public class SynaptixConfiguration extends Configuration {

	private static final Log LOG = LogFactory.getLog(SynaptixConfiguration.class);

	private static final Integer lockResultMap = new Integer(0);

	private static final Integer lockMappedStatement = new Integer(1);

	private static final Pattern findEntityByIdPattern = Pattern.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*/findEntityById");

	private static final Pattern findChildrenByIdParentPattern = Pattern.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*/findChildrenByIdParent\\?[a-zA-Z_$][a-zA-Z\\d_$]*");

	private FindMappedStatement findMappedStatement;

	private SynaptixCacheManager synaptixCacheManager;

	private ComponentResultMapHelper componentResultMapHelper;

	private SynaptixUserSession synaptixUserSession;

	public SynaptixConfiguration() {
		super();

		languageRegistry.setDefaultDriverClass(MyXMLLanguageDriver.class);
	}

	public void setFindMappedStatement(FindMappedStatement findMappedStatement) {
		this.findMappedStatement = findMappedStatement;
	}

	public void setSynaptixCacheManager(SynaptixCacheManager synaptixCacheManager) {
		this.synaptixCacheManager = synaptixCacheManager;
	}

	public void setComponentResultMapHelper(ComponentResultMapHelper componentResultMapHelper) {
		this.componentResultMapHelper = componentResultMapHelper;
	}

	public void setSynaptixUserSession(SynaptixUserSession synaptixUserSession) {
		this.synaptixUserSession = synaptixUserSession;
	}

	/**
	 * Get has cache and verify and create component cache
	 */
	@Override
	public boolean hasCache(String id) {
		boolean res = super.hasCache(id);
		if (!res) {
			res = verifyAndCreateComponentCache(id);
		}
		return res;
	}

	/**
	 * Get has cache without verify and create
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasComponentCache(String id) {
		return super.hasCache(id);
	}

	@Override
	public Cache getCache(String id) {
		if (hasCache(id)) {
			return super.getCache(id);
		}
		return super.getCache(id);
	}

	private boolean verifyAndCreateComponentCache(String id) {
		boolean res = false;
		Class<? extends IComponent> componentClass = getComponentClass(id);
		if (componentClass != null) {
			CacheResult cacheResult = synaptixCacheManager.getCache(componentClass);
			res = cacheResult != null && cacheResult.getCache() != null;
		}
		return res;
	}

	@Override
	public LanguageDriverRegistry getLanguageRegistry() {
		return super.getLanguageRegistry();
	}

	/**
	 * Get has result map with verify and create component result map
	 */
	@Override
	public boolean hasResultMap(String id) {
		boolean res = super.hasResultMap(id);
		if (!res) {
			res = verifyAndCreateComponentResultMap(id);
		}
		return res;
	}

	/**
	 * Get has result map without verify and create component result map
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasComponentResultMap(String id) {
		return super.hasResultMap(id);
	}

	public ResultMap getComponentResultMap(String id) {
		return super.getResultMap(id);
	}

	@Override
	public ResultMap getResultMap(String id) {
		if (hasResultMap(id)) {
			return super.getResultMap(id);
		}
		return super.getResultMap(id);
	}

	private boolean verifyAndCreateComponentResultMap(String id) {
		boolean res = false;
		Class<? extends IComponent> componentClass = getComponentClass(id);
		if (componentClass != null) {
			ResultMap resultMap = componentResultMapHelper.createResultMap(componentClass);
			res = resultMap != null;
		}
		return res;
	}

	/**
	 * Get has statement without verify and create
	 * 
	 * @param statementName
	 * @return
	 */
	public boolean hasComponentStatement(String statementName) {
		return super.hasStatement(statementName, false);
	}

	@Override
	public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
		boolean res = super.hasStatement(statementName, validateIncompleteStatements);
		if (!res) {
			res = verifyAndCreateComponentMappedStatement(statementName);
		}
		return res;
	}

	public MappedStatement getComponentMappedStatement(String id) {
		return super.getMappedStatement(id, false);
	}

	@Override
	public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
		if (hasStatement(id, validateIncompleteStatements)) {
			return super.getMappedStatement(id, validateIncompleteStatements);
		}
		return super.getMappedStatement(id, validateIncompleteStatements);
	}

	private boolean verifyAndCreateComponentMappedStatement(String id) {
		boolean res = false;
		if (id != null) {
			if (findEntityByIdPattern.matcher(id).matches()) {
				String componentName = id.substring(0, id.indexOf("/"));
				Class<? extends IComponent> componentClass = getComponentClass(componentName);
				if (componentClass != null) {
					MappedStatement mappedStatement = findMappedStatement.createFindEntityById(componentClass);
					res = mappedStatement != null;
				}
			} else if (findChildrenByIdParentPattern.matcher(id).matches()) {
				String componentName = id.substring(0, id.indexOf("/"));
				Class<? extends IComponent> componentClass = getComponentClass(componentName);
				String idParentPropertyName = id.substring(id.lastIndexOf("?") + 1);
				if (componentClass != null && idParentPropertyName != null) {
					MappedStatement mappedStatement = findMappedStatement.createFindChildrenByIdParent(componentClass, idParentPropertyName);
					res = mappedStatement != null;
				}
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends IComponent> getComponentClass(String componentName) {
		Class<? extends IComponent> res = null;
		if (componentName != null) {
			try {
				Class<?> clazz = this.getClass().getClassLoader().loadClass(componentName);
				if (ComponentFactory.isClass(clazz)) {
					res = (Class<? extends IComponent>) clazz;
				}
			} catch (ClassNotFoundException e) {
			}
		}
		return res;
	}

	@Override
	public Executor newExecutor(Transaction transaction, ExecutorType executorType, boolean autoCommit) {
		if (synaptixUserSession != null) {
			try {
				synaptixUserSession.insertTempUserSession(transaction.getConnection());
			} catch (SQLException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return super.newExecutor(transaction, executorType, autoCommit);
	}

	public void replaceStatement(String statementName, MappedStatement mappedStatement) {
		if (super.hasStatement(statementName)) {
			mappedStatements.remove(statementName);
			mappedStatements.put(statementName, mappedStatement);
		}
	}

	@Override
	public void addResultMap(ResultMap rm) {
		synchronized (lockResultMap) {
			if (!hasComponentResultMap(rm.getId())) {
				super.addResultMap(rm);
			}
		}
	}

	@Override
	public void addMappedStatement(MappedStatement ms) {
		synchronized (lockMappedStatement) {
			if (!hasComponentStatement(ms.getId())) {
				super.addMappedStatement(ms);
			}
		}
	}
}
