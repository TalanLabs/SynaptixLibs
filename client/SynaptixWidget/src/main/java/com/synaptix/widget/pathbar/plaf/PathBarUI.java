package com.synaptix.widget.pathbar.plaf;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.plaf.ComponentUI;

import com.synaptix.widget.pathbar.JPathBar;

public abstract class PathBarUI extends ComponentUI {

	public abstract int indexAtPoint(JPathBar pathBar, Point p);

	public abstract Rectangle getCellRect(JPathBar pathBar, int index);

}
