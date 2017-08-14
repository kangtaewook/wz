package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by galuster on 2017-04-08.
 */

public class Data_Beacon_info_Array extends Data_Base {

    @SerializedName("flag")
    public String flag;

    @SerializedName("val")
    public ArrayList<Data_ActionItem> val;

}
