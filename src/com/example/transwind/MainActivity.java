package com.example.transwind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.transwind.data.User;
import com.example.transwind.fragment.HomeFragment;
import com.example.transwind.fragment.MineFragment;
import com.example.transwind.httptools.HttpControler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {

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

	private ProgressDialog progress_dialog;

	private HomeFragment frg_home;

	// 数据相关
	private User user;
	private String phonenum;
	private int type = 0;
	private long lastTime = 0;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				progress_dialog.dismiss();
				String content = (String) msg.obj;
				// 如果网络错误，则从本地获取用户类型
				if (content.equals("INTERNET_ERROR")) {
					type = getSharedPreferences("user", MODE_PRIVATE).getInt(
							"type", -1);
					Log.d("MainActivity", "type:" + type);
					if (type != 0 && type != 1 && type != 2)
						Log.e("MainActivity", "Type error!");
				} else {
					try {
						// JSON解析
						JSONArray jsonarray;
						jsonarray = new JSONArray(content);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						int result_code = jsonobject.getInt("result_code");
						// 获取类型成功，保存类型信息到本地
						if (result_code == 0) {
							type = jsonobject.getInt("type");
							Log.d("MainActivity", "type:" + type);

							// 保存相关配置
							SharedPreferences.Editor editor = getSharedPreferences(
									"user", MODE_PRIVATE).edit();
							editor.putInt("type", type);
							editor.commit();
						} else if (result_code == 1)
							Log.e("MainActivity", "Phonenum error!");
						else
							Log.e("MainActivity", "result_code error!");
					} catch (JSONException e) {
						Toast.makeText(MainActivity.this, "JSON解析错误",
								Toast.LENGTH_LONG).show();
					}
				}
				// 设置下面的栏
				setTab(type);
				// 设置选中"首页"栏目
				rad_home.setChecked(true);
				break;
			default:
				break;
			}
		}
	};

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

		progress_dialog = new ProgressDialog(this);
		progress_dialog.setCanceledOnTouchOutside(false);
		progress_dialog.setCancelable(false);

		// 初始化碎片类
		frg_home = new HomeFragment();

		// 获取手机号
		phonenum = getSharedPreferences("user", MODE_PRIVATE).getString(
				"phonenum", "");
		if (phonenum.equals(""))
			Log.e("MainActivity", "phonenum get error!");

		// 显示对话框
		progress_dialog.setMessage("正在读取用户类型，请稍候...");
		progress_dialog.show();

		// 根据手机号获取用户类型
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result_content = HttpControler.getType(phonenum);
				Message msg = new Message();
				msg.what = 100;
				msg.obj = result_content;
				handler.sendMessage(msg);
			}
		}).start();

		// 改变按钮时改变布局
		radg_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedID) {
				switch (checkedID) {
				case (R.id.rad_main_home):
					getFragmentManager().beginTransaction()
							.replace(R.id.fly_main_view, frg_home).commit();
					/*
					 * if (isFirst_home) { isFirst_home = false; initHome(); }
					 */
					break;
				case (R.id.rad_main_language_market_merchant):
					break;
				case (R.id.rad_main_language_market_translator):
					break;
				case (R.id.rad_main_talent_merchant):
					break;
				case (R.id.rad_main_talent_translator):
					break;
				case (R.id.rad_main_competition):
					break;
				case (R.id.rad_main_bbs):
					break;
				case (R.id.rad_main_read_every):
					break;
				case (R.id.rad_main_mine):
					getFragmentManager().beginTransaction()
							.replace(R.id.fly_main_view, new MineFragment())
							.commit();
					/*
					 * if (isFirst_mine) { isFirst_home = false; initMine(); }
					 */
					break;
				default:
					Toast.makeText(MainActivity.this, "radio类型错误",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
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

	// 根据用户类型显示相关的模块
	private void setTab(int type) {
		switch (type) {
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
	}
}
