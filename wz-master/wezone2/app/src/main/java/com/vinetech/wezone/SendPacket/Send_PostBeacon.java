package com.vinetech.wezone.SendPacket;

import com.vinetech.wezone.Data.Data_ActionItem;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-02-17.
 */

public class Send_PostBeacon {
    public String device_id;
    public String model;
    public String firmware_ver;
    public String mac;
    public String beacon_uuid;
    public String beacon_major;
    public String beacon_minor;
    public String beacon_serial;
    public String registration_code;

    public String name;
    public String img_url;
    public String push_flag;

    public ArrayList<Data_ActionItem> short_id;
    public ArrayList<Data_ActionItem> long_id;
    public ArrayList<Data_ActionItem> near_id;
    public ArrayList<Data_ActionItem> mid_id;
    public ArrayList<Data_ActionItem> far_id;
}