package com.vinetech.wezone.Message;


import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.ImageGetter;
import com.vinetech.util.LibDateUtil;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.Data.Data_Message;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.util.ArrayList;
import java.util.HashSet;

public final class ChattingListAdapter extends BaseAdapter
{

	private ChattingActivity mActivity;

	private ArrayList<Data_Message> mListItems;

	private int lastPosition = -1;

	private LayoutInflater mInflater;

	private String user_uuid;

	private Data_OtherUser mOtherUser;

	private boolean isDisposed;

	private ImageGetter mImageGetter;
	private HashSet<String> mThumbEmoticonSet;
	private HashSet<String> mEmoticonSet;

	public ChattingListAdapter(ChattingActivity activity, ArrayList<Data_Message> listItems)
	{
		mActivity = activity;

		mListItems = listItems;

		mImageGetter = new ImageGetter(mActivity);

		mInflater = LayoutInflater.from(mActivity);

		user_uuid = mActivity.getUuid();

		mThumbEmoticonSet = new HashSet<String>();

		for( String thumbEmoticonText : Define.Emoticon.THUMB_EMOTICON_TEXTS )
			mThumbEmoticonSet.add(thumbEmoticonText);

		mEmoticonSet = new HashSet<String>();

		for( String EmoticonText : Define.Emoticon.EMOTICON_TEXTS )
			mEmoticonSet.add(EmoticonText);


	}

	public void setOtherUser(Data_OtherUser user){
		mOtherUser = user;
	}

	public Data_OtherUser getOtherUser(){
		return mOtherUser;
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

	public String getText(int position) {
		Data_Message item = mListItems.get(position - 1);
		return item.txt;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;

		if(convertView == null)
		{
			convertView = mInflater.inflate(R.layout.chatting_list_item, null);

			holder = new ViewHolder();
			holder.linearlayout_other_chat_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_other_chat_area);

			holder.imageview_other_profile = (ImageView) convertView.findViewById(R.id.imageview_other_profile);
			holder.linearlayout_frame = (LinearLayout) convertView.findViewById(R.id.linearlayout_frame);

			holder.textview_other_name = (TextView) convertView.findViewById(R.id.textview_other_name);

			// other chat & call
			holder.linearlayout_other_type_chat = (LinearLayout) convertView.findViewById(R.id.linearlayout_other_type_chat);

			holder.imageview_other_icon = (ImageView) convertView.findViewById(R.id.imageview_other_icon);
			holder.linearlayout_other_contents_bg = (LinearLayout) convertView.findViewById(R.id.linearlayout_other_contents_bg);
			holder.textview_other_message = (TextView) convertView.findViewById(R.id.textview_other_message);

			holder.textview_other_read = (TextView) convertView.findViewById(R.id.textview_other_read);
			holder.textview_other_time = (TextView) convertView.findViewById(R.id.textview_other_time);

			holder.linearlayout_my_chat_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_my_chat_area);

//			holder.textview_my_name = (TextView) convertView.findViewById(R.id.textview_my_name);

			// my chat or call
			holder.linearlayout_my_type_chat = (LinearLayout) convertView.findViewById(R.id.linearlayout_my_type_chat);

			holder.imageview_my_icon = (ImageView) convertView.findViewById(R.id.imageview_my_icon);
			holder.linearlayout_my_contents_bg = (LinearLayout) convertView.findViewById(R.id.linearlayout_my_contents_bg);
			holder.textview_my_message = (TextView) convertView.findViewById(R.id.textview_my_message);

			holder.textview_my_read = (TextView) convertView.findViewById(R.id.textview_my_read);
			holder.textview_my_time = (TextView) convertView.findViewById(R.id.textview_my_time);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}

		if( holder == null ) return null;

		Data_Message item = mListItems.get(position);

		holder.linearlayout_other_chat_area.setVisibility(View.GONE);
		holder.linearlayout_my_chat_area.setVisibility(View.GONE);

		holder.linearlayout_frame.setVisibility(View.VISIBLE);



		if(user_uuid.equals(item.sfrom)){
			holder.linearlayout_my_chat_area.setVisibility(View.VISIBLE);

			holder.linearlayout_my_type_chat.setVisibility(View.GONE);


            holder.linearlayout_my_type_chat.setVisibility(View.VISIBLE);
            holder.imageview_my_icon.setVisibility(View.GONE);

//            String decodeStr = UIControl.decodeBase64WithString(item.content);
			String contents = item.txt;
			int contentsLength = (contents!=null)?contents.length():0;
			String htmlContents = null;
			htmlContents  = WezoneUtil.toRichContentHtmlText(contents);
			htmlContents = WezoneUtil.toReplaceNBSP(htmlContents);

//			setHtmlText(holder.textview_my_message, htmlContents);
			if( contents != null && mEmoticonSet.contains(contents)  == true )
			{
				if( contentsLength < 5 )
				{
					mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_MID);

					setHtmlText(holder.textview_my_message, htmlContents);
					holder.linearlayout_my_contents_bg.setBackgroundColor(Color.TRANSPARENT);
				}
				else
				{
					mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_SMALL);

					setHtmlText(holder.textview_my_message, htmlContents);

					holder.linearlayout_my_contents_bg.setBackgroundResource(R.drawable.text_sheet_blue);
				}
			}else{
				if( contents != null && mThumbEmoticonSet.contains(contents) == true ){
					mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_MID);
					holder.linearlayout_my_contents_bg.setBackgroundColor(Color.TRANSPARENT);
				}else{
					mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_SMALL);
					holder.linearlayout_my_contents_bg.setBackgroundResource(R.drawable.text_sheet_blue);
				}
				setHtmlText(holder.textview_my_message, htmlContents);
			}

//			if(WezoneUtil.isEmptyStr(item.created_at)){
//				String currentTime = LibDateUtil.getCurrentTime(Define.DEFUALT_DATE_FORMAT);
//				holder.textview_my_time.setText(LibDateUtil.getPassTime(mActivity,currentTime,Define.DEFUALT_DATE_FORMAT));
//			}else{
//				holder.textview_my_time.setText(LibDateUtil.getPassTime(mActivity, item.created_at, Define.DEFUALT_DATE_FORMAT));
//			}

			if(WezoneUtil.isEmptyStr(item.created_at)){
				String currentTime = LibDateUtil.getCurruntDayWithFormat(Define.DEFUALT_DATE_FORMAT);
				holder.textview_my_time.setText(LibDateUtil.getChangeDateType(Define.DEFUALT_DATE_FORMAT,"a KK:mm",currentTime));
			}else{
				holder.textview_my_time.setText(LibDateUtil.getChangeDateType(Define.DEFUALT_DATE_FORMAT,"a KK:mm",item.created_at));
			}

			String readCnt = WezoneUtil.isEmptyStr(item.is_read) ? "0" : item.is_read;
			holder.textview_my_read.setText(readCnt + " 읽음");

		}else{

			holder.linearlayout_other_chat_area.setVisibility(View.VISIBLE);

			if(WezoneUtil.isEmptyStr(item.img_url)) {
				holder.imageview_other_profile.setImageResource(R.drawable.im_bunny_photo);
			}else{
				mActivity.showImageFromRemote(item.img_url,R.drawable.im_bunny_photo,holder.imageview_other_profile);
			}

            if(WezoneUtil.isEmptyStr(item.user_name)){
                holder.textview_other_name.setVisibility(View.GONE);
            }else{
                holder.textview_other_name.setVisibility(View.VISIBLE);
                holder.textview_other_name.setText(item.user_name);
            }

            holder.linearlayout_other_type_chat.setVisibility(View.VISIBLE);
			holder.imageview_other_icon.setVisibility(View.GONE);

//            String decodeStr = UIControl.decodeBase64WithString(item.content);

			String contents = item.txt;
			int contentsLength = (contents!=null)?contents.length():0;
			String htmlContents = null;
			htmlContents  = WezoneUtil.toRichContentHtmlText(contents);
			htmlContents = WezoneUtil.toReplaceNBSP(htmlContents);

//			setHtmlText(holder.textview_my_message, htmlContents);
			if( contents != null && mEmoticonSet.contains(contents)  == true )
			{
				if( contentsLength < 5 )
				{
					mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_MID);

					setHtmlText(holder.textview_other_message, htmlContents);
					holder.linearlayout_other_contents_bg.setBackgroundColor(Color.TRANSPARENT);
				}
				else
				{
					mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_SMALL);

					setHtmlText(holder.textview_other_message, htmlContents);

					holder.linearlayout_other_contents_bg.setBackgroundResource(R.drawable.text_sheet_white);
				}
			}else{
				if( contents != null && mThumbEmoticonSet.contains(contents) == true ){
					mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_MID);
					holder.linearlayout_other_contents_bg.setBackgroundColor(Color.TRANSPARENT);
				}else{
					mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_SMALL);
					holder.linearlayout_other_contents_bg.setBackgroundResource(R.drawable.text_sheet_white);
				}
				setHtmlText(holder.textview_other_message, htmlContents);
			};

//			if(WezoneUtil.isEmptyStr(item.created_at)){
//				String currentTime = LibDateUtil.getCurruntDayWithFormat(Define.DEFUALT_DATE_FORMAT);
//				holder.textview_other_time.setText(LibDateUtil.getPassTime(mActivity,currentTime,Define.DEFUALT_DATE_FORMAT));
//			}else{
//				holder.textview_other_time.setText(LibDateUtil.getPassTime(mActivity,item.created_at,Define.DEFUALT_DATE_FORMAT));
//			}

			if(WezoneUtil.isEmptyStr(item.created_at)){
				String currentTime = LibDateUtil.getCurruntDayWithFormat(Define.DEFUALT_DATE_FORMAT);
				holder.textview_other_time.setText(LibDateUtil.getChangeDateType(Define.DEFUALT_DATE_FORMAT,"a KK:mm",currentTime));
			}else{
				holder.textview_other_time.setText(LibDateUtil.getChangeDateType(Define.DEFUALT_DATE_FORMAT,"a KK:mm",item.created_at));
			}


			String readCnt = WezoneUtil.isEmptyStr(item.is_read) ? "0" : item.is_read;
			holder.textview_other_read.setText(readCnt + " 읽음");
		}
		return convertView;
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}


	private void setHtmlText(TextView textView, String htmlContents)
	{
		if( htmlContents != null ){
			textView.setText(Html.fromHtml(htmlContents, mImageGetter, null));
		}
		else {
			textView.setText(null);
		}
	}

	public final class ViewHolder
	{
		private LinearLayout linearlayout_other_chat_area;

		private ImageView imageview_other_profile;
		private LinearLayout linearlayout_frame;

		private TextView textview_other_name;

		private LinearLayout linearlayout_other_type_chat;
		private ImageView imageview_other_icon;

		private LinearLayout linearlayout_other_contents_bg;
		private TextView textview_other_message;

		private TextView textview_other_read;
		private TextView textview_other_time;

		private LinearLayout linearlayout_my_chat_area;

		private LinearLayout linearlayout_my_type_chat;
		private LinearLayout linearlayout_my_contents_bg;
		private ImageView imageview_my_icon;
		private TextView textview_my_message;
		private TextView textview_my_time;
		private TextView textview_my_read;

	}

}


