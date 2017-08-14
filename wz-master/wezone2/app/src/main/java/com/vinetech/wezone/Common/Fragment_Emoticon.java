package com.vinetech.wezone.Common;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.vinetech.wezone.R;

public final class Fragment_Emoticon extends Fragment 
{
	private Activity mActivity;
	
	 private GridView mGridView;
	 
	 private int mGridViewLayoutId, mGridViewItemLayoutId;
	 
	 private Adapter_Emoticon_GridView mAdapter;
	 
	 private ArrayList<EmoticonItem> mEmotionItems;
	 
	 private int mTouchDownPosition;
	 
	 //private OnItemClickListener mListener = null;
	 
	 private OnTouchDownListener mListener;
	 
	 public interface OnTouchDownListener
	 {
		 public void onEmoticonTouchDown(int position);
	 }

	public Fragment_Emoticon(){

	}

	public Fragment_Emoticon(ArrayList<EmoticonItem> emoticonitems, Activity activity, OnTouchDownListener listener) 
	{
		  this(emoticonitems, activity, listener, R.layout.fragment_emoticon, R.layout.gridview_emoticon_item);
	 }
	
	public Fragment_Emoticon(ArrayList<EmoticonItem> emoticonitems, Activity activity, OnTouchDownListener listener, int gridViewLayoutId, int gridViewItemLayoutId) 
	{
		  this.mEmotionItems = emoticonitems;
		  this.mActivity = activity;
		  this.mListener = listener;
		  this.mGridViewLayoutId = gridViewLayoutId;
		  this.mGridViewItemLayoutId = gridViewItemLayoutId;
		  mTouchDownPosition = -1;
	 }	
	 

	
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	 {
		  View view = inflater.inflate(mGridViewLayoutId, container, false);
		  mGridView = (GridView)view.findViewById(R.id.Gridview_emoticon);
		  
		  mGridView.setOnTouchListener(new View.OnTouchListener() 
		  {
		        public boolean onTouch(View v, MotionEvent me) 
		        {
		            if( mListener != null )
		            {
		            	int position = mGridView.pointToPosition((int)me.getX(), (int)me.getY());
		            	
		            	switch(me.getActionMasked())
		            	{
		            	case MotionEvent.ACTION_DOWN:
		            		mTouchDownPosition = position;
		            		break;
		            		
		            	case MotionEvent.ACTION_UP:
				            if( position >= 0 && mTouchDownPosition == position )
				            	mListener.onEmoticonTouchDown(position);
				            
				            mTouchDownPosition = -1;
		            		break;
		            		
		            	case MotionEvent.ACTION_CANCEL:
		            	case MotionEvent.ACTION_OUTSIDE:
		            		mTouchDownPosition = -1;
		            		break;		            		
		            	}
		            }
		            return false;
		        }
		  });
		  
		  return view;
	 }
	 
	 @Override
	 public void onActivityCreated(Bundle savedInstanceState) 
	 {
		 super.onActivityCreated(savedInstanceState);
	 
		 if( mActivity != null ) 
		 {
			 mAdapter = new Adapter_Emoticon_GridView(mActivity, mEmotionItems,mGridViewItemLayoutId);
			 if (mGridView != null)
				 mGridView.setAdapter(mAdapter);
			  //mGridView.setOnItemClickListener(mListener);
	  	}
	 }

//	 @Override
//	public void onDestroy() 
//	 {
//		if(mGridView !=  null)
//		{
//			int count = mGridView.getChildCount();
//			
//			View v;
//			for(int i=0; i < count; i++)
//			{
//				if( (v = mGridView.getChildAt(i)) instanceof ImageView)
//				{
//					ImageView iv = (ImageView)v;
//					UIBitmap.recycleBitmap(iv);
//				}
//			}
//		}
//		super.onDestroy();
//		
//	}

}
