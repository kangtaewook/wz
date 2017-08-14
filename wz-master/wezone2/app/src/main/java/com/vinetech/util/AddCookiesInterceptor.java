package com.vinetech.util;

import android.content.Context;

import com.vinetech.util.crypt.CryptPreferences;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {

    private Context mContext;
    public AddCookiesInterceptor(Context context){
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        Set<String> cookiedata = CryptPreferences.getStringSet(mContext,"Cookie",new HashSet<String>());

        for (String cookie : cookiedata) {
            builder.addHeader("Cookie", cookie);
        }
         builder.removeHeader("User-Agent").addHeader("User-Agent", "Android");

        return chain.proceed(builder.build());
    }
}