package com.minimax.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FilePathListView extends FrameLayout implements View.OnClickListener,FileOperationController.DirectoryChangeListener {

	private ArrayList<ListItem> mPathList;

	public static final String TAG = "FilePathListView";
	public String fileSeparator = "/";
	
	private  FileOperationController controller;

	private LinearLayout mItemList;

	public FilePathListView(Context context) {
		super(context);
		onCreate();
	}

	public FilePathListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onCreate();
	}

	public FilePathListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate();
	}

	public void onCreate() {
		addView(View.inflate(this.getContext(), R.layout.path_list, null));
		mItemList = (LinearLayout) findViewById(R.id.item_list);
		mPathList = new ArrayList<ListItem>();
		controller = FileOperationController.getInstance();
		controller.registerDirectoryChangeListener(this);
	}
	
	public void onDestroy(){
		controller.unregisterDirectoryChangeListener(this);
	}


	private void setCurrentPath(String curPath) {
		mPathList.clear();


		if (curPath.length() < 1)
			return;
		else if (curPath.length() > 1)
			curPath = curPath.substring(1);
		else
			curPath.equals("");

		// 先加入根目录
		ListItem root = new ListItem();
		root.dirName = "/";
		root.path = "/";
		mPathList.add(root);

		String[] pathlist = curPath.split(fileSeparator);
		if(pathlist.length==1&&pathlist[0].equals("")){
			return;
		}
		String path = "/";

		for (String str : pathlist) {
			ListItem i = new ListItem();
			i.dirName = str;
			path += i.dirName + "/";
			i.path = path.substring(0, path.length() - 1);
			mPathList.add(i);

			Log.d(TAG, "dirName:" + i.dirName);
			Log.d(TAG, "path:" + i.path);
		}

		updateUi();

	}

	void updateUi() {
		mItemList.removeAllViews();
		for (ListItem i : mPathList) {
			ListItemView view = new ListItemView(getContext());
			MarginLayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			lp.setMargins(6, 6, 6, 10);
			view.setLayoutParams(lp);
			view.setTextSize(16.0f);
			view.setText(i.dirName);
			view.setTextColor(getResources().getColor(R.color.white));
			view.setGravity(Gravity.CENTER);
			view.setBackgroundResource(R.drawable.button_green_bg);
			view.setPadding(12, 5, 12, 5);
			i.tvDir = view;
			view.setOnClickListener(this);
			view.mListItem = i;
			mItemList.addView(view);
		}
	}

	static class ListItem {
		String path;
		String dirName;
		TextView tvDir;
	}
	
	
	static class ListItemView extends TextView{

		public ListItemView(Context context) {
			super(context);
		}
		
		ListItem mListItem;
		
	}

	@Override
	public void onClick(View view) {
		ListItemView listItemView = (ListItemView)view;
		
		controller.setCurrentDir(new File(listItemView.mListItem.path));
		
	}

	@Override
	public void onDirectoryChanged(File old, File current) {
		setCurrentPath(current.getPath());
	}

}


