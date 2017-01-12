package com.example.shoppingtalk.talk.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shoppingtalk.AppContext;
import com.example.shoppingtalk.R;
import com.example.shoppingtalk.Utils;
import com.example.shoppingtalk.constant.Urls;
import com.example.shoppingtalk.info.NickNameInfo;
import com.example.shoppingtalk.info.StatusInfo;
import com.example.shoppingtalk.talk.Constant;
import com.example.shoppingtalk.talk.db.InviteMessgeDao;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.NetUtils;

public class ConversationListFragment extends EaseConversationListFragment {

	private final String TAG = "ConversationListFragment";
	private TextView errorText;
	private NickNameInfo nickname;
	private EaseTitleBar title;
	@Override
	protected void initView() {
		super.initView();
		View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
		errorItemContainer.addView(errorView);
		errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
		title = (EaseTitleBar) getView().findViewById(R.id.title_bar);
		title.setLayoutVisibility(View.GONE);
	}

	@Override
	protected void setUpView() {
		super.setUpView();
		// 注册上下文菜单
		registerForContextMenu(conversationListView);
		conversationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = conversationListView.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(EMClient.getInstance().getCurrentUser()))
					Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, 0).show();
				else {
					getNcikNameResut(username);
				}
			}
		});
	}

	private void getNcikNameResut(final String username) {
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "EMClient.getInstance().onCancel");
			}
		});
		pd.setMessage("正在初始化数据...");
		pd.show();

		AppContext.getInstance().cancelPendingRequests(TAG);

		StringRequest stringRequest = new StringRequest(Request.Method.POST, com.example.shoppingtalk.constant.Constant.URL_TEST + Urls.GET_NICKNAME_USER, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				pd.dismiss();
				showName(result, username);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				pd.dismiss();
				Utils.showText(getActivity(), "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("im_username", username);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void showName(String result, String username) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			nickname = gson.fromJson(result, NickNameInfo.class);
			// 进入聊天页面
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			intent.putExtra(Constant.EXTRA_USER_ID, username);
			intent.putExtra("user_nickname", nickname.getNickname());
			startActivity(intent);
		}
	}

	@Override
	protected void onConnectionDisconnected() {
		super.onConnectionDisconnected();
		if (NetUtils.hasNetwork(getActivity())) {
			errorText.setText(R.string.can_not_connect_chat_server_connection);
		} else {
			errorText.setText(R.string.the_current_network);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean deleteMessage = false;
		if (item.getItemId() == R.id.delete_message) {
			deleteMessage = true;
		} else if (item.getItemId() == R.id.delete_conversation) {
			deleteMessage = false;
		}
		EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
		if (tobeDeleteCons == null) {
			return true;
		}
		try {
			// 删除此会话
			EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.getUserName(), deleteMessage);
			InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
			inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		refresh();

		// 更新消息未读数
		// ((MainActivity) getActivity()).updateUnreadLabel();
		return true;
	}

}