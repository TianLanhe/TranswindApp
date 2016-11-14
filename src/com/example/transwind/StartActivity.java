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
		
		//Bmob���ŷ���
		BmobSMS.initialize(getApplicationContext(),"dbcf2bdee4e2c560f01edfd4cd13e73a");
		
		// �������ת����������
		new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
            	//����Ѿ���¼�ˣ�����ת����ҳ�棬��δ��¼������ת����¼����
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
