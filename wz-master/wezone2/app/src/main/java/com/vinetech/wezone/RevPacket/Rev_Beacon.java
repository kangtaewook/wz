package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Beacon;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_Beacon extends Rev_Base {

    @SerializedName("beacon_info")
    public Data_Beacon beacon_info;
}
