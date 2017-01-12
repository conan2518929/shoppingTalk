package com.example.shoppingtalk.widget;

import com.example.shoppingtalk.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Dialog {
	public static android.app.Dialog showListDialog(Context context, String title, String[] items, final DialogItemClickListener dialogItemClickListener) {
		return ShowDialog(context, title, items, dialogItemClickListener);
	}

	private static android.app.Dialog ShowDialog(Context context, String title, String[] items, final DialogItemClickListener dialogClickListener) {
		final android.app.Dialog dialog = new android.app.Dialog(context, R.style.DialogStyle);
		dialog.setCancelable(false);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_radio, null);
		dialog.setContentView(view);
		TextView titleTv = ((TextView) view.findViewById(R.id.title));
		titleTv.setText(title);
		titleTv.setTextColor(context.getResources().getColor(R.color.fenxiang_color_zan));
		LinearLayout parent = (LinearLayout) view.findViewById(R.id.dialogLayout);
		parent.removeAllViews();
		int length = items.length;
		for (int i = 0; i < items.length; i++) {
			LayoutParams params1 = new LayoutParams(-1, -2);
			params1.rightMargin = 1;
			final TextView tv = new TextView(context);
			tv.setLayoutParams(params1);
			tv.setTextSize(18);
			tv.setText(items[i]);
			tv.setTextColor(context.getResources().getColor(R.color.fenxiang_color_zan));
			int pad = context.getResources().getDimensionPixelSize(R.dimen.padding10);
			tv.setPadding(pad, pad, pad, pad);
			tv.setSingleLine(true);
			tv.setGravity(Gravity.CENTER);
			if (i != length - 1)
				tv.setBackgroundResource(R.drawable.menudialog_center_selector);
			else
				tv.setBackgroundResource(R.drawable.menudialog_bottom2_selector);

			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					dialogClickListener.confirm(tv.getText().toString());
				}
			});
			parent.addView(tv);
			if (i != length - 1) {
				TextView divider = new TextView(context);
				LayoutParams params = new LayoutParams(-1, (int) 1);
				divider.setLayoutParams(params);
				divider.setBackgroundResource(android.R.color.darker_gray);
				parent.addView(divider);
			}
		}
		view.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		Window mWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.width = getScreenWidth(context);
		mWindow.setGravity(Gravity.BOTTOM);
		mWindow.setWindowAnimations(R.style.dialogAnim);
		mWindow.setAttributes(lp);
		dialog.show();
		return dialog;
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public interface DialogItemClickListener {
		public abstract void confirm(String result);
	}
}
