package com.synaptix.common.helper;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public final class ArrayHelper {

	private ArrayHelper() {

	}

	public static final <T> boolean isEmpty(final T[] array) {
		return array == null || array.length == 0;
	}

	public static final boolean isNotEmpty(final int[] array) {
		return array != null && array.length > 0;
	}

	public static final <T> boolean isNotEmpty(final T[] array) {
		return array != null && array.length > 0;
	}

	public static final <T> boolean isSingleton(final T[] col) {
		return col != null && col.length == 1;
	}

	public static final <T> boolean isNotSingleton(final T[] col) {
		return col != null && col.length > 1;
	}

	public static final int size(final int[] col) {
		return col == null ? 0 : col.length;
	}

	public static final <T> int size(final T[] col) {
		return col == null ? 0 : col.length;
	}

	public static final <T> T getFirstElement(final T[] col) {
		return col != null && col.length > 0 ? col[0] : null;
	}

	public static final int getFirstElement(final int[] col) {
		return col != null && col.length > 0 ? col[0] : -1;
	}

	public static final <T> T getLastElement(final T[] col) {
		return col != null && col.length > 0 ? col[col.length - 1] : null;
	}

	public static final <T> int sizeMin(final T[]... cols) {
		int sizeMin = 0;
		if (cols != null && cols.length > 0) {
			sizeMin = Integer.MAX_VALUE;
			for (T[] col : cols) {
				sizeMin = Math.min(sizeMin, size(col));
			}
		}
		return sizeMin;
	}

	public static final <T> int sizeMax(final T[]... cols) {
		int sizeMax = 0;
		if (cols != null && cols.length > 0) {
			sizeMax = -1;
			for (T[] col : cols) {
				sizeMax = Math.max(sizeMax, size(col));
			}
		}
		return sizeMax;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] getUniqueElementArray(final T[] tab) {
		return tab == null ? null : (T[]) (new ArrayList<T>(new HashSet<T>(Arrays.asList(tab)))).toArray();
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] getUniqueElementArray(final T[] tab, final Class<?> clazz) {
		return tab == null ? null : (T[]) (new ArrayList<T>(new HashSet<T>(Arrays.asList(tab)))).toArray((T[]) Array.newInstance(clazz, size(tab)));
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] allocate(final int size, final Class<?> clazz) {
		return (T[]) Array.newInstance(clazz, size);
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] allocate(final T[] tab, final Class<?> clazz) {
		return (T[]) Array.newInstance(clazz, size(tab));
	}

	public static final <T extends Comparable<T>> void sort(final T[] tab) {
		if (isNotSingleton(tab)) {
			Arrays.sort(tab);
		}
	}

	public static final <T extends Comparable<T>> T min(final T[] tab) {
		if (isNotSingleton(tab)) {
			return minimum(tab);
		}
		return null;
	}

	public static final <T extends Comparable<T>> T max(final T[] tab) {
		if (isNotSingleton(tab)) {
			return maximum(tab);
		}
		return null;
	}

	public static final <T> void sort(final T[] tab, final Comparator<T> comparator) {
		if (isNotSingleton(tab)) {
			Arrays.sort(tab, comparator);
		}
	}

	public static final <T> T min(final T[] list, final Comparator<T> comparator) {
		if (isNotSingleton(list)) {
			return minimum(list, comparator);
		}
		return null;
	}

	public static final <T> T max(final T[] tab, final Comparator<T> comparator) {
		if (isNotSingleton(tab)) {
			return maximum(tab, comparator);
		}
		return null;
	}

	private static final <T extends Comparable<T>> T minimum(final T[] tab) {
		T min = tab[0];
		T cur;
		final int nbElement = size(tab);
		for (int index = 1; index < nbElement; ++index) {
			cur = tab[index];
			if (min.compareTo(cur) > 0) {
				min = cur;
			}
		}
		return min;
	}

	private static final <T extends Comparable<T>> T maximum(final T[] tab) {
		T max = tab[0];
		T cur;
		final int nbElement = size(tab);
		for (int index = 1; index < nbElement; ++index) {
			cur = tab[index];
			if (max.compareTo(cur) < 0) {
				max = cur;
			}
		}
		return max;
	}

	private static final <T> T minimum(final T[] tab, final Comparator<T> comparator) {
		T min = tab[0];
		T cur;
		final int nbElement = size(tab);
		for (int index = 1; index < nbElement; ++index) {
			cur = tab[index];
			if (comparator.compare(min, cur) > 0) {
				min = cur;
			}
		}
		return min;
	}

	private static final <T> T maximum(final T[] tab, final Comparator<T> comparator) {
		T max = tab[0];
		T cur;
		final int nbElement = size(tab);
		for (int index = 1; index < nbElement; ++index) {
			cur = tab[index];
			if (comparator.compare(max, cur) < 0) {
				max = cur;
			}
		}
		return max;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] append(final T[] arrayOne, final T[] arrayTwo, final Class<?> clazz) {
		final int sizeFirst = size(arrayOne);
		final int sizeSecond = size(arrayTwo);
		final T[] ret = (T[]) Array.newInstance(clazz, sizeFirst + sizeSecond);
		System.arraycopy(arrayOne, 0, ret, 0, sizeFirst);
		System.arraycopy(arrayTwo, 0, ret, sizeFirst, sizeSecond);
		return ret;
	}

	public static final <T> void visu(final T[] array) {
		visu(array, System.out);
	}

	public static final <T> void visu(final T[] array, final PrintStream out) {
		final int size = size(array);
		for (int index = 0; index < size; ++index) {
			out.println(index + " : " + array[index]); //$NON-NLS-1$
		}
	}

	public static void main(final String... args) {
		final Integer[] testOne = new Integer[] { 1, 2, 3 };
		final Integer[] testTwo = new Integer[] { 4, 5, 6, 7 };
		final Integer[] testFinal = ArrayHelper.append(testTwo, testOne, Integer.class);
		System.out.println("1");
		visu(testOne);
		System.out.println("2");
		visu(testTwo);
		System.out.println("3");
		visu(testFinal);
		System.out.println("4");

	}

}
