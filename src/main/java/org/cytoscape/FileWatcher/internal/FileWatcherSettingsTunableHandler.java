package org.cytoscape.FileWatcher.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.AbstractGUITunableHandler;



public class FileWatcherSettingsTunableHandler extends AbstractGUITunableHandler {

		FileWatcherSettingsGUI mypanel;
		private CyNetwork network;
		private FileUtil util; 
		private CySwingApplication app;
		
		protected FileWatcherSettingsTunableHandler(Method getter, Method setter,
				Object instance, Tunable tunable, CyNetwork network, CySwingApplication app, FileUtil util) {
			super(getter, setter, instance, tunable);
			this.network = network;
			this.app = app;
			this.util = util;
			init();
		}

		protected FileWatcherSettingsTunableHandler(Field field, Object instance,
				Tunable tunable, CyNetwork network, CySwingApplication app,FileUtil util) {
			super(field, instance, tunable);
			this.network = network;
			this.app = app;
			this.util = util;
			init();
		}

		/**
		 * Generate the GUI and replace the panel with the generated GUI.
		 */
		private void init()
		{

			mypanel = new FileWatcherSettingsGUI(network,util,app);
			panel = mypanel;
		}
		@Override
		public void handle() {
			try{
			try{
				FileWatcherSettings props = mypanel.getSettings();
				setValue(props);
			}
			catch(IllegalAccessException| InvocationTargetException e)
			{
				e.printStackTrace(System.out);
			}
			}
			catch(Exception e)
			{
				e.printStackTrace(System.out);
				throw e;
			}

		}
}
