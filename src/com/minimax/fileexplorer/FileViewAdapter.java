/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.minimax.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FileViewAdapter extends ArrayAdapter<FileInfo> {
    private LayoutInflater mInflater;



    private Context mContext;
    private List<FileInfo>mFileList;

    public FileViewAdapter(Context context, int resource,
            List<FileInfo> fileList) {
        super(context, resource, fileList);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mFileList = fileList;
        
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout.gv_file_item, parent, false);
        }
        FileInfo fileInfo= mFileList.get(position);
        TextView tv = (TextView)view.findViewById(R.id.file_name);
        ImageView imageView = (ImageView)view.findViewById(R.id.file_icon);
        
        if(fileInfo.IsDir){
        	imageView.setImageResource(R.drawable.directory_icon);
        }else{
        	imageView.setImageResource(R.drawable.file_icon_normal);
        }
        
        tv.setText(fileInfo.fileName);
        

        
        return view;
    }
}
