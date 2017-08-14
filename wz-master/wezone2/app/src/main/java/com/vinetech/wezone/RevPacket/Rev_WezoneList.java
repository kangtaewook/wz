package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_WeZone;

import java.util.List;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_WezoneList extends Rev_Base{

    @SerializedName("list")
    public List<Data_WeZone> list;
}
