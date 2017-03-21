package com.example.transwind.fragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.transwind.R;
import com.example.transwind.WebActivity;
import com.example.transwind.data.Advertisement;
import com.example.transwind.data.Book;
import com.example.transwind.httptools.HttpControler;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

@SuppressLint("InflateParams")
public class HomeFragment extends Fragment {

	// UI相关
	View view;// 碎片总布局
	LinearLayout lly_spot;// Banner装点的容器
	LinearLayout lly_books;// 装书的容器
	View view_banner[];// Banner滚动的布局
	ImageView img_spot[];// 点
	ViewPager viewpager;
	PullToRefreshScrollView pullrefreshscrollview;
	ScrollView scrollview;

	Activity activity;
	ProgressDialog progress_dialog;

	// 数据相关
	boolean isFirst = true;// 是否第一次加载碎片
	boolean isExist = false;// 碎片是否正在运行
	int book_num = 0;// 已经获取到的书的数量
	Advertisement advertisements[];
	List<Book> books = new ArrayList<Book>();
	int currentItem = 0;// ViewPager当前的item
	Timer timer;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		private int count_ad = 0;// 加载广告图片完成的计数器
		private int count_all = 0;// 加载Banner和Book是否完成的计数器，只有两件事都完成了，才能将等待框取消
		String content;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				content = (String) msg.obj;

				// 如果网络错误，则显示刷新失败或从本地获取Banner
				if (content.equals("INTERNET_ERROR")) {
					if (pullrefreshscrollview.isRefreshing()) {
						pullrefreshscrollview.onRefreshComplete();
						Toast.makeText(activity, "刷新失败，请连接网络",
								Toast.LENGTH_SHORT).show();
					} else {
						if (!loadAdvertisementsFromFile())
							Log.e("HomeFragment",
									"Load Banner From File Error!");
						// 将Advertisement设置到ViewPager中
						initSpotAndBanner();

						++count_all;
						if (count_all == 2) {
							count_all = 0;
							progress_dialog.dismiss();
							Toast.makeText(activity, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					try {
						JSONArray jsonarray = new JSONArray(content);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						int result_code = jsonobject.getInt("result_code");

						// 如果result_code==0表明成功，则读取JSON数据，储存进广告类中
						// 如果result_code==1，表示参数什么的出错或图书已经读完了
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
								new Thread(new AdvertisementRunnable(i - 1,
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
				++count_ad;
				if (count_ad == advertisements.length) {
					count_ad = 0;
					// 将Advertisement设置到ViewPager中
					initSpotAndBanner();

					// 保存到本地
					if (!saveToFile("advertisements", advertisements))
						Log.e("HomeFragment", "Save Banner To File Error!");

					if (pullrefreshscrollview.isRefreshing())
						pullrefreshscrollview.onRefreshComplete();
					else {
						++count_all;
						if (count_all == 2) {
							count_all = 0;
							progress_dialog.dismiss();
						}
					}
				}
				break;
			case 300:
				viewpager
						.setCurrentItem((currentItem + 1) % view_banner.length);
				break;
			case 400:
				content = (String) msg.obj;

				// 如果网络错误，则显示刷新失败或从本地获取Book
				if (content.equals("INTERNET_ERROR")) {
					if (pullrefreshscrollview.isRefreshing()) {
						pullrefreshscrollview.onRefreshComplete();
						Toast.makeText(activity, "刷新失败，请连接网络",
								Toast.LENGTH_SHORT).show();
					} else {
						if (!loadBooksFromFile())
							Log.e("HomeFragment", "Load Book From File Error!");

						// 将Book添加进去
						addBooks();

						++count_all;
						if (count_all == 2) {
							count_all = 0;
							progress_dialog.dismiss();
							Toast.makeText(activity, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					try {
						JSONArray jsonarray = new JSONArray(content);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						int result_code = jsonobject.getInt("result_code");

						// 如果result_code表明成功，则读取JSON数据，储存进图书类中
						if (result_code == 0) {
							for (int i = 1; i < jsonarray.length(); ++i) {
								jsonobject = jsonarray.getJSONObject(i);

								Log.d("HomeFragment",
										"Name:" + jsonobject.getString("name"));
								Log.d("HomeFragment", "Description:"
										+ jsonobject.getString("description"));
								Log.d("HomeFragment",
										"Picture:"
												+ jsonobject
														.getString("picture"));

								// 设置图书的名字和简短描述
								books.add(new Book(
										jsonobject.getString("name"),
										jsonobject.getString("description")));
								// 开启新线程读取图书的图片，注意要加上book_num
								new Thread(new BookRunnable(i - 1 + book_num,
										jsonobject.getString("picture")))
										.start();
							}
						} else if (result_code == 1){
							Log.e("HomeFragment", "Get Book Error!");
							// 有可能是后台数据库不够图书了
							if (pullrefreshscrollview.isRefreshing())
								pullrefreshscrollview.onRefreshComplete();
						}else
							Log.e("HomeFragment", "result_code error!");
					} catch (JSONException e) {
						Toast.makeText(activity, "JSON解析错误", Toast.LENGTH_LONG)
								.show();
					}
				}
				break;
			case 500:
				++book_num;
				if (book_num == books.size()) {
					// 将图书添加进去
					addBooks();

					// 第一次添加的6本书，则保存到本地
					if (books.size() == 6 && !saveToFile("books", books))
						Log.e("HomeFragment", "Save Books To File Error!");

					if (pullrefreshscrollview.isRefreshing()) {
						pullrefreshscrollview.onRefreshComplete();
						// 如果是刷新添加的，完成后将页面拉到最下面，延迟0.2秒，不然太急了，看起来怪怪的
						scrollview.postDelayed(new Runnable() {
							@Override
							public void run() {
								scrollview.fullScroll(View.FOCUS_DOWN);
							}
						}, 200);
					} else {
						++count_all;
						if (count_all == 2) {
							count_all = 0;
							progress_dialog.dismiss();
						}
					}
				}
				break;
			default:
				Log.e("HomeFragment", "Handle return message error!");
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
			isFirst = false;

			view = inflater.inflate(R.layout.fragment_home, container, false);
			activity = getActivity();// 不能在构造函数中调用，会返回null，必须在fragment与activity建立联系后调用才有效

			viewpager = (ViewPager) view.findViewById(R.id.vwp_home_banner);
			lly_spot = (LinearLayout) view.findViewById(R.id.lly_home_spots);
			lly_books = (LinearLayout) view.findViewById(R.id.lly_home_books);
			pullrefreshscrollview = (PullToRefreshScrollView) view
					.findViewById(R.id.pull_refresh_scrollview);
			scrollview = pullrefreshscrollview.getRefreshableView();
			;

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

			// 从服务器获取图书信息，取6本图书
			new Thread(new Runnable() {
				@Override
				public void run() {
					String result_content = HttpControler.getBook(0, 6);
					Message msg = new Message();
					msg.what = 400;
					msg.obj = result_content;
					handler.sendMessage(msg);
				}
			}).start();

			// PullRefreshScrollView设置刷新事件
			pullrefreshscrollview
					.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
						@Override
						public void onPullDownToRefresh(
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

						@Override
						public void onPullUpToRefresh(
								PullToRefreshBase<ScrollView> refreshView) {
							// 从服务器获取图书信息
							new Thread(new Runnable() {
								@Override
								public void run() {
									String result_content = HttpControler
											.getBook(book_num, 6);
									Message msg = new Message();
									msg.what = 400;
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
						if (arg0 == view_banner[i]) {
							Intent intent = new Intent(activity,
									WebActivity.class);
							intent.putExtra("url", advertisements[i].getUrl());
							startActivity(intent);
						}
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

	// 加载精选阅读界面的图书
	private void addBooks() {
		int child_num = lly_books.getChildCount();// 最开始有一个TextView孩子
		for (int i = 0; i != books.size() / 2 - child_num + 1; ++i) {
			View view = LayoutInflater.from(activity).inflate(
					R.layout.linearlayout_selected_read, null);

			((ImageView) view.findViewById(R.id.img_book_left_picture))
					.setImageBitmap(books.get((i + child_num - 1) * 2)
							.getPicture());
			((TextView) view.findViewById(R.id.txt_book_left_description))
					.setText(books.get((i + child_num - 1) * 2)
							.getDescription());

			// 给每一个imageview带一个整数，该整数设为books的下标值，以此区分不同的imageview
			((ImageView) view.findViewById(R.id.img_book_left_picture))
					.setTag((i + child_num - 1) * 2);
			((ImageView) view.findViewById(R.id.img_book_left_picture))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							int index = (Integer) view.getTag();
							Toast.makeText(activity, "Index=" + index,
									Toast.LENGTH_SHORT).show();
							// TODO
						}
					});

			((ImageView) view.findViewById(R.id.img_book_right_picture))
					.setImageBitmap(books.get((i + child_num - 1) * 2 + 1)
							.getPicture());
			((TextView) view.findViewById(R.id.txt_book_right_description))
					.setText(books.get((i + child_num - 1) * 2 + 1)
							.getDescription());

			// 给每一个imageview带一个整数，该整数设为books的下标值，以此区分不同的imageview
			((ImageView) view.findViewById(R.id.img_book_right_picture))
					.setTag((i + child_num - 1) * 2 + 1);
			((ImageView) view.findViewById(R.id.img_book_right_picture))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							int index = (Integer) view.getTag();
							Toast.makeText(activity, "Index=" + index,
									Toast.LENGTH_SHORT).show();
							// TODO Auto-generated method stub
						}
					});
			lly_books.addView(view);
		}
	}

	// 将从服务器读取到的对象保存到本地
	private boolean saveToFile(String filename, Object obj) {
		FileOutputStream out;
		ObjectOutputStream objectout;
		try {
			out = activity.openFileOutput(filename, Context.MODE_PRIVATE);
			objectout = new ObjectOutputStream(out);

			objectout.writeObject(obj);

			objectout.close();
			out.close();
		} catch (Exception exp) {
			return false;
		}
		return true;
	}

	// 从外存读取Advertisement
	private boolean loadAdvertisementsFromFile() {
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

	// 从外存读取Book
	@SuppressWarnings("unchecked")
	private boolean loadBooksFromFile() {
		FileInputStream in;
		ObjectInputStream objectin;
		try {
			in = activity.openFileInput("books");
			objectin = new ObjectInputStream(in);

			books = (List<Book>) objectin.readObject();

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

	// 内部类，开启新线程时传入参数，读取广告的图片
	class AdvertisementRunnable implements Runnable {
		private int index;
		private String picture;

		public AdvertisementRunnable(int index, String picture) {
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

	// 内部类，开启新线程时传入参数，读取图书的图片
	class BookRunnable implements Runnable {
		private int index;
		private String picture;

		public BookRunnable(int index, String picture) {
			this.index = index;
			this.picture = picture;
		}

		@Override
		public void run() {
			Log.d("HomeFragment", "index:" + index + "  picture:" + picture);
			Bitmap bitmap;
			bitmap = HttpControler.getPicture(picture);
			if (bitmap != null) {
				books.get(index).setPicture(bitmap);
				Message msg = new Message();
				msg.what = 500;
				handler.sendMessage(msg);
			} else {
				Log.e("HomeFragment", "Loading Picture Error!");
			}
		}
	}
}
