package com.minimax.fileexplorer;

import java.io.File;
import java.util.ArrayList;

public class FileOperationController {

	
	private File currentDir = new File("/");
	
	public void setCurrentDir(File dir){
			notifyDirectoryChange(currentDir,dir);
			currentDir = dir;
		
	}
	public File getCurrentDir(){
		return currentDir;
	}
	public static interface DirectoryChangeListener{
		void onDirectoryChanged(File old,File current);
	}
	
	ArrayList<DirectoryChangeListener>mDirectoryChangeListenerList;
	
	private static FileOperationController instance;
	private FileOperationController(){
		mDirectoryChangeListenerList = new ArrayList<FileOperationController.DirectoryChangeListener>();
		
	}
	
	public void registerDirectoryChangeListener(DirectoryChangeListener listener){
		mDirectoryChangeListenerList.add(listener);
	}
	
	public void unregisterDirectoryChangeListener(DirectoryChangeListener listener){
		mDirectoryChangeListenerList.remove(listener);
	}
	
	public void notifyDirectoryChange(File old,File current){
		for(DirectoryChangeListener listener:mDirectoryChangeListenerList){
			listener.onDirectoryChanged(old, current);
		}
	}
	
	public synchronized static  FileOperationController getInstance(){
		
		if(instance == null){
			instance = new FileOperationController();
		}
		return instance;
	}
}
