package com.vinetech.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.widget.EditText;
import android.widget.TextView;

import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.advertising.VineBeacon;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by galuster3 on 2017-01-20.
 */

public class WezoneUtil {

    /**
     * Used to determine if the current device is a Google TV
     *
     * @param context The {@link Context} to use
     * @return True if the device has Google TV, false otherwise
     */
    public static final boolean isGoogleTV(final Context context) {
        return context.getPackageManager().hasSystemFeature("com.google.android.tv");
    }

    /**
     * Used to determine if the device is running Froyo or greater
     *
     * @return True if the device is running Froyo or greater, false otherwise
     */
    public static final boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * Used to determine if the device is running Gingerbread or greater
     *
     * @return True if the device is running Gingerbread or greater, false
     *         otherwise
     */
    public static final boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Used to determine if the device is running Honeycomb or greater
     *
     * @return True if the device is running Honeycomb or greater, false
     *         otherwise
     */
    public static final boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Used to determine if the device is running Honeycomb-MR1 or greater
     *
     * @return True if the device is running Honeycomb-MR1 or greater, false
     *         otherwise
     */
    public static final boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Used to determine if the device is running ICS or greater
     *
     * @return True if the device is running Ice Cream Sandwich or greater,
     *         false otherwise
     */
    public static final boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * Used to determine if the device is running Jelly Bean or greater
     *
     * @return True if the device is running Jelly Bean or greater, false
     *         otherwise
     */
    public static final boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Used to determine if the device is a tablet or not
     *
     * @param context The {@link Context} to use.
     * @return True if the device is a tablet, false otherwise.
     */
    public static final boolean isTablet(final Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Used to determine if the device is currently in landscape mode
     *
     * @param context The {@link Context} to use.
     * @return True if the device is in landscape mode, false otherwise.
     */
    public static final boolean isLandscape(final Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static String getRoundString(String str){
        if(str == null)
            return "";

        float temp = Float.valueOf(str);

        double tempDouble = Math.round(temp * 10.0) / 10.0;

        return String.valueOf(tempDouble);
    }

    public static boolean isNotEmptyStr(String value) {
        return value != null && value.length() > 0;
    }

    public static boolean isEmptyStr(String str){
        return str == null || "".equals(str) || "null".equals(str);
    }

    public static int parseInt(String value, int defaultValue) {
        if (WezoneUtil.isNotEmptyStr(value) == true) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public static boolean parseBoolean(String value, boolean defaultValue) {
        if (WezoneUtil.isNotEmptyStr(value) == true) {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static int dip2px(Context c, float dipValue){
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale +0.5f);
    }

    public static int px2dip(Context c, float pxValue){
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static Bitmap loadBitmapOptimizeMemory(Activity activity,
                                                  String bitmapPath) {
        Bitmap bitmap = null;

        if (bitmapPath != null) {
            BitmapFactory.Options bitmapDecodeOptions = new BitmapFactory.Options();
            bitmapDecodeOptions.inJustDecodeBounds = true;

            // 사이즈만 구한다.
            bitmap = BitmapFactory.decodeFile(bitmapPath, bitmapDecodeOptions);
            getBitmapSizeOptimizeMemory(activity, bitmapDecodeOptions);
            bitmap = BitmapFactory.decodeFile(bitmapPath, bitmapDecodeOptions);
        }
        return bitmap;
    }

    private static int SMALL_SCREENSIZE;

    private static void getBitmapSizeOptimizeMemory(Activity activity,
                                                    BitmapFactory.Options bitmapDecodeOptions) {
        int targetWidth, targetHeight;

        boolean isHeight2XOver = false;

        if (bitmapDecodeOptions.outWidth > bitmapDecodeOptions.outHeight) {
            targetWidth = (int) (600 * 1.3);
            targetHeight = 600;
        } else {
            targetWidth = 600;
            targetHeight = (int) (600 * 1.3);

            isHeight2XOver = bitmapDecodeOptions.outHeight > bitmapDecodeOptions.outWidth * 2;
        }

        boolean isScaleByHeight = Math.abs(bitmapDecodeOptions.outHeight
                - targetHeight) >= Math.abs(bitmapDecodeOptions.outWidth
                - targetWidth);
        if (bitmapDecodeOptions.outHeight * bitmapDecodeOptions.outWidth * 2 >= 16384) {
            double sampleSize = (isScaleByHeight) ? bitmapDecodeOptions.outHeight
                    / targetHeight
                    : bitmapDecodeOptions.outWidth / targetWidth;

            bitmapDecodeOptions.inSampleSize = (int) Math.pow(2d,
                    Math.floor(Math.log(sampleSize) / Math.log(2d)));

            if (SMALL_SCREENSIZE == 0) {
                Display display = activity.getWindow().getWindowManager()
                        .getDefaultDisplay();
                SMALL_SCREENSIZE = Math.min(display.getWidth(),
                        display.getHeight());
            }

            if (isHeight2XOver == true
                    && bitmapDecodeOptions.outWidth > SMALL_SCREENSIZE) {
                while (bitmapDecodeOptions.inSampleSize > 1) {
                    if (bitmapDecodeOptions.outWidth
                            / bitmapDecodeOptions.inSampleSize < SMALL_SCREENSIZE)
                        bitmapDecodeOptions.inSampleSize /= 2;
                    else
                        break;
                }
            }
        }
        bitmapDecodeOptions.inJustDecodeBounds = false;
        bitmapDecodeOptions.inTempStorage = new byte[16 * 1024];
    }

    public static boolean saveBitmapToPngFile(Bitmap bitmap,
                                              String strFilePath, boolean isCrypt) {
        return saveBitmapToPngFile(bitmap, new File(strFilePath), isCrypt);
    }

    public static boolean saveBitmapToPngFile(Bitmap bitmap, File file,
                                              boolean isCrypt) {
        return saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100, isCrypt);
    }

    public static boolean saveBitmapToJpgFile(Bitmap bitmap,
                                              String strFilePath, boolean isCrypt) {
        return saveBitmapToJpgFile(bitmap, new File(strFilePath), isCrypt);
    }

    public static boolean saveBitmapToJpgFile(Bitmap bitmap, File file,
                                              boolean isCrypt) {
        return saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.JPEG, 100, isCrypt);
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, File file,
                                           Bitmap.CompressFormat format, int quality, boolean isCrypt) {
        if (bitmap == null || file == null)
            return false;

        if (file.exists() == true)
            return true;

        FileOutputStream fos = null;

        try {
            // if( isCrypt == true && Define.CRYPT_CACHE_IMAGE_ENABLE == true )
            // {
            // //암호화후
            // ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // bitmap.compress( format, quality, baos);
            //
            // byte[] chiperBytes =
            // AesCryptByUser.toEncryptBytes(baos.toByteArray());
            //
            // if( chiperBytes != null )
            // {
            // fos = new FileOutputStream(file);
            // fos.write(chiperBytes);
            // fos.flush();
            // }
            // }
            // else
            // {
            // 암호화전
            fos = new FileOutputStream(file);
            bitmap.compress(format, quality, fos);
            fos.flush();
            // }
            return true;
        } catch (Exception e) {
            if(Define.LOG_YN) {
                Log.d(Define.LOG_TAG, e.toString());
            }
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    public static boolean isNeedFitContentMaxSizeImageFile(String originPath) {
        if (originPath != null) {
            BitmapFactory.Options bitmapDecodeOptions = new BitmapFactory.Options();
            bitmapDecodeOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(originPath, bitmapDecodeOptions);
            // if( BitmapFactory.decodeFile(originPath, bitmapDecodeOptions) !=
            // null )
            {
                int maxSize = Math.max(bitmapDecodeOptions.outWidth,
                        bitmapDecodeOptions.outHeight);

                return maxSize > Define.IMAGE_CONTENT_MAX_SIZE;
            }
        }
        return false;
    }

    public static String createFitContentMaxSizeImageFile(String originPath) {

        BitmapFactory.Options bitmapDecodeOptions = new BitmapFactory.Options();
        bitmapDecodeOptions.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(originPath,
                bitmapDecodeOptions);
        if (bitmap != null) {
            Bitmap resizeBitmap;
            if ((resizeBitmap = UIBitmap.resizeRatioBitmap(bitmap,
                    Define.IMAGE_CONTENT_MAX_SIZE)) != null) {
                if (bitmap != resizeBitmap) {
                    bitmap.recycle();

                    originPath = WezoneUtil.saveBitmapToJpgInTempFolder(
                            resizeBitmap, originPath, false);
                    resizeBitmap.recycle();
                }
            } else {
                bitmap.recycle();
            }
        }
        return originPath;
    }

    public static String saveBitmapToJpgInTempFolder(Bitmap bitmap,
                                                     String bitmapPath, boolean isCrypt) {
        // SDSLog.e(SDSLog.TAGIDX_CKY,"bitmap="+bitmap);
        // SDSLog.e(SDSLog.TAGIDX_CKY,"bitmapPath="+bitmapPath);
        if (bitmap != null && bitmapPath != null) {
            try {
                String tempCacheDirPath = FileCache.getInstance()
                        .getTempFilePath();

                String newFilePath;
                int offset = bitmapPath.lastIndexOf('/');
                if (offset > 0)
                    newFilePath = tempCacheDirPath
                            + bitmapPath.substring(offset + 1);
                else
                    newFilePath = tempCacheDirPath
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg";

                // SDSLog.e(SDSLog.TAGIDX_CKY,"newFilePath="+newFilePath);

                File file = new File(newFilePath);
                if (file.exists() == true)
                    file.delete();

                // SDSLog.e(SDSLog.TAGIDX_CKY,
                // "저장 "+bitmap.getWidth()+"x"+bitmap.getHeight());

                if (saveBitmapToJpgFile(bitmap, file, isCrypt) == true) {
                    return newFilePath;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 포지션은 0 ~ 4  총 5가지 색상임
     * @param currentTheme
     * @param position
     * @return
     */
    public static int getThemeIconBgResId(int currentTheme, int position ){

        if(currentTheme == Define.THEME_BLUE){

            if(position == 0){
                return R.drawable.circle_blue_00;
            }else if(position == 1){
                return R.drawable.circle_blue_01;
            }else if(position == 2){
                return R.drawable.circle_blue_02;
            }else if(position == 3){
                return R.drawable.circle_blue_03;
            }else{
                return R.drawable.circle_blue_04;
            }

        }else if(currentTheme == Define.THEME_RED){
            if(position == 0){
                return R.drawable.circle_red_00;
            }else if(position == 1){
                return R.drawable.circle_red_01;
            }else if(position == 2){
                return R.drawable.circle_red_02;
            }else if(position == 3){
                return R.drawable.circle_red_03;
            }else{
                return R.drawable.circle_red_04;
            }

        }else{
            if(position == 0){
                return R.drawable.circle_yellow_00;
            }else if(position == 1){
                return R.drawable.circle_yellow_01;
            }else if(position == 2){
                return R.drawable.circle_yellow_02;
            }else if(position == 3){
                return R.drawable.circle_yellow_03;
            }else{
                return R.drawable.circle_yellow_04;
            }
        }
    }

    public static boolean isMember(String member_type){
        if(isEmptyStr(member_type)){
            return false;
        }else{
            return Define.TYPE_MANAGER.equals(member_type) || Define.TYPE_STAFF.equals(member_type) || Define.TYPE_NORMAL.equals(member_type);
        }
    }

    public static boolean isManager(String member_type){
        return Define.TYPE_MANAGER.equals(member_type);
    }

    public static boolean isNetworkStateConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getHTMLFontColor(String contents, String color){

        if(contents == null || color == null)
            return null;

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("<font color=\"");
        strBuffer.append(color);
        strBuffer.append("\">");
        strBuffer.append(contents);
        strBuffer.append("</font>");

        return strBuffer.toString();
    }

    public static String getHTMLImgTag(String resName){

        if(resName == null)
            return null;

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("<img src=\"");
        strBuffer.append(resName);
        strBuffer.append("\" />");
        return strBuffer.toString();
    }

    public static String getEmoticonName(int idx)
    {
        if( idx >= 1 && idx <= 20 )
        {
            return Define.Emoticon.EMOTICON_TEXTS[idx-1];
        }

        else if( idx >= 21 && idx <= 101 )
        {
            return Define.Emoticon.THUMB_EMOTICON_TEXTS[idx-21];
        }
        return "";
    }

    public static int getEmoticonImageId(int idx)
    {

        if( idx >= 1 && idx <= 20 )
            return R.drawable.e01 + idx-1;
//        else if( idx >= 21 && idx <= 74 )
//            return R.drawable.sticker01 + (idx-21);
        else
            return 0;
    }

    public static void deleteLastCharacter(EditText editText, Editable editable)
    {
        if( editText == null ) return;

        if( editable == null )
            editable = editText.getEditableText();

        int startCursor = editText.getSelectionStart();
        int endCursor = editText.getSelectionEnd();

        if( startCursor == endCursor && startCursor > 0 )
            editable.delete(startCursor-1, startCursor);
        else if( endCursor > startCursor )
            editable.delete(startCursor, endCursor);
    }

    public static String toRichContentHtmlText(String content)
    {
        if( content == null ) return null;

        // Html 태그 시작 '<' 예외처리
        content = content.replace("<", "<![CDATA[<]]>").toString();

        if( content.indexOf('\n') < 0 && content.indexOf('(') < 0 ) return content;

        final String[] keywords = Define.Emoticon.ALL_TEXTS;

        final int length = content.length();
        final int innerLength = length - 1;

        char[] contentChars = content.toCharArray();

        StringBuilder sb = new StringBuilder();

        for( int i = 0, j ; i < length ; i++ )
        {
            char ch = contentChars[i];
            if( ch == '(' && i < innerLength && (j = content.indexOf(')', i+1)) > 0 )
            {
                String found = content.substring(i, j + 1);
                //android.util.SDSLog.e(SDSLog.TAGIDX_CKY,"found="+found);

                for( int k = 0, count = keywords.length ; k < count ; k++ )
                {
                    if( keywords[k].equals(found) == true )
                    {
                        sb.append("<img src=\"");
                        sb.append(k+1);
                        sb.append("\">");
                        found = null;
                        break;
                    }
                }

                if( found == null )
                    i = j;
                else
                    sb.append(ch);
            }
            else if( ch == '\n' )
            {
                sb.append("<br>");
            }
            else
            {
                sb.append(ch);
            }
        }
        return (sb.length() > 0)?sb.toString():content;
    }

    public static String toReplaceNBSP(String content)
    {
        if( content == null ) return "";

        if( content.indexOf(' ') < 0 ) return content;

        char[] chars = content.toCharArray();

        StringBuilder sb = new StringBuilder();

        for( int i = 0, count = chars.length ; i < count ; i++ )
        {
            if( chars[i] == ' ' )
            {
                int k = i-4;
                if( k >= 0 && content.indexOf("<img src=", k) == k )
                    sb.append(' ');
                else
                    sb.append("&nbsp;");
            }
            else
            {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    public static String toRichContentText(TextView textview)
    {
        if( textview == null )
            return null;

        String content;

        if( textview == null || (content = textview.getText().toString().trim()).length() == 0 )
            return null;

        Spanned editableText = textview.getEditableText();
        return (editableText!=null)?toRichContentText(content, Html.toHtml(editableText)):content;
    }

    public static String toRichContentText(String content, String htmlContent)
    {
//		android.util.SDSLog.e(SDSLog.TAGIDX_CKY, "content="+content);
//		android.util.SDSLog.e(SDSLog.TAGIDX_CKY, "htmlContent="+htmlContent);

        //htmlContent = htmlContent.replaceAll("<br>", "\n");

//		android.util.SDSLog.e(SDSLog.TAGIDX_CKY, "htmlContent="+htmlContent);
//		android.util.SDSLog.e(SDSLog.TAGIDX_CKY, "content="+content);
        StringBuilder sb = new StringBuilder();

        int offset = 0, end = 0, emoticonIndex;

        char[] contentChars = content.toCharArray();
        for( char c : contentChars )
        {
            if( c == 65532 )
            {
                if( (offset = htmlContent.indexOf("<img src=\"", end)) >= 0 )
                {
                    if( (end = htmlContent.indexOf("\">", offset+1)) > 0 )
                    {
                        try
                        {
                            emoticonIndex = Integer.parseInt(htmlContent.substring(offset+10, end));

                        } catch (Exception e) { emoticonIndex = -1; }

                        if( emoticonIndex >= 0 )
                            sb.append(WezoneUtil.getEmoticonName(emoticonIndex));

                        offset = end;
                    }
                }
            }
            else
            {
                sb.append(c);
            }
        }

//		android.util.SDSLog.e(SDSLog.TAGIDX_CKY, "sb.toString()="+sb.toString());
        return (sb.length() > 0)?sb.toString():content;
    }

    public static boolean isSameBeacon(BluetoothLeDevice ble, Data_Beacon beacon){

        VineBeacon vb = ble.getVineBeacon();
        if(vb == null || beacon == null)
            return false;

        if(vb.getUUID().toString() == null || beacon.beacon_uuid == null)
            return false;

        if(isEmptyStr(beacon.beacon_major) || isEmptyStr(beacon.beacon_minor))
            return false;

        if(vb.getUUID().toString().equals(beacon.beacon_uuid) &&
                vb.getmUsableMajor() == Integer.valueOf(beacon.beacon_major) &&
                vb.getMinor() == Integer.valueOf(beacon.beacon_minor)){
               return true;
        }else{
            return false;
        }
    }
}
