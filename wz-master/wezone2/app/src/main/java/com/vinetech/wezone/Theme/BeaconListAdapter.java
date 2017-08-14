package com.vinetech.wezone.Theme;

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
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class BeaconListAdapter extends BaseAdapter {

    private ArrayList<Data_Beacon> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    public BeaconListAdapter(BaseActivity a, ArrayList<Data_Beacon> userInfos){
        mActivity = a;
        mListItem = userInfos;
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
            convertView = mInflater.inflate(R.layout.beacon_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_profile = (ImageView) convertView.findViewById(R.id.imageview_profile);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);
            holder.linearlayout_btn_icon = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_icon);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_Beacon beaconInfo = mListItem.get(position);

        if(WezoneUtil.isEmptyStr(beaconInfo.img_url) == false){
            mActivity.showImageFromRemote(beaconInfo.img_url,R.drawable.im_beacon_add,holder.imageview_profile);
        }else{
            holder.imageview_profile.setImageResource(R.drawable.im_beacon_add);
        }

        holder.textview_name.setText(beaconInfo.name);

        holder.linearlayout_btn_icon.setVisibility(View.GONE);

        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_profile;
        public TextView textview_name;
        public TextView textview_desc;
        public LinearLayout linearlayout_btn_icon;
    }
}
