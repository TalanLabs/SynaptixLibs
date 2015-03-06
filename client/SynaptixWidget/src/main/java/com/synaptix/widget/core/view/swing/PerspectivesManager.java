package com.synaptix.widget.core.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.xml.sax.SAXException;

import com.synaptix.client.view.IView;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockableState.Location;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.event.DockableSelectionEvent;
import com.vlsolutions.swing.docking.event.DockableSelectionListener;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;

/**
 * Permet de gérer les perspectives du docking
 * 
 * @author Gaby
 * 
 */
public class PerspectivesManager {

	private SyDockingContext dockingContext;

	private final List<Perspective> defaultPerspectives = new ArrayList<Perspective>();

	private final List<Perspective> personalPerspectives = new ArrayList<Perspective>();

	public PerspectivesManager(SyDockingContext dockingContext) {
		super();

		this.dockingContext = dockingContext;

		UIManager.put("DockingDesktop.closeActionAccelerator", KeyStroke.getKeyStroke(87, InputEvent.CTRL_DOWN_MASK)); // ctrl + w
	}

	public final void clear() {
		defaultPerspectives.clear();
		personalPerspectives.clear();
	}

	/**
	 * Ajoute une perspective par défaut
	 * 
	 * @param id
	 * @param name
	 * @param xml
	 */
	public void addDefaultPerspective(String id, String name, String xml) {
		addDefaultPerspective(id, name, null, null, null, xml);
	}

	/**
	 * Ajoute une perspective par defaut
	 * 
	 * @param id
	 * @param name
	 * @param icon
	 * @param keyStroke
	 * @param description
	 * @param xml
	 */
	public void addDefaultPerspective(String id, String name, Icon icon, KeyStroke keyStroke, String description, String xml) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		if (name == null) {
			throw new NullArgumentException("name");
		}
		if (xml == null) {
			throw new NullArgumentException("xml");
		}
		if (getDefaultPerspectiveById(id) != null) {
			throw new IllegalArgumentException("Default perspective existe " + id);
		}
		defaultPerspectives.add(new PerspectiveImpl(id, name, icon, xml, keyStroke, description));
	}

	/**
	 * Ajoute une perspective par défaut
	 * 
	 * @param id
	 * @param name
	 * @param xml
	 */
	public void addPersonalPerspective(String id, String name, String xml) {
		addPersonalPerspective(id, name, null, null, null, xml);
	}

	/**
	 * Ajoute une perspective personelle
	 * 
	 * @param id
	 * @param name
	 * @param icon
	 * @param keyStroke
	 * @param xml
	 */
	public void addPersonalPerspective(String id, String name, Icon icon, KeyStroke keyStroke, String description, String xml) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		if (name == null) {
			throw new NullArgumentException("name");
		}
		if (xml == null) {
			throw new NullArgumentException("xml");
		}
		if (getDefaultPerspectiveById(id) != null) {
			throw new IllegalArgumentException("Default perspective existe " + id);
		}
		if (getPersonalPerspectiveById(id) != null) {
			throw new IllegalArgumentException("Personal perspective existe " + id);
		}
		personalPerspectives.add(new PerspectiveImpl(id, name, icon, xml, keyStroke, description));
		if (keyStroke != null) {
			registerStrokes();
		}
	}

	/**
	 * Efface une perspective personelle
	 * 
	 * @param id
	 */
	public void removePersonalPerspective(String id) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		PerspectiveImpl p = (PerspectiveImpl) getPersonalPerspectiveById(id);
		if (p != null) {
			personalPerspectives.remove(p);
		}
		registerStrokes();
	}

	/**
	 * Modifie une perspective
	 * 
	 * @param id
	 * @param name
	 * @param icon
	 * @param keyStroke
	 * @param xml
	 */
	public void modifyPersonalPerspective(String id, String name, Icon icon, KeyStroke keyStroke, String description, String xml) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		if (name == null) {
			throw new NullArgumentException("name");
		}
		if (xml == null) {
			throw new NullArgumentException("xml");
		}
		PerspectiveImpl p = (PerspectiveImpl) getPersonalPerspectiveById(id);
		if (p != null) {
			p.setName(name);
			p.setIcon(icon);
			p.setKeyStroke(keyStroke);
			p.setXml(xml);
			p.setDescription(description);
			if (keyStroke != null) {
				registerStrokes();
			}
		}
	}

	/**
	 * Donne toutes les perspectives par defaut
	 * 
	 * @return
	 */
	public List<Perspective> getDefaultPerspectives() {
		return Collections.unmodifiableList(defaultPerspectives);
	}

	/**
	 * Donne toutes les perspectives personelles
	 * 
	 * @return
	 */
	public List<Perspective> getPersonalPerspectives() {
		return Collections.unmodifiableList(personalPerspectives);
	}

	/**
	 * Retrouve une perspective par defaut par son id
	 * 
	 * @param id
	 * @return
	 */
	public Perspective getDefaultPerspectiveById(String id) {
		return getPerspectiveById(defaultPerspectives, id);
	}

	/**
	 * Retrouve une perspective personelle par son id
	 * 
	 * @param id
	 * @return
	 */
	public Perspective getPersonalPerspectiveById(String id) {
		return getPerspectiveById(personalPerspectives, id);
	}

	private Perspective getPerspectiveById(List<Perspective> perspectives, String id) {
		Perspective res = null;
		Iterator<Perspective> it = perspectives.iterator();
		while (it.hasNext() && res == null) {
			Perspective p = it.next();
			if (p.getId().equals(id)) {
				res = p;
			}
		}
		return res;
	}

	/**
	 * Active une perspective, qu'elle soit par defaut ou personelle. La defaut est prioritaire.
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void activePerspective(String id) {
		Perspective p = getDefaultPerspectiveById(id);
		if (p == null) {
			p = getPersonalPerspectiveById(id);
		}
		if (p != null) {
			setCurrentWorkspace(p.getXml());
		}
	}

	/**
	 * Sauvegarde le workspace courant dans une perspective personelle
	 * 
	 * @param id
	 * @param name
	 * @param icon
	 * @param keyStroke
	 * @param description
	 * @throws Exception
	 */
	public void saveCurrentPerspective(String id, String name, Icon icon, KeyStroke keyStroke, String description) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		if (name == null) {
			throw new NullArgumentException("name");
		}
		String xml = getCurrentWorkspace();
		addPersonalPerspective(id, name, icon, keyStroke, description, xml);
	}

	/**
	 * Renvoie le xml du workspace
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCurrentWorkspace() {
		String xml = null;
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			dockingContext.writeXML(out);
			xml = out.toString("UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return xml;
	}

	/**
	 * Change le workspace courant
	 * 
	 * @param xml
	 * @throws Exception
	 */
	public void setCurrentWorkspace(String xml) {
		if (xml != null) {
			if (dockingContext.closeAllDockables()) {
				ByteArrayInputStream in = null;
				try {
					in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
					dockingContext.readXML(in);
					((DockingDesktop) dockingContext.getDesktopList().get(0)).repaint();
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (ParserConfigurationException e) {
					throw new RuntimeException(e);
				} catch (SAXException e) {
					throw new RuntimeException(e);
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
	}

	public interface Perspective {

		public String getId();

		public String getName();

		public Icon getIcon();

		public String getXml();

		public KeyStroke getKeyStroke();

		public String getDescription();
	}

	private class PerspectiveImpl implements Perspective {
		private String id;

		private String name;

		private Icon icon;

		private String xml;

		private KeyStroke keyStroke;

		private String description;

		public PerspectiveImpl(String id, String name, Icon icon, String xml, KeyStroke keyStroke, String description) {
			super();
			this.id = id;
			this.name = name;
			this.icon = icon;
			this.xml = xml;
			this.keyStroke = keyStroke;
			this.description = description;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public Icon getIcon() {
			return icon;
		}

		public void setIcon(Icon icon) {
			this.icon = icon;
		}

		@Override
		public String getXml() {
			return xml;
		}

		public void setXml(String xml) {
			this.xml = xml;
		}

		@Override
		public KeyStroke getKeyStroke() {
			return keyStroke;
		}

		public void setKeyStroke(KeyStroke keyStroke) {
			this.keyStroke = keyStroke;
		}

		@Override
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * Starts by cleaning all strokes then register all for each perspective, if they have one
	 */
	public final void registerStrokes() {
		@SuppressWarnings("unchecked")
		ArrayList<DockingDesktop> desktopList = dockingContext.getDesktopList();
		if (desktopList != null) {

			List<Perspective> perspectives = new ArrayList<Perspective>();
			PerspectivesManager pm = dockingContext.getPerspectivesManager();
			if (pm != null) {
				if (pm.getDefaultPerspectives() != null) {
					perspectives.addAll(pm.getDefaultPerspectives());
				}
				if (pm.getPersonalPerspectives() != null) {
					perspectives.addAll(pm.getPersonalPerspectives());
				}
			}

			for (final DockingDesktop d : desktopList) {
				d.getInputMap().clear();
				d.getActionMap().clear();
				if (!perspectives.isEmpty()) {
					for (Perspective p : perspectives) {
						if (p.getKeyStroke() != null) {
							// The key is the keyStroke so that the overwrite works fine:
							// the priority is given to personal perspectives
							d.getActionMap().put(p.getKeyStroke(), new ShortcutAction(p));
							d.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(p.getKeyStroke(), p.getKeyStroke());
						}
					}
				}
			}
		}

		refreshStrokes();
	}

	private void refreshStrokes() {
		@SuppressWarnings("unchecked")
		List<DockingDesktop> desktopList = dockingContext.getDesktopList();
		if (desktopList != null) {

			for (final DockingDesktop d : desktopList) {
				final MyDockableSelectionListener listener = new MyDockableSelectionListener(d);

				d.addDockableSelectionListener(listener);
				d.addDockableStateChangeListener(listener);
				AbstractAction closeAction = new AbstractAction() {

					private static final long serialVersionUID = 4597596457233136194L;

					@Override
					public void actionPerformed(ActionEvent e) {
						Dockable selectedDockable = d.getSelectedDockable() != null ? d.getSelectedDockable() : listener.getSelectedDockable();
						if ((selectedDockable != null) && (d.getDockableState(selectedDockable) != null)) {
							if ((d.getDockableState(selectedDockable).isDocked()) && (selectedDockable.getDockKey().isCloseEnabled())) {
								d.close(selectedDockable);
							}
						}
					}
				};
				AbstractAction maximizeAction = new AbstractAction() {

					private static final long serialVersionUID = 2581009525709616660L;

					@Override
					public void actionPerformed(ActionEvent e) {
						Dockable selectedDockable = d.getSelectedDockable() != null ? d.getSelectedDockable() : listener.getSelectedDockable();
						if ((selectedDockable != null) && (d.getDockableState(selectedDockable) != null)) {
							Location location = d.getDockableState(selectedDockable).getLocation();
							if (location == DockableState.Location.DOCKED) {
								d.maximize(selectedDockable);
							} else if (location == DockableState.Location.MAXIMIZED) {
								d.restore(selectedDockable);
							}
						}
					}
				};
				AbstractAction dockAction = new AbstractAction() {

					private static final long serialVersionUID = 3518490860695190350L;

					@Override
					public void actionPerformed(ActionEvent e) {
						Dockable selectedDockable = d.getSelectedDockable() != null ? d.getSelectedDockable() : listener.getSelectedDockable();
						if ((selectedDockable != null) && (d.getDockableState(selectedDockable) != null)) {
							if (selectedDockable.getDockKey().isAutoHideEnabled()) {
								DockableState.Location location = selectedDockable.getDockKey().getLocation();
								if (location == DockableState.Location.DOCKED) {
									d.setAutoHide(selectedDockable, true);
								} else if (location == DockableState.Location.HIDDEN) {
									d.setAutoHide(selectedDockable, false);
								}
							}
						}
					}
				};
				AbstractAction floatAction = new AbstractAction() {

					private static final long serialVersionUID = 7004677406486110624L;

					@Override
					public void actionPerformed(ActionEvent e) {
						Dockable selectedDockable = d.getSelectedDockable() != null ? d.getSelectedDockable() : listener.getSelectedDockable();
						if ((selectedDockable != null) && (d.getDockableState(selectedDockable) != null)) {
							if (selectedDockable.getDockKey().isFloatEnabled()) {
								DockableState.Location location = selectedDockable.getDockKey().getLocation();
								if (location == DockableState.Location.DOCKED) {
									d.setFloating(selectedDockable, true);
								} else if (location == DockableState.Location.FLOATING) {
									d.setFloating(selectedDockable, false);
								}
							}
						}
					}
				};

				KeyStroke ks = (KeyStroke) UIManager.get("DockingDesktop.closeActionAccelerator");
				if (ks != null) {
					d.getActionMap().put("close", closeAction);
					d.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "close");
				}

				ks = (KeyStroke) UIManager.get("DockingDesktop.maximizeActionAccelerator");
				if (ks != null) {
					d.getActionMap().put("maximize", maximizeAction);
					d.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "maximize");
				}

				ks = (KeyStroke) UIManager.get("DockingDesktop.dockActionAccelerator");
				if (ks != null) {
					d.getActionMap().put("dock", dockAction);
					d.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "dock");
				}

				ks = (KeyStroke) UIManager.get("DockingDesktop.floatActionAccelerator");
				if (ks != null) {
					d.getActionMap().put("float", floatAction);
					d.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "float");
				}

				if (dockingContext.getFrontendContext() != null) {
					KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
					AbstractAction helpAction = new AbstractAction() {

						private static final long serialVersionUID = -5098975788276659645L;

						@Override
						public void actionPerformed(ActionEvent e) {
							Dockable selectedDockable = d.getSelectedDockable() != null ? d.getSelectedDockable() : listener.getSelectedDockable();
							if ((selectedDockable != null) && (selectedDockable.getComponent() instanceof IView)) {
								dockingContext.getFrontendContext().showHelp((IView) selectedDockable.getComponent(), selectedDockable.getDockKey().getKey());
							}
						}
					};

					d.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "help");
					d.getActionMap().put("help", helpAction);
				}
			}
		}
	}

	private class ShortcutAction extends AbstractAction {

		private static final long serialVersionUID = -3509886678680601879L;

		private Perspective p;

		private ShortcutAction(Perspective p) {
			this.p = p;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			PerspectivesManager pm = dockingContext.getPerspectivesManager();
			pm.activePerspective(p.getId());
		}
	};

	private final class MyDockableSelectionListener implements DockableSelectionListener, DockableStateChangeListener {

		private final DockingDesktop dockingDesktop;

		private final List<Dockable> selectedDockableList;

		public MyDockableSelectionListener(DockingDesktop d) {
			super();

			this.selectedDockableList = new ArrayList<Dockable>();
			this.dockingDesktop = d;
		}

		@Override
		public void selectionChanged(DockableSelectionEvent e) {
			selectedDockableList.remove(e.getSelectedDockable());
			DockableState dockableState = dockingDesktop.getDockableState(e.getSelectedDockable());
			if ((dockableState != null) && (dockableState.getLocation() == DockableState.Location.DOCKED)) {
				// selectedDockable = e.getSelectedDockable();
				selectedDockableList.add(e.getSelectedDockable());
			}
		}

		public Dockable getSelectedDockable() {
			for (int i = selectedDockableList.size() - 1; i >= 0; i--) {
				Dockable dockable = selectedDockableList.get(i);
				DockableState dockableState = dockingDesktop.getDockableState(dockable);
				if ((dockableState != null) && (dockableState.getLocation() == DockableState.Location.DOCKED)) {
					return dockable;
				}
			}
			Dockable dockable = dockingDesktop.getSelectedDockable();
			return dockable;
		}

		@Override
		public void dockableStateChanged(DockableStateChangeEvent event) {
			DockableState prevDockableState = event.getPreviousState();
			DockableState nextDockableState = event.getNewState();
			if ((prevDockableState != null) && (nextDockableState != null)) {
				if (prevDockableState.getLocation() != nextDockableState.getLocation()) {
					selectedDockableList.remove(event.getNewState().getDockable());
					if (nextDockableState.getLocation() == DockableState.Location.DOCKED) {
						selectedDockableList.add(nextDockableState.getDockable());
					}
				}
			}
		}
	}
}
