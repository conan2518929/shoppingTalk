package com.example.shoppingtalk;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.example.shoppingtalk.sharepref.SharedPref;
import com.example.shoppingtalk.sharepref.SharedPrefConstant;
import com.example.shoppingtalk.talk.DemoHelper;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

/**
 * 2015-9-17 易戈
 * 
 * 这个类的功能主要是引入第三方功能设置全局变量的初始化
 * 
 */

public class AppContext extends Application implements UncaughtExceptionHandler{
	
	public static final String TAG = AppContext.class.getSimpleName();
	public static RequestQueue mRequestQueue;
	public static AppContext mInstance = null;
	public static SharedPref mSharedPref;
	public static SharedPref imp_SharedPref;
	private ImageLoaderKit imageLodaer;
	
	@Override
	public void onCreate() {
		MultiDex.install(this);
		super.onCreate();
		mInstance = this;
		mSharedPref = SharedPref.getInstance(SharedPrefConstant.TEMPORARY, this);
		imp_SharedPref = SharedPref.getInstance(SharedPrefConstant.IMPORTANT, this);
		imageLodaer = new ImageLoaderKit(mInstance, null);
		
		DemoHelper.getInstance().init(mInstance);
		
	}
	
	public static synchronized AppContext getInstance() {
        return mInstance;
    }
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
        	
			 DefaultHttpClient httpclient = new DefaultHttpClient();
			 //非持久化存储(内存存储) BasicCookieStore | 持久化存储 PreferencesCookieStore
			 CookieStore cookieStore = new PreferencesCookieStore(mInstance);
			 httpclient.setCookieStore(cookieStore);
			 HttpStack httpStack = new HttpClientStack(httpclient);
			 mRequestQueue = Volley.newRequestQueue(mInstance,httpStack);
//         mRequestQueue = Volley.newRequestQueue(mInstance);
        }
        return mRequestQueue;
    }
	
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    
 	@Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
    
}
