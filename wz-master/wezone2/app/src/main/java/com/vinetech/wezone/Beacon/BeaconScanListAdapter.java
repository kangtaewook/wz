package com.vinetech.wezone.Beacon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class BeaconScanListAdapter extends BaseAdapter {

    private ArrayList<BluetoothLeDevice> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    public BeaconScanListAdapter(BaseActivity a, ArrayList<BluetoothLeDevice> beacons){
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
            convertView = mInflater.inflate(R.layout.beacon_scan_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_profile = (ImageView) convertView.findViewById(R.id.imageview_profile);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);
            holder.textview_distance = (TextView) convertView.findViewById(R.id.textview_distance);
            holder.textview_rssi = (TextView) convertView.findViewById(R.id.textview_rssi);
            holder.imageview_distance_icon = (ImageView) convertView.findViewById(R.id.imageview_distance_icon);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        BluetoothLeDevice beacon = mListItem.get(position);

        holder.textview_name.setText("WeCON" + " - " + beacon.getVineBeacon().getMinor());
        holder.textview_desc.setVisibility(View.GONE);
//        holder.textview_desc.setText("Beacon " + beacon.getMacAddr());

        double distance = BluetoothLeDevice.calculateDistance(beacon.getVineBeacon().getPower(),beacon.getLastRssi());
        double b = Math.round(distance * 100d) / 100d;
        holder.textview_distance.setText(b + " m");
        holder.textview_rssi.setText(String.valueOf(beacon.getLastRssi()));


        int rssi = beacon.getLastRssi();
        if(rssi != 0 && rssi >  -50) {
            holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_max);
        }else if(rssi != 0 && rssi >  -70) {
            holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_medium);
        }else if(rssi != 0 && rssi >  -90){
            holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_minimum);
        }else{
            holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_none);
        }

        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_profile;
        public TextView textview_name;
        public TextView textview_desc;
        public TextView textview_distance;
        public TextView textview_rssi;
        public ImageView imageview_distance_icon;
    }
}
