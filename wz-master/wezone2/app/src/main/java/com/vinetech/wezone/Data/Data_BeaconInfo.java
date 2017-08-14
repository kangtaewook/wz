package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-03-08.
 */

public class Data_BeaconInfo extends Data_Base{

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("firmware_ver")
    public String firmware_ver;

    @SerializedName("create_datetime")
    public String create_datetime;

    @SerializedName("update_datetime")
    public String update_datetime;
}
