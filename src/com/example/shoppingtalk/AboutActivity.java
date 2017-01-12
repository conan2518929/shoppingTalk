package com.example.shoppingtalk;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.example.shoppingtalk.adapter.MyAboutHuDongListAdapter;
import com.example.shoppingtalk.adapter.MyAboutHuDongListAdapter.MyHuDongClickListener;
import com.example.shoppingtalk.constant.Constant;
import com.example.shoppingtalk.constant.Urls;
import com.example.shoppingtalk.entity.BaseData;
import com.example.shoppingtalk.entity.BaseHDListInfo;
import com.example.shoppingtalk.entity.BaseZanSCInfo;
import com.example.shoppingtalk.entity.CommentsInfo;
import com.example.shoppingtalk.entity.HDInfo;
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

public class AboutActivity extends BaseActivity implements OnClickListener {

	private String user_type = "", user_id = "";
	private LinearLayout back, ll_hint, hdhf, hdsc, hdfb;

	private TextView tv_fb_num, tv_fb;
	private TextView tv_hf_num, tv_hf;
	private TextView tv_sc_num, tv_sc;

	private String carType = "1";

	private PullToRefreshListView mPullListView;

	private ListView listView;

	private List<PostsInfo> posts;

	private List<ZanListInfo> data;

	private RelativeLayout jianpan;

	private Animation mShowAction, mHiddenAction;

	private ContainsEmojiEditText input;
	private Button send;

	private int clickPosition = -1;// 点击哪个评论
	private MyBroadcastReciver myBroadcast;

	private InputMethodManager imm;

	private MyAboutHuDongListAdapter adapter;

	private String userId = AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.USERID, "");
	private String userType = "shop";
	private String typeSend = "-1";
	private String post_id = "", father_id = "", father_user_type = "", father_user_id = "", content = "", userTYpe = "", userID = "";
	private HDInfo hdData;
	private CircleImageView image_tx;

	private int a = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
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
		hdhf = getView(R.id.hdhf);
		hdsc = getView(R.id.hdsc);
		hdfb = getView(R.id.hdfb);
		image_tx = getView(R.id.image_tx);

		tv_fb_num = getView(R.id.tv_fb_num);
		tv_fb = getView(R.id.tv_fb);
		tv_hf_num = getView(R.id.tv_hf_num);
		tv_hf = getView(R.id.tv_hf);
		tv_sc_num = getView(R.id.tv_sc_num);
		tv_sc = getView(R.id.tv_sc);

		jianpan = getView(R.id.jianpan);
		ll_hint = getView(R.id.ll_hint);
		input = getView(R.id.input);
		send = getView(R.id.send);

		mPullListView = getView(R.id.listview);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		user_type = getIntent().getExtras().getString("user_type");
		user_id = getIntent().getExtras().getString("user_id");

		posts = new ArrayList<>();
		data = new ArrayList<>();

		send.setOnClickListener(this);
		ll_hint.setOnClickListener(this);

		hdhf.setOnClickListener(this);
		hdsc.setOnClickListener(this);
		hdfb.setOnClickListener(this);
		back.setOnClickListener(this);
		mPullListView.setPullLoadEnabled(true);
		mPullListView.setScrollLoadEnabled(false);

		listView = mPullListView.getRefreshableView();
		adapter = new MyAboutHuDongListAdapter(AboutActivity.this, posts, mListener, carType);
		adapter.setListView(listView);
		listView.setAdapter(adapter);
		listView.setVerticalScrollBarEnabled(false);
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (carType.equals("1")) {
					System.out.println("互动发表");
					getData(Urls.API_GET_POST_LIST_SOMEONE);
				}
				if (carType.equals("2")) {
					System.out.println("互动回复");
					getData(Urls.API_GET_POST_LIST_COMMENTED);
				}
				if (carType.equals("3")) {
					System.out.println("互动收藏");
					getData(Urls.API_GET_POST_LIST_SAVED);
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullListView.onPullUpRefreshComplete();
			}
		});
		mPullListView.setLastUpdatedLabel(AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME));
		mPullListView.doPullRefreshing(false, 100);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.hdhf:
			initColor();
			carType = "2";
			tv_hf_num.setTextColor(Color.parseColor("#53affa"));
			tv_hf.setTextColor(Color.parseColor("#53affa"));
			getData(Urls.API_GET_POST_LIST_COMMENTED);
			break;
		case R.id.hdfb:
			initColor();
			carType = "1";
			tv_fb_num.setTextColor(Color.parseColor("#53affa"));
			tv_fb.setTextColor(Color.parseColor("#53affa"));
			getData(Urls.API_GET_POST_LIST_SOMEONE);
			break;
		case R.id.hdsc:
			initColor();
			carType = "3";
			tv_sc_num.setTextColor(Color.parseColor("#53affa"));
			tv_sc.setTextColor(Color.parseColor("#53affa"));
			getData(Urls.API_GET_POST_LIST_SAVED);
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
				if (typeSend.equals("-1")) {// 点击直接发布的按钮
					post_id = posts.get(clickPosition).getId();// clickPosition
					father_id = "0";
					father_user_type = "0";
					father_user_id = "0";
				}
				if (typeSend.equals("0")) {// 点击评论列表的item直接发布的消息的
					if (userId.equals(userID) && userType.equals(userTYpe)) {
						father_id = "0";
						father_user_type = "0";
						father_user_id = "0";
					}
				}
				if (typeSend.equals("4")) {// 点击评论列表的item 回复发布的消息的
					if (userId.equals(userID) && userType.equals(userTYpe)) {
						father_id = "0";
						father_user_type = "0";
						father_user_id = "0";
					}
				}
				sendCcomment();
			} else {
				Utils.showShortToast(AboutActivity.this, "请输入评论内容...");
			}
			break;
		default:
			break;
		}
	}

	private void initColor() {
		tv_fb_num.setTextColor(Color.parseColor("#878787"));// #53affa
		tv_fb.setTextColor(Color.parseColor("#878787"));
		tv_hf_num.setTextColor(Color.parseColor("#878787"));
		tv_hf.setTextColor(Color.parseColor("#878787"));
		tv_sc_num.setTextColor(Color.parseColor("#878787"));
		tv_sc.setTextColor(Color.parseColor("#878787"));

	}

	private MyHuDongClickListener mListener = new MyHuDongClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			switch (v.getId()) {
			case R.id.play:
				String videoUrl = posts.get(position).getVideos().get(0).getVideo_url();
				if (videoUrl != null && videoUrl.length() != 0) {
					Intent i = new Intent(AboutActivity.this, VideoActivity.class);
					i.putExtra("info", Constant.URL_TEST + videoUrl);
					startActivity(i);
				} else {
					Utils.showText(AboutActivity.this, "没有播放视频...");
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
			case R.id.delete:
				String post_id = posts.get(position).getId();
				deleteHudong(Urls.API_DELETE_POST, post_id, position);
				break;
			case R.id.talkimg:
				String name = posts.get(position).getPoster_im_username();
				if (name.equals(AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.IM_NAME, ""))) {
					Utils.showShortToast(AboutActivity.this, "不能和自己聊天...");
					return;
				}
				String niciname = posts.get(position).getPoster_name();
				if (name != null && name.length() != 0 && niciname != null && niciname.length() != 0) {
					Intent intent = new Intent(AboutActivity.this, ChatActivity.class);
					intent.putExtra(com.example.shoppingtalk.talk.Constant.EXTRA_USER_ID, name);
					intent.putExtra("user_nickname", niciname);
					startActivity(intent);
				}
				break;
			case R.id.img:
				Bundle data = new Bundle();
				data.putString("user_type", posts.get(position).getPoster_type());
				data.putString("user_id", posts.get(position).getPoster_id());
				Utils.goOtherWithDataActivity(AboutActivity.this, HDShopDetailActivity.class, data);
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

	@SuppressWarnings("deprecation")
	private void getData(String url) {
		AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME, new Date().toLocaleString());
		mPullListView.setLastUpdatedLabel(new Date().toLocaleString());
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + url, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {

				try {
					JSONObject json = new JSONObject(result);
					System.out.println("我的互动===" + json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealQD(result);
				mPullListView.onPullDownRefreshComplete();
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(AboutActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				if (carType.equals("1")) {
					params.put("to_user_type", "shop");
					params.put("to_user_id", user_id);
				}
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealQD(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if (statusInfo != null) {
			int status = statusInfo.getStatus();
			if (status == 200) {
				BaseHDListInfo info = gson.fromJson(result, BaseHDListInfo.class);
				if (info != null) {
					hdData = info.getData();

					if (hdData != null) {
						if (hdData.getSave_num() != null && hdData.getSave_num().length() != 0) {
							tv_fb_num.setText(hdData.getPost_num());
							tv_sc_num.setText(hdData.getSave_num());
							tv_hf_num.setText(hdData.getComment_num());
						}
						if (a == 0) {
							ImageLoader.getInstance().displayImage(Constant.URL_TEST + hdData.getAvatar(), image_tx);
							a = 1;
						}

						posts = hdData.getPosts();
						if (posts != null) {
							adapter.refresh(posts, carType);
						}
					}
				}
			}
		}
	}

	/**
	 * 赞或者收藏
	 */
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
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(AboutActivity.this, "网络访问失败");
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

					if (hdData != null) {
						if (hdData.getSave_num() != null && hdData.getSave_num().length() != 0) {
							tv_sc_num.setText(Integer.parseInt(hdData.getSave_num()) + 1 + "");
							hdData.setSave_num(Integer.parseInt(hdData.getSave_num()) + 1 + "");
						}
					}

				} else if (type.equals("2")) {// 取消收藏
					posts.get(position).setSave(0);
					int number = posts.get(position).getSave_num() - 1;
					if (number < 1) {
						posts.get(position).setSave_num(0);
					} else {
						posts.get(position).setSave_num(number);
					}
					adapter.updateItemData(posts.get(position), position, 0);

					if (hdData != null) {
						if (hdData.getSave_num() != null && hdData.getSave_num().length() != 0) {
							if ((Integer.parseInt(hdData.getSave_num()) - 1) >= 0) {
								tv_sc_num.setText(Integer.parseInt(hdData.getSave_num()) - 1 + "");
								hdData.setSave_num(Integer.parseInt(hdData.getSave_num()) - 1 + "");
							}
						}
					}

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
	 */
	private void sendCcomment() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_COMMENT_POST, new Response.Listener<String>() {

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
				Utils.showText(AboutActivity.this, "网络访问失败");
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
			if (action.equals("com.app.action.broadcast_myhudong")) {

				typeSend = data.getString("type");
				if (typeSend.equals("0") || typeSend.equals("4")) {
					clickPosition = data.getInt("position");// 评论列表 整体的第几位
					CommentsInfo info = (CommentsInfo) data.getSerializable("info");
					post_id = posts.get(clickPosition).getId();

					father_id = info.getId();
					father_user_type = info.getUser_type();
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
				if (typeSend.equals("6")) {
					clickPosition = data.getInt("hold");
					int huifuweizhi = data.getInt("huifuweizhi");
					deleteCcomment(Urls.API_DELETE_COMMENT_POST, posts.get(clickPosition).getComments().get(huifuweizhi).getId());
				}
			}
		}
	}

	private void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		myBroadcast = new MyBroadcastReciver();
		intentFilter.addAction("com.app.action.broadcast_myhudong");
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
	 * 删除某一条互动
	 */
	private void deleteHudong(String url, final String post_id, final int position) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + url, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("====删除互动===" + json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealResult(result, position);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(AboutActivity.this, "网络访问失败");
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

	private void dealResult(String result, int position) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if (statusInfo != null) {
			int status = statusInfo.getStatus();
			if (status == 200) {
				posts.remove(position);
				adapter.refresh(posts, "1");
				if (hdData != null) {
					if (hdData.getPost_num() != null && hdData.getPost_num().length() != 0) {
						tv_fb_num.setText(Integer.parseInt(hdData.getPost_num()) - 1 + "");
						hdData.setPost_num(Integer.parseInt(hdData.getPost_num()) - 1 + "");
					}
				}
			}
		}
	}

	/**
	 * 删除某条评论
	 */
	private void deleteCcomment(String url, final String post_id) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + url, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("====删除消息===" + json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealDeleteCcomment(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(AboutActivity.this, "网络访问失败");
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

	private void dealDeleteCcomment(String result) {
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
					adapter.updateItemData(posts.get(clickPosition), clickPosition, 2);
				}
			}
		}
	}
}
