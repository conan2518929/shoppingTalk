package com.example.shoppingtalk;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

@SuppressLint("ShowToast")
public class VideoActivity extends BaseActivity{
	private String info = "";
	private LinearLayout back;
	private VideoView mVideoView;//ԭʼ�ؼ�
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
//		showLoadingDialog("���ڼ�����Ƶ...");
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		back = (LinearLayout) findViewById(R.id.back);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		info = getIntent().getStringExtra("info");
		Uri uri = Uri.parse(info);  //������Ƶ
  		mVideoView = (VideoView) findViewById(R.id.videoView); 
  		mVideoView.setMediaController(new MediaController(this));  
  		mVideoView.setVideoURI(uri);  
  		mVideoView.start();  
//  		dismissLoadingDialog();
  		mVideoView.requestFocus();  
  		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
 			
 			@Override
 			public void onCompletion(MediaPlayer mp) {
 				// TODO Auto-generated method stub
 			Toast.makeText(getApplicationContext(), "��Ƶ���Ž�����", 1).show();
 		}
 		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
