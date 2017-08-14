package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-02-21.
 */

public class Data_Theme extends Data_Base{

    public static final int TYPE_THEME = 0;
    public static final int TYPE_THEMEZONE = 1;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("theme_id")
    public String theme_id;

    @SerializedName("push_flag")
    public String push_flag;

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("bg_img_url")
    public String bg_img_url;

    @SerializedName("bg_color")
    public String bg_color;

    @SerializedName("title")
    public String title;

    @SerializedName("name")
    public String name;

    @SerializedName("content")
    public String content;

    @SerializedName("theme_type")
    public String theme_type;

    @SerializedName("theme_in")
    public Data_ActionItem theme_in;

    @SerializedName("theme_out")
    public Data_ActionItem theme_out;

    @SerializedName("beacon")
    public ArrayList<Data_Beacon> beacons;

    @SerializedName("start_beacon")
    public ArrayList<Data_Beacon> start_beacon;

    public int resIdPos;
    public boolean isSelected;

    public int themeType;
}
