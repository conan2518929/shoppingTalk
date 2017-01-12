package com.example.shoppingtalk.sharepref;
/**
 * 2015-9-17 易戈
 * 
 * 我们要开发一个类
 * 这个类的功能主要是对需要存储的信息保存到对应名字的临时文件上
 * 
 */
public class SharedPrefConstant {
	public static final String LOGOUT = "logout";
	/*
	 * 可以删除的数据所在的临时文件
	 * */
	public static final String TEMPORARY = "sharepre_temporary";
	
	/*
	 * 必须保留且不能删除的数据所在的临时文件
	 * */
	public static final String IMPORTANT = "sharepre_Important";
	
	/*
	 * 清除历史记录临时文件
	 * */
	public static final String EXTRA_VER_URL = "ver_url";
	public static final String EXTRA_VER_VERDICT = "ver_verdict";
	
	
	/*
	 * 记录选择的城市
	 * */
	public static final String CHOOSE_CITY = "choose_city";
	public static final String CHOOSE_CITY_NAME = "choose_city_name";
	/*
	 * 记录客服电话和规则
	 * */
	public static final String KEFU_TEL = "kefu_tel";
	public static final String RULE = "RULE";
	/*
	 * 记录消息数目
	 * */
	public static final String TALK_NUMBER = "talk_number";
	
	/**
	 * 下拉刷新时间
	 * */
	public static final String LASTUPDATETIME_HOME = "LASTUPDATETIME_HOME";
	/**
	 * 登录保存个人资料
	 * */
	public static final String REN_NAME = "username";
	public static final String REN_ADDRESS = "address";
	public static final String IS_SHOW = "show_outbtn";
	public static final String USERID = "userid";
	public static final String PHONEONE = "mobile_register";
	public static final String USERID_PWD = "userid_pwd";
	public static final String PHONETWO = "mobile_in_use";
	public static final String IM_NAME = "im_username";
	public static final String IM_PWD = "im_password";
	
	
	public static final String TOKEN = "token";
	public static final String ACCOUNT = "account";
	
	
}
