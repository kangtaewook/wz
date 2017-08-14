package com.vinetech.wezone.SendPacket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by galuster on 2017-02-24.
 */

public class Send_PutPushFlag extends Send_PutBase {

    @SerializedName("push_flag")
    public String push_flag;

    @SerializedName("other_uuids")
    public ArrayList<HashMap<String,String>> other_uuids;
}
