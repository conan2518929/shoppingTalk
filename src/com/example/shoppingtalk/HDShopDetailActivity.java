package com.example.shoppingtalk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import com.example.shoppingtalk.adapter.HuDongDetailListAdapter;
import com.example.shoppingtalk.adapter.HuDongDetailListAdapter.MyDetailClickListener;
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

public class HDShopDetailActivity extends BaseActivity implements OnClickListener {

	private String user_type = "", user_id = "";
	
	private LinearLayout back, ll_hint;
	
	private ImageView hd_tel, hd_shop, bg;

	private String mTel = "";

	private PullToRefreshListView mPullListView;

	private ListView listView;

	private HuDongDetailListAdapter adapter;

	private List<PostsInfo> posts;

	private List<ZanListInfo> data;

	private RelativeLayout jianpan;

	private Animation mShowAction, mHiddenAction;

	private ContainsEmojiEditText input;
	private CircleImageView image_tx;
	private Button send;

	private int clickPosition = -1;// ����ĸ�����
	private MyBroadcastReciver myBroadcast;

	private InputMethodManager imm;

	private String userId = AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.USERID, "");
	private String userType = "shop";
	private String typeSend = "-1";
	private String post_id = "", father_id = "", father_user_type = "", father_user_id = "", content = "", userTYpe = "", userID = "";

	private TextView shopname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hd_shop);
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
		hd_tel = getView(R.id.hd_tel);
		hd_shop = getView(R.id.hd_shop);
		mPullListView = getView(R.id.listview);

		jianpan = getView(R.id.jianpan);
		ll_hint = getView(R.id.ll_hint);
		input = getView(R.id.input);
		send = getView(R.id.send);

		bg = getView(R.id.bg_img);
		image_tx = getView(R.id.image_tx);
		shopname = getView(R.id.shopname);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		user_type = getIntent().getExtras().getString("user_type");
		user_id = getIntent().getExtras().getString("user_id");
		System.out.println("user_id="+user_id);
		
		posts = new ArrayList<>();
		data = new ArrayList<>();

		back.setOnClickListener(this);
		hd_shop.setOnClickListener(this);
		send.setOnClickListener(this);
		ll_hint.setOnClickListener(this);
		hd_tel.setOnClickListener(this);
		
		mPullListView.setPullLoadEnabled(true);
		mPullListView.setScrollLoadEnabled(false);

		listView = mPullListView.getRefreshableView();
		adapter = new HuDongDetailListAdapter(HDShopDetailActivity.this, posts, mDetailListener);
		adapter.setListView(listView);
		listView.setAdapter(adapter);
		listView.setVerticalScrollBarEnabled(false);
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getData(Urls.API_GET_POST_LIST_SOMEONE);
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
		case R.id.hd_tel:
			if (!mTel.equals("")) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mTel));
				startActivity(intent);
			}
			break;
		case R.id.hd_shop:
			if (!user_id.equals("")) {
				Bundle data = new Bundle();
				data.putString("shop_id", user_id);
//				Utils.goOtherWithDataActivity(HDShopDetailActivity.this, ShopDetailActivity.class, data);
			}
			break;
		case R.id.ll_hint:
			imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
			// jianpan.startAnimation(mHiddenAction);
			jianpan.setVisibility(View.GONE);
			input.setText("");
			break;
		case R.id.send:// ������Ϣ
			content = input.getText().toString().trim();
			if (content.length() != 0) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				if (typeSend.equals("-1")) {// ���ֱ�ӷ����İ�ť
					post_id = posts.get(clickPosition).getId();// clickPosition
					father_id = "0";
					father_user_type = "0";
					father_user_id = "0";
				}
				if (typeSend.equals("0")) {// ��������б��itemֱ�ӷ�������Ϣ��
					if (userId.equals(userID) && userType.equals(userTYpe)) {
						father_id = "0";
						father_user_type = "0";
						father_user_id = "0";
					}
				}
				if (typeSend.equals("4")) {// ��������б��item �ظ���������Ϣ��
					if (userId.equals(userID) && userType.equals(userTYpe)) {
						father_id = "0";
						father_user_type = "0";
						father_user_id = "0";
					}
				}
				sendCcomment();
			} else {
				Utils.showShortToast(HDShopDetailActivity.this, "��������������...");
			}
			break;
		default:
			break;
		}
	}

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
					System.out.println("ҳ���ʼ��===" + json.toString());
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
				Utils.showText(HDShopDetailActivity.this, "�������ʧ��");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("to_user_type", user_type);
				params.put("to_user_id", user_id);
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
				BaseHDListInfo info = gson.fromJson(result, BaseHDListInfo.class);
				if (info != null) {
					HDInfo data = info.getData();
					if (data != null) {
						ImageLoader.getInstance().displayImage(Constant.URL_TEST + data.getHudong_bg(), bg);
						if (user_type.equals("user")) {
							hd_shop.setVisibility(View.GONE);
							image_tx.setImageResource(R.drawable.my_tx);
						} else {
							hd_shop.setVisibility(View.GONE);
							ImageLoader.getInstance().displayImage(Constant.URL_TEST + data.getAvatar(), image_tx);
						}
						
						mTel = data.getTel();
						shopname.setText(data.getUser_name());
						posts = data.getPosts();
						if (posts != null) {
							adapter.refresh(posts);
						}
					}
				}
			}
		}
	}

	/**
	 * �޻����ղ�
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
				mPullListView.onPullDownRefreshComplete();
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(HDShopDetailActivity.this, "�������ʧ��");
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

				if (type.equals("1")) {// 1�ղ�
					posts.get(position).setSave(1);
					posts.get(position).setSave_num(posts.get(position).getSave_num() + 1);
					adapter.updateItemData(posts.get(position), position, 0);
				} else if (type.equals("2")) {// ȡ���ղ�
					posts.get(position).setSave(0);
					int number = posts.get(position).getSave_num() - 1;
					if (number < 1) {
						posts.get(position).setSave_num(0);
					} else {
						posts.get(position).setSave_num(number);
					}
					adapter.updateItemData(posts.get(position), position, 0);

				} else if (type.equals("3")) {// ����
					data = info.getData();
					posts.get(position).setZan_list(data);
					posts.get(position).setZan(1);
					adapter.updateItemData(posts.get(position), position, 1);// �ղ�0
																				// ����1
				} else if (type.equals("4")) {// ȡ������
					data = info.getData();
					posts.get(position).setZan_list(data);
					posts.get(position).setZan(0);
					adapter.updateItemData(posts.get(position), position, 1);
				}
			}
		}
	}

	/**
	 * ��������
	 */
	private void sendCcomment() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_COMMENT_POST, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("====������Ϣ===" + json.toString());
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
				Utils.showText(HDShopDetailActivity.this, "�������ʧ��");
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
			if (action.equals("com.app.action.broadcast_detail")) {

				typeSend = data.getString("type");
				if (typeSend.equals("0") || typeSend.equals("4")) {
					clickPosition = data.getInt("position");// �����б� ����ĵڼ�λ
					CommentsInfo info = (CommentsInfo) data.getSerializable("info");
					post_id = posts.get(clickPosition).getId();

					father_id = info.getId();
					father_user_type = info.getUser_type();
					father_user_id = info.getUser_id();
					userTYpe = info.getUser_type();
					userID = info.getUser_id();
					imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					jianpan.startAnimation(mShowAction);
					jianpan.setVisibility(View.VISIBLE);
					input.setText("");
					input.requestFocus();
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
		intentFilter.addAction("com.app.action.broadcast_detail");
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
	
	private MyDetailClickListener mDetailListener = new MyDetailClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			switch (v.getId()) {
			case R.id.play:
				String videoUrl = posts.get(position).getVideos().get(0).getVideo_url();
				if (videoUrl != null && videoUrl.length() != 0) {
					Intent i = new Intent(HDShopDetailActivity.this, VideoActivity.class);
					i.putExtra("info", Constant.URL_TEST + videoUrl);
					startActivity(i);
				} else {
					Utils.showText(HDShopDetailActivity.this, "û�в�����Ƶ...");
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
					Utils.showShortToast(HDShopDetailActivity.this, "���ܺ��Լ�����...");
					return;
				}
				String niciname = posts.get(position).getPoster_name();
				if (name != null && name.length() != 0 && niciname != null && niciname.length() != 0) {
					Intent intent = new Intent(HDShopDetailActivity.this, ChatActivity.class);
					intent.putExtra(com.example.shoppingtalk.talk.Constant.EXTRA_USER_ID, name);
					intent.putExtra("user_nickname", niciname);
					startActivity(intent);
				}
				break;
			case R.id.pl:// ����
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
	
	/**
	 * ɾ��ĳ������
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
							System.out.println("====ɾ����Ϣ===" + json.toString());
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
						Utils.showText(HDShopDetailActivity.this, "�������ʧ��");
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
