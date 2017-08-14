package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Beacon;

import java.util.List;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_Advert extends Rev_Base {

    @SerializedName("market_flag")
    public String market_flag;

    @SerializedName("market_url")
    public String market_url;

    @SerializedName("advert_action")
    public String advert_action;

    @SerializedName("advert_data")
    public String advert_data;

    @SerializedName("advert_url")
    public String advert_url;

    @SerializedName("main_adtext")
    public String main_adtext;

    @SerializedName("theme_id")
    public String theme_id;

    @SerializedName("market_naver")
    public String market_naver;

    @SerializedName("market_auction")
    public String market_auction;

    @SerializedName("market_gmarket")
    public String market_gmarket;

}
