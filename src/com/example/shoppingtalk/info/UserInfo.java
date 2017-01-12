package com.example.shoppingtalk.info;

public class UserInfo {
	private String nickname;
	private String im_username;
	private String im_password;
	private String shop_id;
	
	public String getShop_id() {
		return shop_id;
	}

	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}

	private NewVersionInfo version;
	public String getIm_username() {
		return im_username;
	}

	public void setIm_username(String im_username) {
		this.im_username = im_username;
	}

	public String getIm_password() {
		return im_password;
	}

	public void setIm_password(String im_password) {
		this.im_password = im_password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public NewVersionInfo getVersion() {
		return version;
	}

	public void setVersion(NewVersionInfo version) {
		this.version = version;
	}
	
}
