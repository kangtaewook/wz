package com.vinetech.wezone.Notice;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.LibDateUtil;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Notice;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.util.ArrayList;


public final class NoticeListAdapter extends BaseAdapter
{

	private BaseActivity mActivity;

	private ArrayList<Data_Notice> mListItems;

	private int lastPosition = -1;

	private LayoutInflater mInflater;


	public NoticeListAdapter(BaseActivity activity, ArrayList<Data_Notice> listItems)
	{
		mActivity = activity;

		mListItems = listItems;

		mInflater = LayoutInflater.from(mActivity);

	}

	@Override
	public int getCount()
	{
		return (mListItems!=null)?mListItems.size():0;
	}

	@Override
	public Object getItem(int position)
	{
		return (mListItems!=null && position < mListItems.size())?mListItems.get(position) : null;
	}

	@Override
	public long getItemId(int position) { return position; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;

		if(convertView == null)
		{

			convertView = mInflater.inflate(R.layout.notice_list_item, null);

			holder = new ViewHolder();

			holder.imageview_profile = (ImageView)convertView.findViewById(R.id.imageview_profile);

			holder.linearlayout_frame = (LinearLayout)convertView.findViewById(R.id.linearlayout_frame);

			holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);

			holder.imageview_msg_icon = (ImageView) convertView.findViewById(R.id.imageview_msg_icon);

			holder.textview_message = (TextView) convertView.findViewById(R.id.textview_message);

			holder.linearlayout_noti_cnt_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_noti_cnt_area);
			holder.textview_noti_cnt = (TextView) convertView.findViewById(R.id.textview_noti_cnt);

//			holder.linearlayout_icon_new = (LinearLayout) convertView.findViewById(R.id.linearlayout_icon_new);

			holder.textview_time = (TextView) convertView.findViewById(R.id.textview_time);

			convertView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}

		if( holder == null ) return null;


		Data_Notice notiItem = mListItems.get(position);

        holder.linearlayout_frame.setVisibility(View.VISIBLE);

        if(WezoneUtil.isEmptyStr(notiItem.sender_url)) {
            holder.imageview_profile.setImageResource(R.drawable.im_bunny_photo);
        }else{
			mActivity.showImageFromRemote(notiItem.sender_url,R.drawable.im_bunny_photo,holder.imageview_profile);
        }

        holder.textview_name.setText(notiItem.sender_name);


		holder.imageview_msg_icon.setVisibility(View.GONE);

        holder.textview_message.setText(notiItem.content);

		holder.textview_time.setText(LibDateUtil.getPassTime(mActivity,notiItem.create_datetime,Define.DEFUALT_DATE_FORMAT));

		return convertView;
	}

	public final class ViewHolder
	{

		public ImageView imageview_profile;

		public LinearLayout linearlayout_frame;

		public TextView textview_name;

		public ImageView imageview_msg_icon;
		public TextView textview_message;

		public LinearLayout linearlayout_noti_cnt_area;
		public TextView textview_noti_cnt;
		public TextView textview_time;
	}



}


