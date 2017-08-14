package com.vinetech.wezone.SendPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Beacon_info_Array;

import java.util.ArrayList;

/**
 * Created by galuster on 2017-04-08.
 */

public class Send_PutBeaconArray {
    @SerializedName("beacon_id")
    public String beacon_id;

    @SerializedName("beacon_info")
    public ArrayList<Data_Beacon_info_Array> beacon_info;
}
