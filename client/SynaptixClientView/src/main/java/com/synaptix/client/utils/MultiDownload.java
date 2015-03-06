package com.synaptix.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiDownload<E> implements IDownload<E> {

	private IDownload<E>[] callables;

	public MultiDownload(IDownload<E>... callables) {
		this.callables = callables;
	}

	public void call(final E e) throws Exception {
		ExecutorService ex = Executors.newFixedThreadPool(callables.length);

		List<Future<E>> futureList = new ArrayList<Future<E>>();

		for (final IDownload<E> download : callables) {
			futureList.add(ex.submit(new Callable<E>() {
				public E call() throws Exception {
					download.call(e);
					return e;
				}
			}));
		}

		for (Future<E> future : futureList) {
			future.get();
		}

		ex.shutdown();
	}
}
