package com.example.shoppingtalk.constant;

import android.os.Environment;
/**
 * 2015-9-17 易戈
 * 
 * 这个类的功能主要是访问地址的根地址，包括测试地址和上线生产地址，图片路径存储地址
 */
public class Constant {
//	public static final String URL_TEST = "http://www.peipeixia.com/";//测试
	public static final String URL_TEST = "http://api.martincao.com/";//测试
	
	public static String ROOT_URL = URL_TEST;
	 // SDCard路径 
 	public static final String SD_PATH = Environment
 			.getExternalStorageDirectory().getAbsolutePath();
 // 缓存图片路径
 	public static final String BASE_IMAGE_CACHE = "/lvyou/image";
	public static final int IO_BUFFER_SIZE = 2*1024;
}
