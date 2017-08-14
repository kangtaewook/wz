package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_WeZone;

/**
 * Created by galuster on 2017-04-12.
 */

public class Rev_Wezoninfo extends Rev_Base{

    @SerializedName("wezone_info")
    public Data_WeZone wezone_info;

}
