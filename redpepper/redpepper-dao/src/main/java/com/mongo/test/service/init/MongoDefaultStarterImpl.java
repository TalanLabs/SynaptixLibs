package com.mongo.test.service.init;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.google.inject.Inject;
import com.mongo.test.config.Config;
import com.mongo.test.domain.Domain;
import com.mongo.test.service.dao.common.EntityCollectionManager;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class MongoDefaultStarterImpl implements DbStarter {
	Morphia morphia;
	MongoClient mClient;
	private final Config config;
	Map<String, Datastore> dsMap;
	private final EntityCollectionManager enitityManager;

	@Inject
	public MongoDefaultStarterImpl(Config config, EntityCollectionManager enitityManager) {
		this.config = config;
		this.enitityManager = enitityManager;
		init();
	}

	private void init() {
		try {
			dsMap = new HashMap<String, Datastore>();
			mClient = new MongoClient(config.getMongoServer(), config.getMongoPort());
			mClient.setWriteConcern(WriteConcern.JOURNALED);
			morphia = new Morphia();
			processMappings();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void processMappings() {
		Reflections reflection = new Reflections(Domain.class.getPackage().getName());
		Set<Class<?>> typesAnnotatedWith = reflection.getTypesAnnotatedWith(Entity.class);
		for (Class<?> c : typesAnnotatedWith) {
			System.out.println("Morphicating " + c + " !");
			Entity entity = c.getAnnotation(Entity.class);
			enitityManager.register(entity.value(), c);
			morphia.map(c);
			System.out.println(c + " is morphicated !");
		}
	}

	@Override
	public Datastore getDatabaseByName(String name) {
		if (dsMap.get(name) == null) {
			Datastore ds = morphia.createDatastore(mClient, name);
			ds.ensureCaps();
			ds.ensureIndexes();
			dsMap.put(name, ds);
		}
		return dsMap.get(name);
	}

}
