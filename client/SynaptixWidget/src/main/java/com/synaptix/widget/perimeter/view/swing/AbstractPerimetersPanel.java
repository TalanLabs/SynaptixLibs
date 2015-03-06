package com.synaptix.widget.perimeter.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.VerticalLayout;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.IView;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.JArrowScrollPane;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.ButtonHelper;
import com.synaptix.swing.utils.Utils;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.actions.view.swing.AbstractClearAction;
import com.synaptix.widget.actions.view.swing.AbstractDeleteAction;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.JArrowButtonPanel;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;
import com.synaptix.widget.view.swing.helper.StaticImage;
import com.synaptix.widget.view.swing.helper.ToolBarActionsBuilder;

public abstract class AbstractPerimetersPanel extends WaitComponentFeedbackPanel implements IView {

	private static final Log LOG = LogFactory.getLog(AbstractPerimetersPanel.class);

	private static final long serialVersionUID = 8828107132674356505L;

	private static final Icon FAVORITE_ICON = StaticImage.getImageScale(new ImageIcon(AbstractPerimetersPanel.class.getResource("/com/synaptix/widget/actions/view/swing/iconFavorite.png")), 16); //$NON-NLS-1$

	private static final Icon MINIMIZE = StaticImage.getImageScale(new ImageIcon(AbstractPerimetersPanel.class.getResource("/com/synaptix/widget/actions/view/swing/iconMinimize.gif")), 16); //$NON-NLS-1$

	private static final Icon MINI_MINUS = StaticImage.getImageScale(new ImageIcon(AbstractPerimetersPanel.class.getResource("/com/synaptix/widget/actions/view/swing/iconMiniMoins.png")), 10); //$NON-NLS-1$

	private static final Icon MINI_PLUS = StaticImage.getImageScale(new ImageIcon(AbstractPerimetersPanel.class.getResource("/com/synaptix/widget/actions/view/swing/iconMiniPlus.png")), 10); //$NON-NLS-1$

	private MyPanel panel;

	private JXCollapsiblePane collapsiblePane;

	private JArrowButtonPanel showButton;

	private Action clearAction;

	private Map<String, IPerimeterWidget> perimetreWidgetMap;

	private Map<String, CollapsibleWidget> collapsibleWidgetMap;

	private MyPerimetreWidgetListener perimetreWidgetListener;

	private int direction;

	private int gap;

	private MyCollapseWidgetActionListener collapseWidgetActionListener;

	private String id;

	private Action addFavoriteAction;

	private Action deleteFavoriteAction;

	private Action showFavoritesAction;

	private Action clearFiltersAction;

	private Action expandAllAction;

	private Action collapseAllAction;

	private JPopupMenu favorisPopupMenu;

	private JComponent centerComponent;

	private List<MyFavorite> defaultFavoriteList;

	private List<MyFavorite> userFavoriteList;

	private JButton showFavoritesButton;

	protected static final String REFRESH = "refresh";

	protected static final String VALUE_CHANGED = "valueChanged";

	private PanelBuilder builder;

	private MinimizeButton minimizeButton;

	private JComponent header;

	public AbstractPerimetersPanel(String id) {
		this(id, SwingConstants.LEFT, 0);
	}

	public AbstractPerimetersPanel(String id, int direction) {
		this(id, direction, 0);
	}

	public AbstractPerimetersPanel(String id, int direction, int gap) {
		super();

		this.id = id;
		this.direction = direction;
		this.gap = gap;
		this.perimetreWidgetMap = new HashMap<String, IPerimeterWidget>();
		this.collapsibleWidgetMap = new HashMap<String, CollapsibleWidget>();
		this.defaultFavoriteList = new ArrayList<MyFavorite>();
		this.userFavoriteList = new ArrayList<MyFavorite>();
	}

	protected final void initialize() {
		initActions();
		initComponents();

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(buildContents(), BorderLayout.CENTER);
		this.addContent(panel);
	}

	private void initActions() {
		addFavoriteAction = new AddFavoriteAction();
		addFavoriteAction.setEnabled(false);
		deleteFavoriteAction = new DeleteFavoriteAction();
		deleteFavoriteAction.setEnabled(false);

		showFavoritesAction = new ShowFavoritesAction();

		clearAction = new ClearAction();
		clearAction.setEnabled(false);

		clearFiltersAction = new ClearFiltersAction();
		clearFiltersAction.setEnabled(false);

		expandAllAction = new ExpandAllAction();

		collapseAllAction = new CollapseAllAction();
	}

	protected abstract void createSubComponents();

	private void createComponents() {
		createSubComponents();

		showFavoritesButton = new JButton(showFavoritesAction);
		minimizeButton = new MinimizeButton();
		collapsiblePane = new JXCollapsiblePane(JXCollapsiblePane.Direction.RIGHT);
		showButton = new JArrowButtonPanel(getPerimeterTitle(), direction);
		showButton.showIcons(false);

		header = buildHeader();
		header.setVisible(collapsiblePane.isCollapsed());
		collapsiblePane.addPropertyChangeListener("collapsed", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				header.setVisible((Boolean) evt.getNewValue());
			}
		});

		panel = new MyPanel();

		perimetreWidgetListener = new MyPerimetreWidgetListener();

		collapseWidgetActionListener = new MyCollapseWidgetActionListener();

		favorisPopupMenu = new JPopupMenu();

		centerComponent = buildCenter();
	}

	protected String getPerimeterTitle() {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().filters();
	}

	private void initComponents() {
		createComponents();

		collapsiblePane.setAnimated(false);

		collapsiblePane.add(centerComponent);

		showButton.addActionListener(new MyActionListener());

		loadUserFavorites();
		updateButtons();
	}

	private JComponent buildContents() {
		JPanel p = new JPanel(new BorderLayout());
		p.add(collapsiblePane, BorderLayout.CENTER);
		p.add(header, direction == SwingConstants.LEFT ? BorderLayout.EAST : BorderLayout.WEST);
		return p;
	}

	private JComponent buildHeader() {
		FormLayout layout = new FormLayout("FILL:24PX", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,2DLU,FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new JButton(showFavoritesAction), cc.xy(1, 1));
		builder.add(showButton, cc.xy(1, 3));
		// builder.add(new JButton(clearAction), cc.xy(1, 3));
		return builder.getPanel();
	}

	protected final JComponent getPanel() {
		return panel;
	}

	protected JComponent buildCenter() {
		FormLayout layout = new FormLayout("FILL:147DLU:GROW(1.0),3DLU,FILL:10DLU:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(getActionToolbar(), cc.xy(1, 1));
		builder.add(minimizeButton, cc.xy(3, 1));
		builder.add(new JArrowScrollPane(getPanel()), cc.xyw(1, 3, 3));
		return builder.getPanel();
	}

	private JComponent getActionToolbar() {
		ToolBarActionsBuilder actionsBuilder = new ToolBarActionsBuilder();
		addActions(actionsBuilder);
		actionsBuilder.addSeparator();
		actionsBuilder.addComponent(showFavoritesButton);
		actionsBuilder.addAction(clearAction);
		return actionsBuilder.build();
	}

	protected void addActions(IToolBarActionsBuilder toolbarActionsBuilder) {
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireStateChanged() {
		fireStateChanged(this);
	}

	private void fireStateChanged(Object source) {
		ChangeListener[] ls = listenerList.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(source);
		for (ChangeListener l : ls) {
			l.stateChanged(e);
		}
	}

	protected void addRefreshListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	protected void removeRefreshListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	protected void fireRefreshAction() {
		ActionListener[] as = listenerList.getListeners(ActionListener.class);
		ActionEvent e = new ActionEvent(this, 0, REFRESH);
		for (ActionListener l : as) {
			l.actionPerformed(e);
		}
	}

	/**
	 * Ajoute un filtre
	 * 
	 * @param id
	 * @param perimeterWidget
	 */
	public void addPerimetreWidget(String id, IPerimeterWidget perimeterWidget) {
		perimetreWidgetMap.put(id, perimeterWidget);

		CollapsibleWidget cw = new CollapsibleWidget(perimeterWidget);
		cw.addActionListener(collapseWidgetActionListener);
		panel.add(cw);

		collapsibleWidgetMap.put(id, cw);

		perimeterWidget.addPerimetreWidgetListener(perimetreWidgetListener);

		loadStats();

		panel.revalidate();
		panel.repaint();
	}

	protected final void removeAllWidgets() {
		for (IPerimeterWidget perimeterWidget : perimetreWidgetMap.values()) {
			perimeterWidget.removePerimetreWidgetListener(perimetreWidgetListener);
		}
		perimetreWidgetMap.clear();

		for (CollapsibleWidget cw : collapsibleWidgetMap.values()) {
			cw.removeActionListener(collapseWidgetActionListener);
		}
		collapsibleWidgetMap.clear();

		if (panel != null) {
			panel.removeAll();

			panel.revalidate();
			panel.repaint();
		}
	}

	public void addSeparator() {
		panel.add(builder.getComponentFactory().createSeparator("", SwingConstants.LEFT));
	}

	/**
	 * Efface les valeurs des filtres
	 */
	public void clear() {
		clear(false);
	}

	public void clear(boolean refresh) {
		for (IPerimeterWidget perimetreWidget : perimetreWidgetMap.values()) {
			perimetreWidget.removePerimetreWidgetListener(perimetreWidgetListener);
			perimetreWidget.setValue(null);
			perimetreWidget.addPerimetreWidgetListener(perimetreWidgetListener);
		}

		updateButtons();

		fireStateChanged();
		if (refresh) {
			fireRefreshAction();
		}
	}

	public void expandAll() {
		for (CollapsibleWidget widget : collapsibleWidgetMap.values()) {
			widget.setCollapsed(false);
		}
		saveStats();
	}

	public void collapseAll() {
		for (CollapsibleWidget widget : collapsibleWidgetMap.values()) {
			widget.setCollapsed(true);
		}
		saveStats();
	}

	/**
	 * Remplace la valeur des filtres
	 * 
	 * @param filtersMap
	 */
	public void setFiltersMap(Map<String, Object> filtersMap) {
		if (filtersMap != null && !filtersMap.isEmpty()) {
			for (Entry<String, IPerimeterWidget> entry : perimetreWidgetMap.entrySet()) {
				IPerimeterWidget perimetreWidget = entry.getValue();
				perimetreWidget.removePerimetreWidgetListener(perimetreWidgetListener);
				entry.getValue().setValue(filtersMap.get(entry.getKey()));
				perimetreWidget.addPerimetreWidgetListener(perimetreWidgetListener);
			}

			updateButtons();

			fireStateChanged();
		} else {
			clear();
		}
	}

	/**
	 * Set a value for a given perimeter
	 * 
	 * @param filterKey
	 * @param value
	 */
	public final void setFilterValue(String filterKey, Object value) {
		for (Entry<String, IPerimeterWidget> entry : perimetreWidgetMap.entrySet()) {
			if (entry.getKey().equals(filterKey)) {
				IPerimeterWidget perimetreWidget = entry.getValue();
				perimetreWidget.setValue(value);
			}
		}
	}

	/**
	 * Renvoie une map des valeurs des filtres
	 * 
	 * @return
	 */
	public Map<String, Object> getFiltersMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		for (Entry<String, IPerimeterWidget> entry : perimetreWidgetMap.entrySet()) {
			res.put(entry.getKey(), entry.getValue().getValue());
		}
		return res;
	}

	/**
	 * Return the final filter map, which uses {@link IComputedPerimeter} to compute some values.
	 * 
	 * @return
	 */
	public Map<String, Object> getFinalFiltersMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		for (Entry<String, IPerimeterWidget> entry : perimetreWidgetMap.entrySet()) {
			if (IComputedPerimeter.class.isAssignableFrom(entry.getValue().getClass())) {
				res.put(entry.getKey(), ((IComputedPerimeter) entry.getValue()).getFinalValue());
			} else {
				res.put(entry.getKey(), entry.getValue().getValue());
			}
		}
		return res;
	}

	protected final void updateButtons() {
		boolean empty = true;
		Iterator<IPerimeterWidget> it = perimetreWidgetMap.values().iterator();
		while (it.hasNext() && empty) {
			IPerimeterWidget perimetreWidget = it.next();
			empty = perimetreWidget.getValue() == null;
		}

		addFavoriteAction.setEnabled(!empty);
		clearAction.setEnabled(!empty);
		clearFiltersAction.setEnabled(!empty);

		deleteFavoriteAction.setEnabled(!userFavoriteList.isEmpty());

		if (empty) {
			showButton.setImportant(false);
		} else {
			showButton.setImportant(true);
		}
	}

	/**
	 * Permet d'ouvrir la barre de perimetres
	 * 
	 * @param showPerimetres
	 */
	public void setShowPerimetres(boolean showPerimetres) {
		if (showPerimetres && collapsiblePane.isCollapsed()) {
			collapsiblePane.setCollapsed(false);
			showButton.inverseDirection();
		} else if (!showPerimetres && !collapsiblePane.isCollapsed()) {
			collapsiblePane.setCollapsed(true);
			showButton.inverseDirection();
		}
	}

	/**
	 * Indique si la barre ouvert
	 * 
	 * @return
	 */
	public boolean isShowPerimetres() {
		return !collapsiblePane.isCollapsed();
	}

	/**
	 * Permet d'ajouter un favoris par defaut
	 * 
	 * @param id
	 * @param title
	 * @param filtersMap
	 */
	public void addDefaultFavorite(String id, String title, Map<String, Object> filtersMap) {
		MyFavorite f = new MyFavorite();
		f.id = id;
		f.title = title;
		f.filtersMap = filtersMap;

		defaultFavoriteList.add(f);

		updateButtons();
	}

	/**
	 * Permet d'utiliser un favoris par defaut selon son id
	 * 
	 * @param id
	 */
	public void showDefaultFavorite(String id) {
		MyFavorite f = findFavoriteById(defaultFavoriteList, id);
		if (f != null) {
			setFiltersMap(f.filtersMap);
		}
	}

	private MyFavorite findFavoriteById(List<MyFavorite> fList, String id) {
		MyFavorite res = null;
		if (id != null && fList != null && !fList.isEmpty()) {
			Iterator<MyFavorite> it = fList.iterator();
			while (it.hasNext() && res == null) {
				MyFavorite f = it.next();
				if (f != null && f.id != null && id.equals(f.id)) {
					res = f;
				}
			}
		}
		return res;
	}

	private MyFavorite findFavoriteByTitle(List<MyFavorite> fList, String title) {
		MyFavorite res = null;
		if (title != null && fList != null && !fList.isEmpty()) {
			Iterator<MyFavorite> it = fList.iterator();
			while (it.hasNext() && res == null) {
				MyFavorite f = it.next();
				if (f != null && f.title != null && title.equals(f.title)) {
					res = f;
				}
			}
		}
		return res;
	}

	private void saveStats() {
		SyPreferences prefs = SyPreferences.getPreferences();
		String name = AbstractPerimetersPanel.class.getName() + "_" + id;

		for (Entry<String, CollapsibleWidget> entry : collapsibleWidgetMap.entrySet()) {
			prefs.putBoolean(name + "_" + entry.getKey(), entry.getValue().isCollapsed());
		}
	}

	private void loadStats() {
		SyPreferences prefs = SyPreferences.getPreferences();
		String name = AbstractPerimetersPanel.class.getName() + "_" + id;

		for (Entry<String, CollapsibleWidget> entry : collapsibleWidgetMap.entrySet()) {
			boolean collapsed = prefs.getBoolean(name + "_" + entry.getKey(), true);
			entry.getValue().setCollapsed(collapsed);
		}
	}

	private void saveUserFavorites() {
		SyPreferences prefs = SyPreferences.getPreferences();
		String name = AbstractPerimetersPanel.class.getName() + "_" + id + "_favorites_user";

		int lastNb = prefs.getInt(name + "_nb", 0);
		if (lastNb > 0) {
			for (int i = 0; i < lastNb; i++) {
				prefs.remove(name + "_" + i + "_title");
				prefs.remove(name + "_" + i + "_filtersMap");
			}
		}
		prefs.remove(name + "_nb");

		if (userFavoriteList != null && !userFavoriteList.isEmpty()) {
			prefs.putInt(name + "_nb", userFavoriteList.size());
			for (int i = 0; i < userFavoriteList.size(); i++) {
				MyFavorite f = userFavoriteList.get(i);
				prefs.put(name + "_" + i + "_title", f.title);
				try {
					prefs.put(name + "_" + i + "_filtersMap", Utils.convertByteArrayToString(Utils.convertObjectToByteArray((Serializable) f.filtersMap)));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					prefs.remove(name + "_" + i + "_filtersMap");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadUserFavorites() {
		SyPreferences prefs = SyPreferences.getPreferences();
		String name = AbstractPerimetersPanel.class.getName() + "_" + id + "_favorites_user";

		int nb = prefs.getInt(name + "_nb", 0);
		for (int i = 0; i < nb; i++) {
			MyFavorite f = new MyFavorite();
			f.title = prefs.get(name + "_" + i + "_title", null);
			try {
				f.filtersMap = (Map<String, Object>) Utils.convertByteArrayToObject(Utils.convertStringToByteArray(prefs.get(name + "_" + i + "_filtersMap", null)));
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				f.filtersMap = new HashMap<String, Object>();
			}
			userFavoriteList.add(f);
		}
	}

	public final void addPerimeterClearListener(PerimeterClearListener perimeterClearListener) {
		listenerList.add(PerimeterClearListener.class, perimeterClearListener);
	}

	private final void firePerimeterCleared() {
		PerimeterClearListener[] pcl = listenerList.getListeners(PerimeterClearListener.class);
		for (PerimeterClearListener p : pcl) {
			p.perimeterCleared();
		}
	}

	private final class MyPerimetreWidgetListener implements PerimeterWidgetListener {

		@Override
		public void titleChanged(IPerimeterWidget source) {
		}

		@Override
		public void valuesChanged(IPerimeterWidget source) {
			fireStateChanged(source);
			updateButtons();

			panel.revalidate();
			panel.repaint();
		}
	}

	private final class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			collapsiblePane.setCollapsed(!collapsiblePane.isCollapsed());
		}
	}

	private final class MyCollapseWidgetActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			saveStats();
		}
	}

	private final class AddFavoriteAction extends AbstractAddAction {

		private static final long serialVersionUID = 8433301832170365910L;

		public AddFavoriteAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().addFavoriteHellip());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String libelleName = JOptionPane.showInputDialog(AbstractPerimetersPanel.this, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().name(), StaticWidgetHelper
					.getSynaptixWidgetConstantsBundle().addFavorite(), JOptionPane.QUESTION_MESSAGE);
			if (libelleName != null && !libelleName.trim().isEmpty()) {
				MyFavorite f = findFavoriteByTitle(userFavoriteList, libelleName.trim());
				if (f == null) {
					f = new MyFavorite();
					f.title = libelleName.trim();
					userFavoriteList.add(f);
				}
				f.filtersMap = getFiltersMap();

				saveUserFavorites();
				updateButtons();
			}
		}
	}

	private final class DeleteFavoriteAction extends AbstractDeleteAction {

		private static final long serialVersionUID = 8433301832170365910L;

		public DeleteFavoriteAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().deleteFavoriteHellip());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (userFavoriteList != null && !userFavoriteList.isEmpty()) {
				List<String> nameList = new ArrayList<String>();
				for (int i = 0; i < userFavoriteList.size(); i++) {
					MyFavorite f = userFavoriteList.get(i);
					nameList.add(f.title);
				}

				String name = (String) JOptionPane.showInputDialog(AbstractPerimetersPanel.this, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().name(), StaticWidgetHelper
						.getSynaptixWidgetConstantsBundle().deleteFavorite(), JOptionPane.QUESTION_MESSAGE, null, nameList.toArray(), nameList.get(0));
				if (name != null) {
					int i = nameList.indexOf(name);
					userFavoriteList.remove(i);

					saveUserFavorites();
					updateButtons();
				}
				fireRefreshAction();
			}
		}
	}

	private final class ShowFavoritesAction extends AbstractAction {

		private static final long serialVersionUID = 8433301832170365910L;

		public ShowFavoritesAction() {
			super("", FAVORITE_ICON); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			favorisPopupMenu.removeAll();

			Component c = showFavoritesButton;
			if (e.getSource() instanceof Component) {
				c = (Component) e.getSource();
			}

			boolean hasDefault = defaultFavoriteList != null && !defaultFavoriteList.isEmpty();
			boolean hasUser = userFavoriteList != null && !userFavoriteList.isEmpty();
			if (hasDefault) {
				for (MyFavorite favorite : defaultFavoriteList) {
					favorisPopupMenu.add(new JMenuItem(new FavoriteAction(favorite)));
				}
			}
			if (hasDefault && hasUser) {
				favorisPopupMenu.addSeparator();
			}
			if (hasUser) {
				for (MyFavorite favorite : userFavoriteList) {
					favorisPopupMenu.add(new JMenuItem(new FavoriteAction(favorite)));
				}
			}
			if (hasDefault || hasUser) {
				favorisPopupMenu.addSeparator();
			}
			favorisPopupMenu.add(addFavoriteAction);
			favorisPopupMenu.add(deleteFavoriteAction);
			favorisPopupMenu.addSeparator();
			favorisPopupMenu.add(clearFiltersAction);
			if (c == showFavoritesButton) {
				favorisPopupMenu.addSeparator();
				favorisPopupMenu.add(expandAllAction);
				favorisPopupMenu.add(collapseAllAction);
			}

			Dimension pref = favorisPopupMenu.getPreferredSize();
			if (direction == SwingConstants.EAST) {
				favorisPopupMenu.show(c, -pref.width, 0);
			} else {
				favorisPopupMenu.show(c, showFavoritesButton.getWidth(), 0);
			}
		}
	}

	private final class FavoriteAction extends AbstractAction {

		private static final long serialVersionUID = 379077400984248479L;

		private MyFavorite favorite;

		public FavoriteAction(MyFavorite favorite) {
			super(favorite.title);

			this.favorite = favorite;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setFiltersMap(favorite.filtersMap);
			fireRefreshAction();
		}

	}

	private final class ClearAction extends AbstractClearAction {

		private static final long serialVersionUID = 4020429824326695659L;

		public ClearAction() {
			super(""); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().clearsFilters());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			clear();

			firePerimeterCleared();
		}
	}

	private final class ClearFiltersAction extends AbstractClearAction {

		private static final long serialVersionUID = 4020429824326695659L;

		public ClearFiltersAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().clearsFilters());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			clear();
		}
	}

	private final class ExpandAllAction extends AbstractAction {

		private static final long serialVersionUID = 8919732653572183996L;

		public ExpandAllAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().expandAll(), MINI_PLUS);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			expandAll();
		}
	}

	private final class CollapseAllAction extends AbstractAction {

		private static final long serialVersionUID = -2239265081560919364L;

		public CollapseAllAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().collapseAll(), MINI_MINUS);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			collapseAll();
		}
	}

	private final class MinimizeButton extends JButton {

		private static final long serialVersionUID = 8380900194673172134L;

		public MinimizeButton() {
			super(MINIMIZE);

			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					collapsiblePane.setCollapsed(!collapsiblePane.isCollapsed());
				}
			});

			ButtonHelper.installButtonChanger(this);
		}
	}

	private final class MyPanel extends JPanel implements Scrollable {

		private static final long serialVersionUID = 5117688030231601426L;

		public MyPanel() {
			super(new VerticalLayout(gap));
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return new Dimension(this.getPreferredSize().width, 0);
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			Point p = visibleRect.getLocation();
			Component c = this.getComponentAt(0, p.y);
			if (c != null) {
				int nb = this.getComponentCount();
				int cell = 0;
				while (cell < nb && !this.getComponent(cell).equals(c)) {
					cell++;
				}
				if (direction > 0) {
					cell++;
					if (cell >= nb) {
						cell = nb - 1;
					}
				} else if (direction < 0) {
					if (c.getY() == p.y) {
						cell--;
						if (cell < 0) {
							cell = 0;
						}
					}
				}
				int y = this.getComponent(cell).getY();
				return Math.abs(y - p.y);
			}
			return 10;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return getScrollableBlockIncrement(visibleRect, orientation, direction);
		}
	}

	private final class MyFavorite {

		public String id;

		public String title;

		public Map<String, Object> filtersMap;

	}

	public interface PerimeterClearListener extends EventListener {

		public void perimeterCleared();

	}
}
