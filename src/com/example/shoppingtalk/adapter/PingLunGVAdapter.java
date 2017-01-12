package com.example.shoppingtalk.adapter;

import java.util.List;

import com.example.shoppingtalk.R;
import com.example.shoppingtalk.entity.PLImgGVInfo;
import com.example.shoppingtalk.widget.MyImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PingLunGVAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private List<PLImgGVInfo> imgList;
	private MyClickListener mListener;
	private DisplayImageOptions options;

	public PingLunGVAdapter(Context mContext, List<PLImgGVInfo> imgList, MyClickListener mListener) {
		this.imgList = imgList;
		this.mContext = mContext;
		this.mListener = mListener;
		inflater = LayoutInflater.from(mContext);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.fabu) // resource
				// drawable
				.showImageForEmptyUri(R.drawable.fabu) // resource or
															// drawable
				.showImageOnFail(R.drawable.fabu) // resource or
														// drawable
				.resetViewBeforeLoading(false) // default
				.delayBeforeLoading(1000).cacheInMemory(false) // default
				.cacheOnDisk(false) // default
				.considerExifParams(false) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.handler(new Handler()) // default
				.build();
	}

	public void refresh(List<PLImgGVInfo> imgList) {
		this.imgList = imgList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return imgList == null ? 0 : imgList.size();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHelp vp = null;
		if (convertView == null) {
			vp = new ViewHelp();
			convertView = inflater.inflate(R.layout.item_gv_pl, null);
			vp.img = (MyImageView) convertView.findViewById(R.id.img);
			vp.showimg = (MyImageView) convertView.findViewById(R.id.showimg);
			vp.delete = (ImageView) convertView.findViewById(R.id.delete);
			convertView.setTag(vp);
		} else {
			vp = (ViewHelp) convertView.getTag();
		}
		PLImgGVInfo info = imgList.get(position);
		vp.img.setVisibility(View.GONE);
		vp.showimg.setVisibility(View.GONE);
		vp.delete.setVisibility(View.GONE);
		if ((info.getUrl()).equals("0")) {
//			Picasso.with(mContext).load(R.drawable.fabu).into(vp.img);
			vp.img.setOnClickListener(mListener);
			vp.img.setTag(position);
			vp.img.setVisibility(View.VISIBLE);
			vp.showimg.setVisibility(View.GONE);
			vp.delete.setVisibility(View.GONE);
		} else {
			ImageLoader.getInstance().displayImage(info.getUrl(), vp.showimg,options);
			vp.delete.setOnClickListener(mListener);
			vp.delete.setTag(position);
			vp.img.setVisibility(View.GONE);
			vp.showimg.setVisibility(View.VISIBLE);
			vp.showimg.setOnClickListener(mListener);
			vp.showimg.setTag(position);
			vp.delete.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class ViewHelp {
		MyImageView img;
		MyImageView showimg;
		ImageView delete;
	}

	@Override
	public Object getItem(int position) {
		return imgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static abstract class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}
}
