package com.example.shoppingtalk.constant;

public class Urls {
	/**
	 * 测试
	 * */
	public static String TEST = "test";
	/**
	 * 登录
	 * */
	public static String LOGIN = "chat_login";
	/**
	 * 获取验证码
	 * */
	public static String YZM = "api_get_verify_code?";
	/**
	 * 注册
	 * */
	public static String REGISTER = "api_register";
	/**
	 * 密码重置
	 * */
	public static String RESET_PWD = "api_reset_password";
	/**
	 * 修改个人信息
	 * */
	public static String EDIT_INFO = "api_edit_user_info";
	/**
	 * 用户退出
	 * */
	public static String LOGOUT = "api_logout";
	/**
	 * 获取热门搜索
	 * */
	public static String HOT_SEARCH = "api_get_hot_search";
	/**
	 * 获取车分类信息
	 * */
	public static String AM_TYPE = "api_select_am_type";
	/**
	 * 获取搜索页的分类列表
	 * */
	public static String SEARCH_LIST = "api_get_search_list";
	/**
	 * 获取单项产品
	 * */
	public static String SEARCH_AM_PART = "api_select_am_part";
	/**
	 * 获取汽车用品
	 * */
	public static String SEARCH_AM_ACCESSORY = "api_select_am_accessory";
	/**
	 * 获取车品牌信息
	 * */
	public static String SEARCH_AM_BRAND = "api_select_am_brand";
	/**
	 * 获取推荐商家
	 * */
	public static String GET_RECOMMEND_SHOPS = "api_get_recommend_shops";
	/**
	 * 获取app初始化信息
	 * */
	public static String GET_INITIAL_DATA = "api_get_initial_data";
	/**
	 * 获取商家详情
	 * */
	public static String GET_SHOP_DETAIL = "api_get_shop_detail";
	/**
	 * 在聊天个人资料跳转商家详情
	 * */
	public static String GET_SHOP_DETAIL_IM = "/api_get_shop_detail_IM";
	/**
	 * 获取通话记录
	 * */
	public static String GET_SHOP_CALLED = "api_get_shop_called";
	/**
	 * 拨打商家电话
	 * */
	public static String CALL_SHOP = "api_call_shop";
	/**
	 * 获取收藏的商家列表
	 * */
	public static String GET_SHOP_SAVED = "api_get_shop_saved";
	/**
	 * 根据内容查找商家
	 * */
	public static String SEARCH_SHOP = "api_search_shop";
	/**
	 * 收藏店铺
	 * */
	public static String SAVE_SHOP = "api_save_shop";
	/**
	 * 取消店铺收藏
	 * */
	public static String DELETE_SHOP_SAVED = "api_delete_shop_saved";
	/**
	 * 删除通话记录
	 * */
	public static String DELETE_SHOP_CALLED = "api_delete_shop_called";
	/**
	 * 发送批量消息
	 * */
	public static String SEND_BATCH_MESSAGE = "api_send_batch_message_base64";
	/**
	 * 通过支付宝支付创建订单
	 * */
	public static String CREATE_ORDER_ALIPAY = "api_create_order_alipay";
	/**
	 * 查询订单是否支付
	 * */
	public static String QUERY_ORDER_STATUS = "api_query_order_status";
	/**
	 * 通过支付宝为未付款订单支付
	 * */
	public static String PAY_OEDER_ALIPAY = "api_pay_order_alipay";
	/**
	 * 查询指定状态的订单
	 * */
	public static String SELECT_PRDER = "api_select_order";
	/**
	 * 取消订单
	 * */
	public static String CANCEL_ORDER = "api_cancel_order";
	
	/**
	 * 确认收货
	 * */
	public static String FINISH_ORDER = "api_finish_order";
	
	/**
	 * 确认收货
	 * */
	public static String GET_NICKNAME_SHOP = "api_get_nickname_shop";
	public static String GET_NICKNAME_USER = "api_get_nickname_user";
	/**
	 * 获取当前联系人列表对应的所有昵称
	 * */
	public static String api_get_ALL_nickname = "get_nickname_user_batch2";
	/**
	 * 商家登录接口
	 * */
	public static String CHAT_LOGIN = "chat_login";
	/**
	 * 在线升级
	 * */
	public static String API_GET_NEW_VERSION = "api_get_new_version";
	
	
	/**
	 * 收藏状态
	 * */
	public static String  API_SAVE_POST = "api_save_post";
	
	/**
	 *取消收藏状态
	 * */
	public static String  API_CANCEL_SAVE_POST = "api_cancel_save_post";
	
	/**
	 *取消收藏状态
	 * */
	public static String  API_COMMENT_POST = "api_comment_post";
	/**
	 * 提交发布数据
	 * */
	public static String  API_NEW_POST = "api_new_post_str";
	
	/**
	 * 点赞
	 * */
	public static String  API_ZAN_POST = "api_zan_post";
	
	/**
	 * 取消点赞
	 * */
	public static String  API_CANCEL_ZAN_POST = "api_cancel_zan_post";
	
	/**
	 * 互动社区初始化
	 * */
	public static String  API_GETL_POST_TYPE = "api_get_post_type";
	/**
	 * 互动社区初始化列表 日韩
	 * */
	public static String  API_GETL_POST_LIST = "api_get_post_list";
	
	/**
	 * 
	 *			获取个人信息
	 *
	 *			to_user_type,要访问的人的类型   
	 *			to_user_id，要访问的人的id
	 *
	 * */
	public static String  API_GET_POST_LIST_SOMEONE = "api_get_post_list_someone";
	/**
	 *			获取个人回复列表
	 *
	 * */
	public static String  API_GET_POST_LIST_COMMENTED = "api_get_post_list_commented";
	/**
	 * 
	 *			获取个人收藏列表
	 *
	 * */
	public static String  API_GET_POST_LIST_SAVED = "api_get_post_list_saved";
	/**
	 * 
	 *			删除某一条 互动
	 *
	 * */
	public static String  API_DELETE_POST = "api_delete_post";
	
	/**
	 * 
	 *			删除某一条 评论
	 *
	 * */
	public static String  API_DELETE_COMMENT_POST = "api_delete_comment_post";
}
