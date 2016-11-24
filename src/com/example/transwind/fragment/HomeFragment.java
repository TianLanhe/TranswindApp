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

	// UI相关
	View view;// 碎片总布局
	LinearLayout lly_spot;// Banner装点的容器
	View view_banner[];// Banner滚动的布局
	ImageView img_spot[];// 点
	ViewPager viewpager;
	PullToRefreshScrollView pullrefreshscrollview;

	Activity activity;
	ProgressDialog progress_dialog;

	// 数据相关
	boolean isFirst = true;//是否第一次加载碎片
	boolean isExist = false;//碎片是否正在运行
	Advertisement advertisements[];
	int currentItem = 0;//ViewPager当前的item
	Timer timer;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		private int count = 0;// 加载图片完成的advertisement计数器

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				String content = (String) msg.obj;

				// 如果网络错误，则显示刷新失败或从本地获取Banner
				if (content.equals("INTERNET_ERROR")) {
					if (pullrefreshscrollview.isRefreshing()) {
						pullrefreshscrollview.onRefreshComplete();
						Toast.makeText(activity, "刷新失败，请连接网络",
								Toast.LENGTH_SHORT).show();
					} else {
						if (!loadFromFile())
							Log.e("HomeFragment",
									"Load Banner From File Error!");
						// 将Advertisement设置到ViewPager中
						initSpotAndBanner();

						progress_dialog.dismiss();
					}
				} else {
					try {
						JSONArray jsonarray = new JSONArray(content);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						int result_code = jsonobject.getInt("result_code");

						// 如果result_code表明成功，则读取JSON数据，储存进广告类中
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

								// 设置广告的标题和链接
								advertisements[i - 1] = new Advertisement(
										jsonobject.getString("title"),
										jsonobject.getString("link_url"));
								// 开启新线程读取广告的图片
								new Thread(new MyRunnable(i - 1,
										jsonobject.getString("picture")))
										.start();
							}
						} else if (result_code == 1)
							Log.e("HomeFragment", "Get Banner Error!");
						else
							Log.e("HomeFragment", "result_code error!");
					} catch (JSONException e) {
						Toast.makeText(activity, "JSON解析错误", Toast.LENGTH_LONG)
								.show();
					}
				}
				break;
			case 200:
				++count;
				if (count == advertisements.length) {
					count = 0;
					// 将Advertisement设置到ViewPager中
					initSpotAndBanner();

					// 保存到本地
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

		// 第一次则加载布局，第二次开始就不用了，直接返回第一次加载好的
		if (isFirst) {
			view = inflater.inflate(R.layout.fragment_home, container, false);
			activity = getActivity();// 不能在构造函数中调用，会返回null，必须在fragment与activity建立联系后调用才有效

			viewpager = (ViewPager) view.findViewById(R.id.vwp_home_banner);
			lly_spot = (LinearLayout) view.findViewById(R.id.lly_home_spots);
			pullrefreshscrollview = (PullToRefreshScrollView) view
					.findViewById(R.id.pull_refresh_scrollview);

			progress_dialog = new ProgressDialog(activity);
			progress_dialog.setCanceledOnTouchOutside(false);
			progress_dialog.setCancelable(false);

			// 显示对话框
			progress_dialog.setMessage("正在加载，请稍候...");
			progress_dialog.show();

			// 从服务器获取广告信息
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

			// PullRefreshScrollView设置刷新事件
			pullrefreshscrollview
					.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
						@Override
						public void onRefresh(
								PullToRefreshBase<ScrollView> refreshView) {
							// 从服务器获取广告信息
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

			// ViewPager设置改变事件
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

	// 初始化Banner界面，加载图片，标题和小点
	private void initSpotAndBanner() {
		view_banner = new View[advertisements.length];
		img_spot = new ImageView[advertisements.length];
		lly_spot.removeAllViews();
		// 如果之前有定时任务，要记得取消
		if (timer != null)
			timer.cancel();
		// 设置当前显示Banner的Item下标为0
		currentItem = 0;

		for (int i = 0; i < view_banner.length; ++i) {
			// 加载Banner布局
			view_banner[i] = LayoutInflater.from(activity).inflate(
					R.layout.viewpager_banner, null);

			// Banner单击事件
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

			// 设置Banner的图片和文字
			((ImageView) view_banner[i].findViewById(R.id.img_banner_picture))
					.setImageBitmap(advertisements[i].getPicture());
			((TextView) view_banner[i].findViewById(R.id.txt_banner_title))
					.setText(advertisements[i].getTitle());

			// 设置点
			img_spot[i] = new ImageView(activity);
			img_spot[i].setImageResource(R.drawable.spot);
			if (i != 0)
				img_spot[i].setEnabled(false);

			lly_spot.addView(img_spot[i]);
		}

		// ViewPager设置适配器
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

		// 设置定时滑动,页面存在时,3秒钟滑动一次
		timer = new Timer();// Timer在cancel后不能再schedule
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

	// 将从服务器读取到的Advertisement保存到本地
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

	// 从外存读取Advertisement
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

	// 内部类，开启新线程时传入参数
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
