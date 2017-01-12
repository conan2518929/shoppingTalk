package com.example.shoppingtalk.sharepref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 2015-9-17 �׸�
 * 
 * ����Ҫ����һ����
 * �����Ĺ�����Ҫ�Ƕ���Ҫ�洢����Ϣ���浽��Ӧ���ֵ���ʱ�ļ���
 * 
 */
public class SharedPref {

	private SharedPreferences mSharedPreferences = null;
	private Editor mEditor = null;
	private static SharedPref sharePref = null;

	/**
	 * @Title: getInstance
	 * @Description: ��ȡSharedPref����
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
	 * ������ʱ�ļ� ��ʼ��
	 * */
	private SharedPref(String PREF_NAME, Context ctx) {
		mSharedPreferences = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}
	/*
	 * ���ַ�������ʱ�ļ���ȡ����������Ĭ��ֵ
	 * */
	public String getSharePrefString(String key, String defValue) {
		return mSharedPreferences.getString(key, defValue);
	}
	/*
	 * ���ַ�������ʱ�ļ���ȡ��
	 * */
	public String getSharePrefString(String key) {
		return mSharedPreferences.getString(key, "");
	}
	/*
	 * ��boolean���ʹ���ʱ�ļ���ȡ����������Ĭ��ֵ
	 * */
	public boolean getSharePrefBoolean(String key, boolean defValue) {
		return mSharedPreferences.getBoolean(key, defValue);
	}
	/*
	 * ��int���ʹ���ʱ�ļ���ȡ��
	 * */
	public int getSharePrefInteger(String key) {
		return mSharedPreferences.getInt(key, -1);
	}
	/*
	 * ��int���ʹ���ʱ�ļ���ȡ����������Ĭ��ֵ
	 * */
	public int getSharePrefInteger(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);
	}
	/*
	 * �����������ʹ���ʱ�ļ���ȡ��
	 * */
	public long getSharePrefLong(String key) {
		return mSharedPreferences.getLong(key, -1);
	}
	/*
	 * �����������ʹ���ʱ�ļ���ȡ����������Ĭ��ֵ
	 * */
	public long getSharePrefLong(String key, int value) {
		return mSharedPreferences.getLong(key, -1);
	}
	/*
	 * ���������ʹ���ʱ�ļ���ȡ��
	 * */
	public float getSharePrefFloat(String key) {
		return mSharedPreferences.getFloat(key, -1);
	}
	/*
	 * ���ַ����洢����ʱ�ļ���
	 * */
	public boolean putSharePrefString(String key, String value) {
		mEditor.putString(key, value);
		return mEditor.commit();
	}
	/*
	 * ��boolean���ʹ洢����ʱ�ļ���
	 * */
	public boolean putSharePrefBoolean(String key, boolean value) {
		mEditor.putBoolean(key, value);
		return mEditor.commit();
	}
	/*
	 * ���������ʹ洢����ʱ�ļ���
	 * */
	public boolean putSharePrefFloat(String key, float value) {
		mEditor.putFloat(key, value);
		return mEditor.commit();
	}
	/*
	 * �����������ʹ洢����ʱ�ļ���
	 * */
	public boolean putSharePrefLong(String key, long value) {
		mEditor.putLong(key, value);
		return mEditor.commit();
	}
	/*
	 * ���������ʹ洢����ʱ�ļ���
	 * */
	public boolean putSharePrefInteger(String key, int value) {
		mEditor.putInt(key, value);
		return mEditor.commit();
	}
	
	/*
	 * ��ĳһ����ɾ��
	 * */
	public boolean removeKey(String key) {
		mEditor.remove(key);
		return mEditor.commit();
	}
	/*
	 * �����ʱ�ļ�����
	 * */
	public boolean clear() {
		mEditor.clear();
		return mEditor.commit();
	}

}
