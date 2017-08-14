package com.vinetech.wezone.Common;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.vinetech.util.UIControl;
import com.vinetech.wezone.R;


public class Adapter_Emoticon_GridView extends BaseAdapter {
	 
	 private Context mContext;
	 private ArrayList<EmoticonItem> mItems;
	 private LayoutInflater mInflater;
	 private int mItemLayoutId;
	 
	 public Adapter_Emoticon_GridView(Context context, ArrayList<EmoticonItem> item) {
//		 this.mContext = context;
//		 this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		 this.items = item;
		 this(context, item, R.layout.gridview_emoticon_item);
	 }
	 
	 public Adapter_Emoticon_GridView(Context context, ArrayList<EmoticonItem> item, int itemLayoutId)
	 {
		 this.mContext = context;
		 this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 this.mItems = item;
		 this.mItemLayoutId = itemLayoutId;
	 }
	 
	 public ArrayList<EmoticonItem> getItems() { return mItems; }
	 
	 public void setItems(ArrayList<EmoticonItem> items) { this.mItems = items; }
	 
	 @Override
	 public int getCount() 
	 {
		 return (mItems != null)?mItems.size():0; 
	 }
	 
//	 @Override
//	 public void notifyDataSetChanged() {
//		 super.notifyDataSetChanged();
//	 }
	 
	 @Override
	 public Object getItem(int position) 
	 {
		 if (mItems != null && position >= 0 && position < getCount()) 
		 {
			 return mItems.get(position);
		 }
		 return null;
	 }
	 
	 @Override
	 public long getItemId(int position) 
	 {
		 if( mItems != null && position >= 0 && position < getCount()) 
		 {
			 return mItems.get(position).Id;
		 }
		 return 0;
	 }
	 
	 public void setItemsList(ArrayList<EmoticonItem> locations) 
	 {
		 this.mItems = locations;
	 }
	 
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) 
	 {
		 ImageView imageView;
		 
		 if(convertView == null) 
		 {
			 convertView = mInflater.inflate(mItemLayoutId, parent, false);

			 imageView = (ImageView) convertView.findViewById(R.id.ImageView_emoticon);
			 
			 convertView.setTag(imageView);
		 }
		 else
		 {
			 imageView = (ImageView)convertView.getTag();
		 }

		 UIControl.setBackgroundDrawable(mContext, imageView, mItems.get(position).Id);
		 
		 return convertView;
	 }
}
