package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-14.
 */

public class Data_BunnyZoneAction extends Data_Base{

    @SerializedName("zone_type")
    public String zone_type;

    @SerializedName("zone_id")
    public String zone_id;

    @SerializedName("near_id")
    public Data_ActionItem near_id;

    @SerializedName("mid_id")
    public Data_ActionItem mid_id;

    @SerializedName("far_id")
    public Data_ActionItem far_id;

}
