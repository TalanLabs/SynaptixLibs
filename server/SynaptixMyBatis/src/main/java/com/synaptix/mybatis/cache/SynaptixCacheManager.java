package com.synaptix.mybatis.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.Configuration;

import com.google.inject.Inject;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.extension.CacheClassExtensionDescriptor;
import com.synaptix.entity.extension.ICacheComponentExtension;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.dao.IDaoUserContext;

public class SynaptixCacheManager {

	private static final Log LOG = LogFactory.getLog(SynaptixCacheManager.class);

	private final Configuration configuration;

	private final IDaoUserContext daoUserContext;

	private Map<Class<? extends IComponent>, CacheResult> cacheMap;

	private List<SynaptixCacheListener> cacheListeners;

	@Inject(optional = true)
	private Set<LinkCache> linkCaches;

	@Inject
	public SynaptixCacheManager(Configuration configuration, IDaoUserContext daoUserContext) {
		super();

		this.configuration = configuration;
		this.daoUserContext = daoUserContext;
		this.cacheMap = new ConcurrentHashMap<Class<? extends IComponent>, CacheResult>();
		this.cacheListeners = new ArrayList<SynaptixCacheListener>();
	}

	public void addCacheListener(SynaptixCacheListener l) {
		synchronized (cacheListeners) {
			cacheListeners.add(l);
		}

		for (Entry<Class<? extends IComponent>, CacheResult> entry : cacheMap.entrySet()) {
			synchronized (entry.getKey()) {
				entry.getValue().listenerCache.addCacheListener(l);
			}
		}
	}

	public void removeCacheListener(SynaptixCacheListener l) {
		synchronized (cacheListeners) {
			cacheListeners.remove(l);
		}

		for (Entry<Class<? extends IComponent>, CacheResult> entry : cacheMap.entrySet()) {
			synchronized (entry.getKey()) {
				entry.getValue().listenerCache.removeCacheListener(l);
			}
		}
	}

	/**
	 * Get a cache
	 * 
	 * @param componentClass
	 * @return
	 */
	public <E extends IComponent> CacheResult getCache(Class<E> componentClass) {
		return _getCache(componentClass);
	}

	private <E extends IComponent> CacheResult _getCache(Class<E> componentClass) {
		CacheResult res;
		synchronized (componentClass) {
			res = (CacheResult) cacheMap.get(componentClass);
			if (res == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Create cache for " + componentClass);
				}

				ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
				CacheClassExtensionDescriptor cced = (CacheClassExtensionDescriptor) cd.getClassExtensionDescriptor(ICacheComponentExtension.class);

				Class<? extends IComponent>[] links = null;

				DelegateSynaptixCacheAdapter cache;
				MyListenerCache listenerCache;
				boolean enabled;
				boolean useNlsMessage = EntityHelper.useNlsMessage(componentClass);
				if (cced != null) {
					listenerCache = new MyListenerCache(new PerpetualSynaptixCache(componentClass.getName()), useNlsMessage);
					LruSynaptixCache lc = new LruSynaptixCache(listenerCache);
					lc.setSize(cced.getSize());
					ScheduledSynaptixCache sc = new ScheduledSynaptixCache(lc);
					sc.setClearInterval(cced.getClearInterval());
					ISynaptixCache c = sc;
					if (!cced.isReadOnly()) {
						c = new SerializedSynaptixCache(c);
					}
					cache = new DelegateSynaptixCacheAdapter(c);
					enabled = true;

					links = cced.getLinks();
				} else {
					listenerCache = new MyListenerCache(new EmptySynaptixCache(componentClass.getName()), useNlsMessage);
					cache = new DelegateSynaptixCacheAdapter(listenerCache);
					enabled = false;
				}

				res = new CacheResult(cache, listenerCache, enabled);
				cacheMap.put(componentClass, res);
				configuration.addCache(cache);

				Set<Class<? extends IComponent>> childs = EntityHelper.findAllChildren(componentClass);
				if (childs != null && !childs.isEmpty()) {
					for (Class<? extends IComponent> child : childs) {
						if (!child.equals(componentClass)) {
							CacheResult cacheResult = _getCache(child);

							cacheResult.listenerCache.childCacheResults.add(res.cache);
						}
					}
				}

				if (links != null) {
					for (Class<? extends IComponent> link : links) {
						CacheResult cacheResult = _getCache(link);
						cacheResult.listenerCache.childCacheResults.add(res.cache);

						Set<Class<? extends IComponent>> childs2 = EntityHelper.findAllChildren(link);
						for (Class<? extends IComponent> child : childs2) {
							if (!child.equals(componentClass)) {
								CacheResult cacheResult2 = _getCache(child);

								cacheResult2.listenerCache.childCacheResults.add(res.cache);
							}
						}
					}
				}

				if (linkCaches != null) {
					for (LinkCache linkCache : linkCaches) {
						if (componentClass.equals(linkCache.getParentClass())) {
							Class<? extends IComponent> link = linkCache.getLinkClass();

							CacheResult cacheResult = _getCache(link);
							cacheResult.listenerCache.childCacheResults.add(res.cache);

							Set<Class<? extends IComponent>> childs2 = EntityHelper.findAllChildren(link);
							for (Class<? extends IComponent> child : childs2) {
								if (!child.equals(componentClass)) {
									CacheResult cacheResult2 = _getCache(child);

									cacheResult2.listenerCache.childCacheResults.add(res.cache);
								}
							}
						}
					}
				}

				List<SynaptixCacheListener> ls = null;
				synchronized (cacheListeners) {
					ls = new ArrayList<SynaptixCacheListener>(cacheListeners);
				}
				for (SynaptixCacheListener l : ls) {
					res.listenerCache.addCacheListener(l);
				}
			}
		}
		return res;
	}

	protected class MyListenerCache implements ISynaptixCache {

		private Set<SynaptixCacheListener> cacheListeners;

		private final ISynaptixCache delegate;

		private final boolean useNlsMessage;

		private Set<ISynaptixCache> childCacheResults;

		public MyListenerCache(ISynaptixCache delegate, boolean useNlsMessage) {
			super();

			this.delegate = delegate;
			this.useNlsMessage = useNlsMessage;

			cacheListeners = new HashSet<SynaptixCacheListener>();

			this.childCacheResults = new HashSet<ISynaptixCache>();
		}

		@Override
		public String getId() {
			return delegate.getId();
		}

		@Override
		public Object getObject(Object key) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("getObject " + getId());
			}
			return delegate.getObject(createKey(key));
		}

		@Override
		public void putObject(Object key, Object value) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("putObject " + getId());
			}
			delegate.putObject(createKey(key), value);
		}

		@Override
		public Object removeObject(Object key) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("removeObject " + getId());
			}
			return delegate.removeObject(createKey(key));
		}

		private Object createKey(Object key) {
			if (useNlsMessage) {
				String language = null;
				if (daoUserContext.getCurrentLocale() != null) {
					try {
						language = daoUserContext.getCurrentLocale().getISO3Language();
					} catch (MissingResourceException e) {
					}
					return new MyKey(key, language, null);
				}
			}
			return key;
		}

		@Override
		public void clear(boolean propagation) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("clear " + getId());
			}
			delegate.clear(propagation);

			if (propagation) {
				if (childCacheResults != null && !childCacheResults.isEmpty()) {
					for (ISynaptixCache childCache : childCacheResults) {
						childCache.clear(false);
					}
				}
			}
			fireCleared(propagation);
		}

		@Override
		public ReadWriteLock getReadWriteLock() {
			return delegate.getReadWriteLock();
		}

		@Override
		public int getSize() {
			return delegate.getSize();
		}

		public void addCacheListener(SynaptixCacheListener l) {
			synchronized (cacheListeners) {
				cacheListeners.add(l);
			}
		}

		public void removeCacheListener(SynaptixCacheListener l) {
			synchronized (cacheListeners) {
				cacheListeners.remove(l);
			}
		}

		protected void fireCleared(boolean propagation) {
			Set<SynaptixCacheListener> ls = null;
			synchronized (cacheListeners) {
				ls = new HashSet<SynaptixCacheListener>(cacheListeners);
			}
			for (SynaptixCacheListener l : ls) {
				l.cleared(getId(), propagation);
			}
		}
	}

	public static class CacheResult {

		protected final DelegateSynaptixCacheAdapter cache;

		protected final boolean enabled;

		protected final MyListenerCache listenerCache;

		protected CacheResult(DelegateSynaptixCacheAdapter cache, MyListenerCache listenerCache, boolean enabled) {
			super();
			this.cache = cache;
			this.listenerCache = listenerCache;
			this.enabled = enabled;
		}

		public Cache getCache() {
			return cache;
		}

		public boolean isEnabled() {
			return enabled;
		}
	}

	private class MyKey {

		final Object key;

		final String language;

		final String country;

		MyKey(Object key, String language, String country) {
			super();
			this.key = key;
			this.language = language;
			this.country = country;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(key).append(language).append(country).toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MyKey) {
				MyKey k = (MyKey) obj;
				return new EqualsBuilder().append(key, k.key).append(language, k.language).append(country, k.country).isEquals();
			}
			return super.equals(obj);
		}
	}
}
