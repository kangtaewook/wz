package com.vinetech.wezone.Main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.R;

import java.util.ArrayList;

import static com.vinetech.wezone.Data.Data_ActionItem.getTitleText;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class MainBeaconListAdapter extends BaseAdapter {

    private ArrayList<Data_Beacon> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    public MainBeaconListAdapter(BaseActivity a, ArrayList<Data_Beacon> beacons){
        mActivity = a;
        mListItem = beacons;
        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public Object getItem(int i) {
        return mListItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.main_beacon_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_beacon = (ImageView) convertView.findViewById(R.id.imageview_beacon);

            holder.imageview_share = (ImageView) convertView.findViewById(R.id.imageview_share);

            holder.imageview_noti = (ImageView) convertView.findViewById(R.id.imageview_noti);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);

            holder.textview_in_title = (TextView) convertView.findViewById(R.id.textview_in_title);
            holder.textview_out_title = (TextView) convertView.findViewById(R.id.textview_out_title);


            holder.imageview_distance_icon = (ImageView) convertView.findViewById(R.id.imageview_distance_icon);
            holder.textview_distance = (TextView) convertView.findViewById(R.id.textview_distance);
            holder.textview_status = (TextView) convertView.findViewById(R.id.textview_status);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_Beacon beaconInfo = mListItem.get(position);

        if(WezoneUtil.isEmptyStr(beaconInfo.img_url) == false){
            mActivity.showImageFromRemote(beaconInfo.img_url, R.drawable.im_bunny_photo, holder.imageview_beacon);
        }else{
            holder.imageview_beacon.setImageResource(R.drawable.im_bunny_photo);
        }

        holder.textview_name.setText(beaconInfo.name);

        int rssi = beaconInfo.rssi;
        if(rssi != 0 && rssi >  -50) {
            holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_max);
        }else if(rssi != 0 && rssi >  -70) {
            holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_medium);
        }else if(rssi != 0 && rssi >  -90){
            holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_minimum);
        }else{
            holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_none);
        }

        if("T".equals(beaconInfo.push_flag)){
            holder.imageview_noti.setImageResource(R.drawable.ic_noti);
        }else{
            holder.imageview_noti.setImageResource(R.drawable.ic_noti_off);
        }

        if(!WezoneUtil.isEmptyStr(beaconInfo.uuid) && mActivity.getUuid().equals(beaconInfo.uuid)){
            holder.imageview_share.setVisibility(View.GONE);
        }else{
            holder.imageview_share.setVisibility(View.VISIBLE);
        }

        String nearTitle;
        if(beaconInfo.beacon_info_vars == null || beaconInfo.beacon_info_vars.beacon == null || beaconInfo.beacon_info_vars.beacon.near_id == null){
            nearTitle = "알림 끔";
        }else{
            nearTitle = getTitleText(beaconInfo.beacon_info_vars.beacon.near_id.id);
        }
        holder.textview_in_title.setText(nearTitle);

        String farTitle;
        if(beaconInfo.beacon_info_vars == null || beaconInfo.beacon_info_vars.beacon == null || beaconInfo.beacon_info_vars.beacon.far_id == null){
            farTitle = "알림 끔";
        }else{
            farTitle = getTitleText(beaconInfo.beacon_info_vars.beacon.far_id.id);
        }
        holder.textview_out_title.setText(farTitle);


        return convertView;
    }

    class ViewHolder {

        public ImageView imageview_beacon;
        public ImageView imageview_share;

        public ImageView imageview_noti;
        public TextView textview_name;

        public TextView textview_in_title;
        public TextView textview_out_title;


        public ImageView imageview_distance_icon;
        public TextView textview_distance;
        public TextView textview_status;
    }
}
