package com.example.shoppingtalk.widget;

import com.example.shoppingtalk.widget.PullToRefreshBase.OnRefreshListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public interface IPullToRefresh<T extends View> {

	public void setPullRefreshEnabled(boolean pullRefreshEnabled);

	public void setPullLoadEnabled(boolean pullLoadEnabled);

	public void setScrollLoadEnabled(boolean scrollLoadEnabled);

	public boolean isPullRefreshEnabled();

	public boolean isPullLoadEnabled();

	public boolean isScrollLoadEnabled();

	public void setOnRefreshListener(OnRefreshListener<T> refreshListener);

	public void onPullDownRefreshComplete();

	public void onPullUpRefreshComplete();

	public T getRefreshableView();

	public LoadingLayout getHeaderLoadingLayout();

	public LoadingLayout getFooterLoadingLayout();

	public void setLastUpdatedLabel(CharSequence label);
}
