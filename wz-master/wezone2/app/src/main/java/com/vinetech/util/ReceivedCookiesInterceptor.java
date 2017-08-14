package com.vinetech.util;

import android.content.Context;

import com.vinetech.util.crypt.CryptPreferences;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by galuster3 on 2017-02-15.
 */

public class ReceivedCookiesInterceptor implements Interceptor {

    private Context mContext;
    public ReceivedCookiesInterceptor(Context context){
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();
            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            CryptPreferences.putStringSet(mContext,"Cookie",cookies);
        }
        return originalResponse;
    }
}
