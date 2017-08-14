package com.vinetech.wezone.SendPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_BeaconArray_location_type;

import java.util.ArrayList;

/**
 * Created by galuster on 2017-05-05.
 */

public class Send_PutDataLocationTypeArray extends Send_PutBase  {
    @SerializedName("flag")
    public String flag;

    @SerializedName("val")
    public ArrayList<Data_BeaconArray_location_type> val;
}
