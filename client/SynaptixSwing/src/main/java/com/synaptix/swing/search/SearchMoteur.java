package com.synaptix.swing.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.synaptix.swing.JWaitGlassPane;
import com.synaptix.swing.SearchMessages;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.view.swing.error.ErrorViewManager;

public class SearchMoteur {

	private static final String TEXT_TROP_RESULTATS = SearchMessages.getString("JSearch.0"); //$NON-NLS-1$

	private static final String TEXT_AUCUN_RESULTAT = SearchMessages.getString("JSearch.1"); //$NON-NLS-1$

	private static final String TEXT_MESSAGE_MAX_COUNT_1 = SearchMessages.getString("JSearch.2"); //$NON-NLS-1$

	private static final String TEXT_MESSAGE_MAX_COUNT_3 = SearchMessages.getString("JSearch.4"); //$NON-NLS-1$

	private static final String TEXT_MESSAGE_MAX_COUNT_4 = SearchMessages.getString("JSearch.5"); //$NON-NLS-1$

	private static final String TEXT_MESSAGE_ZERO_COUNT_1 = SearchMessages.getString("JSearch.6"); //$NON-NLS-1$

	private static final String TEXT_MESSAGE_ZERO_COUNT_2 = SearchMessages.getString("JSearch.7"); //$NON-NLS-1$

	private ISearchMoteurContext context;

	private ISearchHeader searchHeader;

	private ISearchTable searchTable;

	private ISearchPageControl searchPageControl;

	private JWaitGlassPane glassPane;

	private boolean viewDialogContinueCount;

	private boolean viewDialogZeroCount;

	private boolean viewDialogMaxCount;

	private boolean useWaitScreenForFilter;

	private boolean useCount;

	private Map<String, Object> params;

	private int maxResultLine;

	private int currentPage;

	private int maxPage;

	private boolean useWaitScreen;

	public SearchMoteur(ISearchMoteurContext context, ISearchHeader searchHeader, ISearchTable searchTable) {
		this(context, searchHeader, searchTable, null);
	}

	public SearchMoteur(ISearchMoteurContext context, ISearchHeader searchHeader, ISearchTable searchTable, ISearchPageControl searchPageControl) {
		this.context = context;
		this.searchHeader = searchHeader;
		this.searchTable = searchTable;
		this.searchPageControl = searchPageControl;

		viewDialogZeroCount = true;
		viewDialogMaxCount = true;
		viewDialogContinueCount = true;

		useCount = true;
		useWaitScreenForFilter = true;

		initComponents();
	}

	private void initComponents() {
		glassPane = new JWaitGlassPane(JWaitGlassPane.TYPE_DIRECTION_RIGHT_TO_LEFT, "", //$NON-NLS-1$
				JWaitGlassPane.ICON_CLIENT, JWaitGlassPane.ICON_DATABASE_SERVER);

		if (searchPageControl != null) {
			searchPageControl.addActionListener(new MyActionListener());
		}
	}

	public boolean isViewDialogMaxCount() {
		return viewDialogMaxCount;
	}

	public void setViewDialogMaxCount(boolean viewDialogMaxCount) {
		this.viewDialogMaxCount = viewDialogMaxCount;
	}

	public boolean isViewDialogZeroCount() {
		return viewDialogZeroCount;
	}

	public void setViewDialogZeroCount(boolean viewDialogZeroCount) {
		this.viewDialogZeroCount = viewDialogZeroCount;
	}

	public boolean isViewDialogContinueCount() {
		return viewDialogContinueCount;
	}

	public void setViewDialogContinueCount(boolean viewDialogContinueCount) {
		this.viewDialogContinueCount = viewDialogContinueCount;
	}

	public boolean isUseWaitScreenForFilter() {
		return useWaitScreenForFilter;
	}

	public void setUseWaitScreenForFilter(boolean useWaitScreenForFilter) {
		this.useWaitScreenForFilter = useWaitScreenForFilter;
	}

	public void setUseCount(boolean useCount) {
		this.useCount = useCount;
	}

	public boolean isUseCount() {
		return useCount;
	}

	private boolean isSearchContinue(int i, int max) {
		boolean res;
		if (i == 0) {
			res = false;
			if (isViewDialogZeroCount()) {
				StringBuilder sb = new StringBuilder();
				sb.append(TEXT_MESSAGE_ZERO_COUNT_1);
				sb.append("\n"); //$NON-NLS-1$
				sb.append(TEXT_MESSAGE_ZERO_COUNT_2);
				JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), sb.toString(), TEXT_AUCUN_RESULTAT, JOptionPane.WARNING_MESSAGE);
			}
		} else {
			boolean ok = true;
			if (i > max) {
				ok = false;

				if (isViewDialogMaxCount()) {
					StringBuilder sb = new StringBuilder();
					sb.append(MessageFormat.format(TEXT_MESSAGE_MAX_COUNT_1, String.valueOf(i)));
					sb.append("\n"); //$NON-NLS-1$
					sb.append(TEXT_MESSAGE_MAX_COUNT_3);
					if (isViewDialogContinueCount()) {
						sb.append("\n"); //$NON-NLS-1$
						sb.append(TEXT_MESSAGE_MAX_COUNT_4);
						if (JOptionPane.showConfirmDialog(GUIWindow.getActiveWindow(), sb.toString(), TEXT_TROP_RESULTATS, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
							ok = true;
						}
					} else {
						JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), sb.toString(), TEXT_TROP_RESULTATS, JOptionPane.WARNING_MESSAGE);
					}
				}
			}
			res = ok;
		}
		return res;
	}

	public void search() {
		search(useWaitScreenForFilter);
	}

	public void search(boolean useWaitScreen) {
		search(useWaitScreen, null);
	}

	public void search(boolean useWaitScreen, FinishSearch finishSearch) {
		this.useWaitScreen = useWaitScreen;

		params = searchHeader.getValueFilters();

		if (useWaitScreen) {
			GUIWindow.setGlassPaneForActiveWindow(glassPane);
			glassPane.start();
			if (searchPageControl != null || useCount) {
				glassPane.setTypeDirection(JWaitGlassPane.TYPE_DIRECTION_LEFT_TO_RIGHT);
				new SearchCountSwingWorker(params, finishSearch).execute();
			} else {
				glassPane.setTypeDirection(JWaitGlassPane.TYPE_DIRECTION_RIGHT_TO_LEFT);
				new SearchSwingWorker(params, finishSearch).execute();
			}
		} else {
			boolean succes = false;
			try {
				boolean ok = true;
				if (searchPageControl != null || useCount) {
					int i = context.searchCount(params);

					if (searchPageControl != null) {
						updateSearchPageControl(i);
					} else {
						ok = isSearchContinue(i, context.getMaxCount());
					}
				}
				if (ok) {
					Result result;
					if (searchPageControl != null) {
						int skip = (currentPage - 1) * context.getPageMaxCount();
						result = context.search(params, skip, Math.min(context.getPageMaxCount(), maxResultLine - skip));
					} else {
						result = context.search(params);
					}

					searchTable.clearTable();
					searchTable.setResult(result);

					succes = true;
				}
			} catch (Exception e) {
				ErrorViewManager.getInstance().showErrorDialog(null, e);
			}

			if (finishSearch != null) {
				finishSearch.finish(succes);
			}
		}
	}

	private void updateSearchPageControl(int i) {
		maxResultLine = i;

		currentPage = 1;
		double r = (double) maxResultLine / (double) context.getPageMaxCount();
		maxPage = (int) Math.ceil(r);
		searchPageControl.setControl(1, maxPage);
	}

	private final class SearchCountSwingWorker extends SwingWorker<Integer, String> {

		private Map<String, Object> params;

		private FinishSearch finishSearch;

		public SearchCountSwingWorker(Map<String, Object> params, FinishSearch finishSearch) {
			super();

			this.params = params;
			this.finishSearch = finishSearch;
		}

		protected Integer doInBackground() throws Exception {
			publish(SearchMessages.getString("JSearch.17")); //$NON-NLS-1$

			int i = context.searchCount(params);

			publish(SearchMessages.getString("JSearch.18")); //$NON-NLS-1$
			return Integer.valueOf(i);
		}

		protected void process(List<String> chunks) {
			for (String string : chunks) {
				glassPane.setText(string);
			}
		}

		protected void done() {
			glassPane.stop();

			if (searchPageControl != null) {
				searchPageControl.clear();
			}
			searchTable.clearTable();
			try {
				int res = get().intValue();

				if (searchPageControl != null) {
					updateSearchPageControl(res);
				}

				if (searchPageControl != null || isSearchContinue(res, context.getMaxCount())) {
					glassPane.setTypeDirection(JWaitGlassPane.TYPE_DIRECTION_RIGHT_TO_LEFT);
					glassPane.start();
					new SearchSwingWorker(params, finishSearch).execute();
				} else {
					if (finishSearch != null) {
						finishSearch.finish(false);
					}
				}
			} catch (Exception e) {
				ErrorViewManager.getInstance().showErrorDialog(null, e);

				if (finishSearch != null) {
					finishSearch.finish(false);
				}
			}
		}
	}

	private final class SearchSwingWorker extends SwingWorker<Result, String> {

		private Map<String, Object> params;

		private FinishSearch finishSearch;

		public SearchSwingWorker(Map<String, Object> params, FinishSearch finishSearch) {
			super();

			this.params = params;
			this.finishSearch = finishSearch;
		}

		protected Result doInBackground() throws Exception {
			publish(SearchMessages.getString("JSearch.24")); //$NON-NLS-1$

			Result result;
			if (searchPageControl != null) {
				int skip = (currentPage - 1) * context.getPageMaxCount();
				result = context.search(params, skip, Math.min(context.getPageMaxCount(), maxResultLine - skip));
			} else {
				result = context.search(params);
			}

			publish(SearchMessages.getString("JSearch.18")); //$NON-NLS-1$
			return result;
		}

		protected void process(List<String> chunks) {
			for (String string : chunks) {
				glassPane.setText(string);
			}
		}

		protected void done() {
			glassPane.stop();

			searchTable.clearTable();
			try {
				searchTable.setResult(get());

				if (finishSearch != null) {
					finishSearch.finish(true);
				}
			} catch (Exception e) {
				ErrorViewManager.getInstance().showErrorDialog(null, e);

				if (finishSearch != null) {
					finishSearch.finish(false);
				}
			}
		}
	}

	private final class MyActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action != null) {
				if (action.equals("last")) { //$NON-NLS-1$
					currentPage = maxPage;
				} else if (action.equals("next")) { //$NON-NLS-1$
					currentPage++;
				} else if (action.equals("first")) { //$NON-NLS-1$
					currentPage = 1;
				} else if (action.equals("previous")) { //$NON-NLS-1$
					currentPage--;
				}

				searchPageControl.setControl(currentPage, maxPage);

				if (useWaitScreen) {
					GUIWindow.setGlassPaneForActiveWindow(glassPane);
					glassPane.start();
					glassPane.setTypeDirection(JWaitGlassPane.TYPE_DIRECTION_RIGHT_TO_LEFT);
					new SearchSwingWorker(params, null).execute();
				} else {
					try {
						int skip = (currentPage - 1) * context.getPageMaxCount();
						Result result = context.search(params, skip, Math.min(context.getPageMaxCount(), maxResultLine - skip));
						searchTable.clearTable();
						searchTable.setResult(result);
					} catch (Exception e1) {
						ErrorViewManager.getInstance().showErrorDialog(null, e1);
					}
				}
			}
		}
	}

	public static interface FinishSearch {

		public abstract void finish(boolean succes);

	}
}
