package com.example.shoppingtalk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.json.JSONException;
import org.json.JSONObject;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 2015-9-8 易戈
 * 
 * 这个类的功能主要是应用常用功能汇集的工具类
 */
public class Utils {

	/**
	 * @Title: isNetworkAvailable
	 * @Description: 检测网络状态是否为空
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					Log.i(String.valueOf(i), info[i].getTypeName());
					if (info[i].getState() == NetworkInfo.State.CONNECTED
							&& (info[i].getTypeName()
									.equalsIgnoreCase("MOBILE") || info[i]
									.getTypeName().equalsIgnoreCase("WIFI"))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 压缩图片
	 * 
	 * @param path
	 * @return
	 */
	public static File getCompressImg(String path, int compressSize,
			int sampleSize, CompressFormat format) {
		File file = new File(path);
		try {
			Bitmap bitmap;
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(path, option);
			option.inJustDecodeBounds = false;
			option.inSampleSize = sampleSize;
			bitmap = BitmapFactory.decodeFile(path, option);
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(format, compressSize, out)) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 
	 * @param height
	 * @param width
	 * @return
	 */
	public static Bitmap getBitmap(String path, int height, int width) {
		Bitmap bitmap;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(path, opt);
		opt.inSampleSize = (bitmap.getHeight() / height + bitmap.getWidth()
				/ width) / 2;
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(path, opt);
		return bitmap;
	}

	/**
	 * 
	 * 通过流获取Bitmap图片
	 * 
	 * */
	public static Bitmap getBitmap(InputStream is, int height, int width) {
		Bitmap bitmap;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		opt.inSampleSize = calculateInSampleSize(opt, height, width);
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeStream(is, null, opt);
		return bitmap;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		// 先根据宽度进行缩小
		while (width / inSampleSize > reqWidth) {
			inSampleSize++;
		}
		// 然后根据高度进行缩小
		while (height / inSampleSize > reqHeight) {
			inSampleSize++;
		}
		return inSampleSize;
	}

	/**
	 * getwebview
	 * 
	 * @param wv
	 * @return
	 */
	public static WebView setWVSettings(WebView wv) {
		WebSettings settings = wv.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
		settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
		settings.setUseWideViewPort(true); // 将图片调整到适合webview的大小
		settings.setSupportZoom(true); // 支持缩放
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 优先使用缓存
		return wv;
	}

	/**
	 * get version code
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int vCode = 0;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),0);
			vCode = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return vCode;
	}

	/**
	 * go browser
	 * 
	 * @param uriString
	 */
	public static void goBrowser(Context context, String uriString) {
		if (TextUtils.isEmpty(uriString))
			return;
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri uri = Uri.parse(uriString);
		intent.setData(uri);
		context.startActivity(intent);
	}

	public static boolean isChinese(String str) {

		Pattern p = Pattern.compile("^[\u4e00-\u9fa5]*$");

		Matcher m = p.matcher(str);

		return m.matches();

	}

	/**
	 * 是否含有数字
	 * */
	public static boolean isHaveNO(String mobiles) {

		Pattern p = Pattern.compile(".*\\d+.*");

		Matcher m = p.matcher(mobiles);

		return m.matches();

	}

	/*
	 * 判断字符串是否符合手机号格式
	 */
	public static boolean isMobileNO(String mobiles) {

		Pattern p = Pattern
				.compile("^((13[0-9])|(18[0-9])|(17[0-8])|(147)|(15[^4,\\D]))\\d{8}$");

		Matcher m = p.matcher(mobiles);

		return m.matches();

	}

	/*
	 * 判断字符串是否符合邮箱格式
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 匹配身份证
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean isIdCard(String idCard) {
		String str = "^(\\d{17}(x|X|[0-9]))|(\\d{15})$"; // ([0-9]{17}([0-9]|X))|([0-9]{15})
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(idCard);
		return m.matches();
	}

	public static void showText(Context c, String s) {
		Toast.makeText(c, s, 1).show();
	}

	public static void showShortToast(Context context, String text) {
		Toast.makeText(context, text, 0).show();
	}

	/**
	 * set view is visiable
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	public static void setVisiable(View v1, View v2) {
		if (null != v1)
			v1.setVisibility(View.VISIBLE);
		if (null != v2)
			v2.setVisibility(View.VISIBLE);
	}

	/**
	 * set view is gone
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	public static void setGone(View v1, View v2) {
		if (null != v1)
			v1.setVisibility(View.GONE);
		if (null != v2)
			v2.setVisibility(View.GONE);
	}

	/*
	 * 解析json数据 对”“ null 进行处理
	 */
	public static String getStringValue(String params, JSONObject obj) {
		String value = "";
		try {
			if (obj.has(params))
				value = obj.getString(params);
			if (value == null || value.equals("null")) {
				value = "";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static int getIntValue(String params, JSONObject obj) {
		int  value = -100;
		try {
			if (obj.has(params)){
				value = obj.getInt(params);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/*
	 * 将Bitmap转换成流的形式
	 */
	private static InputStream Bitmap2IS(Bitmap bm, int num) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, num, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

	/*
	 * 通过path路径获取Bitmap图片
	 */
	public static Bitmap getURLImage(String path) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/*
	 * 通过路径path获取图片，将图片转换为字符串
	 */
	public static String getStringValue(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		byte[] data = null;
		String contentBase64 = "";
		try {
			Bitmap bitmap = Utils.getURLImage(path);
			InputStream in = Utils.Bitmap2IS(bitmap, 30);
			data = new byte[in.available()];
			in.read(data);
			in.close();
			byte[] encode = Base64.encode(data, Base64.DEFAULT);
			contentBase64 = new String(encode);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return contentBase64;
	}

	/**
	 * 判断当前启动的应用
	 * 
	 * @param context
	 * @param packageName
	 *            指定包名
	 * @return 是否运行
	 */
	public static boolean isRunning(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo rapi : infos) {
			if (TextUtils.equals(rapi.processName, context.getPackageName()))
				return true;
		}
		return false;
	}

	/**
	 * 判断当前启动的view
	 * 
	 * @param context
	 * @param className
	 * @return
	 */
	public static boolean isCurrentView(Context context, String className) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		if (TextUtils.equals(runningActivity, className))
			return true;
		return false;
	}

	/**
	 * 拨打电话
	 * 
	 * @param context
	 * @param phoneNum
	 */
	public static void callPhone(Context context, String phoneNum) {
		if (TextUtils.isEmpty(phoneNum))
			return;
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phoneNum));
		context.startActivity(intent);
	}

	public static Display getDisplay(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay();
	}

	public static long getFolderSize(java.io.File file) throws Exception {
		long size = 0;
		java.io.File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				size = size + getFolderSize(fileList[i]);
			} else {
				size = size + fileList[i].length();
			}
		}
		return size;
	}

	public static double getValueDouble(long size) {
		return ((size * 100) / 1048576) / (double) 100;
	}

	public static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}
	
	
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		System.out.println("scale="+scale);
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static void changeColor(Context c, TextView tv, int value) {
		Drawable nav_up = c.getResources().getDrawable(value);
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
		tv.setCompoundDrawables(nav_up, null, null, null);
	}
	public static void changeRightView(Context c, TextView tv, int value) {
		Drawable nav_up = c.getResources().getDrawable(value);
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
		tv.setCompoundDrawables(null, null, nav_up, null);
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static Bitmap createVideoThumbnail(String url, int width, int height) {
	    Bitmap bitmap = null;
	    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
	    int kind = MediaStore.Video.Thumbnails.MINI_KIND;
	    try {
	      if (Build.VERSION.SDK_INT >= 14) {
	        retriever.setDataSource(url, new HashMap<String, String>());
	      } else {
	        retriever.setDataSource(url);
	      }
	      bitmap = retriever.getFrameAtTime();
	    } catch (IllegalArgumentException ex) {
	    } catch (RuntimeException ex) {
	    } finally {
	      try {
	        retriever.release();
	      } catch (RuntimeException ex) {
	      }
	    }
	    if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
	      bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
	          ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	    }
	    return bitmap;
	  }
	
	
	public static String sendImage(Bitmap bm) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
		byte[] bytes = stream.toByteArray();
		String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
		return img;
	}
	
	public static void showToast(final Activity activity, final String word, final long time){
		activity.runOnUiThread(new Runnable() {	
			public void run() {
				final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_LONG);
				toast.show();
				Handler handler = new Handler();
			        handler.postDelayed(new Runnable() {
			           public void run() {
			               toast.cancel(); 
			           }
			    }, time);
			}
		});
	}
	
	public static Bitmap returnBitMap(String url){ 
	        URL myFileUrl = null;   
	        Bitmap bitmap = null;  
	        try {   
	            myFileUrl = new URL(url);   
	        } catch (MalformedURLException e) {   
	            e.printStackTrace();   
	        }   
	        try {   
	            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();   
	            conn.setDoInput(true);   
	            conn.connect();   
	            InputStream is = conn.getInputStream();   
	            bitmap = BitmapFactory.decodeStream(is);   
	            is.close();   
	        } catch (IOException e) {   
	              e.printStackTrace();   
	        }   
	              return bitmap;   
	    } 
	
	public static void gotoOtherActivity(Context c,Class<?> cls){
		Intent gotoActivity = new Intent(c,cls);
		c.startActivity(gotoActivity);
	}
	
	public static void goOtherWithDataActivity(Context c,Class<?> cls,Bundle data){
		Intent gotoActivity = new Intent(c,cls);
		if (data != null) {
			gotoActivity.putExtras(data);
		}
		c.startActivity(gotoActivity);
	}
	
	public static String MD5STR1(String msg)
	  {
	    byte[] b = MD5(msg.getBytes());
	    return new String(org.apache.commons.codec.binary.Base64.encodeBase64(b));
	  }
	
	public static byte[] MD5(byte[] msg)
	  {
	    MessageDigest messageDigest = null;
	    try {
	      messageDigest = MessageDigest.getInstance("MD5");

	      messageDigest.reset();

	      messageDigest.update(msg);
	    } catch (NoSuchAlgorithmException e) {
	    	System.out.println("MD5 NoSuchAlgorithmException caught!");
	    }
	    return messageDigest.digest();
	  }
	public static String MD5STR(String str){
		MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        md.update(str.getBytes());
        /** ascii */
        byte[] encodedPassword = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encodedPassword.length; i++) {
            
            if ((encodedPassword[i] & 0xff) < 0x10)
            {
                sb.append("0");
            }
            sb.append(Long.toString(encodedPassword[i] & 0xff, 16));
        }
        return sb.toString();
	}
	
	public static int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
		for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
			if (conversation.getType() == EMConversationType.ChatRoom)
				chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal - chatroomUnreadMsgCount;
	}
	
}
