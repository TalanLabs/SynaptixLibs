package com.synaptix.client.core.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.client.core.view.IFrontendView;
import com.synaptix.client.core.view.swing.FrontendFrame;
import com.synaptix.client.view.IView;
import com.synaptix.widget.core.controller.IController;
import com.synaptix.widget.core.controller.ISimpleFrontendContext;

/**
 * Frontend controller
 * 
 * @author Gaby
 * 
 */
public class FrontendController implements ISimpleFrontendContext {

	private static final Log LOG = LogFactory.getLog(FrontendController.class);

	private IFrontendView view;

	private List<IController> controllers = new ArrayList<IController>();

	@Inject
	public FrontendController() {
		super();

		initialize();
	}

	private void initialize() {
		view = new FrontendFrame(this);
	}

	@Override
	public void registerController(IController controller) {
		controllers.add(controller);

		view.registerView(controller.getView());
	}

	@Override
	public void unregisterController(IController controller) {
		if (controllers.remove(controller)) {
			if ((view != null) && (controller.getView() != null)) {
				// view.unregisterView(controller.getView());
			}
		}
	}

	/**
	 * Start frontend controller. Start all controllers and start view
	 */
	public void start() {
		view.start();
	}

	/**
	 * Stop frontend controller. Stop all controllers and stop view
	 * 
	 * @param exit
	 */
	public void stop(final boolean restart) {
		view.stop();
		System.exit(0);
	}

	/**
	 * Logout current user
	 */
	public void logout() {
		stop(true);
	}

	/**
	 * Exit application
	 */
	public void exit() {
		stop(false);
	}

	@Override
	public void showHelp(IView parent, String string) {
	}
}
