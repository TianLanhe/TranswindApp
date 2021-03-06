package com.example.transwind;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class WebActivity extends Activity {

	// UI相关
	WebView wbv_web;
	ImageView img_back;
	TextView txt_title;

	// 数据相关
	String webURL;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		wbv_web = (WebView) findViewById(R.id.wbv_web_web);
		img_back = (ImageView) findViewById(R.id.imgv_header_back);
		txt_title=(TextView) findViewById(R.id.txt_header_title);

		
		// 返回按钮
		img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		txt_title.setText("");
		
		// 从intent里取出链接，其他界面在打开此活动时必须传一个字符串链接
		Intent intent = getIntent();
		webURL = intent.getStringExtra("url");

		wbv_web.getSettings().setJavaScriptEnabled(true);	//支持JS
		wbv_web.getSettings().setBuiltInZoomControls(true);	//支持缩放
		wbv_web.loadUrl(webURL);
		wbv_web.setWebViewClient(new WebViewClient());
		wbv_web.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onReceivedTitle(WebView view, String title){
				txt_title.setText(title); 
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (wbv_web.canGoBack())
			wbv_web.goBack();// 返回上一页面
		else
			super.onBackPressed();
	}
}
