package com.example.transwind;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class WebActivity extends Activity {

	// UI���
	WebView wbv_web;
	ImageView img_back;

	// �������
	String webURL;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		wbv_web = (WebView) findViewById(R.id.wbv_web_web);
		img_back = (ImageView) findViewById(R.id.imgv_header_back);

		// ���ذ�ť
		img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// ��intent��ȡ�����ӣ����������ڴ򿪴˻ʱ���봫һ���ַ�������
		Intent intent = getIntent();
		webURL = intent.getStringExtra("url");

		wbv_web.getSettings().setJavaScriptEnabled(true);	//֧��JS
		wbv_web.getSettings().setBuiltInZoomControls(true);	//֧������
		wbv_web.setWebViewClient(new WebViewClient());
		wbv_web.loadUrl(webURL);
	}

	@Override
	public void onBackPressed() {
		if (wbv_web.canGoBack())
			wbv_web.goBack();// ������һҳ��
		else
			super.onBackPressed();
	}
}
