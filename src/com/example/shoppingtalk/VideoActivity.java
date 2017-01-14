package com.example.shoppingtalk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@SuppressLint("ShowToast")
public class VideoActivity extends BaseActivity{
	private String info = "";
	private LinearLayout back;
	private VideoView mVideoView;//原始控件
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		showLoadingDialog("正在加载视频...");
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
		Uri uri = Uri.parse(info); // 网络视频
		mVideoView = (VideoView) findViewById(R.id.videoView);
		mVideoView.setMediaController(new MediaController(this));
		mVideoView.setVideoURI(uri);
		mVideoView.requestFocus();
		mVideoView.start();
  		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
 			
 			@Override
 			public void onCompletion(MediaPlayer mp) {
 				// TODO Auto-generated method stub
 			Toast.makeText(getApplicationContext(), "视频播放结束了", 1).show();
 		}
 		});
  		mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
			@Override
			public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
				if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
					showLoadingDialog("视频正在缓存中...");
				}else if (what == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
					Toast.makeText(VideoActivity.this, "网络超时", Toast.LENGTH_LONG).show();
				}else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
		            //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
		            if(mediaPlayer.isPlaying()){
		            	dismissLoadingDialog();
		            }
				}
				return false;
			}
		});
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
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
