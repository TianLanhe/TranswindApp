package com.example.transwind;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.newsmssdk.BmobSMS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		//Bmob短信服务
		BmobSMS.initialize(getApplicationContext(),"dbcf2bdee4e2c560f01edfd4cd13e73a");
		
		// 两秒后跳转到其他界面
		new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
            	//如果已经登录了，就跳转到主页面，若未登录，则跳转到登录界面
            	if(getSharedPreferences("user",MODE_PRIVATE).getBoolean("hasLogin", false)){
            		Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                	Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },2000L);
	}
}
