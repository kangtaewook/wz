package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-14.
 */

public class Data_WezoneAction extends Data_Base{

    @SerializedName("zone_type")
    public String zone_type;

    @SerializedName("zone_id")
    public String zone_id;

    @SerializedName("zone_in")
    public Data_ActionItem zone_in;

    @SerializedName("zone_out")
    public Data_ActionItem zone_out;

}
