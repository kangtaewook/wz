/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vinetech.wezone.Login.google;

import android.os.AsyncTask;
import android.util.Log;

import com.vinetech.ui.custompopup.CustomPopupManager;
import com.vinetech.wezone.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Display personalized greeting. This class contains boilerplate code to consume the token but
 * isn't integral to getting the tokens.
 */
public abstract class AbstractGetNameTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "TokenInfoTask";
    private static final String NAME_KEY = "given_name";
    protected BaseActivity mActivity;

    protected String mScope;
    protected String mEmail;

    protected int mResultCode;
    protected String mToken;
    protected String mResultData;
    
    public CustomPopupManager mCustomPopup;
    
    protected GoogleNetListner mGoogleNetListner;
    
    public GoogleNetListner getGoogleNetListner() {
		return mGoogleNetListner;
	}

	public void setGoogleNetListner(GoogleNetListner mGoogleNetListner) {
		this.mGoogleNetListner = mGoogleNetListner;
	}

	AbstractGetNameTask(BaseActivity activity, String email, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = email;
        
        mCustomPopup = new CustomPopupManager(mActivity).show(mActivity);
    }
	
    @Override
    protected Void doInBackground(Void... params) {
      try {
        fetchNameFromProfileServer();
      } catch (IOException ex) {
        onError(ex.getMessage(), ex);
      } catch (JSONException e) {
        onError(e.getMessage(), e);
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    	// TODO Auto-generated method stub
    	super.onPostExecute(result);
    
    	if(mCustomPopup != null){
        	mCustomPopup.onDestroy();
        	mCustomPopup= null;
        }
    	
    	if(mResultCode == HttpURLConnection.HTTP_OK){
    		mGoogleNetListner.onFinish(mToken, mResultData);
    	}
    }

    protected void onError(String msg, Exception e) {
        if (e != null) {
          Log.e(TAG, "Exception: ", e);
        }

        mGoogleNetListner.onError(mResultCode, msg);
//        mActivity.show(msg);  // will be run in UI thread
    }

    /**
     * Get a authentication token if one is not available. If the error is not recoverable then
     * it displays the error message on parent activity.
     */
    protected abstract String fetchToken() throws IOException;

    /**
     * Contacts the user info server to get the profile of the user and extracts the first name
     * of the user from the profile. In order to authenticate with the user info server the method
     * first fetches an access token from Google Play services.
     * @throws IOException if communication with user info server failed.
     * @throws JSONException if the response from the server could not be parsed.
     */
    private void fetchNameFromProfileServer() throws IOException, JSONException {
    	mToken = fetchToken();
        if (mToken == null) {
          // error has already been handled in fetchToken()
          return;
        }
        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + mToken);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        mResultCode = con.getResponseCode();
        if (mResultCode == 200) {
          InputStream is = con.getInputStream();
          mResultData = readResponse(is);
          
          is.close();
          return;
        } else if (mResultCode == 401) {
//            GoogleAuthUtil.invalidateToken(mActivity, mToken);
            onError("Server auth error, please try again.", null);
            Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
            return;
        } else {
          onError("Server returned the following error code: " + mResultCode, null);
          return;
        }
    }

    /**
     * Reads the response from the input stream and returns it as a string.
     */
    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }

    /**
     * Parses the response and returns the first name of the user.
     * @throws JSONException if the response is not JSON or if first name does not exist in response
     */
    private String getFirstName(String jsonResponse) throws JSONException {
      JSONObject profile = new JSONObject(jsonResponse);
      return profile.getString(NAME_KEY);
    }

}
