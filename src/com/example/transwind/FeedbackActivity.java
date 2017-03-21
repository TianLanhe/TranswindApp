package com.example.transwind;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transwind.httptools.HttpControler;

public class FeedbackActivity extends Activity {

	// UI���
	private TextView txt_length;// ������ʾ�������100����
	private EditText edt_content;
	private Button btn_send;

	private ProgressDialog progress_dialog;
	private TextView txt_title;
	private ImageView img_back;

	// �������
	private String phonenum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		txt_length = (TextView) findViewById(R.id.txt_feedback_length);
		edt_content = (EditText) findViewById(R.id.edt_feedback_content);
		btn_send = (Button) findViewById(R.id.btn_feedback_feedback);
		txt_title = (TextView) findViewById(R.id.txt_header_title);
		img_back = (ImageView) findViewById(R.id.imgv_header_back);

		txt_title.setText("�������");
		// ��ȡ�û��ֻ�����
		phonenum = getSharedPreferences("user", MODE_PRIVATE).getString(
				"phonenum", "");

		// ���ذ�ť
		img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// �������������ʱ�ļ�����
		edt_content.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				int length = edt_content.length();
				txt_length.setText(length + "/" + "100");// ���½�ʵʱ��ʾ����
				// ������ȳ���100�������ȡǰ��100��
				if (edt_content.length() == 101) {
					edt_content.setText(edt_content.getText().toString()
							.substring(0, 100));
					edt_content.setSelection(100);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		});

		// "����"��ť
		btn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String content = edt_content.getText().toString();
				if ("".equals(content)) {
					Toast.makeText(FeedbackActivity.this, "�����뷴���������",
							Toast.LENGTH_SHORT).show();
				} else if ("".equals(phonenum)) {
					Log.e("FeedbackActivity", "Phonenum Error!");
				} else {
					// ��ʱ�����Ի���
					progress_dialog = new ProgressDialog(FeedbackActivity.this);
					progress_dialog.setCancelable(false);
					progress_dialog.setCanceledOnTouchOutside(false);
					progress_dialog.setMessage("���ڷ��ͣ����Ժ�...");
					progress_dialog.show();

					// ���������������
					new Thread(new Runnable() {
						@Override
						public void run() {
							int result_code = HttpControler.sendFeedback(
									phonenum, content);
							Message msg = new Message();
							msg.what = result_code;
							handler.sendMessage(msg);
						}
					}).start();
				}
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// ȡ���ȴ���
			progress_dialog.dismiss();
			switch (msg.what) {
			case HttpControler.INTERNET_ERROR:
				Toast.makeText(FeedbackActivity.this, "���������������",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_ERROR:
				Toast.makeText(FeedbackActivity.this, "�ֻ��Ż������ݴ���",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.DATA_ERROR:
				Toast.makeText(FeedbackActivity.this, "����ʧ��",
						Toast.LENGTH_SHORT).show();
				break;
			case HttpControler.HTTP_RESULT_OK:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FeedbackActivity.this);
				builder.setTitle("���ͳɹ�");
				builder.setMessage("��л���ı������!");
				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								finish();
							}
						});
				AlertDialog dialog = builder.create();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				break;
			default:
				break;
			}
		}
	};
}
