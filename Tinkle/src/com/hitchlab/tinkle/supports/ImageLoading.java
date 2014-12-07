package com.hitchlab.tinkle.supports;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ImageLoading {
	DisplayImageOptions options;
	ImageLoader imageLoader;
	ImageLoadingListener animateFirstListener;
	ImageLoaderConfiguration config;
	Context context;
	
	public ImageLoading(Context context) {
		this.context = context;
		animateFirstListener = new AnimateFirstDisplayListener();
		preparingOption();
		preparingConfig();
		initLoader();
	}
	
	private void preparingOption() {
		options = new DisplayImageOptions.Builder()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.resetViewBeforeLoading()
		.build();
	}
	
	private void preparingConfig() {
		config = new ImageLoaderConfiguration.Builder(context)
		.memoryCache(new WeakMemoryCache())
		.denyCacheImageMultipleSizesInMemory()
		.build();
	}
	
	private void initLoader() {
		imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited())
			imageLoader.init(config);
	}
	
	public ImageLoader getImageLoader() {
		return imageLoader;
	}
	
	public DisplayImageOptions getDisplayImagesOption() {
		return options;
	}
	
	public void displayImage(String imgSrc, ImageView imgView) {
		imageLoader.displayImage(imgSrc, imgView, options, animateFirstListener);
	}
	
}
