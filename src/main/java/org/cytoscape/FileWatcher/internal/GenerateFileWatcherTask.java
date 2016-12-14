package org.cytoscape.FileWatcher.internal;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class GenerateFileWatcherTask extends AbstractTask
{

	@Tunable
	public FileWatcherSettings settings;
	
	FileManager mgr;
	
	public GenerateFileWatcherTask(FileManager mgr)
	{
		this.mgr = mgr;
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub			
		mgr.addFileWatcher(settings);
		insertTasksAfterCurrentTask(new UpdateDataFromFileTask(settings));
	}
}

