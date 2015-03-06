package com.synaptix.common.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.common.model.Binome;

public final class CollectionHelper {

	private static final Log LOG = LogFactory.getLog(CollectionHelper.class);

	public static final Random RAND = new Random(new Date().getTime());

	private CollectionHelper() {
	}

	@SuppressWarnings("unchecked")
	public static final <T> List<T> getNotNullReturn(final List<T> list) {
		return list == null ? Collections.EMPTY_LIST : list;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] getArray(final Collection<T> list, final Class<T> tClass) {
		final T[] items = (T[]) Array.newInstance(tClass, CollectionHelper.size(list));
		if (CollectionHelper.isNotEmpty(list)) {
			int index = 0;
			for (final T item : list) {
				items[index++] = item;
			}
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] getArrayGeneric(final List<T> list, final Class<?> tClass) {
		final T[] items = (T[]) Array.newInstance(tClass, CollectionHelper.size(list));
		if (CollectionHelper.isNotEmpty(list)) {
			int index = 0;
			for (final T item : list) {
				items[index++] = item;
			}
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] toArray(final Collection<T> col, final Class<T> tClass) {
		return col != null ? col.toArray((T[]) Array.newInstance(tClass, size(col))) : null;
	}

	public static final boolean isNull(final Object col) {
		return col == null;
	}

	public static final boolean isNotNull(final Object col) {
		return col != null;
	}

	public static final <T> boolean isEmpty(final Collection<T> col) {
		return col == null || col.isEmpty();
	}

	public static final <T, U> boolean isEmpty(final Map<T, U> col) {
		return col == null || col.isEmpty();
	}

	public static final <T> boolean isNotEmpty(final Iterable<T> col) {
		if (col != null) {
			final Iterator<T> it = col.iterator();
			return it != null && it.hasNext();
		}
		return false;
	}

	public static final <T> boolean isNotEmpty(final Collection<T> col) {
		return col != null && !col.isEmpty();
	}

	public static final <T, U> boolean isNotEmpty(final Map<T, U> col) {
		return col != null && !col.isEmpty();
	}

	/**
	 * La collection contient un seul item.
	 * 
	 * @param <T>
	 * @param col
	 * @return vrai si la collection n'est pas nulle et contient un seul item
	 */
	public static final <T> boolean isSingleton(final Collection<T> col) {
		return col != null && col.size() == 1;
	}

	/**
	 * La collection contient plus d'un item.
	 * 
	 * @param <T>
	 * @param col
	 * @return vrai si la collection n'est pas nulle et contient plus d'un item.
	 */
	public static final <T> boolean isNotSingleton(final Collection<T> col) {
		return col != null && col.size() > 1;
	}

	/**
	 * Retourne la taille de la collection.
	 * 
	 * @param <T>
	 * @param col
	 * @return la taille de la collection. Zéro si elle est null.
	 */
	public static final <T> int size(final Collection<T> col) {
		return col == null ? 0 : col.size();
	}

	/**
	 * Retourne la taille de la map.
	 * 
	 * @param <T>
	 * @param col
	 * @return la taille de la map. Zéro si elle est null.
	 */
	public static final <T, U> int size(final Map<T, U> col) {
		return col == null ? 0 : col.size();
	}

	/**
	 * inverse la map, les anciennes valeurs deviennent les nouvelles clefs et
	 * les anciennes clefs sont placés dans des listes associé a leur valeur
	 * d'origine
	 * 
	 * @param map
	 *            <T, U>
	 * @return map<U, List<T>>
	 */

	public static final <T, U> Map<U, List<T>> mapInvert(final Map<T, U> map) {
		final Map<U, List<T>> returnMap = allocateMap(map);
		final Set<Entry<T, U>> entrySet = map.entrySet();
		if (isNotEmpty(entrySet)) {
			final IValueGetter<List<T>> valueGetter = new IValueGetter<List<T>>() {
				@Override
				public List<T> value() {
					return new ArrayList<T>();
				}
			};
			U curValue;
			for (final Map.Entry<T, U> entry : entrySet) {
				curValue = entry.getValue();
				assertAllocationMap(returnMap, curValue, valueGetter);
				returnMap.get(curValue).add(entry.getKey());
			}
		}
		return returnMap;
	}

	public static final int sizeMin(final Collection<?>... cols) {
		int sizeMin = 0;
		if (cols != null && cols.length > 0) {
			sizeMin = Integer.MAX_VALUE;
			for (Collection<?> col : cols) {
				sizeMin = Math.min(sizeMin, size(col));
			}
		}
		return sizeMin;
	}

	public static final int sizeMax(final Collection<?>... cols) {
		int sizeMax = 0;
		if (cols != null && cols.length > 0) {
			sizeMax = -1;
			for (Collection<?> col : cols) {
				sizeMax = Math.max(sizeMax, size(col));
			}
		}
		return sizeMax;
	}

	public static final int sizeMin(final List<Collection<?>> colList) {
		int sizeMin = 0;
		if (isNotEmpty(colList)) {
			sizeMin = Integer.MAX_VALUE;
			for (Collection<?> col : colList) {
				sizeMin = Math.min(sizeMin, size(col));
			}
		}
		return sizeMin;
	}

	public static final int sizeMax(final List<Collection<?>> colList) {
		int sizeMax = 0;
		if (isNotEmpty(colList)) {
			sizeMax = -1;
			for (Collection<?> col : colList) {
				sizeMax = Math.max(sizeMax, size(col));
			}
		}
		return sizeMax;
	}

	public static final <T, U> List<T> allocateList(final Collection<U> list) {
		return new ArrayList<T>(size(list));
	}

	public static final <T, U> List<T> allocateList(final List<U> list) {
		return new ArrayList<T>(size(list));
	}

	public static final <T, U> List<T> allocateList() {
		return new ArrayList<T>();
	}

	public static final <T, U, V> List<T> allocateList(final Map<U, V> map) {
		return new ArrayList<T>(size(map));
	}

	public static final <T> List<T> allocateList(final Class<T> clazz) {
		return new ArrayList<T>();
	}

	public static final <T, U> List<T> allocateList(final int size) {
		return new ArrayList<T>(size);
	}

	public static final <T, U> Set<T> allocateSet(final Collection<U> list) {
		return new HashSet<T>(size(list));
	}

	public static final <T, U> Set<T> allocateOrderSet(final Collection<U> list) {
		return new LinkedHashSet<T>(size(list));
	}

	public static final <T, U> Set<T> allocateSet(final int size) {
		return new HashSet<T>(size);
	}

	public static final <T> Set<T> allocateSet() {
		return new HashSet<T>();
	}

	public static final <T> List<T> getList(final Collection<T> col) {
		return col != null ? new ArrayList<T>(col) : new ArrayList<T>(0);
	}

	public static final <T> Set<T> getSet(final Collection<T> col) {
		return col != null ? new HashSet<T>(col) : new HashSet<T>(0);
	}

	public static final <T> void clear(final Collection<T> col) {
		if (col != null) {
			col.clear();
		}
	}

	public static final <T, U> void clear(final Map<T, U> map) {
		if (map != null) {
			map.clear();
		}
	}

	public static final <T> List<T> clearList(List<T> list) {
		if (list != null) {
			list.clear();
			return list;
		}
		return new ArrayList<T>();
	}

	public static final <T> List<T> getUniqueElementList(final List<T> list) {
		return list == null ? null : new ArrayList<T>(new HashSet<T>(list));
	}

	public static final <T> Set<T> getUniqueElementSet(final List<T> list) {
		return list == null ? null : new HashSet<T>(list);
	}

	public static final <T extends Comparable<? super T>> void sort(final List<T> list) {
		if (isNotSingleton(list)) {
			Collections.sort(list);
		}
	}

	public static final <T extends Comparable<T>> List<T> sortList(final List<T> list) {
		if (isNotSingleton(list)) {
			Collections.sort(list);
		}
		return list;
	}

	public static final <T extends Comparable<T>> T min(final List<T> list) {
		if (isNotEmpty(list)) {
			return Collections.min(list);
		}
		return null;
	}

	public static final <T extends Comparable<T>> T max(final List<T> list) {
		if (isNotEmpty(list)) {
			return Collections.max(list);
		}
		return null;
	}

	public static final <T> void sort(final List<? extends T> list, final Comparator<T> comparator) {
		if (isNotSingleton(list)) {
			Collections.sort(list, comparator);
		}
	}

	public static final <T> List<T> sortList(final List<T> list, final Comparator<T> comparator) {
		if (isNotSingleton(list)) {
			Collections.sort(list, comparator);
		}
		return list;
	}

	public static final <T> T min(final List<T> list, final Comparator<T> comparator) {
		if (isNotEmpty(list)) {
			return Collections.min(list, comparator);
		}
		return null;
	}

	public static final <T> T max(final List<T> list, final Comparator<T> comparator) {
		if (isNotEmpty(list)) {
			return Collections.max(list, comparator);
		}
		return null;
	}

	/**
	 * Convert a object array to List<T>
	 * 
	 * @param clazz
	 * @param ts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> List<T> asListOf(Class<T> clazz, Object... ts) {
		if (ts != null) {
			final int length = ts.length;
			final List<T> ret = new ArrayList<T>(length);
			if (length > 0) {
				for (Object t : ts) {
					ret.add((T) t);
				}
			}
			return ret;
		}
		return null;
	}

	public static final <T> List<T> asList(final T... ts) {
		if (ts != null) {
			final int lenght = ts.length;
			final List<T> ret = new ArrayList<T>(lenght);
			if (lenght > 0) {
				for (final T t : ts) {
					ret.add(t);
				}
			}
			return ret;
		}
		return null;
	}

	public static final <T> T getFirstElement(final List<T> list) {
		if (isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	public static final <T> T getFirstElementHudson(final List<T> list, /*
																		 * @
																		 * SuppressWarnings
																		 * (
																		 * "unused"
																		 * )
																		 */final Class<T> clazz) {
		if (isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	public static final <T> T getFirstElement(final Set<T> set) {
		if (isNotEmpty(set)) {
			return set.iterator().next();
		}
		return null;
	}

	public static final <T> T getLastElement(final List<T> list) {
		if (isNotEmpty(list)) {
			return list.get(size(list) - 1);
		}
		return null;
	}

	public static final <T> List<T> getBeforeElementList(final List<T> list, final T element, final Comparator<T> comparator) {
		if (element != null && isNotEmpty(list)) {
			final List<T> ret = new ArrayList<T>();
			for (final T t : list) {
				if (comparator.compare(t, element) <= 0) {
					ret.add(t);
				}
			}
		}
		return null;
	}

	public static final <T> List<T> getAfterElementList(final List<T> list, final T element, final Comparator<T> comparator) {
		if (element != null && isNotEmpty(list)) {
			final List<T> ret = new ArrayList<T>();
			for (T t : list) {
				if (comparator.compare(t, element) >= 0) {
					ret.add(t);
				}
			}
		}
		return null;
	}

	public static final <T extends Comparable<T>> List<T> getBeforeElementList(final List<T> list, final T element) {
		if (element != null && isNotEmpty(list)) {
			final List<T> ret = new ArrayList<T>();
			for (T t : list) {
				if (t.compareTo(element) <= 0) {
					ret.add(t);
				}
			}
		}
		return null;
	}

	public static final <T extends Comparable<T>> List<T> getAfterElementList(final List<T> list, final T element) {
		if (element != null && isNotEmpty(list)) {
			final List<T> ret = new ArrayList<T>();
			for (T t : list) {
				if (t.compareTo(element) >= 0) {
					ret.add(t);
				}
			}
		}
		return null;
	}

	public static final <T extends Comparable<T>> T getLastBeforeElement(final List<T> list, final T element) {
		if (element != null && isNotEmpty(list)) {
			T max = null;
			for (T t : list) {
				if (t.compareTo(element) <= 0 && (max == null || t.compareTo(max) > 0)) {
					max = t;
				}
			}
			return max;
		}
		return null;
	}

	public static final <T extends Comparable<T>> T getFirstAfterElement(final List<T> list, final T element) {
		if (element != null && isNotEmpty(list)) {
			T min = null;
			for (T t : list) {
				if (t.compareTo(element) >= 0 && (min == null || t.compareTo(min) < 0)) {
					min = t;
				}
			}
			return min;
		}
		return null;
	}

	public static final <T> T getLastBeforeElement(final List<T> list, final T element, final Comparator<T> comparator) {
		if (element != null && isNotEmpty(list)) {
			T max = null;
			for (T t : list) {
				if (comparator.compare(t, element) <= 0 && (max == null || comparator.compare(t, max) > 0)) {
					max = t;
				}
			}
			return max;
		}
		return null;
	}

	public static final <T> T getFirstAfterElement(final List<T> list, final T element, final Comparator<T> comparator) {
		if (element != null && isNotEmpty(list)) {
			T min = null;
			for (T t : list) {
				if (comparator.compare(t, element) >= 0 && (min == null || comparator.compare(t, min) < 0)) {
					min = t;
				}
			}
			return min;
		}
		return null;
	}

	public interface ITimeComparator<T> {

		Date getDateMin(T element);

		Date getDateMax(T element);

		int compare(T element, Date date);
	}

	public abstract static class TimeComparator<T> implements ITimeComparator<T> {

		@Override
		public int compare(final T element, final Date date) {
			final int compareDeb = getDateMin(element).compareTo(date);
			if (compareDeb == 0) {
				return getDateMax(element).compareTo(date);
			}
			return compareDeb;
		}
	}

	public static final <T> Entry<Date, Date> getBorneFromElementList(final List<T> elementList, final TimeComparator<T> timeComparator) {
		if (isNotEmpty(elementList)) {
			Date dateMin = new Date(Long.MAX_VALUE);
			Date dateMax = new Date(0l);
			Date curDateDeb;
			Date curDateFin;
			for (T element : elementList) {
				curDateDeb = timeComparator.getDateMin(element);
				if (dateMin.compareTo(curDateDeb) > 0) {
					dateMin = curDateDeb;
				}
				curDateFin = timeComparator.getDateMax(element);
				if (dateMax.compareTo(curDateFin) < 0) {
					dateMax = curDateFin;
				}
			}
			return new SimpleEntry<Date, Date>(dateMin, dateMax);
		}
		return null;
	}

	public static final <T> T getLastBeforeElement(final List<T> list, final Date date, final TimeComparator<T> comparator) {
		if (isNotEmpty(list)) {
			Date dateMax = null;
			T elementMax = null;
			for (T t : list) {
				if (comparator.compare(t, date) <= 0 && (dateMax == null || comparator.compare(t, dateMax) > 0)) {
					dateMax = comparator.getDateMin(t);
					elementMax = t;
				}
			}
			return elementMax;
		}
		return null;
	}

	public static final <T> T getFirstAfterElement(final List<T> list, final Date date, final TimeComparator<T> comparator) {
		if (isNotEmpty(list)) {
			Date dateMin = null;
			T elementMin = null;
			for (T t : list) {
				if (comparator.compare(t, date) >= 0 && (dateMin == null || comparator.compare(t, dateMin) < 0)) {
					dateMin = comparator.getDateMax(t);
					elementMin = t;
				}
			}
			return elementMin;
		}
		return null;
	}

	public abstract static class Space<T> {

		public abstract T getSpace();

		public abstract boolean equivalent(Space<T> space);

	}

	public interface ISpaceComparator<T, U> {

		Space<U> getSpaceMin(T element);

		Space<U> getSpaceMax(T element);

		boolean compareDebut(T element, Space<U> space);

		boolean compareFin(T element, Space<U> space);
	}

	public abstract static class SpaceComparator<T, U> implements ISpaceComparator<T, U> {

		@Override
		public boolean compareDebut(final T element, final Space<U> space) {
			return getSpaceMin(element).equivalent(space);
		}

		@Override
		public boolean compareFin(final T element, final Space<U> space) {
			return getSpaceMax(element).equivalent(space);
		}
	}

	public abstract static class SpaceTimeComparator<T, U> implements ISpaceComparator<T, U>, ITimeComparator<T> {

	}

	public static final <T, U> boolean getCoherenceAtDate(final List<T> list, final Date date, final SpaceTimeComparator<T, U> comparator) {
		boolean coherent = true;
		if (isNotEmpty(list)) {
			Date dateMin = null;
			T elementMin = null;
			Date dateMax = null;
			T elementMax = null;
			for (T t : list) {
				if (comparator.compare(t, date) >= 0 && (dateMin == null || comparator.compare(t, dateMin) < 0)) {
					dateMin = comparator.getDateMax(t);
					elementMin = t;
				}
				if (comparator.compare(t, date) <= 0 && (dateMax == null || comparator.compare(t, dateMax) > 0)) {
					dateMax = comparator.getDateMin(t);
					elementMax = t;
				}
			}
			if (elementMin != null && elementMax != null) {
				coherent = comparator.compareFin(elementMax, comparator.getSpaceMin(elementMin)) && comparator.compareDebut(elementMin, comparator.getSpaceMax(elementMax));
			}
		}
		return coherent;
	}

	public static final <T, U> boolean getNoElementBetweenDate(final List<T> list, final Date dateFirst, final Date dateLast, final SpaceTimeComparator<T, U> comparator) {
		boolean libre = true;
		if (isNotEmpty(list)) {
			Date dateMin = null;
			T elementMin = null;
			Date dateMax = null;
			T elementMax = null;
			for (T t : list) {
				if (comparator.compare(t, dateFirst) >= 0 && (dateMin == null || comparator.compare(t, dateMin) < 0)) {
					dateMin = comparator.getDateMax(t);
					elementMin = t;
				}
				if (comparator.compare(t, dateLast) <= 0 && (dateMax == null || comparator.compare(t, dateMax) > 0)) {
					dateMax = comparator.getDateMin(t);
					elementMax = t;
				}
			}
			if (elementMin != null) {
				if (elementMax != null) {
					libre = comparator.compare(elementMin, dateLast) >= 0 && comparator.compare(elementMax, dateFirst) <= 0;
				} else {
					libre = comparator.compare(elementMin, dateLast) >= 0;
				}
			} else {
				if (elementMax != null) {
					libre = comparator.compare(elementMax, dateFirst) <= 0;
				}
			}
		}
		return libre;
	}

	public static int getTailleHashMap(final int nbElement) {
		return getTailleHashMap(nbElement, 0.75);
	}

	public static int getTailleHashMap(final int nbElement, final double loadFactor) {
		return (new Long(Math.round(((new Double(nbElement)) * (1.0 / loadFactor))) + 1)).intValue();
	}

	public static final <T, U, V> Map<T, U> allocateMap() {
		return allocateMap(10, 0.75);
	}

	public static final <T, U, V> Map<T, U> allocateMap(final Collection<V> list) {
		return allocateMap(size(list), 0.75);
	}

	public static final <T, U> Map<T, U> allocateMap(final int nbElement) {
		return allocateMap(nbElement, 0.75);
	}

	public static final <T, U, V, W> Map<T, U> allocateMap(final Map<V, W> map) {
		return allocateMap(size(map), 0.75);
	}

	public static final <T, U> Map<T, U> allocateMap(final int nbElement, final double loadFactor) {
		return allocateMap(nbElement, loadFactor, false);
	}

	public static final <T, U> Map<T, U> allocateSynchronizedMap() {
		return allocateMap(10, 0.75, true);
	}

	public static final <T, U> Map<T, U> allocateSynchronizedMap(final Class<T> tClass, final Class<U> uClass) {
		return allocateMap(10, 0.75, true, tClass, uClass);
	}

	public static final <T, U, V> Map<T, U> allocateSynchronizedMap(final Collection<V> list) {
		return allocateMap(size(list), 0.75, true);
	}

	public static final <T, U> Map<T, U> allocateSynchronizedMap(final int nbElement) {
		return allocateMap(nbElement, 0.75, true);
	}

	public static final <T, U> Map<T, U> allocateMap(final int nbElement, final double loadFactor, final boolean synchrone) {
		return synchrone ? Collections.synchronizedMap(new HashMap<T, U>(getTailleHashMap(nbElement, loadFactor))) : new HashMap<T, U>(getTailleHashMap(nbElement, loadFactor));
	}

	public static final <T, U> Map<T, U> allocateMap(final int nbElement, final double loadFactor, final boolean synchrone, /*
																															 * @
																															 * SuppressWarnings
																															 * (
																															 * "unused"
																															 * )
																															 */final Class<T> tClass,
	/* @SuppressWarnings("unused") */final Class<U> uClass) {
		return synchrone ? Collections.synchronizedMap(new HashMap<T, U>(getTailleHashMap(nbElement, loadFactor))) : new HashMap<T, U>(getTailleHashMap(nbElement, loadFactor));
	}

	public static Map<String, Object> getParamMap(final String[] noms, final Object... values) {
		final int size = ArrayHelper.sizeMin(noms, values);
		final Map<String, Object> map = allocateMap(size);
		for (int index = 0; index < size; ++index) {
			map.put(noms[index], values[index]);
		}
		return map;
	}

	public static final <T> T get(final List<T> list, final int index) {
		return list == null ? null : list.get(index);
	}

	public static final <T> T safeGet(final List<T> list, final int index) {
		return (isNotEmpty(list) && index >= 0 && index < size(list)) ? list.get(index) : null;
	}

	public static final <T> int safeIndex(final List<T> list, final T element) {
		return isNotEmpty(list) ? list.indexOf(element) : -1;
	}

	public static final <T> T safeGet(final List<T> list, final T element) {
		return isNotEmpty(list) ? safeGet(list, safeIndex(list, element)) : null;
	}

	public static final <T> List<T> append(final List<T> listOne, final List<T> listTwo) {
		final List<T> ret = new ArrayList<T>(listOne != null ? listOne : new ArrayList<T>(0));
		if (isNotEmpty(listTwo)) {
			ret.addAll(listTwo);
		}
		return ret;
	}

	public static final <T> List<T> copyAll(final List<T> list) {
		return copyOfRange(list, 0, size(list));
	}

	public static final <T> List<T> copyOfRange(final List<T> list, final int begin, final int end) {
		final int size = size(list);
		final int bornedBeg = begin < 0 ? 0 : begin > size ? size : begin;
		final int bornedEnd = end < 0 ? 0 : end > size ? size : end;
		final List<T> ret = new ArrayList<T>(bornedEnd - bornedBeg);
		for (int index = bornedBeg; index < bornedEnd; ++index) {
			ret.add(list.get(index));
		}
		return ret;
	}

	public interface Stringuifier<T> {

		String stringuify(T t);

	}

	public static final <T> void visu(final Collection<T> col) {
		visu(col, System.out);
	}

	public static final <T> void visu(final Collection<T> col, final PrintStream out) {
		visu(col, out, null, new Stringuifier<T>() {
			@Override
			public String stringuify(final T t) {
				return t != null ? t.toString() : "null"; //$NON-NLS-1$
			}
		});
	}

	public static final <T> void visu(final Collection<T> col, final Stringuifier<T> sgf) {
		visu(col, System.out, null, sgf);
	}

	public static final <T> void visu(final Collection<T> col, final Stringuifier<T> sgf, final String title) {
		visu(col, System.out, title, sgf);
	}

	public static final <T> void visu(final Collection<T> col, final String title) {
		visu(col, System.out, title, new Stringuifier<T>() {
			@Override
			public String stringuify(final T t) {
				return t != null ? t.toString() : "null"; //$NON-NLS-1$
			}
		});
	}

	public static final <T> void visu(final Collection<T> col, final PrintStream out, final String title, final Stringuifier<T> sgf) {
		if (col != null) {
			final Iterator<T> it = col.iterator();
			int index = 0;
			if (title != null) {
				out.println("Begin : " + title); //$NON-NLS-1$
			}
			out.println("NbElements : " + size(col)); //$NON-NLS-1$
			while (it.hasNext()) {
				out.println(++index + " : " + sgf.stringuify(it.next())); //$NON-NLS-1$
			}
			if (title != null) {
				out.println("End : " + title); //$NON-NLS-1$
			}
		}
	}

	public static final <T> String toString(final Collection<T> col) {
		return toString(col, new StringBuilder());
	}

	public static final <T> String toString(final Collection<T> col, final StringBuilder sb) {
		return toString(col, sb, null, new Stringuifier<T>() {
			@Override
			public String stringuify(final T t) {
				return t != null ? t.toString() : "null"; //$NON-NLS-1$
			}
		});
	}

	public static final <T> String toString(final Collection<T> col, final Stringuifier<T> sgf) {
		return toString(col, new StringBuilder(), null, sgf);
	}

	public static final <T> String toString(final Collection<T> col, final String title) {
		return toString(col, new StringBuilder(), title, new Stringuifier<T>() {
			@Override
			public String stringuify(final T t) {
				return t != null ? t.toString() : "null"; //$NON-NLS-1$
			}
		});
	}

	public static final <T> String toString(final Collection<T> col, final StringBuilder sb, final String title, final Stringuifier<T> sgf) {
		if (col != null) {
			final Iterator<T> it = col.iterator();
			int index = 0;
			if (title != null) {
				sb.append("Begin : ").append(title); //$NON-NLS-1$
			}
			sb.append("NbElements : ").append(size(col)).append(" "); //$NON-NLS-1$
			while (it.hasNext()) {
				sb.append(++index).append(" : ").append(sgf.stringuify(it.next())); //$NON-NLS-1$
			}
			if (title != null) {
				sb.append("End : ").append(title); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	public static final <T> boolean sameWithNullGeneric(final T objOne, final T objTwo) {
		return (objOne == objTwo) || (objOne != null && objTwo != null && objOne.equals(objTwo));
	}

	public static final boolean sameWithNull(final Object objOne, final Object objTwo) {
		return (objOne == objTwo) || (objOne != null && objTwo != null && objOne.equals(objTwo));
	}

	public static final <T extends Comparable<T>> int compareWithNull(final T one, final T two) {
		return one == two ? 0 : one != null && two != null ? one.compareTo(two) : one != null ? -1 : 1;
	}

	public static final <T> int compareReference(final T one, final T two) {
		return one == two ? 0 : one != null ? -1 : 1;
	}

	public static final <T> int compareWithNull(final T one, final T two, final Comparator<T> comparator) {
		return one == two ? 0 : one != null && two != null ? comparator.compare(one, two) : one != null ? -1 : 1;
	}

	public static final <T> boolean isContained(final List<T> list, final int index) {
		return list != null && !list.isEmpty() && index >= 0 && index < list.size();
	}

	public static final <T> void swap(final List<T> list, final T one, final T two) {
		if (list != null && !list.isEmpty()) {
			final int indexOne = list.indexOf(one);
			if (indexOne != -1) {
				final int indexTwo = list.indexOf(two);
				if (indexTwo != -1) {
					Collections.swap(list, indexOne, indexTwo);
				}
			}
		}
	}

	public static final <T> void swap(final List<T> list, final int indexOne, final int indexTwo) {
		if (isContained(list, indexOne) && isContained(list, indexTwo)) {
			Collections.swap(list, indexOne, indexTwo);
		}
	}

	public static final <T> T getForwardElement(final List<T> list, final T element, final int rank) {
		final int indexElement = list.indexOf(element);
		if (indexElement != -1) {
			return safeGet(list, indexElement + rank);
		}
		return null;
	}

	public static final <T> T getBackwardElement(final List<T> list, final T element, final int rank) {
		final int indexElement = list.indexOf(element);
		if (indexElement != -1) {
			return safeGet(list, indexElement - rank);
		}
		return null;
	}

	public static final <T> T getNextElement(final List<T> list, final T element) {
		return getForwardElement(list, element, 1);
	}

	public static final <T> T getPreviousElement(final List<T> list, final T element) {
		return getBackwardElement(list, element, 1);
	}

	/** Soustrait la deuxième collection de la première */
	public static final <T> Collection<T> substractCollection(final Collection<T> substractCollection, final Collection<T> elementList) {
		if (isNotEmpty(substractCollection) && isNotEmpty(elementList)) {
			substractCollection.removeAll(elementList);
		}
		return substractCollection;
	}

	/** Ajoute la deuxième collection à la première */
	public static final <T> Collection<T> addCollection(final Collection<T> addCollection, final Collection<T> elementList) {
		if (isNotNull(addCollection) && isNotEmpty(elementList)) {
			addCollection.addAll(elementList);
		}
		return addCollection;
	}

	/** Ajoute une map a une collection sans autoriser les doublons */
	public static final <T, U> Collection<T> addMapToCollection(final Collection<T> origCollection, final Map<U, T> toAddMap) {
		return addMapToCollection(origCollection, toAddMap, false);
	}

	/**
	 * Ajoute une map a une collection. Le parametre boolean doublons authorise
	 * ou non les doublons
	 */
	public static final <T, U> Collection<T> addMapToCollection(final Collection<T> origCollection, final Map<U, T> toAddMap, boolean doublons) {
		if (isNotNull(origCollection) && isNotEmpty(toAddMap)) {
			final Collection<T> values = toAddMap.values();
			if (doublons || origCollection instanceof Set<?>) {
				origCollection.addAll(values);
			} else {
				final Set<T> set = new HashSet<T>(origCollection);
				for (final T e : values) {
					if (!set.contains(e)) {
						origCollection.add(e);
						set.add(e);
					}
				}
			}
		}
		return origCollection;
	}

	/**
	 * Ajoute la deuxième liste à la première et au besoin alloue la premiere
	 * liste
	 */
	public static final <T> List<T> addList(final List<T> addCollection, final List<T> elementList) {
		List<T> refAddCollection = addCollection;
		if (isNotNull(refAddCollection)) {
			if (isNotEmpty(elementList)) {
				refAddCollection.addAll(elementList);
			}
		} else {
			refAddCollection = allocateList(elementList);
			if (isNotEmpty(elementList)) {
				refAddCollection.addAll(elementList);
			}
		}
		return refAddCollection;
	}

	public static final <T> List<T> addToList(final List<T> addCollection, final T element) {
		List<T> refAddCollection = addCollection;
		if (isNotNull(refAddCollection)) {
			if (isNotNull(element)) {
				refAddCollection.add(element);
			}
		} else {
			refAddCollection = allocateList(1);
			if (isNotNull(element)) {
				refAddCollection.add(element);
			}
		}
		return refAddCollection;
	}

	public static final <T> Set<T> addSet(final Set<T> addCollection, final Set<T> elementList) {
		Set<T> refAddCollection = addCollection;
		if (isNotNull(refAddCollection)) {
			if (isNotEmpty(elementList)) {
				refAddCollection.addAll(elementList);
			}
		} else {
			refAddCollection = allocateSet(elementList);
			if (isNotEmpty(elementList)) {
				refAddCollection.addAll(elementList);
			}
		}
		return refAddCollection;
	}

	/**
	 * Remplace les elements de la premiere liste present dans la deuxieme par
	 * ceux ci
	 */
	public static final <T> void replaceAll(final List<T> addList, final List<T> elementList) {
		if (isNotEmpty(addList) && isNotEmpty(elementList)) {
			int curIndex;
			for (final T element : elementList) {
				curIndex = addList.indexOf(element);
				if (curIndex != -1) {
					addList.set(curIndex, element);
				}
			}
		}
	}

	public static <T> void addAll(final Collection<T> col, final T[] array) {
		if (col != null) {
			if (ArrayHelper.isNotEmpty(array)) {
				for (final T element : array) {
					col.add(element);
				}
			}
		}
	}

	public interface Distanciator<T, U> {

		int distance(T t, U u);

	}

	public static final <T, U> int getDistanceMinimum(final List<T> list, final U element, final Distanciator<T, U> distanciator) {
		int distanceMin = Integer.MAX_VALUE;
		if (isNotEmpty(list)) {
			int distance = 0;
			for (final T t : list) {
				distance = distanciator.distance(t, element);
				if (distance < distanceMin) {
					distanceMin = distance;
				}
			}
		}
		return distanceMin;
	}

	public static final <T, U> int getDistanceMaximum(final List<T> list, final U element, final Distanciator<T, U> distanciator) {
		int distanceMax = Integer.MIN_VALUE;
		if (isNotEmpty(list)) {
			int distance = 0;
			for (T t : list) {
				distance = distanciator.distance(t, element);
				if (distance > distanceMax) {
					distanceMax = distance;
				}
			}
		}
		return distanceMax;
	}

	public static final <T, U> T getCloserElement(final List<T> list, final U element, final Distanciator<T, U> distanciator) {
		T ret = null;
		int distanceMin = Integer.MAX_VALUE;
		if (isNotEmpty(list)) {
			int distance;
			for (final T t : list) {
				distance = distanciator.distance(t, element);
				if (distance < distanceMin) {
					distanceMin = distance;
					ret = t;
				}
			}
		}
		return ret;
	}

	public static final <T, U> T getFartherElement(final List<T> list, final U element, final Distanciator<T, U> distanciator) {
		T ret = null;
		int distanceMax = Integer.MIN_VALUE;
		if (isNotEmpty(list)) {
			int distance = 0;
			for (T t : list) {
				distance = distanciator.distance(t, element);
				if (distance > distanceMax) {
					distanceMax = distance;
					ret = t;
				}
			}
		}
		return ret;
	}

	public interface IParamFilter<T> {

		boolean isAccepted(T bean);

	}

	public static final <T> List<T> filter(final List<T> dataList, final IParamFilter<T> iParamFilter) {
		if (CollectionHelper.isNotEmpty(dataList)) {
			final List<T> ret = allocateList(dataList);
			for (T bean : dataList) {
				if (iParamFilter.isAccepted(bean)) {
					ret.add(bean);
				}
			}
			return ret;
		}
		return dataList;
	}

	public static final <T> List<T> filterAnd(final List<T> dataList, final List<IParamFilter<T>> iParamFilterList) {
		if (isNotEmpty(dataList) && isNotEmpty(iParamFilterList)) {
			final List<T> ret = allocateList(dataList);
			boolean isAccepted;
			for (T bean : dataList) {
				isAccepted = true;
				for (IParamFilter<T> iParamFilter : iParamFilterList) {
					isAccepted &= iParamFilter.isAccepted(bean);
				}
				if (isAccepted) {
					ret.add(bean);
				}
			}
			return ret;
		}
		return dataList;
	}

	public static final <T> List<T> filterOr(final List<T> dataList, final List<IParamFilter<T>> iParamFilterList) {
		if (isNotEmpty(dataList) && isNotEmpty(iParamFilterList)) {
			final List<T> ret = allocateList(dataList);
			for (T bean : dataList) {
				for (IParamFilter<T> iParamFilter : iParamFilterList) {
					if (iParamFilter.isAccepted(bean)) {
						ret.add(bean);
						break;
					}
				}
			}
			return ret;
		}
		return dataList;
	}

	private static final Map<Class<?>, Comparator<Object>> ASSO_CLASS_COMPARATOR = new HashMap<Class<?>, Comparator<Object>>();

	private static final <T extends Comparable<T>> Comparator<Object> getComparator(final Class<T> clazz) {
		if (!ASSO_CLASS_COMPARATOR.containsKey(clazz)) {
			ASSO_CLASS_COMPARATOR.put(clazz, new Comparator<Object>() {
				@Override
				public int compare(final Object one, final Object two) {
					return (clazz.cast(one)).compareTo(clazz.cast(two));
				}
			});

		}
		return ASSO_CLASS_COMPARATOR.get(clazz);
	}

	/**
	 * Compare la classe T à la classe T
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	private static final <T> Comparator<Object> getComparator2(final Class<T> clazz) {
		if (isComparable(clazz)) {
			if (!ASSO_CLASS_COMPARATOR.containsKey(clazz)) {
				ASSO_CLASS_COMPARATOR.put(clazz, new Comparator<Object>() {
					@Override
					@SuppressWarnings("unchecked")
					public int compare(final Object one, final Object two) {
						return ((Comparable<T>) (clazz.cast(one))).compareTo((clazz.cast(two)));
					}
				});
			}
			return ASSO_CLASS_COMPARATOR.get(clazz);
		}
		throw new IllegalArgumentException();
	}

	public static final boolean isComparable(final Object obj) {
		return obj != null && isComparable(obj.getClass());
	}

	public static final boolean isComparable(final Class<?> clazz) {
		return isAssignable(Comparable.class, clazz);
	}

	/** retourne si la premiere classe est mere de la seconde */
	private static final boolean isAssignable(final Class<?> clazz1, final Class<?> clazz2) {
		return clazz1 != null && clazz2 != null && clazz1.isAssignableFrom(clazz2);
	}

	private static final Comparator<Object> getComparator(final Object one, final Object two) {
		if (!one.getClass().equals(two.getClass())) {
			throw new IllegalArgumentException();
		}
		if (one instanceof String) {
			return getComparator(String.class);
		} else if (one instanceof Date) {
			return getComparator(Date.class);
		} else if (one instanceof BigDecimal) {
			return getComparator(BigDecimal.class);
		} else if (one instanceof Boolean) {
			return getComparator(Boolean.class);
		} else if (one instanceof Double) {
			return getComparator(Double.class);
		} else if (one instanceof Float) {
			return getComparator(Float.class);
		} else if (one instanceof Integer) {
			return getComparator(Integer.class);
		} else if (one instanceof Long) {
			return getComparator(Long.class);
		} else if (one instanceof Short) {
			return getComparator(Short.class);
		}
		return getComparator2(one.getClass());
	}

	public static final int compareWithNullObject(final Object one, final Object two) {
		return one == two ? 0 : one != null && two != null ? compareWithNullObject(one, two, getComparator(one, two)) : one != null ? -1 : 1;
	}

	public static final int compareWithNullObject(final Object one, final Object two, final Comparator<Object> comparator) {
		return one == two ? 0 : one != null && two != null ? comparator.compare(one, two) : one != null ? -1 : 1;
	}

	public static final Class<?> findClass(final Collection<?> col) {
		if (col != null) {
			final Iterator<?> it = col.iterator();
			Class<?> clazz = null;
			Object cur;
			while (it.hasNext()) {
				cur = it.next();
				if (cur == null) {
					continue;
				}
				if (clazz == null) {
					clazz = cur.getClass();
				} else {
					if (!clazz.equals(cur.getClass())) {
						return null;
					}
				}
			}
			return clazz;
		}
		return null;
	}

	public static final <T extends Cloneable> Method findMethodClone(final Class<T> elementClass) {
		try {
			return elementClass.getMethod("clone");
		} catch (final SecurityException e) {
			LOG.error("CollectionHelper.findMethodClone", e);
		} catch (final NoSuchMethodException e) {
			LOG.error("CollectionHelper.findMethodClone", e);
		}
		return null;
	}

	public static final <T, U extends Collection<T>> U allocateCollection(final Class<U> colClass) {
		try {
			return colClass.newInstance();
		} catch (final IllegalArgumentException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final SecurityException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final InstantiationException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final IllegalAccessException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		}
		return null;
	}

	public static final <T, U extends Collection<T>, V extends Collection<T>> V allocateCollection(final U col, final Class<V> colClass) {
		try {
			return colClass.getConstructor(int.class).newInstance(size(col));
		} catch (final IllegalArgumentException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final SecurityException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final InstantiationException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final IllegalAccessException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final InvocationTargetException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final NoSuchMethodException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final Exception e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		}
		return allocateCollection(colClass);
	}

	public static final <T, U extends Collection<T>, V extends Collection<T>, W extends V> V allocateCollection(final U col, final Class<W> colClass, final Class<V> retClass) {
		try {
			return retClass.cast(colClass.getConstructor(int.class).newInstance(size(col)));
		} catch (final IllegalArgumentException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final SecurityException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final InstantiationException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final IllegalAccessException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final InvocationTargetException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final NoSuchMethodException e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		} catch (final Exception e) {
			LOG.error("CollectionHelper.allocateCollection", e);
		}
		return retClass.cast(allocateCollection(colClass));
	}

	public static final <T extends Cloneable, U extends Collection<T>, V extends U> U clone(final U col, final Class<T> elementClass, final Class<V> colClass) {
		if (isNotEmpty(col)) {
			final Method cloneMethod = findMethodClone(elementClass);
			if (elementClass == null || colClass == null || cloneMethod == null) {
				throw new IllegalArgumentException();
			}
			try {
				final U ret = allocateCollection(col, colClass);
				final Iterator<T> it = col.iterator();
				while (it.hasNext()) {
					ret.add(elementClass.cast(cloneMethod.invoke(it.next())));
				}
				return ret;
			} catch (final IllegalAccessException e) {
				LOG.error("CollectionHelper.clone", e);
			} catch (final IllegalArgumentException e) {
				LOG.error("CollectionHelper.clone", e);
			} catch (final InvocationTargetException e) {
				LOG.error("CollectionHelper.clone", e);
			}
		}
		return null;
	}

	public interface IdGetter<ID, OBJ> {

		ID getId(OBJ obj);

	}

	/**
	 * Pour faire des recherches en O(1) dans une collection, exemple : idGetter
	 * renvoie une cle primaire
	 */
	public static final <ID, OBJ> Map<ID, OBJ> linearSearchMaker(final Collection<OBJ> col, final IdGetter<ID, OBJ> idGetter) {
		if (idGetter != null && isNotEmpty(col)) {
			final Map<ID, OBJ> ret = allocateMap(col);
			for (final OBJ obj : col) {
				ret.put(idGetter.getId(obj), obj);
			}
			return ret;
		}
		return null;
	}

	public static final <ID, OBJ> Map<ID, OBJ> linearSearchMaker(final Iterable<OBJ> col, final IdGetter<ID, OBJ> idGetter) {
		if (idGetter != null && isNotEmpty(col)) {
			final Map<ID, OBJ> ret = new HashMap<ID, OBJ>();
			for (final OBJ obj : col) {
				ret.put(idGetter.getId(obj), obj);
			}
			return ret;
		}
		return null;
	}

	public static final <ID, OBJ> Map<ID, OBJ> linearSearchMaker(final OBJ[] col, final IdGetter<ID, OBJ> idGetter) {
		if (idGetter != null && (col != null && col.length > 0)) {
			final Map<ID, OBJ> ret = allocateMap(col.length);
			for (final OBJ obj : col) {
				ret.put(idGetter.getId(obj), obj);
			}
			return ret;
		}
		return null;
	}

	/** meme principe mais pour une cle secondaire */
	public static final <ID, OBJ> Map<ID, List<OBJ>> linearSearchMakerList(final Collection<OBJ> col, final IdGetter<ID, OBJ> idGetter) {
		return linearSearchMakerList(col, idGetter, null);
	}

	/** meme principe mais pour une cle secondaire */
	public static final <ID, OBJ> Map<ID, List<OBJ>> linearSearchMakerList(final Collection<OBJ> col, final IdGetter<ID, OBJ> idGetter, final Integer bestAlloc) {
		if (idGetter != null && isNotEmpty(col)) {
			final int nbAlloc = bestAlloc != null ? bestAlloc.intValue() : 10;
			ID curId;
			final Map<ID, List<OBJ>> ret = allocateMap(col);
			for (final OBJ obj : col) {
				curId = idGetter.getId(obj);
				if (!ret.containsKey(curId)) {
					ret.put(curId, new ArrayList<OBJ>(nbAlloc));
				}
				ret.get(curId).add(obj);
			}
			return ret;
		}
		return null;
	}

	public static final <ID, OBJ> Binome<Map<ID, List<OBJ>>, Integer> linearSearchMakerListMax(final Collection<OBJ> col, final IdGetter<ID, OBJ> idGetter) {
		if (idGetter != null && isNotEmpty(col)) {
			final Map<ID, List<OBJ>> ret = allocateMap(col);
			int max = Integer.MIN_VALUE;
			List<OBJ> curList;
			ID curId;
			for (final OBJ obj : col) {
				curId = idGetter.getId(obj);
				if (!ret.containsKey(curId)) {
					ret.put(curId, new ArrayList<OBJ>());
				}
				curList = ret.get(curId);
				curList.add(obj);
				max = Math.max(max, curList.size());
			}
			return new Binome<Map<ID, List<OBJ>>, Integer>(ret, max);
		}
		return null;
	}

	public static final <ID, OBJ> Map<ID, List<OBJ>> linearSearchMakerList(final Iterable<OBJ> col, final IdGetter<ID, OBJ> idGetter) {
		return linearSearchMakerList(col, idGetter, null);
	}

	public static final <ID, OBJ> Map<ID, List<OBJ>> linearSearchMakerList(final Iterable<OBJ> col, final IdGetter<ID, OBJ> idGetter, final Integer bestAlloc) {
		final Map<ID, List<OBJ>> ret = new HashMap<ID, List<OBJ>>();
		if (idGetter != null && isNotEmpty(col)) {
			final int nbAlloc = bestAlloc != null ? bestAlloc.intValue() : 10;
			ID curId;
			for (final OBJ obj : col) {
				curId = idGetter.getId(obj);
				if (!ret.containsKey(curId)) {
					ret.put(curId, new ArrayList<OBJ>(nbAlloc));
				}
				ret.get(curId).add(obj);
			}
		}
		return ret;
	}

	public static final <T> T linearFind(final Iterable<T> iterable, final T objectToFind) {
		if (iterable != null) {
			final Iterator<T> it = iterable.iterator();
			T t;
			while (it.hasNext()) {
				t = it.next();
				if (sameWithNull(t, objectToFind)) {
					return t;
				}
			}
		}
		return null;
	}

	public static final <T> T linearFind(final List<T> col, final T objectToFind) {
		if (col != null && col.size() > 0) {
			final int index = col.indexOf(objectToFind);
			return index != -1 ? col.get(index) : null;
		}
		return null;
	}

	public static final <T> boolean contains(final Collection<T> list, final T element) {
		return list != null && list.size() > 0 ? list.contains(element) : false;
	}

	/* la liste doit etre triee ascendante par rapport */
	public static final <T extends Comparable<T>> boolean fastContains(final List<T> list, final T element) {
		return list != null && list.size() > 0 ? Collections.binarySearch(list, element) >= 0 : false;
	}

	/* utilise le containsKey de la map */
	public static final <T, V> boolean containsKey(final Map<T, V> map, final T key) {
		return map != null && map.containsKey(key);
	}

	/* utilise le containValue de la map */
	public static final <T, V> boolean contains(final Map<T, V> map, final V element) {
		return map != null && map.containsValue(element);
	}

	/* utilise le !containsKey de la map */
	public static final <T, V> boolean doesNotContainKey(final Map<T, V> map, final T key) {
		return map == null || !map.containsKey(key);
	}

	/* utilise le !containValue de la map */
	public static final <T, V> boolean doesNotContains(final Map<T, V> map, final V element) {
		return map == null || !map.containsValue(element);
	}

	// public static final <T> boolean fastContains(final List<? extends T>
	// list, final T element, final Comparable<? super T> comparable) {
	// return col != null && col.size() > 0 ? Collections.binarySearch(col,
	// element, comparable) >= 0 : false;
	// return list != null && list.size() > 0 ? Collections.binarySearch(list,
	// element, comparable) >= 0 : false;
	// }

	public static final <T> List<T> reverse(final List<T> list) {
		if (list != null && list.size() > 1) {
			Collections.reverse(list);
		}
		return list;
	}

	public static final <T> Set<T> asSet(final Collection<T> col) {
		return col != null ? new HashSet<T>(col) : null;
	}

	public static final <T> Set<T> asSet(final T... col) {
		return col != null ? new HashSet<T>(asList(col)) : null;
	}

	public interface Instanciator<T, U> {

		U instanciate(T t);

	}

	public interface InstanciatorList<T, U> {

		U instanciate(List<T> tList);

	}

	public static final <T, U> List<U> adapterList(final List<T> sourceList, final Instanciator<T, U> instanciator) {
		final List<U> ret = allocateList(sourceList);
		if (isNotEmpty(sourceList)) {
			for (final T source : sourceList) {
				ret.add(instanciator.instanciate(source));
			}
		}
		return ret;
	}

	public static final <T> List<List<T>> getChunkedList(final List<T> listToProcess, int nbProc) {
		if (nbProc <= 0 || listToProcess == null || listToProcess.size() <= 0) {
			throw new IllegalArgumentException("Arguments impossibles"); //$NON-NLS-1$
		}

		final List<List<T>> chunkedList = new ArrayList<List<T>>();

		if (nbProc <= 1) {
			chunkedList.add(listToProcess);
		} else {
			final int listSize = listToProcess.size();
			final int chunkSize = listSize / nbProc;

			if (chunkSize > 0) {

				List<T> curList;
				int decalage;
				for (int proc = 0; proc < nbProc - 1; ++proc) {
					curList = new ArrayList<T>(chunkSize);
					decalage = proc * chunkSize;
					for (int index = 0; index < chunkSize; ++index) {
						curList.add(listToProcess.get(index + decalage));
					}
					chunkedList.add(curList);
				}

				final int debLastChunk = (nbProc - 1) * chunkSize;
				curList = new ArrayList<T>(listSize - debLastChunk);
				for (int index = debLastChunk; index < listSize; ++index) {
					curList.add(listToProcess.get(index));
				}
				chunkedList.add(curList);
			} else {
				chunkedList.add(listToProcess);
			}
		}
		return chunkedList;
	}

	public static synchronized final <T> T getRandom(final List<T> list) {
		return isEmpty(list) ? null : list.get(RAND.nextInt(size(list)));
	}

	public static <T> boolean instanceFind(final Iterable<T> it, final T toFind) {
		if (it != null && toFind != null) {
			for (final T t : it) {
				if (t == toFind) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Créer un String à partir de la collection
	 * 
	 * @param <T>
	 * @param collection
	 * @param toString
	 * @return
	 */
	public static final <T> String toStringCollection(Collection<T> collection, IToString<T> toString) {
		StringBuilder sb = new StringBuilder("["); //$NON-NLS-1$
		if (collection != null && !collection.isEmpty()) {
			int i = 0;
			for (T t : collection) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(toString.toString(t));
				i++;
			}
		}
		return sb.append("]").toString(); //$NON-NLS-1$
	}

	public static interface IToString<T> {

		public abstract String toString(T t);

	}

	/**
	 * Renvoie la liste jusqu'à l'index compris
	 * 
	 * @param <T>
	 * @param list
	 * @param index
	 * @return
	 */
	public static final <T> List<T> getBeforeElementList(List<T> list, int index) {
		List<T> ret = null;
		if (isNotEmpty(list) && list.size() > index) {
			ret = new ArrayList<T>();
			int i = 0;
			Iterator<T> it = list.iterator();
			while (it.hasNext() && i <= index) {
				ret.add(it.next());
				i++;
			}
		}
		return ret;
	}

	/**
	 * Renvoie la liste à partir de l'index
	 * 
	 * @param <T>
	 * @param list
	 * @param index
	 * @return
	 */
	public static final <T> List<T> getAfterElementList(final List<T> list, int index) {
		List<T> ret = null;
		if (isNotEmpty(list) && list.size() > index) {
			ret = new ArrayList<T>();
			int i = 0;
			for (T t : list) {
				if (i >= index) {
					ret.add(t);
				}
				i++;
			}
		}
		return ret;
	}

	/**
	 * Renvoie une sous liste de la liste
	 * 
	 * @param list
	 * @param indexDebut
	 * @param indexFin
	 * @return
	 */
	public static final <T> List<T> subList(List<T> list, int indexDebut, int indexFin) {
		List<T> res = new ArrayList<T>();
		for (int i = indexDebut; i <= indexFin; i++) {
			res.add(list.get(i));
		}
		return res;
	}

	/*
	 * 
	 * effectue un clone profond de l'argument si la serialisation de l argument
	 * est bien faite
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(final T obj) {
		T clone = null;
		ObjectOutputStream objOs = null;
		ObjectInputStream objIs = null;
		try {
			final ByteArrayOutputStream byteArrOs = new ByteArrayOutputStream();
			objOs = new ObjectOutputStream(byteArrOs);
			objOs.writeObject(obj);
			final ByteArrayInputStream byteArrIs = new ByteArrayInputStream(byteArrOs.toByteArray());
			objIs = new ObjectInputStream(byteArrIs);
			clone = (T) objIs.readObject();
		} catch (final Exception e) {
			LOG.error("CollectionHelper.clone.SerializationException", e);
			if (obj instanceof Cloneable) {
				Method cloneMethod;
				try {
					cloneMethod = obj.getClass().getMethod("clone");
					if (cloneMethod != null) {
						clone = (T) cloneMethod.invoke(obj);
					} else {
						clone = obj;
					}
				} catch (final Exception e1) {
					LOG.error("ClientServiceFactory.clone.CloneableException", e);
					clone = obj;
				}
			} else {
				clone = obj;
			}
		} finally {
			if (objOs != null) {
				try {
					objOs.close();
				} catch (final IOException e) {
					LOG.error("ClientServiceFactory.clone.ObjectOutputStream.close", e);
				}
			}
			if (objIs != null) {
				try {
					objIs.close();
				} catch (final IOException e) {
					LOG.error("ClientServiceFactory.clone.ObjectInputStream.close", e);
				}
			}
		}
		return clone;
	}

	public static interface IValueGetter<T> {

		T value();

	}

	public static <T, U> void assertAllocationMap(final Map<T, U> map, final T key, final IValueGetter<U> valueGetter) {
		if (valueGetter != null && map != null) {
			if (!map.containsKey(key)) {
				map.put(key, valueGetter.value());
			}
		}
	}

	public static int hashCodeNull(final Object obj) {
		return obj != null ? obj.hashCode() : 0;
	}

	public static final <T> Set<T> mergeSet(final Set<T>... sets) {
		final int nbSet = sets != null ? sets.length : 0;
		if (nbSet > 0) {
			int maxSize = 0;
			for (int set = 0; set < nbSet; ++set) {
				maxSize += size(sets[set]);
			}
			Set<T> curSet;
			final Set<T> retSet = allocateSet(maxSize);
			for (int set = 0; set < nbSet; ++set) {
				curSet = sets[set];
				if (curSet != null) {
					retSet.addAll(curSet);
				}
			}
			return retSet;
		}
		return null;
	}

	/**
	 * Crée une liste de t élements de taille nbElement
	 * 
	 * @param t
	 * @param nbElement
	 *            taille de la liste
	 * @return liste de t élements de taille nbElement
	 * @see Collections#fill(List, Object)
	 */
	public static final <T> List<T> getFilledList(final T t, final int nbElement) {
		final List<T> ret = allocateList(nbElement);
		for (int element = 0; element < nbElement; ++element) {
			ret.add(t);
		}
		return ret;
	}

	public static final <T> Set<T> getReadOnlySet(final Set<T> set) {
		if (set != null) {
			return Collections.unmodifiableSet(set);
		}
		return Collections.unmodifiableSet(new HashSet<T>(0));
	}
}