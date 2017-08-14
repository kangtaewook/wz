package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_UserMail;

/**
 * Created by galuster3 on 2017-02-10.
 */

public class Rev_Base {
    public static final String LOGIN_ERROR_ID = "401001";
    public static final String LOGIN_ERROR_PW = "401002";
    public static final String LOGIN_ERROR_WEZONE = "401003";
    public static final String LOGIN_ERROR_TYPE = "401004";
    public static final String LOGIN_ERROR_UUID = "401005";
    public static final String LOGIN_ERROR_PROVIDER = "401006";
    public static final String LOGIN_ERROR_EMAIL = "401007";
    public static final String LOGIN_ERROR_CODE = "401008";
    public static final String LOGIN_ERROR_PW_FAIL = "401009";
    public static final String LOGIN_ERROR_ALREADY_EMAIl = "401010";
    public static final String LOGIN_ERROR_ALREADY_REGI = "401011";


    public static final String ERROR_NO_CHANGE = "300001";
    public static final String ERROR_ALREADY_REVIEW = "300002";
    public static final String ERROR_ALREADY_REFUND = "300003";

    public static final String ERROR_ALREADY_SCORE = "401020";
    public static final String ERROR_ALREADY_ASSIGN = "401030";

    @SerializedName("code")
    public String code;

    @SerializedName("msg")
    public String msg;

    @SerializedName("total_count")
    public String total_count;

    @SerializedName("data")
    public Data_UserMail data;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("themezone_id")
    public String themezone_id;
}
