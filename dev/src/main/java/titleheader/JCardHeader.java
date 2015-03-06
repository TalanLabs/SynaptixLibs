package titleheader;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.UIManager;

import titleheader.ui.BasicCardHeaderUI;

public class JCardHeader extends JComponent {

	private static final long serialVersionUID = 564784311130693407L;

	private static final String uiClassID = "PanelHeaderUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicCardHeaderUI.class.getName());
	}

	private String title;

	private CardHeaderTitleRenderer titleRenderer;

	private CardHeaderActionRenderer actionRenderer;

	private int cellHeight = 25;

	private boolean selected = false;

	private Color selectedForegroundColor;

	private Color selectedBackgroundColor;

	private Color backgroundColor;

	private Color foregroundColor;

	private Action[] rightActions;

	public JCardHeader() {
		this("");
	}

	public JCardHeader(String title) {
		super();

		this.title = title;

		this.titleRenderer = createTitleRenderer();
		this.actionRenderer = createActionRenderer();

		this.setBackground(new Color(240, 240, 240));
		this.setForeground(Color.GRAY);
		this.selectedForegroundColor = Color.DARK_GRAY;
		this.selectedBackgroundColor = Color.LIGHT_GRAY;

		updateUI();
	}

	@Override
	public void updateUI() {
		super.updateUI();

		setUI(UIManager.getUI(this));
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String oldValue = this.title;
		this.title = title;
		firePropertyChange("title", oldValue, title);
	}

	protected CardHeaderTitleRenderer createTitleRenderer() {
		return new DefaultCardHeaderTitleRenderer();
	}

	public CardHeaderTitleRenderer getTitleRenderer() {
		return titleRenderer;
	}

	public void setTitleRenderer(CardHeaderTitleRenderer titleRenderer) {
		CardHeaderTitleRenderer oldValue = this.titleRenderer;
		this.titleRenderer = titleRenderer;
		firePropertyChange("titleRenderer", oldValue, titleRenderer);
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		int oldValue = this.cellHeight;
		this.cellHeight = cellHeight;
		firePropertyChange("cellHeight", oldValue, cellHeight);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		boolean oldValue = this.selected;
		this.selected = selected;

		if (selected) {
			super.setBackground(selectedBackgroundColor);
			super.setForeground(selectedForegroundColor);
		} else {
			super.setBackground(backgroundColor);
			super.setForeground(foregroundColor);
		}

		firePropertyChange("selected", oldValue, selected);
	}

	public Color getSelectedForegroundColor() {
		return selectedForegroundColor;
	}

	public void setSelectedForegroundColor(Color selectedForegroundColor) {
		Color oldValue = this.selectedForegroundColor;
		this.selectedForegroundColor = selectedForegroundColor;
		firePropertyChange("selectedForegroundColor", oldValue, selectedForegroundColor);
	}

	public Color getSelectedBackgroundColor() {
		return selectedBackgroundColor;
	}

	public void setSelectedBackgroundColor(Color selectedBackgroundColor) {
		Color oldValue = this.selectedBackgroundColor;
		this.selectedBackgroundColor = selectedBackgroundColor;
		firePropertyChange("selectedBackgroundColor", oldValue, selectedBackgroundColor);
	}

	@Override
	public void setBackground(Color bg) {
		this.backgroundColor = bg;
		if (!selected) {
			super.setBackground(bg);
		}
	}

	@Override
	public void setForeground(Color fg) {
		this.foregroundColor = fg;
		if (!selected) {
			super.setForeground(fg);
		}
	}

	protected CardHeaderActionRenderer createActionRenderer() {
		return new DefaultCardHeaderActionRenderer();
	}

	public CardHeaderActionRenderer getActionRenderer() {
		return actionRenderer;
	}

	public void setActionRenderer(CardHeaderActionRenderer actionRenderer) {
		CardHeaderActionRenderer oldValue = this.actionRenderer;
		this.actionRenderer = actionRenderer;
		firePropertyChange("actionRenderer", oldValue, actionRenderer);
	}

	public Action[] getRightActions() {
		return rightActions;
	}

	public void setRightActions(Action[] rightActions) {
		Action[] oldValue = this.rightActions;
		this.rightActions = rightActions;
		firePropertyChange("rightActions", oldValue, rightActions);
	}
}