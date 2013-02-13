package com.minimax.fileexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DetailsDialog extends AlertDialog {

	public  static final String TAG = "DetailsDialog";
	File mFile;
	TextView tvPermissions;
	TextView tvOwner;
	TextView tvGroup;
	TextView tvMime;
	TextView tvPath;
	TextView tvSize;
	TextView tvLastModifiedTime;

	protected DetailsDialog(Context context) {
		super(context, R.style.file_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_details);

		
		tvPermissions = (TextView)findViewById(R.id.tv_permissions_value);
		tvOwner = (TextView)findViewById(R.id.tv_owner_value);
		tvGroup = (TextView)findViewById(R.id.tv_group_value);
		tvPath = (TextView) findViewById(R.id.tv_path_value);
		tvSize = (TextView) findViewById(R.id.tv_size_value);
		tvMime = (TextView)findViewById(R.id.tv_mime_value);;
		tvLastModifiedTime = (TextView) findViewById(R.id.tv_mtime_value);

		View v= findViewById(R.id.popup_cancel);
		v.setOnClickListener(new android.view.View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});

	}

	void setFile(File file) {
		mFile = file;
	}

	public void getValues() {
		tvPath.setText(mFile.getPath());
		tvSize.setText(Util.getReadableFileSize(mFile.length()));
		tvLastModifiedTime.setText(Util.getTime(mFile.lastModified()));
		tvMime.setText(Util.getMimeType(mFile.getName()));
		try {
			Process process = Runtime.getRuntime().exec(
					" ls -l " + mFile.getPath());
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String outputString = buffer.readLine();
			Log.d(TAG,outputString);
			String[]props = outputString.split("\\s+");
			if(props.length>2){
				tvPermissions.setText(props[0].substring(1,props[0].length()-1));
				tvOwner.setText(props[1]);
				tvGroup.setText(props[2]);
			}
			for(String prop:props){
				Log.d(TAG,prop); // -------输出null ,
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void show() {

		super.show();// onCreate会在show()之后调用
		getValues();
	}
}
