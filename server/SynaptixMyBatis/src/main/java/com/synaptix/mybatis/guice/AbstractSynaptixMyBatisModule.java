package com.synaptix.mybatis.guice;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.type.TypeHandler;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.component.IComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.mybatis.cache.LinkCache;
import com.synaptix.mybatis.dao.listener.IEntitySaveOrUpdateListener;
import com.synaptix.mybatis.helper.HintProcess;

public abstract class AbstractSynaptixMyBatisModule extends AbstractModule {

	private static final AtomicInteger atomicInteger = new AtomicInteger();

	private MapBinder<Class<? extends IEntity>, IEntitySaveOrUpdateListener<?>> entitySaveOrUpdateListenerMapBinder;

	private Multibinder<TypeHandler<?>> typeHandlerMultibinder;

	private Multibinder<LinkCache> linkCacheMultibinder;

	private MapBinder<Integer, Class<?>> mapperMapBinder;

	private Multibinder<HintProcess> hintProcessMultibinder;

	private MapBinder<Class<? extends IEntity>, IEntitySaveOrUpdateListener<?>> getEntitySaveOrUpdateListenerMapBinder() {
		if (entitySaveOrUpdateListenerMapBinder == null) {
			entitySaveOrUpdateListenerMapBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<Class<? extends IEntity>>() {
			}, new TypeLiteral<IEntitySaveOrUpdateListener<?>>() {
			});
		}
		return entitySaveOrUpdateListenerMapBinder;
	}

	private Multibinder<TypeHandler<?>> getTypeHandlerMultibinder() {
		if (typeHandlerMultibinder == null) {
			typeHandlerMultibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<TypeHandler<?>>() {
			});
		}
		return typeHandlerMultibinder;
	}

	private Multibinder<LinkCache> getLinkCacheMultibinder() {
		if (linkCacheMultibinder == null) {
			linkCacheMultibinder = Multibinder.newSetBinder(binder(), LinkCache.class);
		}
		return linkCacheMultibinder;
	}

	private MapBinder<Integer, Class<?>> getMapperMapBinder() {
		if (mapperMapBinder == null) {
			mapperMapBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<Integer>() {
			}, new TypeLiteral<Class<?>>() {
			});
		}
		return mapperMapBinder;
	}

	private Multibinder<HintProcess> getHintProcessBinder() {
		if (hintProcessMultibinder == null) {
			hintProcessMultibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<HintProcess>() {
			});
		}
		return hintProcessMultibinder;
	}

	/**
	 * Adds the user defined mapper classes.
	 *
	 * @param mapperClasses
	 *            the user defined mapper classes.
	 */
	protected final void addMapperClass(Class<?> mapperClass) {
		Preconditions.checkArgument(mapperClass != null, "Parameter 'mapperClass' must not be null");
		bindMapper(mapperClass);
	}

	/**
	 *
	 * @param <T>
	 * @param mapperType
	 */
	private final <T> void bindMapper(Class<T> mapperType) {
		// bind(mapperType).toProvider(new SynaptixMapperProvider<T>(mapperType)).in(Scopes.SINGLETON);
		getMapperMapBinder().addBinding(atomicInteger.incrementAndGet()).toInstance(mapperType);
	}

	/**
	 * Add type handler
	 *
	 * @param javaTypeClass
	 * @param typeHandlerClass
	 */
	protected final void addTypeHandler(Class<? extends TypeHandler<?>> typeHandlerClass) {
		getTypeHandlerMultibinder().addBinding().to(typeHandlerClass).in(Singleton.class);
	}

	/**
	 * Add link cache, if link class is cleared then parentClass is clear
	 *
	 * @param parentClass
	 * @param linkClass
	 */
	protected final void addLinkCache(Class<? extends IComponent> parentClass, Class<? extends IComponent> linkClass) {
		getLinkCacheMultibinder().addBinding().toInstance(new LinkCache(parentClass, linkClass));
	}

	/**
	 * Add entity save listener
	 *
	 * @param entityClass
	 * @param entitySaveOrUpdateListenerClass
	 */
	protected final <E extends IEntity> void addEntitySaveOrUpdateListener(Class<E> entityClass, Class<? extends IEntitySaveOrUpdateListener<E>> entitySaveOrUpdateListenerClass) {
		getEntitySaveOrUpdateListenerMapBinder().addBinding(entityClass).to(entitySaveOrUpdateListenerClass).in(Singleton.class);
	}

	/**
	 * Add a hint process
	 *
	 * @param hintProcessClass
	 */
	protected final <E extends HintProcess> void addHintProcess(Class<E> hintProcessClass) {
		getHintProcessBinder().addBinding().to(hintProcessClass).in(Singleton.class);
	}
}
