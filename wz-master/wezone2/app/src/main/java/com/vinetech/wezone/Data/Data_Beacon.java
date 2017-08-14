package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-14.
 */

public class Data_Beacon extends Data_Base {

    @SerializedName("mac")
    public String mac;

    @SerializedName("beacon_id")
    public String beacon_id;

    @SerializedName("device_id")
    public String device_id;

    @SerializedName("theme_id")
    public String theme_id;

    @SerializedName("push_flag")
    public String push_flag;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("zone_type")
    public String zone_type;

    @SerializedName("share_flag")
    public String share_flag;

    @SerializedName("beacon_uuid")
    public String beacon_uuid;

    @SerializedName("beacon_major")
    public String beacon_major;

    @SerializedName("beacon_minor")
    public String beacon_minor;

    @SerializedName("beacon_serial")
    public String beacon_serial;

    @SerializedName("model")
    public String model;

    @SerializedName("firmware_ver")
    public String firmware_ver;

    @SerializedName("firmware_ver_new")
    public String firmware_ver_new;

    @SerializedName("firmware_url")
    public String firmware_url;

    @SerializedName("last_update")
    public String last_update;

    @SerializedName("name")
    public String name;

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("short_id")
    public String short_id;

    @SerializedName("long_id")
    public String long_id;

    @SerializedName("create_datetime")
    public String create_datetime;

    @SerializedName("update_datetime")
    public String update_datetime;

    @SerializedName("delete_datetime")
    public String delete_datetime;

    @SerializedName("delete_flag")
    public String delete_flag;

    @SerializedName("beacon_info_vars")
    public Data_Beacon_Info_Vas beacon_info_vars;

    @SerializedName("manage_type")
    public String manage_type;

    @SerializedName("registration_code")
    public String registration_code;

    @SerializedName("rssi_in")
    public String rssi_in;

    @SerializedName("rssi_out")
    public String rssi_out;


    public boolean isSelected;
    public double distance;
    public int rssi;
    public int txPower;
    public String bluetoothAddress;
    public int beaconTypeCode;
    public int serviceUUid;
    public int[] interval = new int[2];
    public boolean isSearch;

    public static final int STATUE_IN = 0;
    public static final int STATUE_OUT = 1;
    public int beaconStatus = 999;

}
