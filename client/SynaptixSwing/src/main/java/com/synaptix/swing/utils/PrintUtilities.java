package com.synaptix.swing.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Arrays;
import java.util.List;

import javax.swing.RepaintManager;

import com.synaptix.view.swing.error.ErrorViewManager;

public class PrintUtilities implements Printable {

	private List<Component> componentsToBePrinted;

	public static void printComponent(List<Component> cs) {
		new PrintUtilities(cs).print();
	}

	public static void printComponent(Component cmp) {
		new PrintUtilities(Arrays.asList(cmp)).print();
	}

	public PrintUtilities(List<Component> componentsToBePrinted) {
		this.componentsToBePrinted = componentsToBePrinted;
	}

	public void print() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		if (printJob.printDialog()) {
			try {
				PageFormat pf = printJob.defaultPage();
				pf.setOrientation(PageFormat.LANDSCAPE);
				printJob.setPrintable(this, pf);
				printJob.print();
			} catch (PrinterException e) {
				ErrorViewManager.getInstance().showErrorDialog(null, e);
			}
		}
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex >= componentsToBePrinted.size()) {
			return (NO_SUCH_PAGE);
		} else {
			Component componentToBePrinted = (componentsToBePrinted.get(pageIndex));
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			double scaleh = pageFormat.getImageableHeight() / componentToBePrinted.getHeight();
			double scalew = pageFormat.getImageableWidth() / componentToBePrinted.getWidth();
			double scale = Math.min(scaleh, scalew);
			g2d.scale(scale, scale);
			componentToBePrinted.setVisible(true);
			disableDoubleBuffering(componentToBePrinted);
			componentToBePrinted.paint(g2d);
			enableDoubleBuffering(componentToBePrinted);
			return (PAGE_EXISTS);
		}
	}

	private void disableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}

	private void enableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}
}
