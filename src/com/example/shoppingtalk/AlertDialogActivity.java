package com.example.shoppingtalk;

import com.example.shoppingtalk.sharepref.SharedPrefConstant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * 2015-12-16 �׸�
 * 
 * ������ǶԻ���ҳ��
 * initViews()ʵ�ֿؼ���ʼ��
 * initData()ʵ�����ݳ�ʼ��
 *	
 */
public class AlertDialogActivity extends BaseActivity implements OnClickListener{
	
	private TextView tv;
	
	private Button ok;
	
	private Button cancel;
	
	private String strUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);
		setContentView(R.layout.jubao_tishi);
		initViews();
		initData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void initData() {
		Intent intent = getIntent();
		tv.setText(R.string.update_content);
		ok.setText(R.string.update_ok);
		cancel.setText("ȡ��");
		strUrl = intent.getStringExtra(SharedPrefConstant.EXTRA_VER_URL);
		int netVerdict = intent.getIntExtra(SharedPrefConstant.EXTRA_VER_VERDICT, 1);
		if(netVerdict > 0){//ǿ������
			cancel.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ok:
			if(!TextUtils.isEmpty(strUrl))
				Utils.goBrowser(this, strUrl);
			break;
		case R.id.cancel:
			AppManager.getAppManager().finishActivity(this);
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		tv = (TextView) findViewById(R.id.tv);
		ok = (Button) findViewById(R.id.ok);
		cancel = (Button) findViewById(R.id.cancel); 
	}
}
