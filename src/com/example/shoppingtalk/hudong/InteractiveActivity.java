package com.example.shoppingtalk.hudong;

import java.util.ArrayList;
import java.util.Date;
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
import com.example.shoppingtalk.AboutActivity;
import com.example.shoppingtalk.AppContext;
import com.example.shoppingtalk.BaseActivity;
import com.example.shoppingtalk.HDShopDetailActivity;
import com.example.shoppingtalk.R;
import com.example.shoppingtalk.ReleaseActivity;
import com.example.shoppingtalk.Utils;
import com.example.shoppingtalk.VideoActivity;
import com.example.shoppingtalk.adapter.HuDongListAdapter;
import com.example.shoppingtalk.adapter.HuDongListAdapter.MyClickListener;
import com.example.shoppingtalk.constant.Constant;
import com.example.shoppingtalk.constant.Urls;
import com.example.shoppingtalk.entity.BaseData;
import com.example.shoppingtalk.entity.BaseHDListInfo;
import com.example.shoppingtalk.entity.BaseHuDongInfo;
import com.example.shoppingtalk.entity.BaseZanSCInfo;
import com.example.shoppingtalk.entity.CommentsInfo;
import com.example.shoppingtalk.entity.PostsInfo;
import com.example.shoppingtalk.entity.ZanListInfo;
import com.example.shoppingtalk.info.StatusInfo;
import com.example.shoppingtalk.sharepref.SharedPrefConstant;
import com.example.shoppingtalk.talk.ui.ChatActivity;
import com.example.shoppingtalk.widget.CircleImageView;
import com.example.shoppingtalk.widget.ContainsEmojiEditText;
import com.example.shoppingtalk.widget.PullToRefreshBase;
import com.example.shoppingtalk.widget.PullToRefreshBase.OnRefreshListener;
import com.example.shoppingtalk.widget.PullToRefreshListView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InteractiveActivity extends BaseActivity implements OnClickListener{
	private LinearLayout back, ll_hint;

	private ImageView add, bg;

	private TextView oumei, rihan, guochan, about;

	private PullToRefreshListView mPullListView;

	private ListView listView;
	// private String type = "1";//0不传参数 初始化 1日系、2欧美、3国产

	private HuDongListAdapter adapter;

	private List<PostsInfo> posts;

	private int isFirst = 0;

	private List<ZanListInfo> data;

	private RelativeLayout jianpan;
	private CircleImageView image_tx;
	private Animation mShowAction, mHiddenAction;

	private ContainsEmojiEditText input;

	private Button send;

	private InputMethodManager imm;

	private int clickPosition = -1;// 点击哪个评论

	private String post_id = "", father_id = "", father_user_type = "", father_user_id = "", content = "",userTYpe = "",userID = "";

	private MyBroadcastReciver myBroadcast;

	private String typeSend = "-1";
	
	private String userId = AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.USERID,"");
	private String userType = "shop";
	
	private String carType = "1";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hudong);
		initViews();
		initData();
		initBroadcast();
		mShowAction = AnimationUtils.loadAnimation(this, R.anim.dialog_show);
		mHiddenAction = AnimationUtils.loadAnimation(this, R.anim.dialog_dismiss);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		back = getView(R.id.back);
		add = getView(R.id.add);
		bg = getView(R.id.bg_img);
		image_tx = getView(R.id.image_tx);

		mPullListView = getView(R.id.listview);

		oumei = getView(R.id.oumei);
		rihan = getView(R.id.rihan);
		guochan = getView(R.id.tv_guochan);
		about = getView(R.id.about);

		jianpan = getView(R.id.jianpan);
		ll_hint = getView(R.id.ll_hint);
		input = getView(R.id.input);
		send = getView(R.id.send);
	}

	@Override
	public void initData() {
		posts = new ArrayList<>();
		data = new ArrayList<>();
		oumei.setOnClickListener(this);
		guochan.setOnClickListener(this);
		rihan.setOnClickListener(this);
		about.setOnClickListener(this);
		back.setOnClickListener(this);
		add.setOnClickListener(this);
		send.setOnClickListener(this);
		ll_hint.setOnClickListener(this);
		mPullListView.setPullLoadEnabled(true);
		mPullListView.setScrollLoadEnabled(false);

		listView = mPullListView.getRefreshableView();
		adapter = new HuDongListAdapter(InteractiveActivity.this, posts, mListener);
		adapter.setListView(listView);
		listView.setAdapter(adapter);
		listView.setVerticalScrollBarEnabled(false);
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (isFirst == 1) {
					getData(carType , Urls.API_GETL_POST_LIST);
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullListView.onPullUpRefreshComplete();
			}
		});
		mPullListView.setLastUpdatedLabel(AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME));
		mPullListView.doPullRefreshing(false, 100);
		getData("0", Urls.API_GETL_POST_TYPE);
	}

	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			switch (v.getId()) {
			case R.id.play:
				String videoUrl = posts.get(position).getVideos().get(0).getVideo_url();
				if (videoUrl != null && videoUrl.length() != 0) {
					Intent i = new Intent(InteractiveActivity.this, VideoActivity.class);
					i.putExtra("info", Constant.URL_TEST + videoUrl);
					startActivity(i);
				} else {
					Utils.showText(InteractiveActivity.this, "没有播放视频...");
				}
				break;
			case R.id.soucang:
				String id = posts.get(position).getId();
				int numSC = posts.get(position).getSave();
				if (numSC == 0) {
					isZanOrSave("1", Urls.API_SAVE_POST, id, position);
				} else {
					isZanOrSave("2", Urls.API_CANCEL_SAVE_POST, id, position);
				}
				break;
			case R.id.zan:
				String idZan = posts.get(position).getId();
				int numZan = posts.get(position).getZan();
				if (numZan == 0) {
					isZanOrSave("3", Urls.API_ZAN_POST, idZan, position);
				} else {
					isZanOrSave("4", Urls.API_CANCEL_ZAN_POST, idZan, position);
				}
				break;
			case R.id.talkimg:
				String name = posts.get(position).getPoster_im_username();
				if (name.equals(AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.IM_NAME, ""))) {
					Utils.showShortToast(InteractiveActivity.this, "不能和自己聊天...");
					return;
				}
				String niciname = posts.get(position).getPoster_name();
				if (name != null && name.length() != 0 && niciname != null && niciname.length() != 0) {
					Intent intent = new Intent(InteractiveActivity.this, ChatActivity.class);
					intent.putExtra(com.example.shoppingtalk.talk.Constant.EXTRA_USER_ID, name);
					intent.putExtra("user_nickname", niciname);
					startActivity(intent);
				}
				break;
			case R.id.img:
				Bundle data = new Bundle();
				data.putString("user_type", posts.get(position).getPoster_type());
				data.putString("user_id", posts.get(position).getPoster_id());
				Utils.goOtherWithDataActivity(InteractiveActivity.this, HDShopDetailActivity.class,data);
				break;
			case R.id.pl:// 评论
				jianpan.startAnimation(mShowAction);
				jianpan.setVisibility(View.VISIBLE);
				input.setText("");
				input.requestFocus();
				imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				clickPosition = position;
				typeSend = "-1";
				break;
			default:
				break;
			}
		}
	};

	// private void updateAlias(String name) {
	// Intent intent = new Intent(InteractiveActivity.this, ChatActivity.class);
	// intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, name);
	// intent.putExtra("user_nickname", baseInfo.getShop_name());
	// startActivity(intent);
	// }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.oumei:
			initColor();
			carType = "2";
			oumei.setBackgroundColor(Color.parseColor("#3490f5"));
			getData("2", Urls.API_GETL_POST_LIST);
			break;
		case R.id.rihan:
			initColor();
			carType = "1";
			rihan.setBackgroundColor(Color.parseColor("#3490f5"));
			getData("1", Urls.API_GETL_POST_LIST);
			break;
		case R.id.tv_guochan:
			initColor();
			carType = "3";
			guochan.setBackgroundColor(Color.parseColor("#3490f5"));
			getData("3", Urls.API_GETL_POST_LIST);
			break;
		case R.id.about:// 与我相关
			Bundle bundle = new Bundle();
			bundle.putString("user_type", userType);
			bundle.putString("user_id",userId);
			Utils.goOtherWithDataActivity(InteractiveActivity.this, AboutActivity.class, bundle);
			break;
		case R.id.back:
			finish();
			break;
		case R.id.add:
			Utils.gotoOtherActivity(InteractiveActivity.this, ReleaseActivity.class);// 发布页面
			break;
		case R.id.ll_hint:
			imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
			// jianpan.startAnimation(mHiddenAction);
			jianpan.setVisibility(View.GONE);
			input.setText("");
			break;
		case R.id.send:// 发送消息
			content = input.getText().toString().trim();
			if (content.length() != 0) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				if(typeSend.equals("-1")){//点击直接发布的按钮
					post_id = posts.get(clickPosition).getId();// clickPosition
					father_id = "0";
					father_user_type = "0";
					father_user_id = "0";
				}
				if(typeSend.equals("0")){//点击评论列表的item直接发布的消息的 
					if(userId.equals(userID) && userType.equals(userTYpe)){
						father_id = "0";
						father_user_type = "0";
						father_user_id = "0";
					}
				}
				if(typeSend.equals("4")){//点击评论列表的item 回复发布的消息的 
					if(userId.equals(userID) && userType.equals(userTYpe)){
						father_id = "0";
						father_user_type = "0";
						father_user_id = "0";
					}
				}
				sendCcomment();
			} else {
				Utils.showShortToast(InteractiveActivity.this, "请输入评论内容...");
			}
			break;
		}
	}

	private void initColor() {
		rihan.setBackgroundColor(Color.parseColor("#cccccc"));
		oumei.setBackgroundColor(Color.parseColor("#cccccc"));
		guochan.setBackgroundColor(Color.parseColor("#cccccc"));
	}

	@SuppressWarnings("deprecation")
	private void getData(final String type, String url) {
		AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME, new Date().toLocaleString());
		mPullListView.setLastUpdatedLabel(new Date().toLocaleString());
		// AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + url, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {

				if (type.equals("0")) {
					try {
						JSONObject json = new JSONObject(result);
						System.out.println("初始化互动页面===" + json.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dealQD(result);
				} else {
					try {
						JSONObject json = new JSONObject(result);
						System.out.println("日韩欧美国产===" + json.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dealListValue(result);
				}
				mPullListView.onPullDownRefreshComplete();
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(InteractiveActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				if (!type.equals("0")) {
					System.out.println("==type==="+type);
					params.put("post_type_filter", type);
				}
				return params;
			}
		};
		;
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealQD(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if (statusInfo != null) {
			int status = statusInfo.getStatus();
			if (status == 200) {
				BaseHuDongInfo info = gson.fromJson(result, BaseHuDongInfo.class);
				ImageLoader.getInstance().displayImage(Constant.URL_TEST + info.getHudong_bg(), bg);
				ImageLoader.getInstance().displayImage(Constant.URL_TEST + info.getAvatar(), image_tx);
				isFirst = 1;
				getData(carType, Urls.API_GETL_POST_LIST);
			}
		}
	}

	private void dealListValue(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if (statusInfo != null) {
			int status = statusInfo.getStatus();
			if (status == 200) {
				BaseHDListInfo base = gson.fromJson(result, BaseHDListInfo.class);
				posts = base.getData().getPosts();
				if (posts != null) {
					adapter.refresh(posts);
				}
			}
		}
	}

	/**
	 * 赞或者收藏
	 * */
	private void isZanOrSave(final String type, String url, final String post_id, final int position) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + url, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("====" + type + "===" + json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealZanOrSC(result, type, position);
				mPullListView.onPullDownRefreshComplete();
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(InteractiveActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("post_id", post_id);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealZanOrSC(String result, String type, int position) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if (statusInfo != null) {
			int status = statusInfo.getStatus();
			if (status == 200) {
				BaseZanSCInfo info = gson.fromJson(result, BaseZanSCInfo.class);

				if (type.equals("1")) {// 1收藏
					posts.get(position).setSave(1);
					posts.get(position).setSave_num(posts.get(position).getSave_num() + 1);
					adapter.updateItemData(posts.get(position), position, 0);
				} else if (type.equals("2")) {// 取消收藏
					posts.get(position).setSave(0);
					int number = posts.get(position).getSave_num() - 1;
					if (number < 1) {
						posts.get(position).setSave_num(0);
					} else {
						posts.get(position).setSave_num(number);
					}
					adapter.updateItemData(posts.get(position), position, 0);

				} else if (type.equals("3")) {// 点赞
					data = info.getData();
					posts.get(position).setZan_list(data);
					posts.get(position).setZan(1);
					adapter.updateItemData(posts.get(position), position, 1);// 收藏0
																				// 点赞1
				} else if (type.equals("4")) {// 取消点赞
					data = info.getData();
					posts.get(position).setZan_list(data);
					posts.get(position).setZan(0);
					adapter.updateItemData(posts.get(position), position, 1);
				}
			}
		}
	}

	/**
	 * 发表评论
	 * */
	private void sendCcomment() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_COMMENT_POST,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						try {
							JSONObject json = new JSONObject(result);
							System.out.println("====发布消息===" + json.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dealCcomment(result);
						mPullListView.onPullDownRefreshComplete();
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(InteractiveActivity.this, "网络访问失败");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("post_id", post_id);
				params.put("father_id", father_id);
				params.put("father_user_type", father_user_type);
				params.put("father_user_id", father_user_id);
				params.put("content", content);
				System.out.println("post_id=" + post_id);
				System.out.println("father_id=" + father_id);
				System.out.println("father_user_type=" + father_user_type);
				System.out.println("father_user_id=" + father_user_id);
				System.out.println("content=" + content);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealCcomment(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if (statusInfo != null) {
			int status = statusInfo.getStatus();
			if (status == 200) {
				BaseData baseData = gson.fromJson(result, BaseData.class);
				List<CommentsInfo> data = new ArrayList<>();
				if (baseData != null) {
					data = baseData.getData();
					posts.get(clickPosition).setComments(data);
					adapter.updateItemData(posts.get(clickPosition), clickPosition, 2);
				}
				input.setText("");
			}
		}
	}

	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle data = intent.getExtras();
			String action = intent.getAction();
			if (action.equals("com.app.action.broadcast_send_content")) {
				
					typeSend = data.getString("type");
					if(typeSend.equals("0") || typeSend.equals("4")){
						clickPosition = data.getInt("position");//评论列表 整体的第几位
						CommentsInfo info = (CommentsInfo) data.getSerializable("info");
						post_id = posts.get(clickPosition).getId();
						
						father_id = info.getId();
						father_user_type =info.getUser_type();
						father_user_id = info.getUser_id();
						userTYpe = info.getUser_type();
						userID = info.getUser_id();
						
						jianpan.startAnimation(mShowAction);
						jianpan.setVisibility(View.VISIBLE);
						input.setText("");
						input.requestFocus();
						imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					}
					if(typeSend.equals("5")){
						String leixing = data.getString("leixing");
						initColor();
						if(leixing.equals("1")){
							rihan.setBackgroundColor(Color.parseColor("#3490f5"));
							getData("1", Urls.API_GETL_POST_LIST);
						}else if(leixing.equals("2")){
							oumei.setBackgroundColor(Color.parseColor("#3490f5"));
							getData("2", Urls.API_GETL_POST_LIST);
						}else if(leixing.equals("3")){
							guochan.setBackgroundColor(Color.parseColor("#3490f5"));
							getData("3", Urls.API_GETL_POST_LIST);
						}
					}
					
					if(typeSend.equals("6")){
						clickPosition = data.getInt("hold");
						int huifuweizhi = data.getInt("huifuweizhi");
						deleteCcomment(Urls.API_DELETE_COMMENT_POST,posts.get(clickPosition).getComments().get(huifuweizhi).getId(),clickPosition,huifuweizhi);
					}
			}
		}
	}

	private void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		myBroadcast = new MyBroadcastReciver();
		intentFilter.addAction("com.app.action.broadcast_send_content");
		registerReceiver(myBroadcast, intentFilter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver();
		super.onDestroy();
	}

	private void unregisterReceiver() {
		if (myBroadcast != null) {
			unregisterReceiver(myBroadcast);
		}
	}
	/**
	 * 删除某条评论
	 * */
	private void deleteCcomment(String url,final String post_id,final int position,final int huifuweizhi) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						try {
							JSONObject json = new JSONObject(result);
							System.out.println("====删除消息===" + json.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dealDeleteCcomment(result,position,huifuweizhi);
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(InteractiveActivity.this, "网络访问失败");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("comment_id", post_id);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealDeleteCcomment(String result,int position,int huifuweizhi) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if (statusInfo != null) {
			int status = statusInfo.getStatus();
			if (status == 200) {
				BaseData baseData = gson.fromJson(result, BaseData.class);
				if (baseData != null) {
					List<CommentsInfo> data = new ArrayList<>();
					data = baseData.getData();
					posts.get(clickPosition).setComments(data);
//					posts.get(clickPosition).getComments().remove(huifuweizhi);
//					adapter.updateItemData(posts.get(clickPosition), clickPosition, 2);
					adapter.updateItemData(posts.get(clickPosition), clickPosition, 2);
				}
//				posts.get(clickPosition).getComments().remove(huifuweizhi);
//				adapter.refreshCommentData(posts.get(clickPosition).getComments(),huifuweizhi);
			}
		}
	}
}
