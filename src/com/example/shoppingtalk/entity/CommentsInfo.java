package com.example.shoppingtalk.entity;

import java.io.Serializable;

public class CommentsInfo implements Serializable{
	
	private String id;// ����id
	private String post_id;
	private String father_user_type;// �ظ������۵ķ����ߵ�����
	private String father_id;// �ظ������۵�id
	private String father_user_id;// �ظ������۵ķ����ߵ�id
	private String user_type;// ���������۵��˵�����
	private String user_id;// ���������۵��˵�id
	private String content;// ��������
	private String comment_time;// ����ʱ��
	private String user_name;// ���������۵��˵�����
	private String father_user_name;// �ظ������۵ķ����ߵ�����
	private int can_delete;// �ظ������۵ķ����ߵ�����
	public int getCan_delete() {
		return can_delete;
	}

	public void setCan_delete(int can_delete) {
		this.can_delete = can_delete;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getFather_user_type() {
		return father_user_type;
	}

	public void setFather_user_type(String father_user_type) {
		this.father_user_type = father_user_type;
	}

	public String getFather_id() {
		return father_id;
	}

	public void setFather_id(String father_id) {
		this.father_id = father_id;
	}

	public String getFather_user_id() {
		return father_user_id;
	}

	public void setFather_user_id(String father_user_id) {
		this.father_user_id = father_user_id;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getComment_time() {
		return comment_time;
	}

	public void setComment_time(String comment_time) {
		this.comment_time = comment_time;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getFather_user_name() {
		return father_user_name;
	}

	public void setFather_user_name(String father_user_name) {
		this.father_user_name = father_user_name;
	}

}
