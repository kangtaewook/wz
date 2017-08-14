package com.vinetech.wezone.Common;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vinetech.ui.custompopup.CustomPopupManager;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

public class Activity_WebView extends BaseActivity {

	public static String WEBVIEW_TYPE = "type";
	public static int TYPE_NORMAL = 0;

	public static String WEB_URL = "web_url";
	public static String WEBVIEW_TITLE = "webview_title";


	public final static String INTENTKEY_STATE = "state";
	public final static int INTENTKEY_SUCCESS = 0;
	public final static int INTENTKEY_FAIL = 1;
	public final static String INTENTKEY_DATA = "data";


	public static void startWebViewActivity(BaseActivity activity, String url, String title)
	{
		Intent intent = new Intent(activity, Activity_WebView.class);
		intent.putExtra(WEBVIEW_TYPE, TYPE_NORMAL);
		intent.putExtra(WEB_URL, url);
		intent.putExtra(WEBVIEW_TITLE, title);
		activity.moveActivity(intent);
	}

	private WebView mWebViewMain;
	private String mUrl;

	private String webViewTitle;

	private int mType;

	private CustomPopupManager mCustomPopupManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_webview);

//		mCustomPopupManager = new CustomPopupManager(Activity_WebView.this);
//		mCustomPopupManager.onCreatePopup(new Progress_FrameAnim(Activity_WebView.this, R.layout.custom_popup_progress, R.drawable.loading_ani_coin));

		mUrl = getIntent().getStringExtra(WEB_URL);

		webViewTitle = getIntent().getStringExtra(WEBVIEW_TITLE);

		mType = getIntent().getIntExtra(WEBVIEW_TYPE,0);

		setHeaderView(R.drawable.btn_back_white,webViewTitle,0);

		mWebViewMain = (WebView)findViewById(R.id.webview_main);

		mWebViewMain.getSettings().setJavaScriptEnabled(true);

//		mWebViewMain.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		mWebViewMain.addJavascriptInterface(new JavaScriptInterface(), "galuster");

		mWebViewMain.setWebViewClient(new CustomWebClient());
		mWebViewMain.setWebChromeClient(new CustomWebChromeClient());

		if (Define.LOG_YN) {
			Log.d(Define.LOG_TAG, mUrl);
		}
		mWebViewMain.loadUrl(mUrl);

	}


	@Override
	public void onBackPressed() {

		if(mWebViewMain.canGoBack()){
			mWebViewMain.goBack();
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public void onClickLeftBtn(View v) {
		if(mWebViewMain.canGoBack()){
			mWebViewMain.goBack();
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
//		destroyPopup();
		hidePorgressPopup();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		destroyProgressPopup();
//		destroyPopup();
	}

//	public void destroyPopup(){
//		if (mCustomPopupManager != null) {
//			mCustomPopupManager.onDestroy();
//			mCustomPopupManager = null;
//		}
//	}

	class CustomWebChromeClient extends WebChromeClient {


		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

			new AlertDialog.Builder(Activity_WebView.this)
					.setTitle("Wezone")
					.setMessage(message)
					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int which) {
									result.confirm();
								}
							}).setCancelable(false).create().show();

			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			new AlertDialog.Builder(Activity_WebView.this)
					.setTitle("Wezone")
					.setMessage(message)
					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int which) {
									result.confirm();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new AlertDialog.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int which) {
									result.cancel();
								}
							}).setCancelable(false).create().show();
			return true;
		}
	}

	class CustomWebClient extends WebViewClient {

		// loading 상태를 유지
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			if (mCustomPopupManager != null) {
//				mCustomPopupManager.onShow();
//			}
			view.loadUrl(url);

			if(Define.LOG_YN) {
				Log.d("SUMIN", url);
			}
			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
//			if (mCustomPopupManager != null) {
//				mCustomPopupManager.onShow();
//			}
			showProgressPopup();
		}

		@Override
		// 완전히 web loading 이 일어난 후에 실행되는 메소드
		public void onPageFinished(WebView view, String address) {
			hidePorgressPopup();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			hidePorgressPopup();
		}
	}

	public class JavaScriptInterface {

		@JavascriptInterface
		public void onClickBack() {
			setResult(RESULT_CANCELED);
			finish();
		}


		@JavascriptInterface
		public void onSuccess(String totalCount) {
			Intent data = new Intent();
			data.putExtra(INTENTKEY_STATE,INTENTKEY_SUCCESS);
			data.putExtra(INTENTKEY_DATA, totalCount);
			setResult(RESULT_OK,data);
			finish();
		}

		@JavascriptInterface
		public void onFail(String msg){
			Intent data = new Intent();
			data.putExtra(INTENTKEY_STATE,INTENTKEY_FAIL);
			data.putExtra(INTENTKEY_DATA, msg);
			setResult(RESULT_OK,data);
			finish();
		}
	}
}
