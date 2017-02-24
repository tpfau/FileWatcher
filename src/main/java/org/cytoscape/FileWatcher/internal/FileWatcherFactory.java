package org.cytoscape.FileWatcher.internal;


import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

/**

 */
public class FileWatcherFactory implements NetworkViewTaskFactory {

	
	FileManager mgr;
	
	public FileWatcherFactory( FileManager mgr)
	{
		this.mgr = mgr;
	}
	
	@Override
	public TaskIterator createTaskIterator(CyNetworkView arg0) {		
		return new TaskIterator(new GenerateFileWatcherTask(mgr));
	}

	@Override
	public boolean isReady(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return arg0 != null && arg0.getModel() != null;
	}


}

	
