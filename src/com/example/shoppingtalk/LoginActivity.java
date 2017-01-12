package com.example.shoppingtalk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.example.shoppingtalk.info.TalkNickNameInfo;
import com.example.shoppingtalk.info.UserInfo;
import com.example.shoppingtalk.sharepref.SharedPrefConstant;
import com.example.shoppingtalk.talk.DemoHelper;
import com.example.shoppingtalk.talk.db.DemoDBManager;
import com.example.shoppingtalk.talk.ui.LianXiRenListActivity;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	private EditText account, pwd;
	private String mAccount = "", mPWD = "";
	private Button btn;
	private boolean progressShow;
	private UserInfo baseInfo;
	private List<TalkNickNameInfo>data;
	private NewVersionInfo version;
	private Map<String, String> map = UtilMap.getInstance().init();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		account = getView(R.id.account);
		pwd = getView(R.id.pwd);
		btn = getView(R.id.btn);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		data = new ArrayList<TalkNickNameInfo>();
		
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAccount = account.getText().toString().trim();
				mPWD = pwd.getText().toString().trim();
				if (mAccount.length() != 11) {
					Utils.showToast(LoginActivity.this, "请正确填写您的手机号码...", 1000);
					return;
				}

//				if (mPWD.length() < 6 || mPWD.length() > 10) {
//					Utils.showToast(LoginActivity.this, "请输入6-10位密码...", 1000);
//					return;
//				}
				
				mPWD = SHA(mPWD);
				System.out.println("mPWD=" + mPWD);
				
				if (!EaseCommonUtils.isNetWorkConnected(LoginActivity.this)) {
					Toast.makeText(LoginActivity.this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
					return;
				}
				
				showLoadingDialog();
				AppContext.getInstance().cancelPendingRequests(TAG);
				StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.LOGIN, new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						try {
							JSONObject json = new JSONObject(result);
							System.out.println("登陆返回的结果"+json.toString());
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
						Utils.showText(LoginActivity.this, "网络访问失败");
					}
				}) {

					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put("mobile", mAccount);
						params.put("password", mPWD);
						params.put("platform", "android_chat");
						return params;
					}
				};
				AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
			}
		});
	}

	private void dealData(String result) {

		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, UserInfo.class);
			if (baseInfo != null) {
				String name = baseInfo.getIm_username();
				String pwd = baseInfo.getIm_password();
				version = baseInfo.getVersion();
				int vCode = Utils.getVersionCode(LoginActivity.this);
				int ver = Integer.parseInt(version.getVer());
				int isUpdate = Integer.parseInt(version.getCompulsive());
				String strUrl = Constant.URL_TEST+version.getUrl();
				if (ver > vCode) {
					Intent intent = new Intent(LoginActivity.this, AlertDialogActivity.class);
					intent.putExtra(SharedPrefConstant.EXTRA_VER_URL, strUrl);
					intent.putExtra(SharedPrefConstant.EXTRA_VER_VERDICT, isUpdate);
					startActivity(intent);
				}else{
					login(name, pwd);
				}
			}
		} else if (status == 500) {
			Utils.showText(LoginActivity.this, "用户名或者密码错误...");
		}
		dismissLoadingDialog();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		AppContext.getInstance().cancelPendingRequests(TAG);
	}

	public void login(String userName, String pwd) {
		
		if (!EaseCommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "EMClient.getInstance().onCancel");
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();

		// After logout，the DemoDB may still be accessed due to async callback,
		// so the DemoDB will be re-opened again.
		// close it before login to make sure DemoDB not overlap
		DemoDBManager.getInstance().closeDB();

		// reset current user name before login
		DemoHelper.getInstance().setCurrentUserName(userName);

		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		Log.d(TAG, "EMClient.getInstance().login");
		EMClient.getInstance().login(userName, pwd, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "login: onSuccess");

				if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
					pd.dismiss();
				}

				// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
				EMClient.getInstance().chatManager().loadAllConversations();
				EMClient.getInstance().groupManager().loadAllGroups();
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMClient.getInstance().updateCurrentUserNick(baseInfo.getNickname());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				// 异步获取当前用户的昵称和头像(从自己服务器获取，demo使用的一个第三方服务)
				DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
				EMClient.getInstance().updateCurrentUserNick(baseInfo.getNickname());// 设置ios推送名称显示
				// 进入主页面shop_id
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.USERID, baseInfo.getShop_id());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.USERID_PWD, mPWD);
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.IM_PWD, baseInfo.getIm_password());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.IM_NAME, baseInfo.getIm_username());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.REN_NAME, baseInfo.getNickname());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.PHONEONE, mAccount);
				
				AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, true);
				
				Intent intent = new Intent(LoginActivity.this,LianXiRenListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}

			@Override
			public void onProgress(int progress, String status) {
				Log.d(TAG, "login: onProgress");
			}

			@Override
			public void onError(final int code, final String message) {
				Log.d(TAG, "login: onError: " + code);
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	public static String SHA(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 监听返回键
	 * */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent home = new Intent(Intent.ACTION_MAIN);  
		    home.addCategory(Intent.CATEGORY_HOME);  
		    startActivity(home);
			return true;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}
}
