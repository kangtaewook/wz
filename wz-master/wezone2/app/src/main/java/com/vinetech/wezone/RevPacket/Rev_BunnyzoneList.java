package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_BunnyZone;

import java.util.List;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_BunnyzoneList extends Rev_Base {

    @SerializedName("list")
    public List<Data_BunnyZone> list;
}
