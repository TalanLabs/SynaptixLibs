import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.locks.Lock;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.factory.DefaultComputedFactory;
import com.synaptix.entity.IId;
import com.synaptix.entity.IdRaw;
import com.synaptix.entity.extension.BusinessComponentExtensionProcessor;
import com.synaptix.entity.extension.CacheComponentExtensionProcessor;
import com.synaptix.entity.extension.DatabaseComponentExtensionProcessor;
import com.synaptix.entity.extension.IBusinessComponentExtension;
import com.synaptix.entity.extension.ICacheComponentExtension;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.mybatis.SynaptixMyBatisServer;
import com.synaptix.mybatis.dao.IDaoUserContext;
import com.synaptix.mybatis.guice.AbstractSynaptixMyBatisModule;
import com.synaptix.mybatis.guice.SynaptixMyBatisModule;
import com.synaptix.mybatis.handler.RawToSerializableTypeHandler;

import oracle.sql.RAW;

public class MainBytes {

	public static class MyModule extends AbstractModule {

		@Override
		protected void configure() {
			install(new SynaptixMyBatisModule(MainBytes.class.getResourceAsStream("/mybatis-config.xml"), DefaultUserSession.class));

			bind(DefaultUserSession.class).in(Singleton.class);

			install(new AbstractSynaptixMyBatisModule() {
				@Override
				protected void configure() {
					addTypeHandler(MyRawToSerializableTypeHandler.class);
				}
			});
		}
	}

	@MappedTypes(Serializable.class)
	public static class MyRawToSerializableTypeHandler extends RawToSerializableTypeHandler {

		@Override
		public void setNonNullParameter(PreparedStatement ps, int i, Serializable parameter, JdbcType jdbcType) throws SQLException {
			byte[] bs;
			if (parameter instanceof IdRaw) {
				IdRaw raw = (IdRaw) parameter;
				bs = RAW.hexString2Bytes(raw.getHex());
			} else {
				bs = (byte[]) parameter;
			}
			ps.setBytes(i, bs);
		}
	}

	public static class DefaultUserSession implements IDaoUserContext {

		@Override
		public String getCurrentLogin() {
			return "test";
		}

		@Override
		public IId getCurrentIdUser() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Locale getCurrentLocale() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static void createUserDb(String schema) throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");

		Connection connection = null;
		Statement statement = null;
		try {
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.61.127.127:1521:DPSC", "PSC", "PSC968574");
			statement = connection.createStatement();
			statement.addBatch("CREATE USER " + schema + " IDENTIFIED BY " + schema);
			statement.addBatch("GRANT CREATE SESSION TO " + schema);
			statement.addBatch("GRANT DELETE ANY TABLE TO " + schema);
			statement.addBatch("GRANT UPDATE ANY TABLE TO " + schema);
			statement.addBatch("GRANT INSERT ANY TABLE TO " + schema);
			statement.addBatch("GRANT CREATE ANY TABLE TO " + schema);
			statement.addBatch("GRANT UNLIMITED TABLESPACE TO " + schema);

			statement.executeBatch();
		} finally {
			try {
				statement.close();
			} finally {
				connection.close();
			}
		}
	}

	private static void deleteUserDb(String schema) throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");

		Connection connection = null;
		Statement statement = null;
		try {
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.61.127.127:1521:DPSC", "PSC", "PSC968574");
			statement = connection.createStatement();
			statement.addBatch("DROP USER " + schema + " CASCADE");

			statement.executeBatch();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String schema = "SANDRA";

		createUserDb(schema);

		ComponentFactory.getInstance().addExtension(IDatabaseComponentExtension.class, new DatabaseComponentExtensionProcessor());
		ComponentFactory.getInstance().addExtension(IBusinessComponentExtension.class, new BusinessComponentExtensionProcessor());
		ComponentFactory.getInstance().addExtension(ICacheComponentExtension.class, new CacheComponentExtensionProcessor());
		ComponentFactory.getInstance().setComputedFactory(new DefaultComputedFactory());

		Injector injector = Guice.createInjector(new MyModule());

		Configuration configuration = injector.getInstance(Configuration.class);
		// configuration.getTypeHandlerRegistry().register(typeHandlerClass);

		PooledDataSource dataSource = new PooledDataSource("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@10.61.127.127:1521:DPSC", schema, schema);
		Environment environment = new Environment.Builder("oracle").transactionFactory(new JdbcTransactionFactory()).dataSource(dataSource).build();
		configuration.setEnvironment(environment);

		SynaptixMyBatisServer synaptixMyBatisServer = injector.getInstance(SynaptixMyBatisServer.class);
		synaptixMyBatisServer.start();

		SqlSessionManager sqlSessionManager = injector.getInstance(SqlSessionManager.class);
		sqlSessionManager.startManagedSession(false);

		ScriptRunner sr1 = new ScriptRunner(sqlSessionManager.getConnection());
		sr1.setLogWriter(null);
		sr1.runScript(new InputStreamReader(MainBytes.class.getResourceAsStream("/CreateDB.sql")));

		sqlSessionManager.commit();
		sqlSessionManager.close();

		IDatabaseTester databaseTester = new DataSourceDatabaseTester(dataSource, schema);
		IDatabaseConnection connection = databaseTester.getConnection();
		connection.getConfig().setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
		connection.getConfig().setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
		connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
		FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
		flatXmlDataSetBuilder.setColumnSensing(true);
		FlatXmlDataSet wkfDataSet = flatXmlDataSetBuilder.build(MainBytes.class.getResource("/test.xml"));
		DatabaseOperation.INSERT.execute(connection, wkfDataSet);
		connection.close();

		// sqlSessionManager.startManagedSession(false);
		//
		// ScriptRunner sr2 = new
		// ScriptRunner(sqlSessionManager.getConnection());
		// sr2.setSendFullScript(true);
		// sr2.runScript(new
		// InputStreamReader(MainBytes.class.getResourceAsStream("/DeleteDB2.sql")));
		//
		// sqlSessionManager.commit();
		// sqlSessionManager.close();

		Collection<Cache> caches = configuration.getCaches();
		for (Cache cache : caches) {
			Lock w = cache.getReadWriteLock().writeLock();
			w.lock();
			try {
				cache.clear();
			} finally {
				w.unlock();
			}
		}

		dataSource.forceCloseAll();

		deleteUserDb(schema);
	}
}
