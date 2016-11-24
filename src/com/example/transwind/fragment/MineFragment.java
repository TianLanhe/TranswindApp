package com.example.transwind.fragment;

import com.example.transwind.LoginOrRegistActivity;
import com.example.transwind.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MineFragment extends Fragment {
	TextView txt_login_out;
	Activity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();
		View view = inflater.inflate(R.layout.fragment_mine, container, false);

		txt_login_out = (TextView) view.findViewById(R.id.txt_mine_login_out);

		txt_login_out.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 修改成未登录
				SharedPreferences.Editor editor = activity
						.getSharedPreferences("user", Activity.MODE_PRIVATE)
						.edit();
				editor.putBoolean("hasLogin", false);
				editor.commit();

				// 跳转到登录界面
				Intent intent = new Intent(activity,
						LoginOrRegistActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
		});

		return view;
	}
}
