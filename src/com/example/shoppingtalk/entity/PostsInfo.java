package com.example.shoppingtalk.entity;

import java.util.List;

public class PostsInfo {
	private String id;
	private String poster_type;
	private String poster_id;
	private String post_type;
	private String post_text;
	private String post_time;
	private List<PicsInfo> pics;
	private List<VideosInfo> videos;
	private int zan;
	private int save;
	private int save_num;
	private int can_delete;
	private String poster_name;
	private String poster_im_username;
	private String poster_avatar;
	private List<CommentsInfo> comments;
	private List<ZanListInfo>zan_list;
	
	public List<ZanListInfo> getZan_list() {
		return zan_list;
	}

	public void setZan_list(List<ZanListInfo> zan_list) {
		this.zan_list = zan_list;
	}

	public int getSave_num() {
		return save_num;
	}

	public void setSave_num(int save_num) {
		this.save_num = save_num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPoster_type() {
		return poster_type;
	}

	public void setPoster_type(String poster_type) {
		this.poster_type = poster_type;
	}

	public String getPoster_id() {
		return poster_id;
	}

	public void setPoster_id(String poster_id) {
		this.poster_id = poster_id;
	}

	public String getPost_type() {
		return post_type;
	}

	public void setPost_type(String post_type) {
		this.post_type = post_type;
	}

	public String getPost_text() {
		return post_text;
	}

	public void setPost_text(String post_text) {
		this.post_text = post_text;
	}

	public String getPost_time() {
		return post_time;
	}

	public void setPost_time(String post_time) {
		this.post_time = post_time;
	}

	public List<PicsInfo> getPics() {
		return pics;
	}

	public void setPics(List<PicsInfo> pics) {
		this.pics = pics;
	}

	public List<VideosInfo> getVideos() {
		return videos;
	}

	public void setVideos(List<VideosInfo> videos) {
		this.videos = videos;
	}

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

	public int getSave() {
		return save;
	}

	public void setSave(int save) {
		this.save = save;
	}

	public int getCan_delete() {
		return can_delete;
	}

	public void setCan_delete(int can_delete) {
		this.can_delete = can_delete;
	}

	public String getPoster_name() {
		return poster_name;
	}

	public void setPoster_name(String poster_name) {
		this.poster_name = poster_name;
	}

	public String getPoster_im_username() {
		return poster_im_username;
	}

	public void setPoster_im_username(String poster_im_username) {
		this.poster_im_username = poster_im_username;
	}

	public String getPoster_avatar() {
		return poster_avatar;
	}

	public void setPoster_avatar(String poster_avatar) {
		this.poster_avatar = poster_avatar;
	}

	public List<CommentsInfo> getComments() {
		return comments;
	}

	public void setComments(List<CommentsInfo> comments) {
		this.comments = comments;
	}

}
