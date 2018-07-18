package com.pait.smartpos.constant;

//Created by ANUP on 6/7/2018.

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class AppSingleton {


    private static AppSingleton mAppSingleton;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private AppSingleton(Context _context){
        this.mContext = _context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            LruCache<String, Bitmap> cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url,bitmap);
            }
        });
    }

    public static AppSingleton getInstance(Context _context){
        if(mAppSingleton == null){
            mAppSingleton = new AppSingleton(_context);
        }
        return mAppSingleton;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(60000,0,0));
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

    public void cancelRequest(Object tag){
        if(mRequestQueue!=null){
            mRequestQueue.cancelAll(tag);
        }
    }
}
