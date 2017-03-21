package com.example.transwind.fragment;

import com.example.transwind.FeedbackActivity;
import com.example.transwind.LoginOrRegistActivity;
import com.example.transwind.ModifyPasswordActivity;
import com.example.transwind.R;
import com.example.transwind.data.User;
import com.example.transwind.github.CircleImageView;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MineFragment extends Fragment {

	// UI相关
	View view;// 碎片总布局
	TextView txt_login_out;
	TextView txt_modify_password;
	TextView txt_user_name;
	TextView txt_user_info;
	TextView txt_feedback;
	TextView txt_share;
	TextView txt_about;
	CircleImageView cimg_icon;

	Activity activity;
	ProgressDialog progress_dialog;

	// 数据相关
	boolean isFirst = true;
	User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (isFirst) {
			isFirst = false;

			activity = getActivity();// 不能在构造函数中调用，会返回null，必须在fragment与activity建立联系后调用才有效
			view = inflater.inflate(R.layout.fragment_mine, container, false);

			cimg_icon = (CircleImageView) view
					.findViewById(R.id.cimg_mine_icon);
			txt_user_info = (TextView) view.findViewById(R.id.txt_mine_info);
			txt_user_name = (TextView) view
					.findViewById(R.id.txt_mine_user_name);
			txt_about = (TextView) view.findViewById(R.id.txt_mine_about);
			txt_feedback = (TextView) view.findViewById(R.id.txt_mine_feedback);
			txt_login_out = (TextView) view
					.findViewById(R.id.txt_mine_login_out);
			txt_modify_password = (TextView) view
					.findViewById(R.id.txt_mine_modify_password);
			txt_share = (TextView) view.findViewById(R.id.txt_mine_share);

			// 耗时操作对话框
			progress_dialog = new ProgressDialog(activity);
			progress_dialog.setCancelable(false);
			progress_dialog.setCanceledOnTouchOutside(false);

			// "退出登录"按钮
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

			// "意见反馈"按钮
			txt_feedback.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(activity, FeedbackActivity.class);
					activity.startActivity(intent);
				}
			});

			// "修改密码"按钮
			txt_modify_password.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(activity,
							ModifyPasswordActivity.class);
					activity.startActivity(intent);
				}
			});
		}
		return view;
	}
}
