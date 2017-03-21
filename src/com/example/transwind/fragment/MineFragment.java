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

	// UI���
	View view;// ��Ƭ�ܲ���
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

	// �������
	boolean isFirst = true;
	User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (isFirst) {
			isFirst = false;

			activity = getActivity();// �����ڹ��캯���е��ã��᷵��null��������fragment��activity������ϵ����ò���Ч
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

			// ��ʱ�����Ի���
			progress_dialog = new ProgressDialog(activity);
			progress_dialog.setCancelable(false);
			progress_dialog.setCanceledOnTouchOutside(false);

			// "�˳���¼"��ť
			txt_login_out.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// �޸ĳ�δ��¼
					SharedPreferences.Editor editor = activity
							.getSharedPreferences("user", Activity.MODE_PRIVATE)
							.edit();
					editor.putBoolean("hasLogin", false);
					editor.commit();

					// ��ת����¼����
					Intent intent = new Intent(activity,
							LoginOrRegistActivity.class);
					activity.startActivity(intent);
					activity.finish();
				}
			});

			// "�������"��ť
			txt_feedback.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(activity, FeedbackActivity.class);
					activity.startActivity(intent);
				}
			});

			// "�޸�����"��ť
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
