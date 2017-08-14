package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by galuster on 2017-02-17.
 */

public class Data_WeZone extends Data_Base {

    public static int HEADER_MYZONE = 0;
    public static int HEADER_NEARZONE = 1;

    @SerializedName("id")
    public String id;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("zone_type")
    public String zone_type;

    @SerializedName("zone_id")
    public String zone_id;

    @SerializedName("manage_type")
    public String manage_type;

    @SerializedName("push_flag")
    public String push_flag;

    @SerializedName("wait_datetime")
    public String wait_datetime;

    @SerializedName("join_datetime")
    public String join_datetime;

    @SerializedName("delete_datetime")
    public String delete_datetime;

    @SerializedName("delete_flag")
    public String delete_flag;

    @SerializedName("wezone_id")
    public String wezone_id;

    @SerializedName("title")
    public String title;

    @SerializedName("introduction")
    public String introduction;

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("bg_img_url")
    public String bg_img_url;

    @SerializedName("short_url")
    public String short_url;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("address")
    public String address;

    @SerializedName("hashtag")
    public String hashtag;

    @SerializedName("wezone_type")
    public String wezone_type;

    @SerializedName("member_limit")
    public String member_limit;

    @SerializedName("location_type")
    public String location_type;

    @SerializedName("location_data")
    public String location_data;

    @SerializedName("member_count")
    public String member_count;

    @SerializedName("board_count")
    public String board_count;

    @SerializedName("create_datetime")
    public String create_datetime;

    @SerializedName("update_datetime")
    public String update_datetime;

    @SerializedName("distance")
    public String distance;

    @SerializedName("zone_possible")
    public String zone_possible;

    @SerializedName("zone_in")
    public Data_ActionItem zone_in;

    @SerializedName("zone_out")
    public Data_ActionItem zone_out;

    @SerializedName("beacon")
    public List<Data_Beacon> beacons;

    @SerializedName("member")
    public List<Data_UserInfo> members;

    public int headerId;
}
