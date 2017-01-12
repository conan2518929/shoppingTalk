package com.example.shoppingtalk.adapter;

import java.util.List;

import com.example.shoppingtalk.HDShopDetailActivity;
import com.example.shoppingtalk.R;
import com.example.shoppingtalk.entity.CommentsInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private List<CommentsInfo> list;
	private CommentClickListener listener;
	private ListView mListView;
	private int number;
	
	public CommentAdapter(Context mContext, List<CommentsInfo> list,int number) {
		this.list = list;
		this.mContext = mContext;
		this.number = number;
		inflater = LayoutInflater.from(mContext);
	}

	public void refresh(List<CommentsInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public void setListView(ListView lisv) {
		this.mListView = lisv;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHelp vp = null;
		if (convertView == null) {
			vp = new ViewHelp();
			convertView = inflater.inflate(R.layout.item_comment, null);
			vp.content = (TextView) convertView.findViewById(R.id.content);
			vp.detail = (LinearLayout) convertView.findViewById(R.id.detail);
			convertView.setTag(vp);
		} else {
			vp = (ViewHelp) convertView.getTag();
		}
		if (list.size() > 0) {
			CommentsInfo info = list.get(position);
			if (info.getFather_id().equals("0")) {
				
				SpannableString spanableInfo = new SpannableString(info.getUser_name() + ": " + info.getContent());

				spanableInfo.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.blue_one)), 0, info.getUser_name().length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				spanableInfo.setSpan(new Clickable(position,1,info,number), 0, info.getUser_name().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				spanableInfo.setSpan(new Clickable(position,0,info,number), info.getUser_name().length() + 2, info.getUser_name().length() + 2
						+ info.getContent().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				vp.content.setText(spanableInfo);
				
				vp.content.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				SpannableString spanableInfo = new SpannableString(info.getUser_name() + "回复" + info.getFather_user_name() + ":"
						+ info.getContent());

				spanableInfo.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.blue_one)), 
						0, info.getUser_name().length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				spanableInfo.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.blue_one)),
						info.getUser_name().length()+2, 
						info.getUser_name().length()+2+info.getUser_name().length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				spanableInfo.setSpan(new Clickable(position,2,info,number), 
						0, info.getUser_name().length(), 
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//点击的是第1个名字

				spanableInfo.setSpan(new Clickable(position,3,info,number), //点击的是第2个名字
						info.getUser_name().length() + 2, 
						info.getUser_name().length() + 2 + info.getFather_user_name().length(), 
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				spanableInfo.setSpan(new Clickable(position,4,info,number), //点击的是内容
						info.getUser_name().length() + 2 + info.getFather_user_name().length()+1, 
						info.getUser_name().length() + 2 + info.getFather_user_name().length() +1+ info.getContent().length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				vp.content.setText(spanableInfo);
				
				vp.content.setMovementMethod(LinkMovementMethod.getInstance());

			}
		}

		return convertView;
	}

	public static abstract class CommentClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}
	
	class Clickable extends ClickableSpan {
		
		private int position;
		private int type =0;
		private CommentsInfo info;
		private int numFather;
		
		public Clickable(int num,int typeNum ,CommentsInfo commentsInfo,int number) {
			position = num;
			numFather = number;
			type = typeNum;
			info = commentsInfo;
		}

		/**
		 * 重写父类点击事件
		 */
		@Override
		public void onClick(View v) {
			v.setOnClickListener(mListenerTwo);
			v.setTag(position);
//			mListenerOne.onClick(v);
		}

		/**
		 * 重写父类updateDrawState方法 我们可以给TextView设置字体颜色,背景颜色等等...
		 */
		@Override
		public void updateDrawState(TextPaint ds) {
			// ds.setColor(mContext.getResources().getColor(R.color.video_comment_like_number));
		}
		
		private CommentClickListener mListenerTwo = new CommentClickListener(){
			@Override
			public void myOnClick(int position, View v) {
				if(type == 0){
					Intent intent = new Intent();
					intent.setAction("com.app.action.broadcast_send_content");
					Bundle data = new Bundle();
					data.putString("type","0");
					data.putSerializable("info", info);
					data.putInt("huifuweizhi", position);
					data.putInt("position", numFather);//在整体评论列表的位置 不是回复列表的位置
					intent.putExtras(data);
					mContext.sendBroadcast(intent);
				}
				if(type == 1){
					Intent intent = new Intent(mContext,HDShopDetailActivity.class);
					Bundle data = new Bundle();
					data.putString("user_type", info.getUser_type());
					data.putString("user_id", info.getUser_id());
					intent.putExtras(data);
					mContext.startActivity(intent);
				}
				if(type == 2){
					Intent intent = new Intent(mContext,HDShopDetailActivity.class);
					Bundle data = new Bundle();
					data.putString("user_type", info.getUser_type());
					data.putString("user_id", info.getUser_id());
					intent.putExtras(data);
					mContext.startActivity(intent);
				}
				if(type == 3){
					Intent intent = new Intent(mContext,HDShopDetailActivity.class);
					Bundle data = new Bundle();
					data.putString("user_type", info.getFather_user_type());
					data.putString("user_id", info.getFather_id());
					intent.putExtras(data);
					mContext.startActivity(intent);
				}
				if(type == 4){
					Intent intent = new Intent();
					intent.setAction("com.app.action.broadcast_send_content");
					Bundle data = new Bundle();
					data.putString("type","4");
					data.putSerializable("info", info);
					data.putInt("huifuweizhi", position);
					data.putInt("position", numFather);//在整体评论列表的位置 不是回复列表的位置
					intent.putExtras(data);
					mContext.sendBroadcast(intent);
				}
			}
		};
	}

	class ViewHelp {
		TextView content;
		LinearLayout detail;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}