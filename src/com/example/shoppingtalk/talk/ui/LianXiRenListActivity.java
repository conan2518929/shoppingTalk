package com.example.shoppingtalk.talk.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shoppingtalk.AppContext;
import com.example.shoppingtalk.AppManager;
import com.example.shoppingtalk.BaseActivity;
import com.example.shoppingtalk.LoginActivity;
import com.example.shoppingtalk.R;
import com.example.shoppingtalk.UtilMap;
import com.example.shoppingtalk.Utils;
import com.example.shoppingtalk.constant.Urls;
import com.example.shoppingtalk.hudong.InteractiveActivity;
import com.example.shoppingtalk.info.BaseTalkNickNameInfo;
import com.example.shoppingtalk.info.StatusInfo;
import com.example.shoppingtalk.info.TalkNickNameInfo;
import com.example.shoppingtalk.sharepref.SharedPrefConstant;
import com.example.shoppingtalk.talk.Constant;
import com.example.shoppingtalk.talk.DemoHelper;
import com.example.shoppingtalk.talk.DemoModel;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;

public class LianXiRenListActivity extends BaseActivity{

	protected static final String TAG = "LianXiRenListActivity";

	private ConversationListFragment conversationListFragment;
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo>data;
	private Map<String, String> map = UtilMap.getInstance().init();
	private DemoModel settingsModel;
	
	private TextView out,hudong;
	private MyBroadcastReciver myBroadcast;
	private long mExitTime;
	
	// 账号在别处登录
		public boolean isConflict = false;
		// 账号被移除
		private boolean isCurrentAccountRemoved = false;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			DemoHelper.getInstance().logout(false, null);
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			return;
		}
		setContentView(R.layout.activity_list);
		out = (TextView) findViewById(R.id.out);
		hudong = (TextView) findViewById(R.id.hudong);
		conversationListFragment = new ConversationListFragment();
		data = new ArrayList<TalkNickNameInfo>();
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment).show(conversationListFragment).commit();

		EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
		System.out.println("width:" + getScreenWidth(this) + "  height:" + getScreenHeight(this));
		settingsModel = DemoHelper.getInstance().getModel();
		settingsModel.setSettingMsgVibrate(false);
		String allname = "";
		List<EMConversation> list = loadConversationList();
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				allname = allname+list.get(i).getUserName()+",";
			}
			allname = allname.substring(0, allname.length()-1);
			getNickNameResut(allname);
		}
		out.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onLogout();
			}
		});
		hudong.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Utils.gotoOtherActivity(LianXiRenListActivity.this, InteractiveActivity.class);
			}
		});
		initBroadcast();
		if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	public void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		myBroadcast = new MyBroadcastReciver();
		intentFilter.addAction("com.app.action.broadcast");
		registerReceiver(myBroadcast, intentFilter);
	}
	
	private void unregisterReceiver() {
		if (myBroadcast != null) {
			unregisterReceiver(myBroadcast);
		}
	}
	
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	// 获取屏幕的高度
	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public class MyContactListener implements EMContactListener {
		@Override
		public void onContactAdded(String username) {
		}

		@Override
		public void onContactDeleted(final String username) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null && username.equals(ChatActivity.activityInstance.toChatUsername)) {
						String st10 = getResources().getString(R.string.have_you_removed);
						Toast.makeText(LianXiRenListActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, 1).show();
						ChatActivity.activityInstance.finish();
					}
				}
			});
		}

		@Override
		public void onContactInvited(String username, String reason) {
		}

		@Override
		public void onContactAgreed(String username) {
		}

		@Override
		public void onContactRefused(String username) {
		}
	}

	EMMessageListener messageListener = new EMMessageListener() {

		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			// 提示新消息
			String allname = "";
			for (EMMessage message : messages) {	
				String str1 = message.getFrom();
				allname = allname + message.getFrom() + ",";
				DemoHelper.getInstance().getNotifier().onNewMsg(message);
			}
			allname = allname.substring(0, allname.length()-1);
			getNickNameResut(allname);
		}

		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
		}

		@Override
		public void onMessageReadAckReceived(List<EMMessage> messages) {
		}

		@Override
		public void onMessageDeliveryAckReceived(List<EMMessage> message) {
		}

		@Override
		public void onMessageChanged(EMMessage message, Object change) {
		}
	};

	private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (conversationListFragment != null) {
					conversationListFragment.refresh();
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.pushActivity(this);
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(this);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
	
	
	private void getNickNameResut(final String allname) {
		AppContext.getInstance().cancelPendingRequests(TAG);

		StringRequest stringRequest = new StringRequest(Request.Method.POST, com.example.shoppingtalk.constant.Constant.URL_TEST + Urls.api_get_ALL_nickname, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("昵称=="+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealReturnReuslt(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(LianXiRenListActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("im_username_list", allname);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
    
    private void dealReturnReuslt(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseTalkNickName = gson.fromJson(result, BaseTalkNickNameInfo.class);
			if(baseTalkNickName != null){
				data = baseTalkNickName.getData();
				if(data.size() > 0){
					for(int i=0;i<data.size();i++){
						map.put(data.get(i).getIm_username(), data.get(i).getNickname());
					}
				}
				refreshUIWithMessage();
			}
		}
	}
    
 // 注销
 	private void onLogout() {
 		final ProgressDialog pd = new ProgressDialog(LianXiRenListActivity.this);
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
 						Utils.gotoOtherActivity(LianXiRenListActivity.this, LoginActivity.class);
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
 						Toast.makeText(LianXiRenListActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
 					}
 				});
 			}
 		});
 	}
 	
	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.app.action.broadcast")) {
				getNickNameResut(intent.getStringExtra("id"));
			}
		}
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver();
		AppContext.getInstance().cancelPendingRequests(TAG);
		super.onDestroy();
		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
		try {
			unregisterReceiver(internalDebugReceiver);
		} catch (Exception e) {
		}
	}
	
	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	private BroadcastReceiver internalDebugReceiver;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;
	
	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		
		isConflictDialogShow = true;
		DemoHelper.getInstance().logout(false, null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!LianXiRenListActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(LianXiRenListActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						onLogout();
						isConflictDialogShow = false;
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHelper.getInstance().logout(false, null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!LianXiRenListActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(LianXiRenListActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						finish();
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
			return false; 
			}
		return super.onKeyDown(keyCode, event);
	}
}
