import helper.MainHelper;
import mapper.CountryMapper;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.SqlSessionManager;

import com.synaptix.mybatis.cache.SynaptixCacheListener;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.hack.SynaptixConfiguration;

public class MainMyBatis {

	public static void main(String[] args) throws Exception {
		MainHelper.initMyBatis();

		SynaptixCacheManager synaptixCacheManager = MainHelper.getMyBatisInjector().getInstance(SynaptixCacheManager.class);

		synaptixCacheManager.addCacheListener(new SynaptixCacheListener() {
			@Override
			public void cleared(String idCache, boolean propagation) {
				System.out.println(idCache + " cleared " + propagation);
			}
		});

		SynaptixConfiguration conf = MainHelper.getMyBatisInjector().getInstance(SynaptixConfiguration.class);

		MappedStatement oldMs = conf.getMappedStatement("mapper.CountryMapper.findAll");

		SqlSource ss = conf.getLanguageRegistry().getDefaultDriver()
				.createSqlSource(conf, "<script>select t.* from t_country t <where><if test='code != null'>t.country = '${code}'</if></where></script>", null);

		MappedStatement.Builder builder = new MappedStatement.Builder(oldMs.getConfiguration(), oldMs.getId(), ss, oldMs.getSqlCommandType());
		builder.cache(oldMs.getCache());
		builder.databaseId(oldMs.getDatabaseId());
		builder.fetchSize(oldMs.getFetchSize());
		builder.flushCacheRequired(oldMs.isFlushCacheRequired());
		if (oldMs.getKeyColumns() != null) {
			for (String keyColumn : oldMs.getKeyColumns()) {
				builder.keyColumn(keyColumn);
			}
		}
		builder.keyGenerator(oldMs.getKeyGenerator());
		if (oldMs.getKeyProperties() != null) {
			for (String keyPropertie : oldMs.getKeyProperties()) {
				builder.keyProperty(keyPropertie);
			}
		}
		builder.lang(oldMs.getLang());
		builder.parameterMap(oldMs.getParameterMap());
		builder.resource(oldMs.getResource());
		builder.resultMaps(oldMs.getResultMaps());
		builder.resultOrdered(oldMs.isResultOrdered());
		builder.resultSetType(oldMs.getResultSetType());
		builder.statementType(oldMs.getStatementType());
		builder.timeout(oldMs.getTimeout());
		builder.useCache(oldMs.isUseCache());

		MappedStatement newMs = builder.build();

		conf.replaceStatement("mapper.CountryMapper.find", newMs);

		SqlSessionManager daoSession = MainHelper.getMyBatisInjector().getInstance(SqlSessionManager.class);
		daoSession.startManagedSession();

		// ScriptRunner sr = new ScriptRunner(daoSession.getConnection());
		// sr.setLogWriter(null);
		// sr.runScript(new FileReader("D:\\Projects\\workspace_synaptix\\Test\\src\\main\\resources\\CreateDB2.sql"));

		CountryMapper countryMapper = daoSession.getMapper(CountryMapper.class);

		daoSession.close();
	}
}
