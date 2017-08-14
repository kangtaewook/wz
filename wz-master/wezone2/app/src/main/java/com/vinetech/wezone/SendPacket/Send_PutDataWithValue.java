package com.vinetech.wezone.SendPacket;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-20.
 */

public class Send_PutDataWithValue extends Send_PutBase{

    @SerializedName("flag")
    public String flag;

    @SerializedName("val")
    public String val;
}
