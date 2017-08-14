package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-27.
 */

public class Data_Zone_Member extends Data_Base{

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("user_name")
    public String user_name;

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("friend_uuid")
    public String friend_uuid;

    @SerializedName("zone_id")
    public String zone_id;

    @SerializedName("zone_type")
    public String zone_type;

    @SerializedName("manage_type")
    public String manage_type;

    @SerializedName("distance")
    public String distance;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

}
