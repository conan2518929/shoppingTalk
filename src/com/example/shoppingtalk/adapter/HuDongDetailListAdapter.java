package com.example.shoppingtalk.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.shoppingtalk.R;
import com.example.shoppingtalk.Utils;
import com.example.shoppingtalk.constant.Constant;
import com.example.shoppingtalk.entity.CommentsInfo;
import com.example.shoppingtalk.entity.PicsInfo;
import com.example.shoppingtalk.entity.PostsInfo;
import com.example.shoppingtalk.entity.VideosInfo;
import com.example.shoppingtalk.widget.Dialog;
import com.example.shoppingtalk.widget.Dialog.DialogItemClickListener;
import com.example.shoppingtalk.widget.MyGridView;
import com.example.shoppingtalk.widget.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class HuDongDetailListAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater inflater;
	private List<PostsInfo> posts;
	private MyDetailClickListener mListener;
	private ListView mListView;
	private MyListView lv;
	private Map<Integer,CommentDetailAdapter>map = null;
	private Map<Integer,List>mapPinglun = null;
	public HuDongDetailListAdapter(Context mContext, List<PostsInfo> posts, MyDetailClickListener mListener) {
		this.posts = posts;
		this.mContext = mContext;
		this.mListener = mListener;
		inflater = LayoutInflater.from(mContext);
		map = new HashMap<>();
		mapPinglun = new HashMap<>();
	}

	public void setListView(ListView lisv) {
		this.mListView = lisv;
	}

	public void refresh(List<PostsInfo> posts) {
		this.posts = posts;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return posts == null ? 0 : posts.size();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHelp vp = null;
		if (convertView == null) {
			vp = new ViewHelp();
			convertView = inflater.inflate(R.layout.item_hudong, null);
			vp.myGv = (MyGridView) convertView.findViewById(R.id.gv);
			vp.showFL = (FrameLayout) convertView.findViewById(R.id.showFL);
			vp.play = (ImageView) convertView.findViewById(R.id.play);
			vp.imgVideo = (ImageView) convertView.findViewById(R.id.imgVideo);
			vp.talkimg = (ImageView) convertView.findViewById(R.id.talkimg);
			vp.content = (TextView) convertView.findViewById(R.id.content);
			vp.title = (TextView) convertView.findViewById(R.id.title);
			vp.time = (TextView) convertView.findViewById(R.id.time);

			vp.soucang = (ImageView) convertView.findViewById(R.id.soucang);
			vp.pl = (ImageView) convertView.findViewById(R.id.pl);
			vp.zan = (ImageView) convertView.findViewById(R.id.zan);
			vp.img = (ImageView) convertView.findViewById(R.id.img);

			vp.ll_sc = (LinearLayout) convertView.findViewById(R.id.ll_sc);
			vp.tv_sc = (TextView) convertView.findViewById(R.id.tv_sc);

			vp.ll_zan = (LinearLayout) convertView.findViewById(R.id.ll_zan);
			vp.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);

			vp.mylv = (MyListView) convertView.findViewById(R.id.mylv);

			convertView.setTag(vp);
		} else {
			vp = (ViewHelp) convertView.getTag();
		}

		vp.showFL.setVisibility(View.GONE);
		vp.myGv.setVisibility(View.GONE);
		PostsInfo info = posts.get(position);
		if (info != null) {

			vp.title.setText(info.getPoster_name());
			vp.time.setText(info.getPost_time());
			vp.content.setVisibility(View.GONE);

			if (info.getPoster_type().equals("user")) {
				vp.img.setImageResource(R.drawable.my_tx);
			} else if (info.getPoster_type().equals("shop")) {
				ImageLoader.getInstance().displayImage(Constant.URL_TEST + info.getPoster_avatar(), vp.img);
			}

			if (!info.getPost_text().toString().trim().equals("")) {// �ж������Ƿ����
				vp.content.setVisibility(View.VISIBLE);
				vp.content.setText(info.getPost_text().toString().trim());
			}

			List<PicsInfo> imgList = new ArrayList<>();
			imgList = info.getPics();
			if (imgList.size() > 0) {// �ж�9����ͼƬ�Ƿ����
				vp.myGv.setVisibility(View.VISIBLE);
				vp.showFL.setVisibility(View.GONE);
				HDImgGvAdapter adapter = new HDImgGvAdapter(mContext, imgList);
				vp.myGv.setAdapter(adapter);
			}

			List<VideosInfo> videos = new ArrayList<>();
			videos = info.getVideos();
			if (videos.size() > 0) {// �ж���Ƶ�Ƿ����
				vp.myGv.setVisibility(View.GONE);
				vp.showFL.setVisibility(View.VISIBLE);
				// vp.imgVideo
				ImageLoader.getInstance().displayImage(Constant.URL_TEST + videos.get(0).getPic_url(), vp.imgVideo);
				vp.play.setOnClickListener(mListener);
				vp.play.setTag(position);
			}
			if (info.getSave() == 0) {// û�б��ղع�
				vp.soucang.setImageResource(R.drawable.shoucang_hui);
			} else if (info.getSave() == 1) {// ���ղع�
				vp.soucang.setImageResource(R.drawable.shoucang_lan);
			}
			if (info.getZan() == 1) {// �޹�
				vp.zan.setImageResource(R.drawable.zan_blue);
			} else if (info.getZan() == 0) {// û���޹�
				vp.zan.setImageResource(R.drawable.zan_hui);
			}

			if (info.getSave_num() == 0) {
				vp.ll_sc.setVisibility(View.GONE);
			} else {
				vp.ll_sc.setVisibility(View.VISIBLE);
				vp.tv_sc.setText("�ѱ�" + info.getSave_num() + "�����ղ�");
			}

			int num = info.getZan_list().size();
			if (num == 0) {
				vp.ll_zan.setVisibility(View.GONE);
			} else {
				vp.ll_zan.setVisibility(View.VISIBLE);
				String name = "";
				for (int i = 0; i < num; i++) {
					if (i == num - 1) {
						name += info.getZan_list().get(i).getName();
					} else {
						name += info.getZan_list().get(i).getName() + "��";
					}
				}
				vp.tv_zan.setText(name + "���ú���");
			}

			/**
			 * ���۲���
			 * */
			List<CommentsInfo> list = new ArrayList<>();
			list = info.getComments();
			mapPinglun.put(position, list);
			CommentDetailAdapter adapterComment = new CommentDetailAdapter(mContext, list,position);
			adapterComment.setListView(vp.mylv);
			vp.mylv.setAdapter(adapterComment);
			vp.mylv.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View v, final int num, long arg3) {
//					adapterComment.updateItemData(posts.get(position).getComments().get(num), num);
					
						Dialog.showListDialog(mContext, "��ѡ��", new String[]{"ɾ����������"}, new DialogItemClickListener() {
							
							@Override
							public void confirm(String result) {
								if (result.equals("ɾ����������")) {
									List<CommentsInfo>lists = mapPinglun.get(position);
									if(lists.get(num).getCan_delete() == 0){
										Utils.showShortToast(mContext, "�ܱ�Ǹ,������ɾ�����˷���������...");
										return ;
									}
									Intent intent = new Intent();
									intent.setAction("com.app.action.broadcast_detail");
									Bundle data = new Bundle();
									data.putString("type", "6");
									data.putInt("hold", position);
									data.putInt("huifuweizhi", num);
									intent.putExtras(data);
									mContext.sendBroadcast(intent);
								} 
							}
						});
					return true;
				}
			});
			
			map.put(position, adapterComment);
			
			vp.pl.setOnClickListener(mListener);
			vp.pl.setTag(position);

			vp.img.setOnClickListener(mListener);
			vp.img.setTag(position);

			vp.talkimg.setOnClickListener(mListener);
			vp.talkimg.setTag(position);

			vp.zan.setOnClickListener(mListener);
			vp.zan.setTag(position);

			vp.soucang.setOnClickListener(mListener);
			vp.soucang.setTag(position);
		}
		return convertView;
	}

	class ViewHelp {
		MyGridView myGv;
		FrameLayout showFL;
		ImageView imgVideo;
		ImageView play;
		ImageView talkimg;
		ImageView img;
		TextView content;

		ImageView soucang;
		ImageView pl;
		ImageView zan;

		TextView title;
		TextView time;

		LinearLayout ll_sc;
		TextView tv_sc;

		LinearLayout ll_zan;
		TextView tv_zan;

		MyListView mylv;
	}

	@Override
	public Object getItem(int position) {
		return posts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static abstract class MyDetailClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}

	public void updateItemData(PostsInfo item, int position, int type) {
		Message msg = Message.obtain();
		msg.arg1 = position;
		msg.arg2 = type;
		// ����mDataList��Ӧλ�õ�����
		posts.set(position, item);
		// handleˢ�½���
		han.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler han = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.arg2 == 0) {
				updateItem(msg.arg1, 0);
			}
			if (msg.arg2 == 1) {
				updateItem(msg.arg1, 1);
			}
			if (msg.arg2 == 2) {
				updateItem(msg.arg1, 2);
			}
		};
	};

	private void updateItem(int index, int type) {
		if (mListView == null) {
			return;
		}
		// ��ȡ��ǰ���Կ�����itemλ��
		int visiblePosition = mListView.getFirstVisiblePosition();
		View view = mListView.getChildAt(index - visiblePosition);
		PostsInfo data = (PostsInfo) getItem(index);
		if (type == 0) {
			ImageView scImg = (ImageView) view.findViewById(R.id.soucang);
			LinearLayout ll_sc = (LinearLayout) view.findViewById(R.id.ll_sc);
			TextView tv_sc = (TextView) view.findViewById(R.id.tv_sc);
			if (data.getSave_num() == 0) {
				ll_sc.setVisibility(View.GONE);
			} else {
				ll_sc.setVisibility(View.VISIBLE);
				tv_sc.setText("�ѱ�" + data.getSave_num() + "�����ղ�");
			}
			if (data.getSave() == 0) {// û�б��ղع�
				scImg.setImageResource(R.drawable.shoucang_hui);
			} else if (data.getSave() == 1) {// ���ղع�
				scImg.setImageResource(R.drawable.shoucang_lan);
			}
		}
		if (type == 1) {
			ImageView zanImg = (ImageView) view.findViewById(R.id.zan);
			LinearLayout ll_zan = (LinearLayout) view.findViewById(R.id.ll_zan);
			TextView tv_zan = (TextView) view.findViewById(R.id.tv_zan);
			int number = data.getZan_list().size();
			if (number < 1) {
				ll_zan.setVisibility(View.GONE);
				tv_zan.setText("");
			} else {
				ll_zan.setVisibility(View.VISIBLE);
				String name = "";
				for (int i = 0; i < number; i++) {
					if (i == number - 1) {
						name += data.getZan_list().get(i).getName();
					} else {
						name += data.getZan_list().get(i).getName() + "��";
					}
				}
				tv_zan.setText(name + "���ú���");
			}
			if (data.getZan() == 0) {
				zanImg.setImageResource(R.drawable.zan_hui);
			} else {
				zanImg.setImageResource(R.drawable.zan_blue);
			}
		}
		if(type == 2){
			mapPinglun.put(index, data.getComments());
			map.get(index).refresh(data.getComments());
		}
	}
}
