package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Board;

import java.util.List;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_Board extends Rev_Base {

    @SerializedName("board_info")
    public Data_Board board_info;

}
