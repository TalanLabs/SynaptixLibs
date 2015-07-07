package com.synaptix.swing;

import java.awt.Image;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import com.synaptix.client.view.IWaitWorker;
import com.synaptix.swing.utils.GUIWindow;

public abstract class WaitGlassPaneSwingWorker<T> {

	private static List<WaitGlassPaneSwingWorker<?>> waitList;

	private List<JWaitGlassPane> glassPanes;

	private MySwingWorker swingWorker;

	private int direction;

	private Image leftImage;

	private Image rightImage;

	private boolean showWait;

	private String lastPublish;

	static {
		waitList = new ArrayList<WaitGlassPaneSwingWorker<?>>();
	}

	public WaitGlassPaneSwingWorker() {
		this(true, JWaitGlassPane.ICON_CLIENT, JWaitGlassPane.ICON_DATABASE_SERVER);
	}

	public WaitGlassPaneSwingWorker(boolean showWait) {
		this(showWait, JWaitGlassPane.ICON_CLIENT, JWaitGlassPane.ICON_DATABASE_SERVER);
	}

	public WaitGlassPaneSwingWorker(boolean showWait, Image leftImage, Image rightImage) {
		this(showWait, JWaitGlassPane.TYPE_DIRECTION_RIGHT_TO_LEFT, leftImage, rightImage);
	}

	public WaitGlassPaneSwingWorker(boolean showWait, int direction, Image leftImage, Image rightImage) {
		swingWorker = new MySwingWorker();

		this.showWait = showWait;
		this.direction = direction;
		this.leftImage = leftImage;
		this.rightImage = rightImage;

		glassPanes = new ArrayList<JWaitGlassPane>();
	}

	public final IWaitWorker execute() {
		if (showWait) {
			synchronized (waitList) {
				if (waitList.isEmpty()) {
					startGlassPanes();
				}

				waitList.add(this);
			}
		}
		swingWorker.execute();
		return swingWorker;
	}

	protected abstract T doInBackground() throws Exception;

	protected abstract void done();

	protected final T get() throws Exception {
		return swingWorker.get();
	}

	protected final void publish(String... chunks) {
		swingWorker.myPublish(chunks);
	}

	private void startGlassPanes() {
		synchronized (glassPanes) {
			glassPanes.clear();
			Window window = GUIWindow.getActiveWindow();
			if (GUIWindow.isWindowModal(window)) {
				JWaitGlassPane glassPane = new JWaitGlassPane(direction, "", leftImage, rightImage);
				glassPanes.add(glassPane);

				GUIWindow.setGlassPaneForWindow(window, glassPane);
				glassPane.start();

				glassPane.setText(lastPublish);
			} else {
				Window[] ws = GUIWindow.getWindows();
				for (int i = 0; i < ws.length; i++) {
					Window w = ws[i];

					JWaitGlassPane glassPane = new JWaitGlassPane(direction, "", JWaitGlassPane.ICON_CLIENT, JWaitGlassPane.ICON_DATABASE_SERVER);
					glassPanes.add(glassPane);

					GUIWindow.setGlassPaneForWindow(w, glassPane);
					glassPane.start();

					glassPane.setText(lastPublish);
				}
			}
		}
	}

	private class MySwingWorker extends SwingWorker<T, String> implements IWaitWorker {

		public void myPublish(String... chunks) {
			publish(chunks);
		}

		protected T doInBackground() throws Exception {
			return WaitGlassPaneSwingWorker.this.doInBackground();
		}

		protected void process(List<String> chunks) {
			if (showWait) {
				synchronized (glassPanes) {
					if (chunks != null && !chunks.isEmpty()) {
						lastPublish = chunks.get(chunks.size() - 1);
					} else {
						lastPublish = null;
					}
					for (JWaitGlassPane glassPane : glassPanes) {
						glassPane.setText(lastPublish);
					}
				}
			}
		}

		protected void done() {
			if (showWait) {
				synchronized (waitList) {
					for (JWaitGlassPane glassPane : glassPanes) {
						glassPane.stop();
					}

					waitList.remove(WaitGlassPaneSwingWorker.this);

					if (!waitList.isEmpty()) {
						waitList.get(0).startGlassPanes();
					}
				}
			}

			WaitGlassPaneSwingWorker.this.done();
		}
	}
}
