package org.cytoscape.FileWatcher.internal;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

public class IndividualFileListener extends Thread {

	AtomicBoolean isRunning = new AtomicBoolean();
	DialogTaskManager dtm;
	WatchService watcher;	
	Path folder;
	FileWatcherSettings settings;
	public IndividualFileListener(DialogTaskManager dtm, FileWatcherSettings settings) throws IOException{
		// TODO Auto-generated constructor stub
		this.settings = settings;
		watcher = FileSystems.getDefault().newWatchService();
		folder = settings.f.getParentFile().toPath();
		folder.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
		this.dtm = dtm;		
		isRunning.set(true);
	}
	
	public void deactivate()
	{
		isRunning.set(false);
	}
	@Override	
	public void run() {
		// TODO Auto-generated method stub
		while(isRunning.get())
		{
			WatchKey key;
			try
			{
				key = watcher.take();
			}
			catch(InterruptedException x)
			{
				return;
			}
			

			for (WatchEvent<?> event: key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}
				try{
					WatchEvent<Path> ev = (WatchEvent<Path>)event;		        
					Path filename = ev.context();
					System.out.println(filename);
					// Resolve the filename against the directory.
					// If the filename is "test" and the directory is "foo",
					// the resolved name is "test/foo".
					Path child = folder.resolve(filename);
					System.out.println(child);
					System.out.println(settings.f.toPath());
					if(child.equals(settings.f.toPath()))
					{
						dtm.execute(new TaskIterator(new UpdateDataFromFileTask(settings)));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
				}
			}
			boolean valid = key.reset();
		    if (!valid) {
		        break;
		    }
		}
	}

}
