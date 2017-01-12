package com.example.shoppingtalk.video;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.example.shoppingtalk.AppManager;
import com.example.shoppingtalk.BaseActivity;
import com.example.shoppingtalk.R;

public class FFmpegPreviewActivity extends BaseActivity implements TextureView.SurfaceTextureListener
	,OnClickListener,OnCompletionListener{

	private String path,imagePath;
	private TextureView surfaceView;
	private Button cancelBtn,play_next;
	private MediaPlayer mediaPlayer;
	private ImageView imagePlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ffmpeg_preview);

		cancelBtn = (Button) findViewById(R.id.play_cancel);
		play_next = (Button) findViewById(R.id.play_next);
		cancelBtn.setOnClickListener(this);
		play_next.setOnClickListener(this);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		surfaceView = (TextureView) findViewById(R.id.preview_video);
		
		RelativeLayout preview_video_parent = (RelativeLayout)findViewById(R.id.preview_video_parent);
		LayoutParams layoutParams = (LayoutParams) preview_video_parent.getLayoutParams();
		layoutParams.width = displaymetrics.widthPixels;
		layoutParams.height = displaymetrics.widthPixels;
		preview_video_parent.setLayoutParams(layoutParams);
		
		surfaceView.setSurfaceTextureListener(this);
		surfaceView.setOnClickListener(this);
		
		path = getIntent().getStringExtra("path");
		imagePath  = getIntent().getStringExtra("imagePath");
		
		imagePlay = (ImageView) findViewById(R.id.previre_play);
		imagePlay.setOnClickListener(this);
		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
	}

	@Override
	protected void onStop() {
		if(mediaPlayer.isPlaying()){
			mediaPlayer.pause();
			imagePlay.setVisibility(View.GONE);
		}
		super.onStop();
	}

	private void prepare(Surface surface) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置需要播放的视频
			mediaPlayer.setDataSource(path);
			// 把视频画面输出到Surface
			mediaPlayer.setSurface(surface);
			mediaPlayer.setLooping(true);
			mediaPlayer.prepare();
			mediaPlayer.seekTo(0);
		} catch (Exception e) {
		}
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
			int arg2) {
		prepare(new Surface(arg0));
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_cancel:
			stop();
			break;
		case R.id.previre_play:
			if(!mediaPlayer.isPlaying()){
				mediaPlayer.start();
			}
			imagePlay.setVisibility(View.GONE);
			break;
		case R.id.preview_video:
			if(mediaPlayer.isPlaying()){
				mediaPlayer.pause();
				imagePlay.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.play_next:
//			String name = "";
//			try {
//				name = readStream(path);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println("name="+name);
			
			Intent intent = new Intent();
			intent.setAction("com.app.action.broadcast_video");
			Bundle data = new Bundle();
			data.putString("path", path);
			data.putString("imagePath", imagePath);
			intent.putExtras(data);
			sendBroadcast(intent);
			AppManager.getAppManager().findActivity(FFmpegRecorderActivity.class);
			finish();
			break;
		default:
			break;
		}
	}
	
	private void stop(){
		mediaPlayer.stop();
		Intent intent = new Intent(this,FFmpegRecorderActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		stop();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		imagePlay.setVisibility(View.VISIBLE);
	}
	
	public static String readStream(String path) throws Exception {
		File file = new File(path);
		InputStream inStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		String mImage = new String(Base64.encode(data, Base64.DEFAULT));
//		String mImage = new String(Base64.encode(data));
		outStream.close();
		inStream.close();
		return mImage;
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
