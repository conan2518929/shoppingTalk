package com.example.shoppingtalk;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shoppingtalk.constant.Constant;
import com.example.shoppingtalk.constant.Urls;
import com.example.shoppingtalk.info.BaseTalkNickNameInfo;
import com.example.shoppingtalk.info.NewVersionInfo;
import com.example.shoppingtalk.info.StatusInfo;
import com.example.shoppingtalk.info.UserInfo;
import com.example.shoppingtalk.sharepref.SharedPrefConstant;
import com.example.shoppingtalk.talk.DemoHelper;
import com.example.shoppingtalk.talk.ui.LianXiRenListActivity;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WelcomeActivity extends BaseActivity {

	private static final int sleepTime = 2000;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	private static final long SPLASH_DELAY_MILLIS = 1000;
	private RelativeLayout rootLayout;
	private UserInfo baseInfo;
	private NewVersionInfo version;
	private Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
//				Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
//				startActivity(i);
//				finish();
				onLogout();
				break;
			case GO_GUIDE:
				Intent ii = new Intent(WelcomeActivity.this, LianXiRenListActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
				finish();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		rootLayout = (RelativeLayout) findViewById(R.id.ll_start);
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onResume() {
		super.onResume();
		showLoadingDialog();
		if (AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.PHONEONE, "").equals("")) {
			h.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			AppContext.getInstance().cancelPendingRequests(TAG);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.LOGIN, new Response.Listener<String>() {

				@Override
				public void onResponse(String result) {
					try {
						JSONObject json = new JSONObject(result);
						System.out.println("欢迎页面结果"+json.toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dealData(result);
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError volleyError) {
					dismissLoadingDialog();
					Utils.showText(WelcomeActivity.this, "网络访问失败");
					h.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
				}
			}) {

				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("mobile", AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.PHONEONE));
					params.put("password", AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.USERID_PWD));
					params.put("platform", "android_chat");
					return params;
				}
			};
			AppContext.getInstance().addToRequestQueue(stringRequest, TAG);

		}
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	private void isUpdate() {
		AppContext.getInstance().cancelPendingRequests(TAG);

		StringRequest stringRequest = new StringRequest(Request.Method.POST, com.example.shoppingtalk.constant.Constant.URL_TEST
				+ Urls.API_GET_NEW_VERSION, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("升级==" + json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealUpdateReuslt(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(WelcomeActivity.this, "网络访问失败");
				h.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("platform", "android_chat");
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealUpdateReuslt(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			UserInfo baseInfo = gson.fromJson(result, UserInfo.class);
			if (baseInfo != null) {
				NewVersionInfo version = baseInfo.getVersion();
				int vCode = Utils.getVersionCode(WelcomeActivity.this);
				int ver = Integer.parseInt(version.getVer());
				int isUpdate = Integer.parseInt(version.getCompulsive());
				String strUrl = Constant.URL_TEST + version.getUrl();
				if (ver > vCode) {
					Intent intent = new Intent(WelcomeActivity.this, AlertDialogActivity.class);
					intent.putExtra(SharedPrefConstant.EXTRA_VER_URL, strUrl);
					intent.putExtra(SharedPrefConstant.EXTRA_VER_VERDICT, isUpdate);
					startActivity(intent);
				} else {
					boolean isFirstIn = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
					if (isFirstIn) {
						h.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
					} else {
						h.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
					}
				}
			}
		}
	}

	private void dealData(String result) {

		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, UserInfo.class);
			if (baseInfo != null) {
				// String name = baseInfo.getIm_username();
				// String pwd = baseInfo.getIm_password();
				version = baseInfo.getVersion();
				int vCode = Utils.getVersionCode(WelcomeActivity.this);
				int ver = Integer.parseInt(version.getVer());
				int isUpdate = Integer.parseInt(version.getCompulsive());
				String strUrl = Constant.URL_TEST + version.getUrl();
				if (ver > vCode) {
					Intent intent = new Intent(WelcomeActivity.this, AlertDialogActivity.class);
					intent.putExtra(SharedPrefConstant.EXTRA_VER_URL, strUrl);
					intent.putExtra(SharedPrefConstant.EXTRA_VER_VERDICT, isUpdate);
					startActivity(intent);
				} else {
					h.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
				}
			}
		} else if (status == 500) {
			Utils.showText(WelcomeActivity.this, "用户名或者密码错误...");
		}
		dismissLoadingDialog();
	}
	
	// 注销
	 	private void onLogout() {
	 		final ProgressDialog pd = new ProgressDialog(WelcomeActivity.this);
	 		String st = getResources().getString(R.string.Are_logged_out);
	 		pd.setMessage(st);
	 		pd.setCanceledOnTouchOutside(false);
	 		pd.show();
	 		DemoHelper.getInstance().logout(false, new EMCallBack() {
	 			@Override
	 			public void onSuccess() {
	 				runOnUiThread(new Runnable() {
	 					public void run() {
	 						pd.dismiss();
	 						AppContext.mSharedPref.clear();
	 						AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
	 						Utils.gotoOtherActivity(WelcomeActivity.this, LoginActivity.class);
	 						finish();
	 					}
	 				});
	 			}

	 			@Override
	 			public void onProgress(int progress, String status) {

	 			}

	 			@Override
	 			public void onError(int code, String message) {
	 				runOnUiThread(new Runnable() {

	 					@Override
	 					public void run() {
	 						pd.dismiss();
	 						Toast.makeText(WelcomeActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
	 					}
	 				});
	 			}
	 		});
	 	}
}
