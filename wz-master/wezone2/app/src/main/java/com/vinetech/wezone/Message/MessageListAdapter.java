package com.vinetech.wezone.Message;


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
import com.vinetech.wezone.Data.Data_Chat_UserList;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.util.ArrayList;

import static com.vinetech.wezone.R.id.imageview_profile;


public final class MessageListAdapter extends BaseAdapter
{

	public static int TYPE_NORMAL = 0;
	public static int TYPE_EDIT = 1;

	private int mType;
	public void setType(int type){
		mType = type;
	}
	public int getType(){return this.mType;}

	private MessageListActivity mActivity;

	private ArrayList<Data_Chat_UserList> mListItems;

	private int lastPosition = -1;

	private LayoutInflater mInflater;

	private String user_uuid;

	private ImageGetter mImageGetter;

	public MessageListAdapter(MessageListActivity activity,int type, ArrayList<Data_Chat_UserList> listItems)
	{
		mActivity = activity;

		mListItems = listItems;

		mInflater = LayoutInflater.from(mActivity);

		user_uuid = mActivity.getUuid();

		mImageGetter = new ImageGetter(mActivity);

		setType(type);
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

			convertView = mInflater.inflate(R.layout.message_list_item, null);

			holder = new ViewHolder();

			holder.linearlayout_bg = (LinearLayout) convertView.findViewById(R.id.linearlayout_bg);

			holder.linearlayout_btn_check = (LinearLayout)convertView.findViewById(R.id.linearlayout_btn_check);

			holder.imageview_profile = (ImageView)convertView.findViewById(imageview_profile);

			holder.linearlayout_frame = (LinearLayout)convertView.findViewById(R.id.linearlayout_frame);

			holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);

			holder.imageview_msg_icon = (ImageView) convertView.findViewById(R.id.imageview_msg_icon);

			holder.textview_message = (TextView) convertView.findViewById(R.id.textview_message);


			holder.linearlayout_member_cnt_area = (LinearLayout)convertView.findViewById(R.id.linearlayout_member_cnt_area);
			holder.textview_member_cnt = (TextView)convertView.findViewById(R.id.textview_member_cnt);
			holder.linearlayout_push_flag = (LinearLayout)convertView.findViewById(R.id.linearlayout_push_flag);


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

		Data_Chat_UserList userList = mListItems.get(position);

		holder.linearlayout_bg.setOnClickListener(mOnClickListener);
		holder.linearlayout_bg.setTag(position);

        holder.linearlayout_frame.setVisibility(View.VISIBLE);

        if(WezoneUtil.isEmptyStr(userList.img_url)) {
            holder.imageview_profile.setImageResource(R.drawable.im_bunny_photo);
        }else{
			mActivity.showImageFromRemote(userList.img_url, R.drawable.im_bunny_photo, holder.imageview_profile);
        }


        if(getType() == TYPE_NORMAL){
            holder.linearlayout_btn_check.setVisibility(View.GONE);
        }else{
            holder.linearlayout_btn_check.setVisibility(View.VISIBLE);

            if(userList.isSelected){
                holder.linearlayout_btn_check.setSelected(true);
            }else{
                holder.linearlayout_btn_check.setSelected(false);
            }
        }

		if("groupchat".equals(userList.kind)){
			holder.linearlayout_member_cnt_area.setVisibility(View.VISIBLE);

			holder.textview_member_cnt.setText(userList.member_count);
			holder.textview_name.setText("[위존]"+userList.user_name);
		}else{
			holder.linearlayout_member_cnt_area.setVisibility(View.GONE);
			holder.textview_name.setText(userList.user_name);
		}


		if("T".equals(userList.push_flag)){
			holder.linearlayout_push_flag.setVisibility(View.GONE);
		}else{
			holder.linearlayout_push_flag.setVisibility(View.VISIBLE);
		}

		holder.imageview_msg_icon.setVisibility(View.GONE);

//        String decodeStr = UIControl.decodeBase64WithString(userList.txt);
		String htmlContents = null;
		htmlContents  = WezoneUtil.toRichContentHtmlText(userList.txt);
		htmlContents = WezoneUtil.toReplaceNBSP(htmlContents);
		mImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_SMALL);
		setHtmlText(holder.textview_message, htmlContents);

		holder.textview_time.setText(LibDateUtil.getPassTime(mActivity,userList.update_at,Define.DEFUALT_DATE_FORMAT));


		if(userList.unread != null && userList.unread.msgkeys != null && userList.unread.msgkeys.size() > 0){
			holder.linearlayout_noti_cnt_area.setVisibility(View.VISIBLE);
			holder.textview_noti_cnt.setText(String.valueOf(userList.unread.msgkeys.size()));
		}else{
			holder.linearlayout_noti_cnt_area.setVisibility(View.GONE);
		}

		return convertView;
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

		public LinearLayout linearlayout_bg;

		public LinearLayout linearlayout_btn_check;


		public ImageView imageview_profile;

		public LinearLayout linearlayout_frame;


		public TextView textview_name;

		public ImageView imageview_msg_icon;
		public TextView textview_message;

		public LinearLayout linearlayout_member_cnt_area;
		public TextView textview_member_cnt;

		public LinearLayout linearlayout_push_flag;

		public LinearLayout linearlayout_noti_cnt_area;
		public TextView textview_noti_cnt;

		public TextView textview_time;
	}


	public View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			int pos = (int)v.getTag();

//			if(getType() == TYPE_EDIT){
//				if(mListItems.get(pos).isSelected()){
//					mListItems.get(pos).setSelected(false);
//				}else{
//					mListItems.get(pos).setSelected(true);
//				}
//				notifyDataSetChanged();
//			}else{

			Data_Chat_UserList msgList = mListItems.get(pos);

			Data_OtherUser other = new Data_OtherUser();
			if("groupchat".equals(msgList.kind)){
				String temp[] = msgList.other_uuid.split("@");
				other.user_uuid = temp[0];
				other.user_name = msgList.user_name;
				other.img_url = msgList.img_url;
			}else{
				other.user_uuid = msgList.other_uuid;
				other.user_name = msgList.user_name;
				other.img_url = msgList.img_url;
			}

			if("chat".equals(msgList.kind)){
				ChattingActivity.startActivity(mActivity,other);
			}else{
				ChattingActivity.startActivityWithGroup(mActivity,other);
			}

		}
	};

}


