package com.example.shoppingtalk.sharepref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 2015-9-17 易戈
 * 
 * 我们要开发一个类
 * 这个类的功能主要是对需要存储的信息保存到对应名字的临时文件上
 * 
 */
public class SharedPref {

	private SharedPreferences mSharedPreferences = null;
	private Editor mEditor = null;
	private static SharedPref sharePref = null;

	/**
	 * @Title: getInstance
	 * @Description: 获取SharedPref单例
	 * @param PREF_NAME
	 * @param ctx
	 * @return
	 */
	public synchronized static SharedPref getInstance(String PREF_NAME,
			Context ctx) {
		if (sharePref != null) {
			return sharePref;
		} else {
			return new SharedPref(PREF_NAME, ctx);
		}
	}
	/*
	 * 创建临时文件 初始化
	 * */
	private SharedPref(String PREF_NAME, Context ctx) {
		mSharedPreferences = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}
	/*
	 * 将字符串从临时文件中取出，并设置默认值
	 * */
	public String getSharePrefString(String key, String defValue) {
		return mSharedPreferences.getString(key, defValue);
	}
	/*
	 * 将字符串从临时文件中取出
	 * */
	public String getSharePrefString(String key) {
		return mSharedPreferences.getString(key, "");
	}
	/*
	 * 将boolean类型从临时文件中取出，并设置默认值
	 * */
	public boolean getSharePrefBoolean(String key, boolean defValue) {
		return mSharedPreferences.getBoolean(key, defValue);
	}
	/*
	 * 将int类型从临时文件中取出
	 * */
	public int getSharePrefInteger(String key) {
		return mSharedPreferences.getInt(key, -1);
	}
	/*
	 * 将int类型从临时文件中取出，并设置默认值
	 * */
	public int getSharePrefInteger(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);
	}
	/*
	 * 将长整型类型从临时文件中取出
	 * */
	public long getSharePrefLong(String key) {
		return mSharedPreferences.getLong(key, -1);
	}
	/*
	 * 将长整型类型从临时文件中取出，并设置默认值
	 * */
	public long getSharePrefLong(String key, int value) {
		return mSharedPreferences.getLong(key, -1);
	}
	/*
	 * 将浮点类型从临时文件中取出
	 * */
	public float getSharePrefFloat(String key) {
		return mSharedPreferences.getFloat(key, -1);
	}
	/*
	 * 将字符串存储到临时文件中
	 * */
	public boolean putSharePrefString(String key, String value) {
		mEditor.putString(key, value);
		return mEditor.commit();
	}
	/*
	 * 将boolean类型存储到临时文件中
	 * */
	public boolean putSharePrefBoolean(String key, boolean value) {
		mEditor.putBoolean(key, value);
		return mEditor.commit();
	}
	/*
	 * 将浮点类型存储到临时文件中
	 * */
	public boolean putSharePrefFloat(String key, float value) {
		mEditor.putFloat(key, value);
		return mEditor.commit();
	}
	/*
	 * 将长整形类型存储到临时文件中
	 * */
	public boolean putSharePrefLong(String key, long value) {
		mEditor.putLong(key, value);
		return mEditor.commit();
	}
	/*
	 * 将整形类型存储到临时文件中
	 * */
	public boolean putSharePrefInteger(String key, int value) {
		mEditor.putInt(key, value);
		return mEditor.commit();
	}
	
	/*
	 * 将某一数据删除
	 * */
	public boolean removeKey(String key) {
		mEditor.remove(key);
		return mEditor.commit();
	}
	/*
	 * 清空临时文件数据
	 * */
	public boolean clear() {
		mEditor.clear();
		return mEditor.commit();
	}

}
