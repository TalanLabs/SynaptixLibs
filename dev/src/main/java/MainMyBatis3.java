import java.util.concurrent.CountDownLatch;

import com.synaptix.mybatis.dao.IDaoSession;

import helper.MainHelper;
import mapper.CountryMapper;
import mapper.ZipMapper;
import model.ICountry;
import model.IZip;

public class MainMyBatis3 {

	public static void main(String[] args) {
		MainHelper.initMyBatis();

		final CountDownLatch countDownLatch = new CountDownLatch(1);

		new Thread(new Runnable() {
			@Override
			public void run() {
				IDaoSession daoSession = MainHelper.getMyBatisInjector().getInstance(IDaoSession.class);
				daoSession.begin();

				CountryMapper countryMapper = daoSession.getMapper(CountryMapper.class);
				ICountry country = countryMapper.find("FRA");
				System.out.println(country);

				ZipMapper zipMapper = daoSession.getMapper(ZipMapper.class);
				IZip zip = zipMapper.find("VALENCE", "26000", country.getId());
				System.out.println(zip);

				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				daoSession.end();
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				IDaoSession daoSession = MainHelper.getMyBatisInjector().getInstance(IDaoSession.class);
				daoSession.begin();

				CountryMapper countryMapper = daoSession.getMapper(CountryMapper.class);
				ICountry country = countryMapper.find("FRA");
				System.out.println(country);

				ZipMapper zipMapper = daoSession.getMapper(ZipMapper.class);
				IZip zip = zipMapper.find("VALENCE", "26000", country.getId());

				daoSession.saveOrUpdateEntity(country);

				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				daoSession.end();
			}
		}).start();

		countDownLatch.countDown();
	}

}
