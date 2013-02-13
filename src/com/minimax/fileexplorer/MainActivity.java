package com.minimax.fileexplorer;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.minimax.fileexplorer.FileOperationController.DirectoryChangeListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener, DirectoryChangeListener {

	WallpaperManager mWallpaperMgr;
	
	GridView mGvFileList;
	ProgressBar mLoading;
	FilePathListView mPathListView;// 20120201之后改名
	// File mCurrentDir;
	FileOperationMenu mFileMenu;
	ArrayList<File> mFileNameList;
	private List<FileInfo> mFileList;

	public static final String TAG = "main";
	private FileViewAdapter mFileAdapter;

	private FileOperationController mController;
	private boolean showProgressBar = false;
	private static final long showProgressBarDelay = 330;
	private Object showProgressBarMutext = new Object();

	private ImageView mImgBg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.explorer_main);
		
		mWallpaperMgr = WallpaperManager.getInstance(this);
		
		mImgBg = (ImageView) findViewById(R.id.img_bg);
		setupBackground();
		
		mGvFileList = (GridView) findViewById(R.id.gv_file_list);
		mLoading = (ProgressBar) findViewById(R.id.pb_loading);
		mPathListView = (FilePathListView) findViewById(R.id.path_list);
		mFileMenu = new FileOperationMenu(this);

		Window window = getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		wl.alpha = 0.9f;
		window.setAttributes(wl);

		mFileList = new ArrayList<FileInfo>();
		mFileAdapter = new FileViewAdapter(this, R.layout.gv_file_item,
				mFileList);
		mGvFileList.setAdapter(mFileAdapter);
		mGvFileList.setOnItemClickListener(this);
		mGvFileList.setOnItemLongClickListener(this);

		mController = FileOperationController.getInstance();
		mController.registerDirectoryChangeListener(this);
		mController.setCurrentDir(new File(Environment
				.getExternalStorageDirectory().getPath()));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// unregister
		mController.unregisterDirectoryChangeListener(this);
	}

	@Override
	public void onDirectoryChanged(File old, final File current) {

		mGvFileList.setVisibility(View.INVISIBLE);
		showProgressBar = true;

		// 载入列表需要500ms,使用线程
		new Thread() {
			public void run() {
				updateContent(current);
			}
		}.start();
		mGvFileList.postDelayed(new Runnable() {
			@Override
			public void run() {
				synchronized (showProgressBarMutext) {
					Log.d(TAG," setVisibility show:"+showProgressBar);
					if (showProgressBar)
						mLoading.setVisibility(View.VISIBLE);// 330ms内没有载入完成则显示ProgressBar
				}

			}

		}, showProgressBarDelay);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
		Log.d(TAG, " pos:" + pos);
		FileInfo fi = mFileList.get(pos);
		if (fi.IsDir) {
			mController.setCurrentDir(new File(fi.filePath));

		} else {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(fi.filePath)),
					Util.getMimeType(fi.fileName));

			startActivity(intent);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int pos,
			long arg3) {
		FileInfo fi = mFileList.get(pos);
		// if (fi.IsDir) {
		// } else {
		mFileMenu.setFile(new File(fi.filePath));
		mFileMenu.show(findViewById(R.id.main));
		// }
		return true;
	}

	// 此方法在工作线程中调用
	public synchronized void updateContent(File dir) {
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		FilenameFilter filter = new FilenameFilter(){

			@Override
			public boolean accept(File dir, String filename) {
				
				if(filename.indexOf(".")==0)
					return false;
				return true;
			}
			
		};
		File[] files = dir.listFiles( filter);
		mFileList.clear();
		for (File f : files) {
			FileInfo fi = Util.GetFileInfo(f.getAbsolutePath());
			mFileList.add(fi);
		}
		sortFileList();

		synchronized (showProgressBarMutext) {
			showProgressBar = false;
		}

		mGvFileList.post(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG," notifyDataSetChanged show:"+showProgressBar);
				mLoading.setVisibility(View.INVISIBLE);
				mGvFileList.setVisibility(View.VISIBLE);
				mFileAdapter.notifyDataSetChanged();// 必须在主线程调用
			}

		});
	}

	public void sortFileList() {
		Collections.sort(mFileList, new Comparator<FileInfo>() {

			@Override
			public int compare(FileInfo fi1, FileInfo fi2) {

				int d1 = fi1.IsDir ? 0 : 1;
				int d2 = fi2.IsDir ? 0 : 1;
				if (d1 == d2) {
					return fi1.fileName.compareTo(fi2.fileName);
				} else {
					return d1 - d2;// 升序排列
				}
			}

		});
	}

	public void goToParentDir() {
		File currentDir = mController.getCurrentDir();
		String parentDir = currentDir.getParent();
		Log.d(TAG, "p_dir=" + currentDir.getParent());
		if (parentDir == null) {
			return;
		}
		mController.setCurrentDir(new File(currentDir.getParent()));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mFileMenu.isShowing()) {
				mFileMenu.dismiss();
			} else if (mController.getCurrentDir().getParent() != null) {
				goToParentDir();
			} else {
				finish();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	
	void setupBackground(){
	    // 获取壁纸管理器  
        WallpaperManager wallpaperManager = WallpaperManager  
                .getInstance(this);  
        // 获取当前壁纸  
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();  
        // 将Drawable,转成Bitmap  
        Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();  
        
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int mScreenWidth = metric.widthPixels;
        int mScreenHeight = metric.heightPixels;
        
        
        

        // 需要详细说明一下，mScreenCount、getCurrentWorkspaceScreen()、mScreenWidth、mScreenHeight分别  
        //对应于Launcher中的桌面屏幕总数、当前屏幕下标、屏幕宽度、屏幕高度.等下拿Demo的哥们稍微要注意一下  
        float step = 0;  
        // 计算出屏幕的偏移量  

        Bitmap pbm = Bitmap.createBitmap(bm, (bm.getWidth()-mScreenWidth)/2, 0,  
                mScreenWidth,  
                mScreenHeight);
        // 设置 背景  
        mImgBg.setBackgroundDrawable(new BitmapDrawable(pbm));  
	}
	
	Handler mHandler = new Handler() {

	};
	
	
	
}
