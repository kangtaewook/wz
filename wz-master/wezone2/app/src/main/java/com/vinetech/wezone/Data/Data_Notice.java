package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-14.
 */

public class Data_Notice extends Data_Base {

    public static final String MESSAGE_TYPE_CHAT = "0";
    public static final String MESSAGE_TYPE_STICKERCON = "1";
    public static final String MESSAGE_TYPE_INVITE_BEACON = "21";
    public static final String MESSAGE_TYPE_JOIN_BEACONZONE = "22";
    public static final String MESSAGE_TYPE_INVITE_WEZONE = "31";
    public static final String MESSAGE_TYPE_JOIN_WEZONE = "32";

    //클라이언트에서만 쓰는 임의 값
    //위존 비콘 스캔 됬을때
    public static final String MESSAGE_TYPE_WEZONE_REGIST = "38";
    public static final String MESSAGE_TYPE_WEZONE_NOTICE = "39";

    public static final String MESSAGE_TYPE_BOARD = "41";
    public static final String MESSAGE_TYPE_COMMANT = "42";
    public static final String MESSAGE_TYPE_LOGOUT = "99";


    @SerializedName("id")
    public String id;

    @SerializedName("message_type")
    public String message_type;

    @SerializedName("kind")
    public String kind;

    @SerializedName("sender_id")
    public String sender_id;

    @SerializedName("sender_name")
    public String sender_name;

    @SerializedName("sender_url")
    public String sender_url;

    @SerializedName("content")
    public String content;

    @SerializedName("item_id")
    public String item_id;

    @SerializedName("create_datetime")
    public String create_datetime;

}
