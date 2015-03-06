package com.synaptix.widget.hierarchical.context;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.graphics.ColorUtilities;

import com.synaptix.component.IComponent;
import com.synaptix.component.field.IField;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.hierarchical.column.IHierarchicalColumnControl;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.helper.DefaultObjectToString;
import com.synaptix.widget.hierarchical.view.swing.component.helper.DoubleToString;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalCellRenderer;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanelFactory;
import com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.SummaryPanelFactory;
import com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.TitlePanelFactory;
import com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.ValuePanelFactory;
import com.synaptix.widget.hierarchical.writer.IHierarchicalExportWriter;

/**
 * The configuration context is build by a IHierarchicalContext
 * 
 * @author Nicolas P
 * 
 * @param <E>
 * @param <F>
 * @param <L>
 */
public final class ConfigurationContext<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> {

	private static final int DEFAULT_CELL_WIDTH = 30;

	private static final int DEFAULT_CELL_HEIGHT = 30;

	private static final int DEFAULT_FOOTER_HEIGHT = 30;

	private static final int DEFAULT_HEADER_HEIGHT = 30;

	private static final Font DEFAULT_BIG_FONT_EMPHAZE = new Font("Monospace", Font.BOLD, 14);

	private static final Font DEFAULT_SMALL_FONT_EMPHAZE = new Font("Monospace", Font.BOLD, 12);

	private static final Font DEFAULT_BIG_FONT = new Font("Monospace", Font.PLAIN, 14);

	private static final Font DEFAULT_SMALL_FONT = new Font("Monospace", Font.PLAIN, 12);

	private final IHierarchicalContext<E, F, L> hierarchicalContext;

	private final String title;

	private final Class<L> modelClass;

	private final ConstantsWithLookingBundle bundle;

	private final Map<Class<?>, GenericObjectToString<?>> objectsToStringByClass;

	private final Map<IField, Comparator<?>> comparatorForField;

	private final Map<HierarchicalPanelKind, HierarchicalPanelFactory<?, E, F, L>> factories;

	private final Map<HierarchicalPanelKind, HierarchicalCellRenderer<E, F, L>> renderers;

	private int cellWidth;

	private int cellHeight;

	private int footerHeight;

	private int headerHeight;

	private Font bigFont;

	private Font smallFont;

	private Font bigFontEmphase;

	private Font smallFontEmphase;

	private boolean isShouldRegroupModel;

	private Comparator<F> valueColumnSorter;

	private boolean isShowSummaryPanel;

	private boolean isShowSummaryLine;

	private ColorPalette standardColorPalette;

	private ColorPalette selectionColorPalette;

	private List<F> forcedColumnDefinitionList;

	private IHierarchicalExportWriter<E, F, L> exportWriter;

	private IHierarchicalColumnControl<E, F, L> columnControl;

	private boolean exportEnabled;

	/* package protected */ConfigurationContext(final IHierarchicalContext<E, F, L> hierarchicalContext, final String title, final ConstantsWithLookingBundle translationBundle,
			final Class<L> modelClass) {
		this.hierarchicalContext = hierarchicalContext;
		this.title = title;
		this.modelClass = modelClass;
		this.bundle = translationBundle;
		this.objectsToStringByClass = new HashMap<Class<?>, GenericObjectToString<?>>();
		this.factories = new HashMap<HierarchicalPanelKind, HierarchicalPanelFactory<?, E, F, L>>();
		this.renderers = new HashMap<HierarchicalPanelKind, HierarchicalCellRenderer<E, F, L>>();
		this.comparatorForField = new HashMap<IField, Comparator<?>>();

		this.forcedColumnDefinitionList = Collections.unmodifiableList(new ArrayList<F>());

		initialize();
	}

	private void initialize() {
		initSizes();
		initFonts();
		initObjectsToString();
		initFactories();
		initParameters();
		initColors();
		initCellRenderer();
	}

	private void initSizes() {
		this.cellWidth = DEFAULT_CELL_WIDTH;
		this.cellHeight = DEFAULT_CELL_HEIGHT;
		this.headerHeight = DEFAULT_HEADER_HEIGHT;
		this.footerHeight = DEFAULT_FOOTER_HEIGHT;
	}

	private void initFonts() {
		this.smallFontEmphase = DEFAULT_SMALL_FONT_EMPHAZE;
		this.bigFontEmphase = DEFAULT_BIG_FONT_EMPHAZE;
		this.smallFont = DEFAULT_SMALL_FONT;
		this.bigFont = DEFAULT_BIG_FONT;
	}

	private void initObjectsToString() {
		this.objectsToStringByClass.put(Object.class, new DefaultObjectToString());
		this.objectsToStringByClass.put(Double.class, new DoubleToString());
	}

	private void initFactories() {
		setFactoryForPanelKind(HierarchicalPanelKind.TITLE, new TitlePanelFactory<E, F, L>());
		setFactoryForPanelKind(HierarchicalPanelKind.VALUE, new ValuePanelFactory<E, F, L>());
		setFactoryForPanelKind(HierarchicalPanelKind.SUMMARY, new SummaryPanelFactory<E, F, L>());
	}

	private void initParameters() {
		this.isShowSummaryLine = true;
		this.isShowSummaryPanel = true;
	}

	private void initColors() {
		this.standardColorPalette = new ColorPalette(Color.WHITE);
		this.selectionColorPalette = new ColorPalette(new Color(190, 190, 255));
	}

	private void initCellRenderer() {
		// this.defaultCellRenderer = new DefaultHierarchicalCellRenderer();
		// this.cellRenderer = this.defaultCellRenderer;
	}

	public Class<L> getModelClass() {
		return this.modelClass;
	}

	/* package protected */void setFactoryForPanelKind(final HierarchicalPanelKind panelKind, final HierarchicalPanelFactory<?, E, F, L> factory) {
		if (factory != null) {
			this.factories.put(panelKind, factory);
		}
	}

	public HierarchicalPanelFactory<?, E, F, L> getPanelFactory(HierarchicalPanelKind panelKind) {
		return this.factories.get(panelKind);
	}

	public <T> void addObjectToStringForClass(final Class<T> objectClass, final GenericObjectToString<T> classToString) {
		this.objectsToStringByClass.put(objectClass, classToString);
	}

	@SuppressWarnings("unchecked")
	public <T> String getStringFromObject(final T object) {
		if (object != null && this.objectsToStringByClass.containsKey(object.getClass())) {
			return ((GenericObjectToString<T>) this.objectsToStringByClass.get(object.getClass())).getString(object);
		}
		return ((GenericObjectToString<Object>) this.objectsToStringByClass.get(Object.class)).getString(object);
	}

	public ConstantsWithLookingBundle getTranslationBundle() {
		return this.bundle;
	}

	public final int getCellWidth() {
		return this.cellWidth;
	}

	public final void setCellWidth(final int cellWidth) {
		if (cellWidth > 0) {
			this.cellWidth = cellWidth;
		} else {
			this.cellWidth = DEFAULT_CELL_WIDTH;
		}
	}

	public final int getCellHeight() {
		return this.cellHeight;
	}

	public final void setCellHeight(final int cellHeight) {
		if (cellHeight > 0) {
			this.cellHeight = cellHeight;
		} else {
			this.cellHeight = DEFAULT_CELL_HEIGHT;
		}
	}

	public final int getHeaderHeight() {
		return this.headerHeight;
	}

	public final void setHeaderHeight(final int headerHeight) {
		if (headerHeight > 0) {
			this.headerHeight = headerHeight;
		} else {
			this.headerHeight = DEFAULT_HEADER_HEIGHT;
		}
	}

	public final int getFooterHeight() {
		return this.footerHeight;
	}

	public final void setFooterHeight(final int footerHeight) {
		if (footerHeight > 0) {
			this.footerHeight = footerHeight;
		} else {
			this.footerHeight = DEFAULT_FOOTER_HEIGHT;
		}
	}

	public final Font getBigEmphaseFont() {
		return this.bigFontEmphase;
	}

	public final Font getSmallEmphaseFont() {
		return this.smallFontEmphase;
	}

	public final void setBigEmphaseFont(final Font font) {
		if (font == null) {
			this.bigFontEmphase = DEFAULT_BIG_FONT_EMPHAZE;
		} else {
			this.bigFontEmphase = font;
		}
	}

	public final void setSmallEmphaseFont(final Font font) {
		if (font == null) {
			this.smallFontEmphase = DEFAULT_SMALL_FONT_EMPHAZE;
		} else {
			this.smallFontEmphase = font;
		}
	}

	public final Font getBigFont() {
		return this.bigFont;
	}

	public final Font getSmallFont() {
		return this.smallFont;
	}

	public final void setBigFont(final Font font) {
		if (font == null) {
			this.bigFont = DEFAULT_BIG_FONT;
		} else {
			this.bigFont = font;
		}
	}

	public final void setSmallFont(final Font font) {
		if (font == null) {
			this.smallFont = DEFAULT_SMALL_FONT;
		} else {
			this.smallFont = font;
		}
	}

	public final void setShouldRegroupModel(final boolean shouldRegroupModel) {
		this.isShouldRegroupModel = shouldRegroupModel;
	}

	public final boolean isShouldRegroupModel() {
		return this.isShouldRegroupModel;
	}

	public final void setCellRendererForPanelKind(final HierarchicalPanelKind panelKind, final HierarchicalCellRenderer<E, F, L> cellRenderer) {
		this.renderers.put(panelKind, cellRenderer);
	}

	public final HierarchicalCellRenderer<E, F, L> getCellRendererForPanelKind(final HierarchicalPanelKind panelKind) {
		return this.renderers.get(panelKind);
	}

	public final void setValueColumnSorter(final Comparator<F> sorter) {
		this.valueColumnSorter = sorter;
	}

	public final Comparator<F> getValueColumnSorter() {
		return this.valueColumnSorter;
	}

	/* package protected */final void setShowSummaryPanel(final boolean isShowSummaryPanel) {
		this.isShowSummaryPanel = isShowSummaryPanel;
	}

	public final boolean isShowSummaryPanel() {
		return this.isShowSummaryPanel;
	}

	/* package protected */final void setShowSummaryLine(final boolean isShowSummaryLine) {
		this.isShowSummaryLine = isShowSummaryLine;
	}

	public final boolean isShowSummaryLine() {
		return this.isShowSummaryLine;
	}

	public final void setComparatorForField(final IField field, final Comparator<?> comparator) {
		this.comparatorForField.put(field, comparator);
	}

	public final Comparator<?> getComparatorForField(final IField field) {
		return this.comparatorForField.get(field);
	}

	public final Color getSelectionColor() {
		return this.selectionColorPalette.getBaseColor();
	}

	public final Color getHighlightedSelectionColor() {
		return this.selectionColorPalette.getHighlightColor();
	}

	public final Color getEmphaseSelectionColor() {
		return this.selectionColorPalette.getEmphaseColor();
	}

	public final Color getHighlightedEmphaseSelectionColor() {
		return this.selectionColorPalette.getHighlightEmphaseColor();
	}

	public final void setSelectionColor(final Color baseColor) {
		this.selectionColorPalette = new ColorPalette(baseColor);
	}

	public final Color getStandardColor() {
		return this.standardColorPalette.getBaseColor();
	}

	public final Color getHighlightedStandardColor() {
		return this.standardColorPalette.getHighlightColor();
	}

	public final Color getEmphaseStandardColor() {
		return this.standardColorPalette.getEmphaseColor();
	}

	public final Color getHighlightedEmphaseStandardColor() {
		return this.standardColorPalette.getHighlightEmphaseColor();
	}

	public final void setStandardColor(final Color baseColor) {
		this.standardColorPalette = new ColorPalette(baseColor);
	}

	/**
	 * Force a column definition list
	 * 
	 * @return
	 */
	public final void setForcedColumnDefinitionList(List<F> forcedColumnDefinitionList) {
		if (forcedColumnDefinitionList == null) {
			forcedColumnDefinitionList = new ArrayList<F>();
		}
		this.forcedColumnDefinitionList = Collections.unmodifiableList(forcedColumnDefinitionList);
	}

	/**
	 * Get the forced column definition list
	 * 
	 * @return
	 */
	public final List<F> getForcedColumnDefinitionList() {
		return this.forcedColumnDefinitionList;
	}

	/**
	 * Get the hierarchical context (controller) which manages the component
	 * 
	 * @return
	 */
	public final IHierarchicalContext<E, F, L> getHierarchicalContext() {
		return hierarchicalContext;
	}

	public final boolean hasExportWriter() {
		return exportWriter != null && exportEnabled;
	}

	/* package protected */void setExportEnabled(boolean exportEnabled) {
		this.exportEnabled = exportEnabled;
	}

	public final IHierarchicalExportWriter<E, F, L> getExportWriter() {
		return exportWriter;
	}

	/* package protected */final void setExportWriter(IHierarchicalExportWriter<E, F, L> exportWriter) {
		this.exportWriter = exportWriter;
	}

	public boolean hasColumnControl() {
		return columnControl != null;
	}

	public final IHierarchicalColumnControl<E, F, L> getColumnControl() {
		return columnControl;
	}

	/* package protected */void setColumnControl(IHierarchicalColumnControl<E, F, L> columnControl) {
		this.columnControl = columnControl;
		if (this.columnControl != null) {
			this.columnControl.install(this);
		}
	}

	public void setCanHideColumns(boolean canHideColumns) {
		if (hasColumnControl()) {
			getColumnControl().setCanHideColumns(canHideColumns);
		}
	}

	public final Map<Class<?>, GenericObjectToString<?>> getObjectsToStringByClass() {
		return Collections.unmodifiableMap(objectsToStringByClass);
	}

	public final String getTitle() {
		return title;
	}

	private class ColorPalette {

		private final static float FACTOR = 0.90f;

		private final Color baseColor;

		private final Color highlightColor;

		private final Color emphaseColor;

		private final Color highlightEmphaseColor;

		private ColorPalette(final Color baseColor) {
			this.baseColor = baseColor;
			this.highlightColor = darken(baseColor);
			this.emphaseColor = darken(highlightColor);
			this.highlightEmphaseColor = darken(emphaseColor);
		}

		private Color darken(final Color originalColor) {
			float[] hsl = ColorUtilities.RGBtoHSL(originalColor);
			hsl[2] = hsl[2] * FACTOR;
			final Color darkerColor = ColorUtilities.HSLtoRGB(hsl[0], hsl[1], hsl[2]);
			return darkerColor;
		}

		public Color getHighlightColor() {
			return this.highlightColor;
		}

		public Color getBaseColor() {
			return this.baseColor;
		}

		public Color getEmphaseColor() {
			return this.emphaseColor;
		}

		public Color getHighlightEmphaseColor() {
			return this.highlightEmphaseColor;
		}
	}
}
