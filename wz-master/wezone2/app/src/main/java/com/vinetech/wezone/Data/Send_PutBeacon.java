package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by galuster on 2017-04-03.
 */

public class Send_PutBeacon {

    @SerializedName("beacon_id")
    public String beacon_id;

    @SerializedName("beacon_info")
    public ArrayList<Data_Beacon_info> beacon_info;

}
