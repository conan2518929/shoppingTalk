package com.example.shoppingtalk.constant;

import android.os.Environment;
/**
 * 2015-9-17 �׸�
 * 
 * �����Ĺ�����Ҫ�Ƿ��ʵ�ַ�ĸ���ַ���������Ե�ַ������������ַ��ͼƬ·���洢��ַ
 */
public class Constant {
//	public static final String URL_TEST = "http://www.peipeixia.com/";//����
	public static final String URL_TEST = "http://api.martincao.com/";//����
	
	public static String ROOT_URL = URL_TEST;
	 // SDCard·�� 
 	public static final String SD_PATH = Environment
 			.getExternalStorageDirectory().getAbsolutePath();
 // ����ͼƬ·��
 	public static final String BASE_IMAGE_CACHE = "/lvyou/image";
	public static final int IO_BUFFER_SIZE = 2*1024;
}
