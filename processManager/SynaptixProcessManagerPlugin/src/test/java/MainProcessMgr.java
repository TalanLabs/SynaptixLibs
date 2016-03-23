import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.log4j.PropertyConfigurator;

import com.synaptix.pmgr.plugin.GuicePluginManager;

public class MainProcessMgr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure(MainProcessMgr.class.getClassLoader().getResource("log4j.properties"));
		Log logger = LogFactoryImpl.getLog(MainProcessMgr.class);

		// GuicePluginManager.initPlugins(logger,"TRMT_LOCAL");
		GuicePluginManager guicePluginManager = new GuicePluginManager();
		guicePluginManager.initPlugins(logger, "TRT");
	}
}
