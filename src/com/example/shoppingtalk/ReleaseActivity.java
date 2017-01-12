package com.example.shoppingtalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
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
import com.example.shoppingtalk.adapter.PingLunGVAdapter;
import com.example.shoppingtalk.adapter.PingLunGVAdapter.MyClickListener;
import com.example.shoppingtalk.constant.Constant;
import com.example.shoppingtalk.constant.Urls;
import com.example.shoppingtalk.entity.PLImgGVInfo;
import com.example.shoppingtalk.info.StatusInfo;
import com.example.shoppingtalk.video.FFmpegRecorderActivity;
import com.example.shoppingtalk.widget.Dialog;
import com.example.shoppingtalk.widget.Dialog.DialogItemClickListener;
import com.example.shoppingtalk.widget.FileUtils;
import com.example.shoppingtalk.widget.MyGridView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReleaseActivity extends BaseActivity {

	private LinearLayout back;
	private TextView tijiao, tv_content, rihan, oumei, guochan;
	private MyGridView gv;
	private PingLunGVAdapter adapter;
	private List<PLImgGVInfo> imgList;
	private List<PLImgGVInfo> imgListOne;
	private String type = "1";// 1日系、2欧美、3国产
	private String mContent = "";
	private PLImgGVInfo info = null;
	private String[] str = new String[] { "拍照", "相册", "小视频" };
	private String[] strOne = new String[] { "拍照", "相册" };
	private File file = null;
	private Uri uri = null;
	private Uri uriPaiZhao = null;
	private static int CAMERA_REQUEST_CODE = 1;
	private static int GALLERY_REQUEST_CODE = 2;
	private String mVideo = "";
	private String mVideoPic = "";
	private String pathVideo = "";
	private ImageView imgVideo, play, delete;
	private FrameLayout showFL;
	private String imagePath = "";
	private int isShow = 0;

	private MyBroadcastReciver myBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_release);

		initViews();
		initBroadcast();
		initData();

	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		back = getView(R.id.back);
		tijiao = getView(R.id.tijiao);
		tv_content = getView(R.id.tv_content);
		rihan = getView(R.id.rihan);
		oumei = getView(R.id.oumei);
		guochan = getView(R.id.guochan);
		gv = getView(R.id.gv);
		imgVideo = getView(R.id.imgVideo);
		play = getView(R.id.play);
		showFL = getView(R.id.showFL);
		delete = getView(R.id.delete);
	}

	@Override
	public void initData() {
		imgList = new ArrayList<>();
		imgListOne = new ArrayList<>();
		info = new PLImgGVInfo();
		info.setName("0");
		info.setUrl("0");
		imgList.add(info);
		adapter = new PingLunGVAdapter(ReleaseActivity.this, imgList, mListener);
		gv.setAdapter(adapter);
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// play.setOnClickListener(new OnClickListener() {//播放视频
		//
		// @Override
		// public void onClick(View v) {
		// Intent i = new Intent();
		// i.putExtra("path", pathVideo);
		// startActivity(i);
		// }
		// });

		delete.setOnClickListener(new OnClickListener() {// 删除视频

			@Override
			public void onClick(View v) {
				showFL.setVisibility(View.GONE);
				gv.setVisibility(View.VISIBLE);
				isShow = 0;
				mVideo = "";
				pathVideo = "";
				imagePath = "";
				mVideoPic = "";
			}
		});

		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pathVideo.equals("")) {
					Utils.showText(ReleaseActivity.this, "没有可播放的视频...");
				} else {
					Intent intents = new Intent(Intent.ACTION_VIEW);
					intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intents.setDataAndType(Uri.fromFile(new File(pathVideo)), "video/*");
					startActivity(intents);
				}
			}
		});

		tijiao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContent = tv_content.getText().toString().trim();
				if (mContent.length() == 0) {
					imgListOne.clear();
					imgListOne.addAll(imgList);
					imgListOne.remove(imgListOne.size() - 1);
					if (imgListOne.size() == 0 && mVideo.length() == 0) {
						Utils.showText(ReleaseActivity.this, "发布的信息不能为空...");
						return;
					} else {
						tijiaoData();
					}
				} else {
					tijiaoData();
				}
			}
		});

		rihan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initColor();
				rihan.setBackgroundColor(Color.parseColor("#3490f5"));
				type = "1";
			}
		});
		oumei.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initColor();
				oumei.setBackgroundColor(Color.parseColor("#3490f5"));
				type = "2";
			}
		});
		guochan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initColor();
				guochan.setBackgroundColor(Color.parseColor("#3490f5"));
				type = "3";
			}
		});
	}

	private void initColor() {
		rihan.setBackgroundColor(Color.parseColor("#cccccc"));
		oumei.setBackgroundColor(Color.parseColor("#cccccc"));
		guochan.setBackgroundColor(Color.parseColor("#cccccc"));
	}

	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {

			switch (v.getId()) {
			case R.id.img:

				if (isShow == 0) {
					Dialog.showListDialog(ReleaseActivity.this, "请选择", str, new DialogItemClickListener() {
						@Override
						public void confirm(String result) {
							if (result.equals("拍照")) {
								getphoto();
							} else if (result.equals("相册")) {
								Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
								intent.setType("image/*");
								startActivityForResult(intent, GALLERY_REQUEST_CODE);
							} else if (result.equals("小视频")) {
								createCamcorderIntent();
							}
						}
					});
				}
				if (isShow == 1) {// 拍照
					Dialog.showListDialog(ReleaseActivity.this, "请选择", strOne, new DialogItemClickListener() {
						@Override
						public void confirm(String result) {
							if (result.equals("拍照")) {
								getphoto();
							} else if (result.equals("相册")) {
								Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
								intent.setType("image/*");
								startActivityForResult(intent, GALLERY_REQUEST_CODE);
							}
						}
					});
				}
				break;
			case R.id.showimg:
				if (imgList.size() - 1 > 0) {
					String[] urls = new String[imgList.size() - 1];
					for (int i = 0; i < urls.length; i++) {
						urls[i] = imgList.get(i).getUrl();
					}
					showImage(position, urls);
				}
				break;
			case R.id.delete:
				imgList.remove(position);
				adapter.refresh(imgList);
				if (imgList.size() == 1) {
					isShow = 0;
				} else if (imgList.size() > 1) {
					isShow = 1;
				}
				break;
			default:
				break;
			}
		}
	};

	private void showImage(int position, String[] urls) {
		Intent intent = new Intent(ReleaseActivity.this, ShowImageActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ShowImageActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ShowImageActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}

	/**
	 * 提交数据
	 */
	private void tijiaoData() {
		showLoadingDialog();
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_NEW_POST, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("发布返回结果" + json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ReleaseActivity.this, "网络访问失败");
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				if (mContent.length() != 0) {
					params.put("text", mContent);
				}
				if (isShow == 1) {
					imgListOne.clear();
					imgListOne.addAll(imgList);
					imgListOne.remove(imgListOne.size() - 1);
					if (imgListOne.size() != 0) {
						String[] strArray = new String[imgListOne.size()];
						Gson g = new Gson();
						for (int i = 0; i < imgListOne.size(); i++) {
							strArray[i] = "data:image/png;base64," + imgListOne.get(i).getName();
						}
						params.put("pics", g.toJson(strArray));
					}
				}
				params.put("post_type", type);

				if (isShow == 2) {
					if(pathVideo.length() != 0){
						Gson gOne = new Gson();
						String[] strVideo = new String[1];
						strVideo[0] = "data:video/mp4;base64," + mVideo;
						params.put("videos", gOne.toJson(strVideo));
						Gson gTwo = new Gson();
						String[] strVideoPic = new String[1];
						strVideoPic[0] = "data:image/png;base64," + mVideoPic;
						params.put("video_pics", gTwo.toJson(strVideoPic));
					}
				}
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			Utils.showText(ReleaseActivity.this, "发布成功...");
			Intent intent = new Intent();
			intent.setAction("com.app.action.broadcast_send_content");
			Bundle data = new Bundle();
			data.putString("type","5");
			data.putString("leixing",type);
			intent.putExtras(data);
			sendBroadcast(intent);
			finish();
		} else {
			Utils.showText(ReleaseActivity.this, "发布失败...");
		}
		dismissLoadingDialog();
	}

	private void getphoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		file = FileUtils.getImageFile(this);
		uri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, CAMERA_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST_CODE) {
			if (resultCode == RESULT_CANCELED) {
				return;
			}
//			uriPaiZhao = null;
			uriPaiZhao = uri;
//			System.out.println("照相uriPaiZhao=" + uriPaiZhao.toString());
			// startImageZoom(uriPaiZhao, 1, 1, 240, 240);

			PLImgGVInfo info = new PLImgGVInfo();

			String path = getRealFilePath(ReleaseActivity.this, uriPaiZhao);
			Utils.getCompressImg(path, 40, 5, CompressFormat.JPEG);

			info.setName(Utils.getStringValue(path));
			info.setUrl(uriPaiZhao.toString());
			imgList.add(imgList.size() - 1, info);

			if (imgList.size() > 9) {
				imgListOne.clear();
				imgListOne.addAll(imgList);
				adapter.refresh(imgListOne.subList(0, 9));
			} else {
				adapter.refresh(imgList);
			}
			isShow = 1;
		} else if (requestCode == GALLERY_REQUEST_CODE) {
			if (resultCode == RESULT_CANCELED) {
				return;
			}
			if (data == null) {// xc
				return;
			}
//			uriPaiZhao = null;
			uriPaiZhao = data.getData();
			PLImgGVInfo info = new PLImgGVInfo();

			String path = getRealFilePath(ReleaseActivity.this, uriPaiZhao);
			// Utils.getCompressImg(path, 40, 5, CompressFormat.JPEG);

			info.setName(Utils.getStringValue(path));
			info.setUrl(uriPaiZhao.toString());
			imgList.add(imgList.size() - 1, info);

			if (imgList.size() > 9) {
				imgListOne.clear();
				imgListOne.addAll(imgList);
				adapter.refresh(imgListOne.subList(0, 9));
			} else {
				adapter.refresh(imgList);
			}
			isShow = 1;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static String getRealFilePath(final Context context, final Uri uri) {
		if (null == uri)
			return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[] { ImageColumns.DATA }, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	/**
	 * 录制视频
	 */
	private void createCamcorderIntent() {
		Intent intent = new Intent(ReleaseActivity.this, FFmpegRecorderActivity.class);
		startActivity(intent);
	}

	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals("com.app.action.broadcast_video")) {
				pathVideo = intent.getExtras().getString("path");
				imagePath = intent.getExtras().getString("imagePath");
				Bitmap bitmap = getLoacalBitmap(imagePath);
				imgVideo.setImageBitmap(bitmap);
				showFL.setVisibility(View.VISIBLE);
				gv.setVisibility(View.GONE);
				isShow = 2;
				try {
					mVideo = readStream(pathVideo);
					mVideoPic = readStream(imagePath);
//					System.out.println("mPath=" + mVideo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		myBroadcast = new MyBroadcastReciver();
		intentFilter.addAction("com.app.action.broadcast_video");
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
		// String mImage = new String(Base64.encode(data));
		outStream.close();
		inStream.close();
		return mImage;
	}

	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
