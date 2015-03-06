package com.synaptix.constants;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynaptixTicksHelper {

	private static volatile SynaptixTicksHelper instance;
	private static final Object lock = new Object();

	private final Map<String, Tick> tickMap;

	public static SynaptixTicksHelper getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new SynaptixTicksHelper();
				}
			}
		}
		return instance;
	}

	private SynaptixTicksHelper() {
		super();

		tickMap = new HashMap<String, Tick>();
	}

	public static void clear() {
		getInstance().clearMap();
	}

	public static void clear(String key) {
		getInstance().clearMap(key);
	}

	private void clearMap() {
		tickMap.clear();
	}

	private void clearMap(String key) {
		tickMap.remove(key);
	}

	public static TickResult tick(String key) {
		return tick(key, System.out);
	}

	public static TickResult tick(String key, PrintStream printStream) {
		return getInstance().tickPut(key, printStream);
	}

	private TickResult tickPut(String key, PrintStream printStream) {
		long time = System.nanoTime();
		Tick tick = tickMap.get(key);
		if (tick != null) {
			long startTick = tick.getStartTick();
			long lastTick = tick.getLastTick();
			long t = (time - lastTick) / 1000000;
			StringBuilder sb = new StringBuilder("Ticks for ").append(key).append("\n\tsince last tick: ").append(t);
			sb.append("ms\n\tsince start: ").append((time - startTick) / 1000000).append("ms.");
			printStream.println(sb.toString());
			tick.setLastTick(time);
			return new TickResult(key, t, tick.getTraceList());
		}
		initKey(key, time);
		return new TickResult(key, 0l, Collections.<String> emptyList());
	}

	public static void init(String key) {
		getInstance().initKey(key, System.nanoTime());
	}

	private void initKey(String key, long time) {
		tickMap.put(key, new Tick(time));
	}

	public static void trace(String key, String description) {
		getInstance().traceKey(key, description);
	}

	private void traceKey(String key, String description) {
		long time = System.nanoTime();
		Tick tick = tickMap.get(key);
		if (tick == null) {
			tick = new Tick(time);
		}
		tick.trace(description, time);
	}

	private class Tick {

		private final long startTick;

		private long lastTick;

		private final List<String> traceList;

		private long lastTrace;

		private Tick(long startTick) {
			this.startTick = startTick;
			this.lastTick = startTick;

			this.traceList = new ArrayList<String>();
		}

		public long getStartTick() {
			return startTick;
		}

		public long getLastTick() {
			return lastTick;
		}

		public void setLastTick(long lastTick) {
			this.lastTick = lastTick;
		}

		public void trace(String description, long time) {
			long startTick = lastTick;
			this.lastTrace = Math.max(lastTrace, lastTick);
			long t = (time - lastTrace) / 1000000;
			StringBuilder sb = new StringBuilder("Trace: ").append(description).append("\n\t\tsince last trace: ").append(t);
			sb.append("ms\n\t\tsince last tick: ").append((time - startTick) / 1000000).append("ms.");
			traceList.add(sb.toString());
			this.lastTrace = time;
		}

		public List<String> getTraceList() {
			return traceList;
		}
	}

	public static void main(String[] args) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					getInstance().test();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(false);
		thread.start();
	}

	private void test() throws Exception {
		tick("test");
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < Integer.MAX_VALUE; j++) {
				for (int k = 0; k < Integer.MAX_VALUE; k++) {
				}
			}
		}
		tick("test");
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < Integer.MAX_VALUE; j++) {
				for (int k = 0; k < Integer.MAX_VALUE; k++) {
				}
			}
		}
		trace("test", "1er appel");
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < Integer.MAX_VALUE; j++) {
				for (int k = 0; k < Integer.MAX_VALUE; k++) {
				}
			}
		}
		trace("test", "2e appel");
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < Integer.MAX_VALUE; j++) {
				for (int k = 0; k < Integer.MAX_VALUE; k++) {
				}
			}
		}
		tick("test").expand();
	}

	public class TickResult {

		private final String key;
		private final long tick;
		private final List<String> traceList;

		public TickResult(String key, long tick, List<String> traceList) {
			this.key = key;
			this.tick = tick;
			this.traceList = Collections.unmodifiableList(new ArrayList<String>(traceList));
			traceList.clear(); // burn after read
		}

		public long getTick() {
			return tick;
		}

		public void expand() {
			expand(System.out);
		}

		public void expand(PrintStream printStream) {
			if (traceList.isEmpty()) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("Expending ").append(key);
			for (String s : traceList) {
				sb.append("\n\t- ").append(s);
			}
			printStream.println(sb.toString());
		}
	}
}
