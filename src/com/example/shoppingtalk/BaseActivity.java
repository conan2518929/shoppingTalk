package com.example.shoppingtalk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseBaseActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 2015-11-19 易戈
 * 这个类的功能主要是被所有页面所继承，页面中的共性可以放到此类中，减少重复代功能码。
 * 继承接口BaseInterfaces 实现initViews() initData()方法
 * initViews()实现控件初始化
 * initData()实现数据初始化
 * showToast(String text)、showToast(int resId)实现提示窗口两种方式
 * gotoOtherActivity(Context c,Class<?> cls) 跳转页面时不传递参数跳转
 * goOtherWithDataActivity(Context c,Class<?> cls,Bundle data)跳转页面时传递参数跳转
 * if语句必须有{}不要省略
 * 访问网络连接获取数据方法命名 getValue()
 * 带有判断的变量命名或者方法名前由is开头 例如isCheck
 * 定义顺序按照 public protected private abstract static final transient volatile synchronized native strictfp
 */
public abstract class BaseActivity extends EaseBaseActivity implements BaseInterfaces{
	
	protected String TAG;
	protected ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		AppManager.getAppManager().addActivity(this);
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading));
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.setCancelable(false);
	}
	
	public void showLoadingDialog() {
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	public void showLoadingDialog(String text) {
		if (text != null) {
			dialog.setMessage(text);
		}
		dialog.setCancelable(false); 
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	public void showLoadingDialog(int resId) {
		if (resId != 0) {
			dialog.setMessage(getString(resId));
		}
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void dismissLoadingDialog() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}
	
	
	protected void showLog(String msg) {
		Log.i(TAG, msg);
	}
	
	@SuppressWarnings("unchecked")
	protected final <E extends View> E getView (int id) {
	    try {
	        return (E) findViewById(id);
	    } catch (ClassCastException ex) {
	        Log.e(TAG, "Could not cast View to concrete class.", ex);
	        throw ex;
	    }
	}
	
	protected List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }
}
