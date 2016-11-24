package com.example.transwind.fragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.transwind.R;
import com.example.transwind.data.Advertisement;
import com.example.transwind.httptools.HttpControler;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

	// UI���
	View view;// ��Ƭ�ܲ���
	LinearLayout lly_spot;// Bannerװ�������
	View view_banner[];// Banner�����Ĳ���
	ImageView img_spot[];// ��
	ViewPager viewpager;
	PullToRefreshScrollView pullrefreshscrollview;

	Activity activity;
	ProgressDialog progress_dialog;

	// �������
	boolean isFirst = true;//�Ƿ��һ�μ�����Ƭ
	boolean isExist = false;//��Ƭ�Ƿ���������
	Advertisement advertisements[];
	int currentItem = 0;//ViewPager��ǰ��item
	Timer timer;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		private int count = 0;// ����ͼƬ��ɵ�advertisement������

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				String content = (String) msg.obj;

				// ��������������ʾˢ��ʧ�ܻ�ӱ��ػ�ȡBanner
				if (content.equals("INTERNET_ERROR")) {
					if (pullrefreshscrollview.isRefreshing()) {
						pullrefreshscrollview.onRefreshComplete();
						Toast.makeText(activity, "ˢ��ʧ�ܣ�����������",
								Toast.LENGTH_SHORT).show();
					} else {
						if (!loadFromFile())
							Log.e("HomeFragment",
									"Load Banner From File Error!");
						// ��Advertisement���õ�ViewPager��
						initSpotAndBanner();

						progress_dialog.dismiss();
					}
				} else {
					try {
						JSONArray jsonarray = new JSONArray(content);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						int result_code = jsonobject.getInt("result_code");

						// ���result_code�����ɹ������ȡJSON���ݣ�������������
						if (result_code == 0) {
							advertisements = new Advertisement[jsonarray
									.length() - 1];
							for (int i = 1; i < jsonarray.length(); ++i) {
								jsonobject = jsonarray.getJSONObject(i);

								Log.d("HomeFragment",
										"Title:"
												+ jsonobject.getString("title"));
								Log.d("HomeFragment",
										"Url:"
												+ jsonobject
														.getString("link_url"));
								Log.d("HomeFragment",
										"Picture:"
												+ jsonobject
														.getString("picture"));

								// ���ù��ı��������
								advertisements[i - 1] = new Advertisement(
										jsonobject.getString("title"),
										jsonobject.getString("link_url"));
								// �������̶߳�ȡ����ͼƬ
								new Thread(new MyRunnable(i - 1,
										jsonobject.getString("picture")))
										.start();
							}
						} else if (result_code == 1)
							Log.e("HomeFragment", "Get Banner Error!");
						else
							Log.e("HomeFragment", "result_code error!");
					} catch (JSONException e) {
						Toast.makeText(activity, "JSON��������", Toast.LENGTH_LONG)
								.show();
					}
				}
				break;
			case 200:
				++count;
				if (count == advertisements.length) {
					count = 0;
					// ��Advertisement���õ�ViewPager��
					initSpotAndBanner();

					// ���浽����
					if (!saveToFile())
						Log.e("HomeFragment", "Save Banner To File Error!");

					if (pullrefreshscrollview.isRefreshing())
						pullrefreshscrollview.onRefreshComplete();
					else
						progress_dialog.dismiss();
				}
				break;
			case 300:
				Log.d("mytag", "currentItem:" + currentItem);
				viewpager
						.setCurrentItem((currentItem + 1) % view_banner.length);
				break;
			default:
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isExist = true;

		// ��һ������ز��֣��ڶ��ο�ʼ�Ͳ����ˣ�ֱ�ӷ��ص�һ�μ��غõ�
		if (isFirst) {
			view = inflater.inflate(R.layout.fragment_home, container, false);
			activity = getActivity();// �����ڹ��캯���е��ã��᷵��null��������fragment��activity������ϵ����ò���Ч

			viewpager = (ViewPager) view.findViewById(R.id.vwp_home_banner);
			lly_spot = (LinearLayout) view.findViewById(R.id.lly_home_spots);
			pullrefreshscrollview = (PullToRefreshScrollView) view
					.findViewById(R.id.pull_refresh_scrollview);

			progress_dialog = new ProgressDialog(activity);
			progress_dialog.setCanceledOnTouchOutside(false);
			progress_dialog.setCancelable(false);

			// ��ʾ�Ի���
			progress_dialog.setMessage("���ڼ��أ����Ժ�...");
			progress_dialog.show();

			// �ӷ�������ȡ�����Ϣ
			new Thread(new Runnable() {
				@Override
				public void run() {
					String result_content = HttpControler.getBanner();
					Message msg = new Message();
					msg.what = 100;
					msg.obj = result_content;
					handler.sendMessage(msg);
				}
			}).start();

			// PullRefreshScrollView����ˢ���¼�
			pullrefreshscrollview
					.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
						@Override
						public void onRefresh(
								PullToRefreshBase<ScrollView> refreshView) {
							// �ӷ�������ȡ�����Ϣ
							new Thread(new Runnable() {
								@Override
								public void run() {
									String result_content = HttpControler
											.getBanner();
									Message msg = new Message();
									msg.what = 100;
									msg.obj = result_content;
									handler.sendMessage(msg);
								}
							}).start();
						}
					});

			// ViewPager���øı��¼�
			viewpager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageScrollStateChanged(int arg0) {
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageSelected(int position) {
					img_spot[currentItem].setEnabled(false);
					img_spot[position].setEnabled(true);
					currentItem = position;
				}
			});

			isFirst = false;
		}
		return view;
	}

	// ��ʼ��Banner���棬����ͼƬ�������С��
	private void initSpotAndBanner() {
		view_banner = new View[advertisements.length];
		img_spot = new ImageView[advertisements.length];
		lly_spot.removeAllViews();
		// ���֮ǰ�ж�ʱ����Ҫ�ǵ�ȡ��
		if (timer != null)
			timer.cancel();
		// ���õ�ǰ��ʾBanner��Item�±�Ϊ0
		currentItem = 0;

		for (int i = 0; i < view_banner.length; ++i) {
			// ����Banner����
			view_banner[i] = LayoutInflater.from(activity).inflate(
					R.layout.viewpager_banner, null);

			// Banner�����¼�
			view_banner[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					for (int i = 0; i < view_banner.length; ++i) {
						if (arg0 == view_banner[i])
							// TODO
							Toast.makeText(activity,
									advertisements[i].getUrl(),
									Toast.LENGTH_SHORT).show();
					}
				}
			});

			// ����Banner��ͼƬ������
			((ImageView) view_banner[i].findViewById(R.id.img_banner_picture))
					.setImageBitmap(advertisements[i].getPicture());
			((TextView) view_banner[i].findViewById(R.id.txt_banner_title))
					.setText(advertisements[i].getTitle());

			// ���õ�
			img_spot[i] = new ImageView(activity);
			img_spot[i].setImageResource(R.drawable.spot);
			if (i != 0)
				img_spot[i].setEnabled(false);

			lly_spot.addView(img_spot[i]);
		}

		// ViewPager����������
		viewpager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return view_banner.length;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg1 == arg0;
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(view_banner[position]);
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(view_banner[position]);
				return view_banner[position];
			}
		});

		// ���ö�ʱ����,ҳ�����ʱ,3���ӻ���һ��
		timer = new Timer();// Timer��cancel������schedule
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isExist) {
					Message msg = new Message();
					msg.what = 300;
					handler.sendMessage(msg);
				}
			}
		}, 3000, 3000);
	}

	// ���ӷ�������ȡ����Advertisement���浽����
	private boolean saveToFile() {
		FileOutputStream out;
		ObjectOutputStream objectout;
		try {
			out = activity.openFileOutput("advertisements",
					Context.MODE_PRIVATE);
			objectout = new ObjectOutputStream(out);

			objectout.writeObject(advertisements);

			objectout.close();
			out.close();
		} catch (Exception exp) {
			return false;
		}
		return true;
	}

	// ������ȡAdvertisement
	private boolean loadFromFile() {
		FileInputStream in;
		ObjectInputStream objectin;
		try {
			in = activity.openFileInput("advertisements");
			objectin = new ObjectInputStream(in);

			advertisements = (Advertisement[]) objectin.readObject();

			in.close();
			objectin.close();
		} catch (Exception exp) {
			return false;
		}
		return true;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		isExist = false;
	}

	// �ڲ��࣬�������߳�ʱ�������
	class MyRunnable implements Runnable {
		private int index;
		private String picture;

		public MyRunnable(int index, String picture) {
			this.index = index;
			this.picture = picture;
		}

		@Override
		public void run() {
			Log.d("HomeFragment", "index:" + index + "  picture:" + picture);
			Bitmap bitmap;
			bitmap = HttpControler.getPicture(picture);
			if (bitmap != null) {
				advertisements[index].setPicture(bitmap);
				Message msg = new Message();
				msg.what = 200;
				handler.sendMessage(msg);
			} else {
				Log.e("HomeFragment", "Loading Picture Error!");
			}
		}
	}
}
