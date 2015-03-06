package com.synaptix.view.swing;

import com.synaptix.client.view.IDefaultViewFactory;
import com.synaptix.client.view.IView;
import com.synaptix.client.view.IViewWorker;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.swing.WaitComponentFeedbackPanel;

public class SwingDefaultViewFactory implements IDefaultViewFactory {

	@Override
	public <E> IWaitWorker waitGlassPaneViewWorker(IViewWorker<E, String> viewWorker) {
		return WaitGlassPaneSwingViewWorker.waitGlassPaneSwingViewWorker(true, viewWorker);
	}

	@Override
	public <E> IWaitWorker waitComponentViewWorker(IView view, IViewWorker<E, String> viewWorker) {
		if (view instanceof WaitComponentFeedbackPanel) {
			return WaitComponentSwingViewWorker.waitComponentSwingViewWorker((WaitComponentFeedbackPanel) view, viewWorker);
		} else {
			return waitGlassPaneViewWorker(viewWorker);
		}
	}

	@Override
	public <E> IWaitWorker waitFullComponentViewWorker(IView view, IViewWorker<E, String> viewWorker) {
		if (view instanceof WaitComponentFeedbackPanel) {
			return WaitFullComponentSwingWaitWorker.waitFullComponentSwingWaitWorker((WaitComponentFeedbackPanel) view, viewWorker);
		} else {
			return waitGlassPaneViewWorker(viewWorker);
		}
	}

	@Override
	public <E> IWaitWorker waitSilentViewWorker(IViewWorker<E, String> viewWorker) {
		return WaitSilentSwingViewWorker.waitSilentSwingViewWorker(viewWorker);
	}
}
