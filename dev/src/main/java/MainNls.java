import helper.MainHelper;
import helper.MainHelper.DatabaseUserSession;

import java.util.Locale;

import model.ICountry;

import com.synaptix.entity.IdRaw;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;

public class MainNls {

	public static void main(String[] args) throws Exception {
		MainHelper.initMyBatis();

		DatabaseUserSession databaseUserSession = MainHelper.getMyBatisInjector().getInstance(DatabaseUserSession.class);
		databaseUserSession.setLocale(Locale.FRENCH);

		IDaoSession daoSession = MainHelper.getMyBatisInjector().getInstance(IDaoSession.class);
		daoSession.begin();

		EntityServiceDelegate entityServiceDelegate = MainHelper.getMyBatisInjector().getInstance(EntityServiceDelegate.class);

		ICountry country1 = entityServiceDelegate.findEntityById(ICountry.class, new IdRaw("35BF6DCFDDC34A0DAEB59F1C4B9F909D"));
		System.out.println(country1);

		entityServiceDelegate.editEntity(country1, true);

		daoSession.commit();

		country1.setMeaning("Coucou");

		entityServiceDelegate.editEntity(country1, true);

		ICountry country2 = entityServiceDelegate.findEntityById(ICountry.class, new IdRaw("35BF6DCFDDC34A0DAEB59F1C4B9F909D"));
		System.out.println(country2);

		daoSession.end();
	}
}
