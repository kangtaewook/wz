package com.vinetech.wezone.Common;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.R;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class SelectNotificationListAdapter extends BaseAdapter {

    private SelectNotificationActivity mActivity;

    private LayoutInflater mInflater;

    private String[] mStrArray;
    private Data_ActionItem mSelectedItem;

    private String mONOFF;

    public static final String OFF = "OFF";
    public static final String ON = "ON";

    public static final String ID_NOT_USE = "0";
    public static final String ID_SOUND = "1";
    public static final String ID_PUSH_MSG = "2";
    public static final String ID_IMAGE = "3";
    public static final String ID_APP = "4";
    public static final String ID_EMAIL = "5";
    public static final String ID_CAMERA = "8";
    public static final String ID_SOS = "9";
    public static final String ID_NOTIC = "6";

    public SelectNotificationListAdapter(SelectNotificationActivity a, String[] strArray, Data_ActionItem selectedItem, String OnOff){
        mActivity = a;
        mStrArray = strArray;
        mSelectedItem = selectedItem;
        mInflater = LayoutInflater.from(mActivity);
        mONOFF = OnOff;
    }

    @Override
    public int getCount() {
        return mStrArray.length;
    }

    @Override
    public Object getItem(int i) {
        return mStrArray[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.select_notification_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.linearlayout_item_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_item_area);
            holder.ImageView_select_notification_list_item_check = (ImageView) convertView.findViewById(R.id.ImageView_select_notification_list_item_check);
            holder.textview_title = (TextView) convertView.findViewById(R.id.textview_title);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

      String strId = mStrArray[position];
        holder.textview_title.setText(Data_ActionItem.getTitleText(strId));


        if(strId.equals(mSelectedItem.id)){
            holder.linearlayout_item_area.setSelected(true);


            if(mONOFF.equals(ON)) {
                if(mSelectedItem.data != null && mSelectedItem.data.get(0) != null){
                    if(mSelectedItem.id == Data_ActionItem.ID_APP) {
                        holder.textview_desc.setText(mSelectedItem.data.get(1).value);
                    }else {
                        holder.textview_desc.setText(mSelectedItem.data.get(0).value);
                    }
                }
                holder.textview_title.setTextColor(mActivity.getResources().getColor(R.color.notification_text_title_selected_color));
                holder.textview_desc.setTextColor(mActivity.getResources().getColor(R.color.notification_text_desc_selected_color));
                holder.ImageView_select_notification_list_item_check.setSelected(true);
            }else{
                holder.textview_title.setTextColor(mActivity.getResources().getColor(R.color.notification_text_not_use_color));
                holder.textview_desc.setTextColor(mActivity.getResources().getColor(R.color.notification_text_not_use_color));
                holder.ImageView_select_notification_list_item_check.setSelected(false);
//                holder.ImageView_select_notification_list_item_check.setColorFilter(R.color.notification_text_not_use_color);

            }

        }else if(mONOFF.equals(OFF)) {
            //비활성화 화면

            holder.textview_title.setTextColor(mActivity.getResources().getColor(R.color.notification_text_not_use_color));
            holder.textview_desc.setTextColor(mActivity.getResources().getColor(R.color.notification_text_not_use_color));
            holder.ImageView_select_notification_list_item_check.setSelected(false);

        }else{
            holder.linearlayout_item_area.setSelected(false);
            if(strId.equals(ID_SOUND)){
                holder.textview_desc.setText("TheShow");
            }else if(strId.equals(ID_PUSH_MSG)){
                holder.textview_desc.setText("메세지 알림 내용을 직접 설정할 수 있습니다.");
            }else if(strId.equals(ID_IMAGE)){
                holder.textview_desc.setText("알림시 직접 설정한 이미지를 보여 줍니다.");
            }else if(strId.equals(ID_APP)){
                holder.textview_desc.setText("wezone앱을 자동 실행합니다.");
            }else if(strId.equals(ID_EMAIL)){
                holder.textview_desc.setText("wezone@mail.com");
            }else if(strId.equals(ID_CAMERA)){
                holder.textview_desc.setText("즉시 카메라를 실행 시켜 사진촬영 합니다.");
            }else if(strId.equals(ID_SOS)){
                holder.textview_desc.setText("긴급 시 친구들에게 메세지를 보냅니다.");
            }else{

            }

            holder.textview_title.setTextColor(mActivity.getResources().getColor(R.color.notification_text_title_color));
            holder.textview_desc.setTextColor(mActivity.getResources().getColor(R.color.notification_text_desc_color));
            holder.ImageView_select_notification_list_item_check.setSelected(false);
        }

        return convertView;
    }

    class ViewHolder {
        public LinearLayout linearlayout_item_area;
        public ImageView ImageView_select_notification_list_item_check;
        public TextView textview_title;
        public TextView textview_desc;
    }


}
