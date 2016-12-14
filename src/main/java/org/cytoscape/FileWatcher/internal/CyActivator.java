package org.cytoscape.FileWatcher.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.ActionEnableSupport;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.swing.GUITunableHandlerFactory;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		DialogTaskManager dtm = getService(context, DialogTaskManager.class);
		FileUtil util = getService(context, FileUtil.class);
		FileManager store = new FileManager(dtm);	
		FileWatcherFactory fac = new FileWatcherFactory(store);
		Properties layoutProperties = new Properties();
		layoutProperties.setProperty(ServiceProperties.PREFERRED_ACTION, "NEW");
		layoutProperties.setProperty(ServiceProperties.PREFERRED_MENU, ServiceProperties.NETWORK_APPS_MENU);
		layoutProperties.setProperty(ServiceProperties.IN_TOOL_BAR, "false");
		layoutProperties.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		layoutProperties.setProperty(ServiceProperties.IN_CONTEXT_MENU, "true");
		layoutProperties.setProperty(ServiceProperties.TITLE, "Add File to Watch");		
		layoutProperties.setProperty(ServiceProperties.ENABLE_FOR, ActionEnableSupport.ENABLE_FOR_ALWAYS);
		registerService(context, fac, NetworkViewTaskFactory.class, layoutProperties );			
		layoutProperties.setProperty(ServiceProperties.TITLE, "Remove Watched File");			
		registerService(context, store, TaskFactory.class, layoutProperties );
		
		FileWatcherSettingsGUIHandlerFactory TunableHandler = new FileWatcherSettingsGUIHandlerFactory(getService(context, CyApplicationManager.class), getService(context,CySwingApplication.class), util);		
		registerService(context, TunableHandler, GUITunableHandlerFactory.class, new Properties());
		
	}

}
