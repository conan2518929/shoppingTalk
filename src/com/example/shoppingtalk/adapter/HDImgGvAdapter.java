package com.example.shoppingtalk.adapter;

import java.util.List;

import com.example.shoppingtalk.R;
import com.example.shoppingtalk.ShowImageActivity;
import com.example.shoppingtalk.Utils;
import com.example.shoppingtalk.constant.Constant;
import com.example.shoppingtalk.entity.PicsInfo;
import com.example.shoppingtalk.widget.MyImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

public class HDImgGvAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private List<PicsInfo> pics;
	private DisplayImageOptions options;

	public HDImgGvAdapter(Context mContext, List<PicsInfo> pics) {
		this.pics = pics;
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.fabu) // resource
				.showImageForEmptyUri(R.drawable.fabu) // resource or
				.showImageOnFail(R.drawable.fabu) // resource or
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

	public void refresh(List<PicsInfo> pics) {
		this.pics = pics;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return pics == null ? 0 : pics.size();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHelp vp = null;
		if (convertView == null) {
			vp = new ViewHelp();
			convertView = inflater.inflate(R.layout.item_gv_pl_show, null);
			vp.showimg = (MyImageView) convertView.findViewById(R.id.showimg);
			convertView.setTag(vp);
		} else {
			vp = (ViewHelp) convertView.getTag();
		}
		final PicsInfo info = pics.get(position);
		ImageLoader.getInstance().displayImage(Constant.URL_TEST + info.getPic_url(), vp.showimg, options);
		vp.showimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int num = pics.size();
				if (num > 0) {
					String[] urls = new String[num];
					for (int i = 0; i < urls.length; i++) {
						urls[i] = Constant.URL_TEST + pics.get(i).getPic_url();
					}
					showImage(position, urls);
				} else {
					Utils.showText(mContext, "图片加载失败" + position);
				}
			}
		});
		return convertView;
	}

	class ViewHelp {
		MyImageView showimg;
	}

	@Override
	public Object getItem(int position) {
		return pics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	private void showImage(int position, String[] urls) {
		Intent intent = new Intent(mContext, ShowImageActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ShowImageActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ShowImageActivity.EXTRA_IMAGE_INDEX, position);
		mContext.startActivity(intent);
	}
}
