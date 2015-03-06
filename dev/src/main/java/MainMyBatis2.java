import helper.MainHelper;
import model.ICountry;

import com.synaptix.entity.IdRaw;
import com.synaptix.mybatis.cache.SynaptixCacheListener;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.dao.IDaoSessionExt;
import com.synaptix.service.IEntityService;

public class MainMyBatis2 {

	public static void main(String[] args) throws Exception {
		MainHelper.initMyBatis();

		SynaptixCacheManager synaptixCacheManager = MainHelper.getMyBatisInjector().getInstance(SynaptixCacheManager.class);

		synaptixCacheManager.addCacheListener(new SynaptixCacheListener() {
			@Override
			public void cleared(String idCache, boolean propagation) {
				System.out.println(idCache + " cleared " + propagation);
			}
		});

		IEntityService entityService = MainHelper.getMyBatisInjector().getInstance(IEntityService.class);

		IDaoSession daoSession = MainHelper.getMyBatisInjector().getInstance(IDaoSession.class);
		IDaoSessionExt daoSessionExt = (IDaoSessionExt) daoSession;
		daoSessionExt.setCheckSuperTransactionInSession(true);

		daoSession.begin();

		// ScriptRunner sr = new ScriptRunner(daoSession.getConnection());
		// sr.setLogWriter(null);
		// sr.runScript(new FileReader("D:\\Projects\\workspace_synaptix\\Test\\src\\main\\resources\\CreateDB2.sql"));

		daoSession.begin();

		ICountry country = entityService.findEntityById(ICountry.class, new IdRaw("35BF6DCFDDC34A0DAEB59F1C4B9F909D"));
		System.out.println(country.getIsoCountryCode());
		country.setIsoCountryCode("TE");

		entityService.editEntity(country);

		// daoSession.commit();
		daoSession.end();

		country = entityService.findEntityById(ICountry.class, new IdRaw("35BF6DCFDDC34A0DAEB59F1C4B9F909D"));
		System.out.println(country.getIsoCountryCode());

		daoSession.end();
	}
}
