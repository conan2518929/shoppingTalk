package com.example.shoppingtalk.widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtils {

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			return "/mnt/sdcard";
		}
		return sdDir.getAbsolutePath().toString();
	}

	@SuppressLint("SimpleDateFormat")
	public static String getFileName(Context context) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return "IMG_" + format.format(date);
	}

	public static void createPath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}
	public static File getImageFile(Context context) {
		Log.i("MSG??", "SD---" + getSDPath());
		if (getSDPath() == null) {
			return null;
		}
		createPath(getSDPath() + "/pictures");
		createPath(getSDPath() + "/pictures"+ "/shoppingTalk");
		return new File(getSDPath() +"/pictures"+ "/shoppingTalk", getFileName(context) + ".jpg");
	}
	public static File getImageFile1(Context context) {
		Log.i("MSG??", "SD---" + getSDPath());
		if (getSDPath() == null) {
			return null;
		}
		createPath(getSDPath() + "/com.example.shoppingtalk");
		createPath(getSDPath() + "/com.example.shoppingtalk" + "/image");
		return new File(getSDPath() + "/com.example.shoppingtalk" + "/image", "head_image.jpg");
	}
	public static File getImageFileList(Context context) {
		Log.i("MSG??", "SD---" + getSDPath());
		if (getSDPath() == null) {
			return null;
		}
		createPath(getSDPath() + "/com.example.shoppingtalk");
		createPath(getSDPath() + "/com.example.shoppingtalk" + "/image");
		return new File(getSDPath() + "/com.example.shoppingtalk" + "/image");
	}
}
