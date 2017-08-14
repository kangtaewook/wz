package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by galuster3 on 2017-02-15.
 */

public class Data_BunnyZone extends Data_Base{

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

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("title")
    public String title;

    @SerializedName("theme_id")
    public String theme_id;

    @SerializedName("theme_name")
    public String theme_name;

    @SerializedName("create_datetime")
    public String create_datetime;

    @SerializedName("update_datetime")
    public String update_datetime;

    @SerializedName("near_id")
    public Data_ActionItem near_id;

    @SerializedName("mid_id")
    public Data_ActionItem mid_id;

    @SerializedName("far_id")
    public Data_ActionItem far_id;

    @SerializedName("beacon")
    public List<Data_Beacon> beacons;

    @SerializedName("member")
    public List<Data_UserInfo> members;
}
