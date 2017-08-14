package com.vinetech.wezone.Main;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_BunnyZone;
import com.vinetech.wezone.R;

import java.util.ArrayList;

import static com.vinetech.wezone.R.id.imageview_icon;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class BunnyZoneListAdapter extends BaseAdapter {

    private ArrayList<Data_BunnyZone> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    public BunnyZoneListAdapter(BaseActivity a, ArrayList<Data_BunnyZone> bunnyzoneInfos){
        mActivity = a;
        mListItem = bunnyzoneInfos;

        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.bunnyzone_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_icon = (ImageView) convertView.findViewById(imageview_icon);

            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_beacon_cnt = (TextView) convertView.findViewById(R.id.textview_beacon_cnt);

            holder.linearlayout_notice = (LinearLayout) convertView.findViewById(R.id.linearlayout_notice);
            holder.imageview_notice = (ImageView) convertView.findViewById(R.id.imageview_notice);
            holder.textview_notice = (TextView) convertView.findViewById(R.id.textview_notice);

            holder.linearlayout_user_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_user_area);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_BunnyZone bunnyzoneInfo = mListItem.get(position);

        if(WezoneUtil.isEmptyStr(bunnyzoneInfo.img_url) == false){
            mActivity.showImageFromRemote(bunnyzoneInfo.img_url,R.drawable.im_beacon_add,holder.imageview_icon);
        }else{
            holder.imageview_icon.setImageResource(R.drawable.im_beacon_add);
        }

        holder.textview_name.setText(bunnyzoneInfo.title);

        if("T".equals(bunnyzoneInfo.push_flag)){
            holder.linearlayout_notice.setBackgroundResource(R.drawable.notifications_on);
            holder.imageview_notice.setImageResource(R.drawable.ic_noti);
            holder.textview_notice.setTextColor(Color.parseColor("#ffb448"));
        }else{
            holder.linearlayout_notice.setBackgroundResource(R.drawable.notifications_off);
            holder.imageview_notice.setImageResource(R.drawable.ic_noti_off);
            holder.textview_notice.setTextColor(Color.parseColor("#bebebe"));
        }

        if(bunnyzoneInfo.beacons != null && bunnyzoneInfo.beacons.size() > 0){
            holder.textview_beacon_cnt.setText("Beacon " + String.valueOf(bunnyzoneInfo.beacons.size()));

            holder.linearlayout_user_area.removeAllViews();
            for(Data_Beacon beacon : bunnyzoneInfo.beacons){
                View beaconLayout = mActivity.getLayoutInflater().inflate(R.layout.beacon_image, null, false);
                ImageView imageview_beacon_profile = (ImageView)beaconLayout.findViewById(R.id.imageview_beacon_profile);
                if(WezoneUtil.isEmptyStr(beacon.img_url) == false){
                    mActivity.showImageFromRemote(beacon.img_url,R.drawable.im_beacon_add,imageview_beacon_profile);
                }else{
                    imageview_beacon_profile.setImageResource(R.drawable.im_beacon_add);
                }
                holder.linearlayout_user_area.addView(beaconLayout);
            }
        }else{
            holder.linearlayout_user_area.removeAllViews();
            holder.textview_beacon_cnt.setText("Beacon 0");
        }


        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_icon;
        public TextView textview_name;

        public TextView textview_beacon_cnt;

        public LinearLayout linearlayout_notice;
        public ImageView imageview_notice;
        public TextView textview_notice;

        public LinearLayout linearlayout_user_area;

    }
}
