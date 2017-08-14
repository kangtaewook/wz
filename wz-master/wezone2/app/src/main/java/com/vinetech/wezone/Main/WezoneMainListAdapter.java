package com.vinetech.wezone.Main;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightTextView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.vinetech.ui.CustomImageView;
import com.vinetech.util.LibDateUtil;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.Wezone.GpsInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class WezoneMainListAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

    Data_WeZone wezoneInfo;
    GpsInfo mGpsInfo;
    ArrayList<String> mHeaderItem;
    ArrayList<Data_WeZone> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    private String mType;

    public WezoneMainListAdapter(BaseActivity a, ArrayList<Data_WeZone> wezoneInfos, String type){
        mActivity = a;
        mListItem = wezoneInfos;
        mInflater = LayoutInflater.from(mActivity);
        mType = type;
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
            convertView = mInflater.inflate(R.layout.wezone_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.linearlayout_bg = (LinearLayout) convertView.findViewById(R.id.linearlayout_bg);

            holder.imageview_icon = (CustomImageView) convertView.findViewById(R.id.imageview_icon);
            holder.textview_contents = (DynamicHeightTextView) convertView.findViewById(R.id.textview);

            holder.textview_date = (TextView) convertView.findViewById(R.id.textview_date);
            holder.textview_member_cnt = (TextView) convertView.findViewById(R.id.textview_member_cnt);

            holder.textview_hash = (TextView) convertView.findViewById(R.id.textview_hash);

            holder.imageview_badge = (ImageView) convertView.findViewById(R.id.imageview_badge);
            holder.imageview_badge2 = (ImageView) convertView.findViewById(R.id.imageview_badge2);
            holder.imageview_badge_near = (ImageView) convertView.findViewById(R.id.imageview_badge_near);
            holder.image_wezone_list_item_closed = (ImageView) convertView.findViewById(R.id.image_wezone_list_item_closed);

            holder.textview_wezone_distance = (TextView) convertView.findViewById(R.id.textview_wezone_distance);

            holder.linearLayout_wezone_list = (LinearLayout)convertView.findViewById(R.id.linearLayout_wezone_list);


        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        wezoneInfo = mListItem.get(position);

        if(WezoneUtil.isEmptyStr(wezoneInfo.img_url) == false){
            mActivity.showImageFromRemote(wezoneInfo.img_url,R.drawable.ic_bunny_image,holder.imageview_icon);
        }else{
            holder.imageview_icon.setImageResource(R.drawable.ic_bunny_image);
        }

        String realDate = LibDateUtil.getConvertDate(wezoneInfo.create_datetime,"yyyy-MM-dd HH:mm:ss","yyyy.MM.dd");
        holder.textview_date.setText(realDate);

        holder.textview_member_cnt.setText(wezoneInfo.member_count);

        holder.textview_contents.setText(wezoneInfo.title);

        if(WezoneUtil.isEmptyStr(wezoneInfo.hashtag)==false) {
           String hash = wezoneInfo.hashtag;

          hash = hash.replace(",","#");

            holder.textview_hash.setText("#" + hash);
        }else{
            holder.textview_hash.setText("");
        }

        if(WezoneUtil.isEmptyStr(wezoneInfo.distance) == false) {
            DecimalFormat form = new DecimalFormat("#.##");
            double distance = Double.parseDouble(wezoneInfo.distance);
            holder.textview_wezone_distance.setText("나와의 거리  "+form.format(distance)+"km");
        }else{
            holder.textview_wezone_distance.setText("나와의 거리  "+0+"km");
        }


        holder.imageview_badge.setVisibility(View.VISIBLE);
        holder.imageview_badge2.setVisibility(View.VISIBLE);
//
        holder.linearLayout_wezone_list.setVisibility(View.VISIBLE);


//-------------------- 나의 위존 일때 ---------------------------------------
        if(wezoneInfo.headerId == Data_WeZone.HEADER_MYZONE) {

            holder.linearlayout_bg.setBackgroundColor(Color.parseColor("#ffffff"));

            holder.imageview_badge.setVisibility(View.GONE);
            holder.imageview_badge2.setVisibility(View.GONE);
            holder.imageview_badge_near.setVisibility(View.GONE);
            holder.image_wezone_list_item_closed.setVisibility(View.GONE);

            if("F".equals(wezoneInfo.push_flag)){
                holder.imageview_badge_near.setVisibility(View.VISIBLE);
                holder.imageview_badge_near.setImageResource(R.drawable.ic_chat_off);
            }else{
                holder.imageview_badge_near.setVisibility(View.GONE);
            }

            if (WezoneUtil.isManager(wezoneInfo.manage_type)){
                holder.imageview_badge.setVisibility(View.VISIBLE);
                holder.imageview_badge.setImageResource(R.drawable.ic_manager);
                if ("B".equals(wezoneInfo.location_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }
            }else if (Define.TYPE_INVITED.equals(wezoneInfo.manage_type)) {

            }else if(Define.TYPE_NORMAL.equals(wezoneInfo.manage_type)){
                //회원일 때
                if ("B".equals(wezoneInfo.location_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }
            }

            if ("B".equals(wezoneInfo.location_type)) {
                if (Define.TYPE_INVITED.equals(wezoneInfo.manage_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }
            }

            if("T".equals(wezoneInfo.wezone_type)){
                holder.image_wezone_list_item_closed.setVisibility(View.GONE);
            }else{
                holder.image_wezone_list_item_closed.setVisibility(View.VISIBLE);
            }
        }


//         위존 메인 리스트에 StickGridView로 2개 View로 나뉘어 지고 내 주변 위존이 생길 때 사용

//-------------------- 내 주변 일때 ---------------------------------------

        else if(wezoneInfo.headerId == Data_WeZone.HEADER_NEARZONE) {

            holder.linearlayout_bg.setBackgroundColor(Color.parseColor("#00000000"));

            holder.imageview_badge.setVisibility(View.GONE);
            holder.imageview_badge2.setVisibility(View.GONE);
            holder.imageview_badge_near.setVisibility(View.GONE);
            holder.image_wezone_list_item_closed.setVisibility(View.GONE);

            if("F".equals(wezoneInfo.push_flag)){
                holder.imageview_badge_near.setVisibility(View.VISIBLE);
                holder.imageview_badge_near.setImageResource(R.drawable.ic_chat_off);
            }else{
                holder.imageview_badge_near.setVisibility(View.GONE);
            }

            if (WezoneUtil.isManager(wezoneInfo.manage_type)) {
                holder.imageview_badge.setVisibility(View.VISIBLE);
                holder.imageview_badge.setImageResource(R.drawable.ic_manager);
                if ("B".equals(wezoneInfo.location_type)) {
                    //오른쪽 하단
//                    holder.imageview_badge_near.setVisibility(View.VISIBLE);
//                    holder.imageview_badge_near.setImageResource(R.drawable.ic_beacon_zone);
                    //왼쪽 상단
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }
            } else if (Define.TYPE_INVITED.equals(wezoneInfo.manage_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                if("G".equals(wezoneInfo.location_type)){
                    //위치인식방법이 B 비콘에서 G GPS로 변경했을 때 manage_type을 W -> null로 변경
                    holder.imageview_badge2.setVisibility(View.GONE);
                    wezoneInfo.manage_type = null;
                    if (Define.ZONE_POSSIBLE_T.equals(wezoneInfo.zone_possible)) {
                        //거리가 가까울 때, 입장 대기를 풀었을 때?
                        holder.imageview_badge_near.setVisibility(View.VISIBLE);
                        holder.imageview_badge_near.setImageResource(R.drawable.ic_entry);
                    } else {
                        //거리가 멀 때
                        holder.imageview_badge_near.setVisibility(View.VISIBLE);
                        holder.imageview_badge_near.setImageResource(R.drawable.ic_no_entry);
                    }
                }
            }  else if (Define.TYPE_STAFF.equals(wezoneInfo.manage_type) || Define.TYPE_NORMAL.equals(wezoneInfo.manage_type)){
                holder.imageview_badge.setVisibility(View.VISIBLE);
                holder.imageview_badge.setImageResource(R.drawable.ic_memeber);
                if ("B".equals(wezoneInfo.location_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }else if (Define.ZONE_POSSIBLE_T.equals(wezoneInfo.zone_possible)) {
                }
            } else {
                //1. 비콘일 때
                //2. 거리가 멀 때
                //3. 비콘이면서 거리가 멀 때

                //입장 불가를 보일 지, (비콘스티커)입장대기를 보일 지

                if ("B".equals(wezoneInfo.location_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }else if (Define.ZONE_POSSIBLE_T.equals(wezoneInfo.zone_possible)) {
                    //거리가 가까울 때, 입장 대기를 풀었을 때?
                    holder.imageview_badge_near.setVisibility(View.VISIBLE);
                    holder.imageview_badge_near.setImageResource(R.drawable.ic_entry);
                } else {
                    //거리가 멀 때
                    holder.imageview_badge_near.setVisibility(View.VISIBLE);
                    holder.imageview_badge_near.setImageResource(R.drawable.ic_no_entry);
                }
            }

            if ("T".equals(wezoneInfo.wezone_type)) {
                holder.image_wezone_list_item_closed.setVisibility(View.GONE);
            } else {
                holder.image_wezone_list_item_closed.setVisibility(View.VISIBLE);
            }
        }

        //멤버 프로필에서 멤버가 입장한 위존들에 표시되는 스티커들
        else if (Define.WEZONE_LIST_PROFILE_TYPE.equals(mType)) {
            holder.imageview_badge.setVisibility(View.GONE);
            holder.imageview_badge2.setVisibility(View.GONE);

            holder.image_wezone_list_item_closed.setVisibility(View.GONE);
            if (WezoneUtil.isManager(wezoneInfo.manage_type)) {
                holder.imageview_badge.setVisibility(View.VISIBLE);
                holder.imageview_badge.setImageResource(R.drawable.ic_manager);
                if ("B".equals(wezoneInfo.location_type)) {
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }
            } else if (Define.TYPE_INVITED.equals(wezoneInfo.manage_type)) {

            }

            if (Define.TYPE_INVITED.equals(wezoneInfo.manage_type) || "B".equals(wezoneInfo.location_type)) {
                if (Define.TYPE_INVITED.equals(wezoneInfo.manage_type)) {
                    holder.imageview_badge2.setVisibility(View.VISIBLE);
                    holder.imageview_badge2.setImageResource(R.drawable.ic_beacon_zone);
                }
            }
            //비공개 판별
            if ("T".equals(wezoneInfo.wezone_type)) {
                holder.image_wezone_list_item_closed.setVisibility(View.GONE);
            } else {
                holder.image_wezone_list_item_closed.setVisibility(View.VISIBLE);
            }

        }

        return convertView;
    }

    @Override
    public int getCountForHeader(int header) {
        int cnt = 0;
        for(Data_WeZone wezone : mListItem){
            if(header == wezone.headerId){
                cnt++;
            }
        }
        return cnt;
    }

    @Override
    public int getNumHeaders() {
        return 2;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        HeaderViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.wezone_main_headerview, null);

            holder = new HeaderViewHolder();
            convertView.setTag(holder);

            holder.relativelayout_bg = (RelativeLayout) convertView.findViewById(R.id.relativelayout_bg);
            holder.imageView_mywezone = (ImageView) convertView.findViewById(R.id.imageView_mywezone);
            holder.textview_header_title = (TextView) convertView.findViewById(R.id.textview_header_title);
            holder.ImageView_wezone_gps_check = (ImageView) convertView.findViewById(R.id.ImageView_wezone_gps_check);

        }else{
            holder = (HeaderViewHolder)convertView.getTag();
        }
        mGpsInfo = new GpsInfo(mInflater.getContext());
        if(position == Data_WeZone.HEADER_MYZONE){
            holder.relativelayout_bg.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.textview_header_title.setText("내 위존");
            holder.imageView_mywezone.setImageResource(R.drawable.ic_rabbit_foot);
            // GPS 사용유무 가져오기ic_rabbit_foot
            if (mGpsInfo.isGetLocation()) {
                holder.ImageView_wezone_gps_check.setVisibility(View.GONE);
            } else {
                // GPS 를 사용할수 없으므로
                holder.ImageView_wezone_gps_check.setVisibility(View.VISIBLE);
            }
        }else{
            holder.relativelayout_bg.setBackgroundColor(Color.parseColor("#00000000"));
            holder.textview_header_title.setText("내 주변 위존");
            holder.imageView_mywezone.setImageResource(R.drawable.ic_my_location_black);
            // GPS 사용유무 가져오기
            if (mGpsInfo.isGetLocation()) {
                holder.ImageView_wezone_gps_check.setVisibility(View.GONE);
            } else {
                // GPS 를 사용할수 없으므로
                holder.ImageView_wezone_gps_check.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    class ViewHolder {

        public LinearLayout linearlayout_bg;
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

        public ImageView image_wezone_list_item_closed;
    }

    class HeaderViewHolder{
        public RelativeLayout relativelayout_bg;
        public ImageView imageView_mywezone;
        public TextView textview_header_title;
        public ImageView ImageView_wezone_gps_check;
    }
}
