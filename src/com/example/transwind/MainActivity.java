package com.example.transwind;

import com.example.transwind.data.User;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	private long lastTime = 0;

	// UI相关
	private RadioGroup radg_tab;
	private RadioButton rad_home;
	private RadioButton rad_mine;
	private RadioButton rad_bbs;
	private RadioButton rad_language_merchant;
	private RadioButton rad_language_translator;
	private RadioButton rad_talent_merchant;
	private RadioButton rad_talent_translator;
	private RadioButton rad_competition;
	private RadioButton rad_readevery;

	// 数据相关
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		radg_tab = (RadioGroup) findViewById(R.id.radg_main_tab);
		rad_home = (RadioButton) findViewById(R.id.rad_main_home);
		rad_mine = (RadioButton) findViewById(R.id.rad_main_mine);
		rad_bbs = (RadioButton) findViewById(R.id.rad_main_bbs);
		rad_language_merchant = (RadioButton) findViewById(R.id.rad_main_language_market_merchant);
		rad_language_translator = (RadioButton) findViewById(R.id.rad_main_language_market_translator);
		rad_talent_merchant = (RadioButton) findViewById(R.id.rad_main_talent_merchant);
		rad_talent_translator = (RadioButton) findViewById(R.id.rad_main_talent_translator);
		rad_competition = (RadioButton) findViewById(R.id.rad_main_competition);
		rad_readevery = (RadioButton) findViewById(R.id.rad_main_read_every);

		// 向服务器获取用户信息
user = new User();
user.setType(User.TRANSLATOR);

		// 根据用户类型显示相关的模块
		switch (user.getType()) {
		case (User.MERCHANT):
			rad_language_merchant.setVisibility(View.VISIBLE);
			rad_talent_merchant.setVisibility(View.VISIBLE);
			break;
		case (User.NORMAL):
			rad_competition.setVisibility(View.VISIBLE);
			rad_readevery.setVisibility(View.VISIBLE);
			break;
		case (User.TRANSLATOR):
			rad_language_translator.setVisibility(View.VISIBLE);
			rad_talent_translator.setVisibility(View.VISIBLE);
			rad_competition.setVisibility(View.VISIBLE);
			rad_bbs.setVisibility(View.VISIBLE);
			break;
		default:
			Toast.makeText(this, "类型错误", Toast.LENGTH_SHORT).show();
		}

		// 改变按钮时改变布局
		radg_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedID) {
				switch (checkedID) {
				case(R.id.rad_main_home):
					break;
				case(R.id.rad_main_language_market_merchant):
					break;
				case(R.id.rad_main_language_market_translator):
					break;
				case(R.id.rad_main_talent_merchant):
					break;
				case(R.id.rad_main_talent_translator):
					break;
				case(R.id.rad_main_competition):
					break;
				case(R.id.rad_main_bbs):
					break;
				case(R.id.rad_main_read_every):
					break;
				case(R.id.rad_main_mine):
					break;
				default:
					Toast.makeText(MainActivity.this, "radio类型错误", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
		// 设置选中首页布局
		rad_home.setChecked(true);
	}

	@Override
	public void onBackPressed() {
		// 距离上次按下时间2s的话，就退出程序
		if (System.currentTimeMillis() - lastTime > 2 * 1000) {
			lastTime = System.currentTimeMillis();
			Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT)
					.show();
		} else {
			finish();
		}
	}
}
