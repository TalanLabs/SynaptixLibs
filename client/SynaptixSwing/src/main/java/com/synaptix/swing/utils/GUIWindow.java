package com.synaptix.swing.utils;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.synaptix.swing.JSyDialog;

public class GUIWindow {

	private static GUIWindow guiFrame;

	private Stack<Display> displayStack;

	private GUIWindow() {
		displayStack = new Stack<Display>();
	}

	@Deprecated
	public static GUIWindow getInstance() {
		if (guiFrame == null)
			guiFrame = new GUIWindow();
		return guiFrame;
	}

	@Deprecated
	public void push(final JDialog d) {
		// System.out.println("Push Dialog : "+d);
		Display display = new Display(d);
		displayStack.push(display);
	}

	@Deprecated
	public void push(final JFrame f) {
		// System.out.println("Push Frame : "+f);
		Display display = new Display(f);
		displayStack.push(display);
	}

	@Deprecated
	public Window pop() {
		Window w = displayStack.pop().getWindow();
		// System.out.println("Pop : "+w);
		return w;
	}

	@Deprecated
	public Window peek() {
		Window[] ws = Window.getWindows();
		for (Window w : ws) {
			if (w.isActive()) {
				return w;
			}
		}
		throw new RuntimeException();
		// return displayStack.peek().getWindow();
	}

	@Deprecated
	public boolean isEmpty() {
		return displayStack.isEmpty();
	}

	@Deprecated
	public boolean isFrame() {
		Window[] ws = Window.getWindows();
		for (Window w : ws) {
			if (w.isActive()) {
				return w instanceof JFrame;
			}
		}
		throw new RuntimeException();
		// return displayStack.peek().getWindow() instanceof JFrame;
	}

	@Deprecated
	public boolean isDialog() {
		Window[] ws = Window.getWindows();
		for (Window w : ws) {
			if (w.isActive()) {
				return w instanceof JDialog;
			}
		}
		throw new RuntimeException();
		// return displayStack.peek().getWindow() instanceof JDialog;
	}

	private class Display {

		private Window window;

		public Display(JDialog dialog) {
			this.window = dialog;
		}

		public Display(JFrame frame) {
			this.window = frame;
		}

		public Window getWindow() {
			return window;
		}
	}

	public static Window getActiveWindow() {
		Window[] ws = Window.getWindows();
		for (Window w : ws) {
			if (w instanceof JSyDialog) {
				JSyDialog dialog = (JSyDialog) w;
				if (!dialog.isSeparatedWindow() && w.isActive()
						&& w.isVisible()) {
					return w;
				}
			} else {
				if (w.isActive() && w.isVisible()) {
					return w;
				}
			}
		}
		return null;
	}

	public static boolean isWindowModal(Window window) {
		if (window instanceof JDialog) {
			return ((JDialog) window).isModal();
		}
		return false;
	}

	public static void setGlassPaneForActiveWindow(Component glassPane) {
		setGlassPaneForWindow(getActiveWindow(), glassPane);
	}

	public static void setGlassPaneForWindow(Window window, Component glassPane) {
		if (window instanceof JDialog) {
			((JDialog) window).setGlassPane(glassPane);
		} else if (window instanceof JFrame) {
			((JFrame) window).setGlassPane(glassPane);
		}
	}

	public static Window[] getWindows() {
		Window[] ws = Window.getWindows();
		List<Window> res = new ArrayList<Window>();

		for (Window w : ws) {
			if (w instanceof JSyDialog) {
				JSyDialog dialog = (JSyDialog) w;
				if (!dialog.isSeparatedWindow() && w.isVisible()) {
					res.add(w);
				}
			} else {
				if (w.isVisible()) {
					res.add(w);
				}
			}
		}

		return res.toArray(new Window[res.size()]);

	}
}
