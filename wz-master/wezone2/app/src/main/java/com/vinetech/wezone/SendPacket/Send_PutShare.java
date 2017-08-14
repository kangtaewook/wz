package com.vinetech.wezone.SendPacket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by galuster3 on 2017-02-24.
 */

public class Send_PutShare {

    public static final String SHARE_TYPE_BEACON = "C";
    public static final String SHARE_TYPE_WEZONE = "W";

    public static final String SHARE_FLAG_INVITE = "W";
    public static final String SHARE_FLAG_ACCEPT = "N";
    public static final String SHARE_FLAG_REJECT = "R";
    public static final String SHARE_FLAG_DELETE = "X";

    @SerializedName("type")
    public String type;

    @SerializedName("zone_id")
    public String zone_id;

    @SerializedName("other_uuids")
    public ArrayList<HashMap<String,String>> other_uuids;

    @SerializedName("share_flag")
    public String share_flag;
}
