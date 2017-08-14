package com.vinetech.wezone.Main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightTextView;
import com.vinetech.ui.CustomImageView;
import com.vinetech.util.LibDateUtil;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by galuster on 2017-04-09.
 */

public class WezoneFindListAdapter extends BaseAdapter {

    private ArrayList<Data_WeZone> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;
    private String mType;

    public WezoneFindListAdapter(BaseActivity a, ArrayList<Data_WeZone> wezoneInfos, String type) {
        mActivity = a;
        mListItem = wezoneInfos;
        mType = type;
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.wezone_find_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_icon = (CustomImageView) convertView.findViewById(R.id.imageview_icon);
            holder.textview_contents = (DynamicHeightTextView) convertView.findViewById(R.id.textview);

            holder.textview_date = (TextView) convertView.findViewById(R.id.textview_date);
//            holder.textview_post_cnt = (TextView) convertView.findViewById(R.id.textview_post_cnt);
            holder.textview_member_cnt = (TextView) convertView.findViewById(R.id.textview_member_cnt);

            holder.textview_hash = (TextView) convertView.findViewById(R.id.textview_hash);

            holder.imageview_badge = (ImageView) convertView.findViewById(R.id.imageview_badge);
            holder.imageview_badge2 = (ImageView) convertView.findViewById(R.id.imageview_badge2);
            holder.imageview_badge_near = (ImageView) convertView.findViewById(R.id.imageview_badge_near);

            holder.textview_wezone_distance = (TextView) convertView.findViewById(R.id.textview_wezone_distance);

            holder.linearLayout_wezone_list = (LinearLayout) convertView.findViewById(R.id.linearLayout_wezone_list);

            holder.imageview_badge_near_entry = (ImageView) convertView.findViewById(R.id.imageview_badge_near_entry);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder == null) return null;

        Data_WeZone wezoneInfo = mListItem.get(position);

        if (WezoneUtil.isEmptyStr(wezoneInfo.img_url) == false) {
            mActivity.showImageFromRemote(wezoneInfo.img_url, R.drawable.ic_bunny_image, holder.imageview_icon);
        } else {
            holder.imageview_icon.setImageResource(R.drawable.ic_bunny_image);
        }

        String realDate = LibDateUtil.getConvertDate(wezoneInfo.create_datetime, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd");
        holder.textview_date.setText(realDate);

        holder.textview_member_cnt.setText(wezoneInfo.member_count);

        holder.textview_contents.setText(wezoneInfo.title);

        if (WezoneUtil.isEmptyStr(wezoneInfo.hashtag) == false) {
            String hash = wezoneInfo.hashtag;

            hash = hash.replace(",", "#");

            holder.textview_hash.setText("#" + hash);
        } else {

        }

        //나와의 거리 표시
        if (WezoneUtil.isEmptyStr(wezoneInfo.distance) == false) {
            if (wezoneInfo.distance.contains(".")) {
                int i = wezoneInfo.distance.indexOf(".");
                String mDistance = wezoneInfo.distance.substring(0, i);
                if (Integer.valueOf(mDistance) != 0) {
                    DecimalFormat form = new DecimalFormat("#.#");
                    double distance = Double.parseDouble(wezoneInfo.distance);
                    holder.textview_wezone_distance.setText("나와의 거리  " + form.format(distance) + "km");
                } else if (Double.valueOf(wezoneInfo.distance) > 0.0) {
                    if (Integer.valueOf(mDistance) == 0) {
                        int meter = (int) (Double.valueOf(wezoneInfo.distance) * 1000);
                        holder.textview_wezone_distance.setText("나와의 거리  " + meter + "m");
                    }
                } else if (wezoneInfo.distance.equals("0")) {
                    holder.textview_wezone_distance.setText("나와의 거리  " + 0 + "m");
                }
            } else if (Integer.valueOf(wezoneInfo.distance) > 0) {
                holder.textview_wezone_distance.setText("나와의 거리  " + wezoneInfo.distance + "m");
            } else {
                holder.textview_wezone_distance.setText("나와의 거리  " + 0 + "m");
            }
        }


        holder.imageview_badge.setVisibility(View.VISIBLE);
        holder.imageview_badge2.setVisibility(View.VISIBLE);

        holder.linearLayout_wezone_list.setVisibility(View.VISIBLE);


//-------------------- 내 주변 위존 ----------------------------

        if (mType.equals(Define.WEZONE_LIST_NEAR_TYPE)) {
            holder.imageview_badge.setVisibility(View.GONE);
            holder.imageview_badge2.setVisibility(View.GONE);
            holder.imageview_badge_near.setVisibility(View.GONE);
            holder.imageview_badge_near_entry.setVisibility(View.GONE);

            if (WezoneUtil.isManager(wezoneInfo.manage_type)) { //매니저
                holder.imageview_badge.setVisibility(View.VISIBLE);
                holder.imageview_badge.setImageResource(R.drawable.ic_manager);
            }
            if (Define.TYPE_INVITED.equals(wezoneInfo.manage_type) || "B".equals(wezoneInfo.location_type)) {
                if ("G".equals(wezoneInfo.location_type)) {
                    //위치인식방법이 B 비콘에서 G GPS로 변경했을 때 manage_type을 W -> null로 변경
                    wezoneInfo.manage_type = null;
                    if (Define.ZONE_POSSIBLE_T.equals(wezoneInfo.zone_possible)) {
                        if (WezoneUtil.isMember(wezoneInfo.manage_type) == false) {
                            holder.imageview_badge_near_entry.setVisibility(View.VISIBLE);
                            holder.imageview_badge_near_entry.setImageResource(R.drawable.ic_entry);
                        }
                    } else {
                        holder.imageview_badge_near_entry.setVisibility(View.VISIBLE);
                        holder.imageview_badge_near_entry.setImageResource(R.drawable.ic_no_entry);
                    }
                } else {//입장대기
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }
            } else if (Define.TYPE_STAFF.equals(wezoneInfo.manage_type) || Define.TYPE_NORMAL.equals(wezoneInfo.manage_type)) { //회원
                holder.imageview_badge.setVisibility(View.VISIBLE);
                holder.imageview_badge.setImageResource(R.drawable.ic_memeber);
                if ("B".equals(wezoneInfo.location_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                } else if (Define.ZONE_POSSIBLE_T.equals(wezoneInfo.zone_possible)) {
//                    holder.imageview_badge_near_entry.setVisibility(View.VISIBLE);
//                    holder.imageview_badge_near_entry.setImageResource(R.drawable.ic_entry);
                }
//                else {
//                    holder.imageview_badge_near_entry.setVisibility(View.VISIBLE);
//                    holder.imageview_badge_near_entry.setImageResource(R.drawable.ic_no_entry);
//                }
            } else { //외부인
                if ("B".equals(wezoneInfo.location_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                } else if (Define.ZONE_POSSIBLE_T.equals(wezoneInfo.zone_possible)) {
                    if (WezoneUtil.isMember(wezoneInfo.manage_type) == false) {
                        holder.imageview_badge_near_entry.setVisibility(View.VISIBLE);
                        holder.imageview_badge_near_entry.setImageResource(R.drawable.ic_entry);
                    }
                } else {
                    holder.imageview_badge_near_entry.setVisibility(View.VISIBLE);
                    holder.imageview_badge_near_entry.setImageResource(R.drawable.ic_no_entry);
                }
            }
        }

        return convertView;
    }

    class ViewHolder {
        public CustomImageView imageview_icon;

        public TextView textview_date;
        //        public TextView textview_post_cnt;
        public TextView textview_member_cnt;

        public TextView textview_contents;

        public TextView textview_hash;

        public ImageView imageview_badge;
        public ImageView imageview_badge2;

        public TextView textview_wezone_distance;

        public LinearLayout linearLayout_wezone_list;

        public ImageView imageview_badge_near;

        public ImageView imageview_badge_near_entry;

    }
}
