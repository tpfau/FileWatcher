package org.cytoscape.FileWatcher.internal;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.DialogTaskManager;

public class FileManager implements NetworkViewTaskFactory,TaskFactory{

	private HashMap<Long,Point2D> layoutlocations = new HashMap<>();
	
	DialogTaskManager dtm;
	HashMap<File,IndividualFileListener> usedthreads;
	
	public FileManager(DialogTaskManager dtm) {
		
		this.dtm = dtm;	
		usedthreads = new HashMap<>();
	}
	
	public void addFileWatcher(FileWatcherSettings settings) throws IOException
	{
		IndividualFileListener watcher = new IndividualFileListener(dtm, settings);
		usedthreads.put(settings.f,watcher);
		watcher.start();		
	}
	
	public void removeFileWatcher(File f)
	{
		usedthreads.get(f).deactivate();
		usedthreads.remove(f);
	}

	public Collection<File> getWatchedFiles()
	{
		Set<File> watchedFiles = new HashSet<File>();
		watchedFiles.addAll(usedthreads.keySet());
		return watchedFiles;
	}

	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		Collection<File> currentfiles = getWatchedFiles();
		JComboBox<File> combo = new JComboBox<File>(new DefaultComboBoxModel<File>(currentfiles.toArray(new File[currentfiles.size()])));
		JPanel	pan = new JPanel();
		pan.add(combo);		
		int selected = JOptionPane.showConfirmDialog(null,pan, "Select a File to stop Watching", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(selected == JOptionPane.OK_OPTION)
		{
			return new TaskIterator(new RemoveWatchedFileTask((File)combo.getSelectedItem(),this));	
		}
		return null;
		
	}
	
	private class RemoveWatchedFileTask extends AbstractTask
	{
		File FileToRemove;
		FileManager mgr;
		public RemoveWatchedFileTask(File f, FileManager mgr) {
			// TODO Auto-generated constructor stub
			FileToRemove = f;
			this.mgr = mgr;
		}
		@Override
		public void run(TaskMonitor arg0) throws Exception {
			// TODO Auto-generated method stub
			mgr.removeFileWatcher(FileToRemove);
			
		}
		
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return createTaskIterator();
	}

	@Override
	public boolean isReady(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return !usedthreads.isEmpty();
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return !usedthreads.isEmpty();
	}
}
