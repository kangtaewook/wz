package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-10.
 */

public class Data_UserInfo extends Data_Base {

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("user_name")
    public String user_name;

    @SerializedName("provider_email")
    public String provider_email;

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("bg_img_url")
    public String bg_img_url;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("address")
    public String address;

    @SerializedName("geonameId")
    public String geonameId;

    @SerializedName("state_id")
    public String state_id;

    @SerializedName("state_name")
    public String state_name;

    @SerializedName("suburb_id")
    public String suburb_id;

    @SerializedName("suburb_name")
    public String suburb_name;

    @SerializedName("status_message")
    public String status_message;

    @SerializedName("bunnyzone_count")
    public String bunnyzone_count;

    @SerializedName("friend_count")
    public String friend_count;

    @SerializedName("friend_uuid")
    public String friend_uuid;

    @SerializedName("email_auth_flag")
    public String email_auth_flag;

    @SerializedName("create_datetime")
    public String create_datetime;

    @SerializedName("distance")
    public float distance;

    @SerializedName("beacon_count")
    public String beacon_count;

    public boolean isSelected;
    public boolean isClicked;
}
