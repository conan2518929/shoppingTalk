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
 * 2015-11-19 �׸�
 * �����Ĺ�����Ҫ�Ǳ�����ҳ�����̳У�ҳ���еĹ��Կ��Էŵ������У������ظ��������롣
 * �̳нӿ�BaseInterfaces ʵ��initViews() initData()����
 * initViews()ʵ�ֿؼ���ʼ��
 * initData()ʵ�����ݳ�ʼ��
 * showToast(String text)��showToast(int resId)ʵ����ʾ�������ַ�ʽ
 * gotoOtherActivity(Context c,Class<?> cls) ��תҳ��ʱ�����ݲ�����ת
 * goOtherWithDataActivity(Context c,Class<?> cls,Bundle data)��תҳ��ʱ���ݲ�����ת
 * if��������{}��Ҫʡ��
 * �����������ӻ�ȡ���ݷ������� getValue()
 * �����жϵı����������߷�����ǰ��is��ͷ ����isCheck
 * ����˳���� public protected private abstract static final transient volatile synchronized native strictfp
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
        // ��ȡ���лỰ������İ����
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // ���˵�messages sizeΪ0��conversation
        /**
         * ��������������������Ϣ�յ���lastMsgTime�ᷢ���仯
         * Ӱ��������̣�Collection.sort������쳣
         * ��֤Conversation��Sort���������һ����Ϣ��ʱ�䲻�� 
         * ���Ⲣ������
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
