package com.synaptix.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.synaptix.swing.plaf.SyTableFooterUI;

public class JSyTableFooter extends JComponent implements Accessible {

	private static final long serialVersionUID = -802672022710877119L;

	private static final String uiClassID = "SyTableFooterUI"; //$NON-NLS-1$

	protected JTable table;

	private TableCellRenderer defaultRenderer;

	static {
		UIManager.getDefaults().put("SyTableFooterUI", //$NON-NLS-1$
				"com.synaptix.swing.plaf.basic.BasicSyTableFooterUI"); //$NON-NLS-1$
	}

	public JSyTableFooter() {
		super();

		setFocusable(false);

		initializeLocalVars();

		updateUI();

		// this.setPreferredSize(new Dimension(100,20));
		// this.setBorder(new LineBorder(Color.BLACK));
	}

	public void setTable(JTable table) {
		JTable old = this.table;
		this.table = table;
		firePropertyChange("table", old, table); //$NON-NLS-1$
	}

	/**
	 * Returns the table associated with this header.
	 * 
	 * @return the <code>table</code> property
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * Sets the default renderer to be used when no <code>headerRenderer</code>
	 * is defined by a <code>TableColumn</code>.
	 * 
	 * @param defaultRenderer
	 *            the default renderer
	 */
	public void setDefaultRenderer(TableCellRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	/**
	 * Returns the default renderer used when no <code>headerRenderer</code>
	 * is defined by a <code>TableColumn</code>.
	 * 
	 * @return the default renderer
	 */
	public TableCellRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	/**
	 * Returns the rectangle containing the header tile at <code>column</code>.
	 * When the <code>column</code> parameter is out of bounds this method
	 * uses the same conventions as the <code>JTable</code> method
	 * <code>getCellRect</code>.
	 * 
	 * @return the rectangle containing the header tile at <code>column</code>
	 * @see JTable#getCellRect
	 */
	public Rectangle getHeaderRect(int column) {
		Rectangle r = new Rectangle();
		TableColumnModel cm = table.getColumnModel();

		r.height = getHeight();

		if (column < 0) {
			// x = width = 0;
			if (!getComponentOrientation().isLeftToRight()) {
				r.x = getWidthInRightToLeft();
			}
		} else if (column >= cm.getColumnCount()) {
			if (getComponentOrientation().isLeftToRight()) {
				r.x = getWidth();
			}
		} else {
			for (int i = 0; i < column; i++) {
				r.x += cm.getColumn(i).getWidth();
			}
			if (!getComponentOrientation().isLeftToRight()) {
				r.x = getWidthInRightToLeft() - r.x
						- cm.getColumn(column).getWidth();
			}

			r.width = cm.getColumn(column).getWidth();
		}
		return r;
	}

	/**
	 * Allows the renderer's tips to be used if there is text set.
	 * 
	 * @param event
	 *            the location of the event identifies the proper renderer and,
	 *            therefore, the proper tip
	 * @return the tool tip for this component
	 */
	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();
		int column;

		// Locate the renderer under the event location
		if ((column = table.columnAtPoint(p)) != -1) {
			TableColumn aColumn = table.getColumnModel().getColumn(column);
			TableCellRenderer renderer = aColumn.getHeaderRenderer();
			if (renderer == null) {
				renderer = defaultRenderer;
			}
			Component component = renderer.getTableCellRendererComponent(
					getTable(), aColumn.getHeaderValue(), false, false, -1,
					column);

			// Now have to see if the component is a JComponent before
			// getting the tip
			if (component instanceof JComponent) {
				// Convert the event to the renderer's coordinate system
				MouseEvent newEvent;
				Rectangle cellRect = getHeaderRect(column);

				p.translate(-cellRect.x, -cellRect.y);
				newEvent = new MouseEvent(component, event.getID(), event
						.getWhen(), event.getModifiers(), p.x, p.y, event
						.getClickCount(), event.isPopupTrigger());

				tip = ((JComponent) component).getToolTipText(newEvent);
			}
		}

		// No tip from the renderer get our own tip
		if (tip == null)
			tip = getToolTipText();

		return tip;
	}

	//
	// Managing TableHeaderUI
	//

	/**
	 * Returns the look and feel (L&F) object that renders this component.
	 * 
	 * @return the <code>TableHeaderUI</code> object that renders this
	 *         component
	 */
	public SyTableFooterUI getUI() {
		return (SyTableFooterUI) ui;
	}

	/**
	 * Sets the look and feel (L&F) object that renders this component.
	 * 
	 * @param ui
	 *            the <code>TableHeaderUI</code> L&F object
	 * @see UIDefaults#getUI
	 */
	public void setUI(SyTableFooterUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	/**
	 * Notification from the <code>UIManager</code> that the look and feel
	 * (L&F) has changed. Replaces the current UI object with the latest version
	 * from the <code>UIManager</code>.
	 * 
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		setUI((SyTableFooterUI) UIManager.getUI(this));
		resizeAndRepaint();
		invalidate();// PENDING
	}

	/**
	 * Returns the suffix used to construct the name of the look and feel (L&F)
	 * class used to render this component.
	 * 
	 * @return the string "TableHeaderUI"
	 * 
	 * @return "TableHeaderUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	public String getUIClassID() {
		return uiClassID;
	}

	/**
	 * Returns a default renderer to be used when no header renderer is defined
	 * by a <code>TableColumn</code>.
	 * 
	 * @return the default table column renderer
	 */
	protected TableCellRenderer createDefaultRenderer() {
		DefaultTableCellRenderer label = new UIResourceTableCellRenderer();
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}

	private static class UIResourceTableCellRenderer extends
			DefaultTableCellRenderer implements UIResource {

		private static final long serialVersionUID = 4845024833857364993L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
			setBorder(UIManager.getBorder("TableHeader.cellBorder")); //$NON-NLS-1$
			return this;
		}
	}

	/**
	 * Initializes the local variables and properties with default values. Used
	 * by the constructor methods.
	 */
	protected void initializeLocalVars() {
		setOpaque(true);
		table = null;

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);
		setDefaultRenderer(createDefaultRenderer());
	}

	/**
	 * Sizes the header and marks it as needing display. Equivalent to
	 * <code>revalidate</code> followed by <code>repaint</code>.
	 */
	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	/**
	 * See <code>readObject</code> and <code>writeObject</code> in
	 * <code>JComponent</code> for more information about serialization in
	 * Swing.
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		if ((ui != null) && (getUIClassID().equals(uiClassID))) {
			ui.installUI(this);
		}
	}

	private int getWidthInRightToLeft() {
		if ((table != null)
				&& (table.getAutoResizeMode() != JTable.AUTO_RESIZE_OFF)) {
			return table.getWidth();
		}
		return super.getWidth();
	}

	/**
	 * Returns a string representation of this <code>JTableHeader</code>.
	 * This method is intended to be used only for debugging purposes, and the
	 * content and format of the returned string may vary between
	 * implementations. The returned string may be empty but may not be
	 * <code>null</code>.
	 * <P>
	 * Overriding <code>paramString</code> to provide information about the
	 * specific new aspects of the JFC components.
	 * 
	 * @return a string representation of this <code>JTableHeader</code>
	 */
	protected String paramString() {
		return super.paramString();
	}

	// ///////////////
	// Accessibility support
	// //////////////

	/**
	 * Gets the AccessibleContext associated with this JTableHeader. For
	 * JTableHeaders, the AccessibleContext takes the form of an
	 * AccessibleJTableHeader. A new AccessibleJTableHeader instance is created
	 * if necessary.
	 * 
	 * @return an AccessibleJTableHeader that serves as the AccessibleContext of
	 *         this JTableHeader
	 */
	public AccessibleContext getAccessibleContext() {
		if (accessibleContext == null) {
			accessibleContext = new AccessibleJTableHeader();
		}
		return accessibleContext;
	}

	//
	// *** should also implement AccessibleSelection?
	// *** and what's up with keyboard navigation/manipulation?
	//
	/**
	 * This class implements accessibility support for the
	 * <code>JTableHeader</code> class. It provides an implementation of the
	 * Java Accessibility API appropriate to table header user-interface
	 * elements.
	 * <p>
	 * <strong>Warning:</strong> Serialized objects of this class will not be
	 * compatible with future Swing releases. The current serialization support
	 * is appropriate for short term storage or RMI between applications running
	 * the same version of Swing. As of 1.4, support for long term storage of
	 * all JavaBeans<sup><font size="-2">TM</font></sup> has been added to
	 * the <code>java.beans</code> package. Please see
	 * {@link java.beans.XMLEncoder}.
	 */
	protected class AccessibleJTableHeader extends AccessibleJComponent {

		private static final long serialVersionUID = -6183354962460428347L;

		/**
		 * Get the role of this object.
		 * 
		 * @return an instance of AccessibleRole describing the role of the
		 *         object
		 * @see AccessibleRole
		 */
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.PANEL;
		}

		/**
		 * Returns the Accessible child, if one exists, contained at the local
		 * coordinate Point.
		 * 
		 * @param p
		 *            The point defining the top-left corner of the Accessible,
		 *            given in the coordinate space of the object's parent.
		 * @return the Accessible, if it exists, at the specified location; else
		 *         null
		 */
		public Accessible getAccessibleAt(Point p) {
			int column;

			// Locate the renderer under the Point
			if ((column = JSyTableFooter.this.table.columnAtPoint(p)) != -1) {
				TableColumn aColumn = JSyTableFooter.this.table.getColumnModel()
						.getColumn(column);
				TableCellRenderer renderer = aColumn.getHeaderRenderer();
				if (renderer == null) {
					if (defaultRenderer != null) {
						renderer = defaultRenderer;
					} else {
						return null;
					}
				}
				Component component = renderer.getTableCellRendererComponent(
						JSyTableFooter.this.getTable(),
						aColumn.getHeaderValue(), false, false, -1, column);

				return new AccessibleJTableHeaderEntry(column,
						JSyTableFooter.this, JSyTableFooter.this.table);
			} else {
				return null;
			}
		}

		/**
		 * Returns the number of accessible children in the object. If all of
		 * the children of this object implement Accessible, than this method
		 * should return the number of children of this object.
		 * 
		 * @return the number of accessible children in the object.
		 */
		public int getAccessibleChildrenCount() {
			return JSyTableFooter.this.table.getColumnModel().getColumnCount();
		}

		/**
		 * Return the nth Accessible child of the object.
		 * 
		 * @param i
		 *            zero-based index of child
		 * @return the nth Accessible child of the object
		 */
		public Accessible getAccessibleChild(int i) {
			if (i < 0 || i >= getAccessibleChildrenCount()) {
				return null;
			} else {
				TableColumn aColumn = JSyTableFooter.this.table.getColumnModel()
						.getColumn(i);
				TableCellRenderer renderer = aColumn.getHeaderRenderer();
				if (renderer == null) {
					if (defaultRenderer != null) {
						renderer = defaultRenderer;
					} else {
						return null;
					}
				}
				Component component = renderer.getTableCellRendererComponent(
						JSyTableFooter.this.getTable(),
						aColumn.getHeaderValue(), false, false, -1, i);

				return new AccessibleJTableHeaderEntry(i, JSyTableFooter.this,
						JSyTableFooter.this.table);
			}
		}

		/**
		 * This class provides an implementation of the Java Accessibility API
		 * appropropriate for JTableHeader entries.
		 */
		protected class AccessibleJTableHeaderEntry extends AccessibleContext
				implements Accessible, AccessibleComponent {

			private JSyTableFooter parent;

			private int column;

			private JTable table;

			/**
			 * Constructs an AccessiblJTableHeaaderEntry
			 */
			public AccessibleJTableHeaderEntry(int c, JSyTableFooter p, JTable t) {
				parent = p;
				column = c;
				table = t;
				this.setAccessibleParent(parent);
			}

			/**
			 * Get the AccessibleContext associated with this object. In the
			 * implementation of the Java Accessibility API for this class,
			 * returns this object, which serves as its own AccessibleContext.
			 * 
			 * @return this object
			 */
			public AccessibleContext getAccessibleContext() {
				return this;
			}

			private AccessibleContext getCurrentAccessibleContext() {
				TableColumnModel tcm = table.getColumnModel();
				if (tcm != null) {
					// Fixes 4772355 - ArrayOutOfBoundsException in
					// JTableHeader
					if (column < 0 || column >= tcm.getColumnCount()) {
						return null;
					}
					TableColumn aColumn = tcm.getColumn(column);
					TableCellRenderer renderer = aColumn.getHeaderRenderer();
					if (renderer == null) {
						if (defaultRenderer != null) {
							renderer = defaultRenderer;
						} else {
							return null;
						}
					}
					Component c = renderer
							.getTableCellRendererComponent(JSyTableFooter.this
									.getTable(), aColumn.getHeaderValue(),
									false, false, -1, column);
					if (c instanceof Accessible) {
						return ((Accessible) c).getAccessibleContext();
					}
				}
				return null;
			}

			private Component getCurrentComponent() {
				TableColumnModel tcm = table.getColumnModel();
				if (tcm != null) {
					// Fixes 4772355 - ArrayOutOfBoundsException in
					// JTableHeader
					if (column < 0 || column >= tcm.getColumnCount()) {
						return null;
					}
					TableColumn aColumn = tcm.getColumn(column);
					TableCellRenderer renderer = aColumn.getHeaderRenderer();
					if (renderer == null) {
						if (defaultRenderer != null) {
							renderer = defaultRenderer;
						} else {
							return null;
						}
					}
					return renderer
							.getTableCellRendererComponent(JSyTableFooter.this
									.getTable(), aColumn.getHeaderValue(),
									false, false, -1, column);
				} else {
					return null;
				}
			}

			// AccessibleContext methods

			public String getAccessibleName() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					String name = ac.getAccessibleName();
					if ((name != null) && (name != "")) { //$NON-NLS-1$
						return ac.getAccessibleName();
					}
				}
				if ((accessibleName != null) && (accessibleName != "")) { //$NON-NLS-1$
					return accessibleName;
				} else {
					return table.getColumnName(column);
				}
			}

			public void setAccessibleName(String s) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					ac.setAccessibleName(s);
				} else {
					super.setAccessibleName(s);
				}
			}

			//
			// *** should check toolTip text for desc. (needs MouseEvent)
			//
			public String getAccessibleDescription() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					return ac.getAccessibleDescription();
				} else {
					return super.getAccessibleDescription();
				}
			}

			public void setAccessibleDescription(String s) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					ac.setAccessibleDescription(s);
				} else {
					super.setAccessibleDescription(s);
				}
			}

			public AccessibleRole getAccessibleRole() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					return ac.getAccessibleRole();
				} else {
					return AccessibleRole.COLUMN_HEADER;
				}
			}

			public AccessibleStateSet getAccessibleStateSet() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					AccessibleStateSet states = ac.getAccessibleStateSet();
					if (isShowing()) {
						states.add(AccessibleState.SHOWING);
					}
					return states;
				} else {
					return new AccessibleStateSet(); // must be non null?
				}
			}

			public int getAccessibleIndexInParent() {
				return column;
			}

			public int getAccessibleChildrenCount() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					return ac.getAccessibleChildrenCount();
				} else {
					return 0;
				}
			}

			public Accessible getAccessibleChild(int i) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					Accessible accessibleChild = ac.getAccessibleChild(i);
					ac.setAccessibleParent(this);
					return accessibleChild;
				} else {
					return null;
				}
			}

			public Locale getLocale() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					return ac.getLocale();
				} else {
					return null;
				}
			}

			public void addPropertyChangeListener(PropertyChangeListener l) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					ac.addPropertyChangeListener(l);
				} else {
					super.addPropertyChangeListener(l);
				}
			}

			public void removePropertyChangeListener(PropertyChangeListener l) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					ac.removePropertyChangeListener(l);
				} else {
					super.removePropertyChangeListener(l);
				}
			}

			public AccessibleAction getAccessibleAction() {
				return getCurrentAccessibleContext().getAccessibleAction();
			}

			/**
			 * Get the AccessibleComponent associated with this object. In the
			 * implementation of the Java Accessibility API for this class,
			 * return this object, which is responsible for implementing the
			 * AccessibleComponent interface on behalf of itself.
			 * 
			 * @return this object
			 */
			public AccessibleComponent getAccessibleComponent() {
				return this; // to override getBounds()
			}

			public AccessibleSelection getAccessibleSelection() {
				return getCurrentAccessibleContext().getAccessibleSelection();
			}

			public AccessibleText getAccessibleText() {
				return getCurrentAccessibleContext().getAccessibleText();
			}

			public AccessibleValue getAccessibleValue() {
				return getCurrentAccessibleContext().getAccessibleValue();
			}

			// AccessibleComponent methods

			public Color getBackground() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getBackground();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getBackground();
					} else {
						return null;
					}
				}
			}

			public void setBackground(Color c) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setBackground(c);
				} else {
					Component cp = getCurrentComponent();
					if (cp != null) {
						cp.setBackground(c);
					}
				}
			}

			public Color getForeground() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getForeground();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getForeground();
					} else {
						return null;
					}
				}
			}

			public void setForeground(Color c) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setForeground(c);
				} else {
					Component cp = getCurrentComponent();
					if (cp != null) {
						cp.setForeground(c);
					}
				}
			}

			public Cursor getCursor() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getCursor();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getCursor();
					} else {
						Accessible ap = getAccessibleParent();
						if (ap instanceof AccessibleComponent) {
							return ((AccessibleComponent) ap).getCursor();
						} else {
							return null;
						}
					}
				}
			}

			public void setCursor(Cursor c) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setCursor(c);
				} else {
					Component cp = getCurrentComponent();
					if (cp != null) {
						cp.setCursor(c);
					}
				}
			}

			public Font getFont() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getFont();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getFont();
					} else {
						return null;
					}
				}
			}

			public void setFont(Font f) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setFont(f);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setFont(f);
					}
				}
			}

			public FontMetrics getFontMetrics(Font f) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getFontMetrics(f);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getFontMetrics(f);
					} else {
						return null;
					}
				}
			}

			public boolean isEnabled() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).isEnabled();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.isEnabled();
					} else {
						return false;
					}
				}
			}

			public void setEnabled(boolean b) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setEnabled(b);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setEnabled(b);
					}
				}
			}

			public boolean isVisible() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).isVisible();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.isVisible();
					} else {
						return false;
					}
				}
			}

			public void setVisible(boolean b) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setVisible(b);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setVisible(b);
					}
				}
			}

			public boolean isShowing() {
				if (isVisible() && JSyTableFooter.this.isShowing()) {
					return true;
				} else {
					return false;
				}
			}

			public boolean contains(Point p) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					Rectangle r = ((AccessibleComponent) ac).getBounds();
					return r.contains(p);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						Rectangle r = c.getBounds();
						return r.contains(p);
					} else {
						return getBounds().contains(p);
					}
				}
			}

			public Point getLocationOnScreen() {
				if (parent != null) {
					Point parentLocation = parent.getLocationOnScreen();
					Point componentLocation = getLocation();
					componentLocation.translate(parentLocation.x,
							parentLocation.y);
					return componentLocation;
				} else {
					return null;
				}
			}

			public Point getLocation() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					Rectangle r = ((AccessibleComponent) ac).getBounds();
					return r.getLocation();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						Rectangle r = c.getBounds();
						return r.getLocation();
					} else {
						return getBounds().getLocation();
					}
				}
			}

			public void setLocation(Point p) {
				// if ((parent != null) && (parent.contains(p))) {
				// ensureIndexIsVisible(indexInParent);
				// }
			}

			public Rectangle getBounds() {
				Rectangle r = table.getCellRect(-1, column, false);
				r.y = 0;
				return r;

				// AccessibleContext ac = getCurrentAccessibleContext();
				// if (ac instanceof AccessibleComponent) {
				// return ((AccessibleComponent) ac).getBounds();
				// } else {
				// Component c = getCurrentComponent();
				// if (c != null) {
				// return c.getBounds();
				// } else {
				// Rectangle r = table.getCellRect(-1, column, false);
				// r.y = 0;
				// return r;
				// }
				// }
			}

			public void setBounds(Rectangle r) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setBounds(r);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setBounds(r);
					}
				}
			}

			public Dimension getSize() {
				return getBounds().getSize();
				// AccessibleContext ac = getCurrentAccessibleContext();
				// if (ac instanceof AccessibleComponent) {
				// Rectangle r = ((AccessibleComponent) ac).getBounds();
				// return r.getSize();
				// } else {
				// Component c = getCurrentComponent();
				// if (c != null) {
				// Rectangle r = c.getBounds();
				// return r.getSize();
				// } else {
				// return getBounds().getSize();
				// }
				// }
			}

			public void setSize(Dimension d) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setSize(d);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setSize(d);
					}
				}
			}

			public Accessible getAccessibleAt(Point p) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getAccessibleAt(p);
				} else {
					return null;
				}
			}

			public boolean isFocusTraversable() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).isFocusTraversable();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.isFocusTraversable();
					} else {
						return false;
					}
				}
			}

			public void requestFocus() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).requestFocus();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.requestFocus();
					}
				}
			}

			public void addFocusListener(FocusListener l) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).addFocusListener(l);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.addFocusListener(l);
					}
				}
			}

			public void removeFocusListener(FocusListener l) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).removeFocusListener(l);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.removeFocusListener(l);
					}
				}
			}

		} // inner class AccessibleJTableHeaderElement

	} // inner class AccessibleJTableHeader

} // End of Class JTableHeader
