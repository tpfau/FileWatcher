package org.cytoscape.FileWatcher.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.GUITunableHandlerFactory;

public class FileWatcherSettingsGUIHandlerFactory implements GUITunableHandlerFactory<FileWatcherSettingsTunableHandler> {
		
	/**
	 * Necessary fields for this Factory
	 */
	CyApplicationManager appmgr;
	CySwingApplication swingapp;
	FileUtil util;
	public FileWatcherSettingsGUIHandlerFactory(CyApplicationManager appmgr, CySwingApplication swingapp, FileUtil util) {
		super();
		this.appmgr = appmgr;
		this.swingapp = swingapp;
		this.util = util;
	}

	@Override
	public FileWatcherSettingsTunableHandler createTunableHandler(Field arg0, Object arg1,
			Tunable arg2) {
		// TODO Auto-generated method stub
		System.out.println("Receiving a request for a tunable handling");
		if(!FileWatcherSettings.class.isAssignableFrom(arg0.getType()))
		{
//			PrintFDebugger.Debugging(this, "Obtained a Request for tunable handling for type " + arg0.getType().getSimpleName() );
			return null;
		}
//		PrintFDebugger.Debugging(this, "Generating new Handler");
		return new FileWatcherSettingsTunableHandler(arg0,arg1,arg2,appmgr.getCurrentNetwork(), swingapp,util);
	}

	@Override
	public FileWatcherSettingsTunableHandler createTunableHandler(Method arg0,
			Method arg1, Object arg2, Tunable arg3) {
		System.out.println("Receiving a request for a tunable handling");
		if(!FileWatcherSettings.class.isAssignableFrom(arg0.getReturnType()))
		{
//			PrintFDebugger.Debugging(this, "Obtained a Request for tunable handling for type " + arg0.getReturnType().getSimpleName() );
			return null;
		}
//		PrintFDebugger.Debugging(this, "Generating new Handler");
		return new FileWatcherSettingsTunableHandler(arg0,arg1,arg2,arg3,appmgr.getCurrentNetwork(),swingapp,util);
	}


}
