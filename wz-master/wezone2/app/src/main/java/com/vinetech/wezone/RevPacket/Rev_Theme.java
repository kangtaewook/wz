package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_Theme;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_Theme extends Rev_Base {

    @SerializedName("themezone_info")
    public Data_Theme themezone_info;
}
