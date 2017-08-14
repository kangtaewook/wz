package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Board;

import java.util.List;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_BoardList extends Rev_Base {

    @SerializedName("list")
    public List<Data_Board> list;

    @SerializedName("notice_count")
    public String notice_count;

}
