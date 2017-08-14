package com.vinetech.wezone.SendPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_ActionItem;

import java.util.ArrayList;

/**
 * Created by galuster on 2017-02-24.
 */

public class Send_PutDataWithArray extends Send_PutBase {

    @SerializedName("flag")
    public String flag;

    @SerializedName("val")
    public ArrayList<Data_ActionItem> val;
}
