package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-21.
 */

public class Data_Beacon_Info_Vas extends Data_Base{

    @SerializedName("wezone")
    public Data_WezoneAction wezone;

    @SerializedName("beacon")
    public Data_BeaconAction beacon;

    @SerializedName("themezone")
    public Data_ThemeAction themezone;

    @SerializedName("bunneyzone")
    public Data_BunnyZoneAction bunneyzone;
}
