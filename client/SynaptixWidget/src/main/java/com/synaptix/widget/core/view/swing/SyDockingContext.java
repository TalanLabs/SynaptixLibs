package com.synaptix.widget.core.view.swing;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.synaptix.widget.core.controller.ISimpleFrontendContext;
import com.synaptix.widget.view.swing.EmptyDockablePanel;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockingContext;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.TabbedDockableContainer;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;
import com.vlsolutions.swing.docking.event.DockableStateWillChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateWillChangeListener;

public class SyDockingContext extends DockingContext implements DockableStateWillChangeListener, DockableStateChangeListener {

	private Map<Dockable, List<DockableStateWillChangeListener>> dockableStateWillChangeListenersMap = new HashMap<Dockable, List<DockableStateWillChangeListener>>();

	private Map<Dockable, List<DockableStateChangeListener>> dockableStateChangeListenersMap = new HashMap<Dockable, List<DockableStateChangeListener>>();

	private PerspectivesManager perspectivesManager;

	private ISimpleFrontendContext frontendContext;

	@Inject
	public SyDockingContext() {
		super();

		this.addDockableStateWillChangeListener(this);
		this.addDockableStateChangeListener(this);

		perspectivesManager = new PerspectivesManager(this);
	}

	public final void setFrontendContext(ISimpleFrontendContext frontendContext) {
		this.frontendContext = frontendContext;

		perspectivesManager.registerStrokes();
	}

	public final ISimpleFrontendContext getFrontendContext() {
		return frontendContext;
	}

	/**
	 * Show dockable by dockKey
	 * 
	 * @param dockKey
	 */
	public void showDockable(String dockKey) {
		Dockable d = this.getDockableByKey(dockKey);
		if (d != null) {
			_showDockable(d);
		}
	}

	@Override
	public Dockable getDockableByKey(String dockKey) {
		Dockable dockable = super.getDockableByKey(dockKey);
		if (dockable == null) {
			EmptyDockablePanel emptyDockable = new EmptyDockablePanel(dockKey);
			emptyDockable.initializeDockingContext(this);
			dockable = emptyDockable;
		}
		return dockable;
	}

	/**
	 * Show dockable
	 * 
	 * @param dockable
	 */
	public void showDockable(IDockable dockable) {
		if (dockable != null) {
			_showDockable(dockable);
		}
	}

	/**
	 * Restaure tous les dock qui sont maximisés
	 */
	public void restoreAllDockables() {
		DockableState[] dockableStates = this.getDockables();
		if (dockableStates != null && dockableStates.length > 0) {
			for (DockableState dockableState : this.getDockables()) {
				if (dockableState.isMaximized()) {
					dockableState.getDesktop().restore(dockableState.getDockable());
				}
			}
		}
	}

	private void _showDockable(Dockable dockable) {
		restoreAllDockables();

		DockableState dockableState = this.getDockableState(dockable);
		if (dockableState == null) {
			this.registerDockable(dockable);
			dockableState = this.getDockableState(dockable);
		}
		if (dockableState.isClosed()) {
			if ((this.getDesktopList() != null) && (this.getDesktopList().size() > 0)) {
				DockingDesktop dockingDesktop = (DockingDesktop) this.getDesktopList().get(0);
				for (DockableState s : this.getDockables()) {
					if (!s.isClosed()) {
						dockingDesktop.createTab(s.getDockable(), dockable, 999, true);
						return;
					}
				}
				dockingDesktop.addDockable(dockable);
			}
		}
		Component c = dockable.getComponent();

		if (c != null && c.getParent() != null && c.getParent().getParent() instanceof TabbedDockableContainer) {
			TabbedDockableContainer tab = (TabbedDockableContainer) c.getParent().getParent();
			tab.setSelectedDockable(dockable);
		} else {
			c.requestFocus();
		}
	}

	public void addDockableStateWillChangeListener(Dockable dockable, DockableStateWillChangeListener l) {
		List<DockableStateWillChangeListener> ls = dockableStateWillChangeListenersMap.get(dockable);
		if (ls == null) {
			ls = new ArrayList<DockableStateWillChangeListener>();
			dockableStateWillChangeListenersMap.put(dockable, ls);
		}
		ls.add(l);
	}

	public void removeDockableStateWillChangeListener(Dockable dockable, DockableStateWillChangeListener l) {
		List<DockableStateWillChangeListener> ls = dockableStateWillChangeListenersMap.get(dockable);
		if (ls != null) {
			ls.remove(l);
		}
	}

	public void addDockableStateChangeListener(Dockable dockable, DockableStateChangeListener l) {
		List<DockableStateChangeListener> ls = dockableStateChangeListenersMap.get(dockable);
		if (ls == null) {
			ls = new ArrayList<DockableStateChangeListener>();
			dockableStateChangeListenersMap.put(dockable, ls);
		}
		ls.add(l);
	}

	public void removeDockableStateChangeListener(Dockable dockable, DockableStateChangeListener l) {
		List<DockableStateChangeListener> ls = dockableStateChangeListenersMap.get(dockable);
		if (ls != null) {
			ls.remove(l);
		}
	}

	@Override
	public void dockableStateWillChange(DockableStateWillChangeEvent event) {
		if (event.getCurrentState() != null) {
			Dockable d = event.getCurrentState().getDockable();
			List<DockableStateWillChangeListener> ls = dockableStateWillChangeListenersMap.get(d);
			if (ls != null) {
				for (int i = 0; i < ls.size(); i++) {
					DockableStateWillChangeListener listener = ls.get(i);
					listener.dockableStateWillChange(event);
					if (!event.isAccepted()) {
						return;
					}
				}
			}
		}
	}

	@Override
	public void dockableStateChanged(DockableStateChangeEvent event) {
		Dockable d = event.getNewState().getDockable();
		List<DockableStateChangeListener> ls = dockableStateChangeListenersMap.get(d);
		if (ls != null) {
			for (int i = 0; i < ls.size(); i++) {
				DockableStateChangeListener listener = ls.get(i);
				listener.dockableStateChanged(event);
			}
		}
	}

	/**
	 * Donne accès à la gestion des perspectives
	 * 
	 * @return
	 */
	public PerspectivesManager getPerspectivesManager() {
		return perspectivesManager;
	}

	/**
	 * Permet de fermer tous les dockables encore ouvert
	 * 
	 * @return false si un dock n'a pas été fermé
	 */
	public boolean closeAllDockables() {
		for (DockableState ds : this.getDockables()) {
			if (!ds.isClosed()) {
				Dockable dockable = ds.getDockable();
				this.close(dockable);
				DockableState ds2 = this.getDockableState(dockable);
				if (ds2 != null && !ds2.isClosed()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void addDesktop(DockingDesktop desktop) {
		super.addDesktop(desktop);

		perspectivesManager.registerStrokes();
	}

	/**
	 * Clear desktops and perspectives
	 */
	public void clearAll() {
		@SuppressWarnings("unchecked")
		List<DockingDesktop> desktopList = new ArrayList<DockingDesktop>(getDesktopList());
		for (DockingDesktop desktop : desktopList) {
			removeDesktop(desktop);
		}
		getPerspectivesManager().clear();
	}
}
