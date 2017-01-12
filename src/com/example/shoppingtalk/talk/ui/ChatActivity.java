package com.example.shoppingtalk.talk.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.shoppingtalk.BaseActivity;
import com.example.shoppingtalk.LoginActivity;
import com.example.shoppingtalk.R;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.EasyUtils;

public class ChatActivity extends BaseActivity{
	
	public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    String toChatUsername;
    private String nickname = "";
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //�����˻�Ⱥid
        toChatUsername = getIntent().getExtras().getString("userId");
        nickname = getIntent().getExtras().getString("user_nickname");
        //����ֱ��new EaseChatFratFragmentʹ��
        chatFragment = new ChatFragment();
        //�������
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        // ���notification bar��������ҳ�棬��ֻ֤��һ������ҳ��
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }
    
    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
    
    public String getToChatUsername(){
        return toChatUsername;
    }

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
}

