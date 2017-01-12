package com.example.shoppingtalk.entity;

import java.util.List;

public class HDInfo {
	
	private List<PostsInfo> posts;
	private String user_name;
	private String im_username;
	private String user_type;
	private String hudong_bg;
	private String tel;
	private String user_id;
	private String avatar;
	
	
	private String comment_num;
	private String save_num;
	private String post_num;
	
	public String getComment_num() {
		return comment_num;
	}

	public void setComment_num(String comment_num) {
		this.comment_num = comment_num;
	}

	public String getSave_num() {
		return save_num;
	}

	public void setSave_num(String save_num) {
		this.save_num = save_num;
	}

	public String getPost_num() {
		return post_num;
	}

	public void setPost_num(String post_num) {
		this.post_num = post_num;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getIm_username() {
		return im_username;
	}

	public void setIm_username(String im_username) {
		this.im_username = im_username;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public String getHudong_bg() {
		return hudong_bg;
	}

	public void setHudong_bg(String hudong_bg) {
		this.hudong_bg = hudong_bg;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public List<PostsInfo> getPosts() {
		return posts;
	}

	public void setPosts(List<PostsInfo> posts) {
		this.posts = posts;
	}

}
