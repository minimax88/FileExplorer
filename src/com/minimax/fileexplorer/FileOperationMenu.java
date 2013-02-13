package com.minimax.fileexplorer;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class FileOperationMenu extends PopupWindow implements OnClickListener {

	private Context mContext;
	private TextView tvFileName;
	private View mCancel,mDetails;
	private File mFile;
	public File getFile() {
		return mFile;
	}

	public void setFile(File mFile) {
		this.mFile = mFile;
	}

	FileOperationMenu(Context context) {
		// super(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		super(context);
		mContext = context;
		LayoutInflater mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupView = mLayoutInflater
				.inflate(R.layout.file_popup_menu, null);

		setContentView(popupView);
		tvFileName = (TextView) popupView.findViewById(R.id.tv_file_name);
		mCancel = (LinearLayout) popupView.findViewById(R.id.popup_cancel);
		mCancel.setOnClickListener(this);
		mDetails = (LinearLayout) popupView.findViewById(R.id.popup_details);
		mDetails.setOnClickListener(this);

		// 下面二行必须要指定，否则无法显示
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);

		setAnimationStyle(R.style.popup_animation);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(false);

	}

	public void show(View v) {
		tvFileName.setText(mFile.getName());
		showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);
	}

	public void setFileName(String fileName) {
		tvFileName.setText(fileName);
	}

	@Override
	public void onClick(View v) {
		
		dismiss();
		int id = v.getId();
		switch (id) {
		case R.id.popup_cancel:
			break;
		case R.id.popup_details:
			DetailsDialog dialog = new DetailsDialog(mContext);
			dialog.setFile(mFile);
			dialog.show();
			break;
		default:
			break;
		}
		
	}


}
