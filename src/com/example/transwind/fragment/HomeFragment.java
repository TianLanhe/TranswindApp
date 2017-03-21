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

	// UI���
	View view;// ��Ƭ�ܲ���
	LinearLayout lly_spot;// Bannerװ�������
	LinearLayout lly_books;// װ�������
	View view_banner[];// Banner�����Ĳ���
	ImageView img_spot[];// ��
	ViewPager viewpager;
	PullToRefreshScrollView pullrefreshscrollview;
	ScrollView scrollview;

	Activity activity;
	ProgressDialog progress_dialog;

	// �������
	boolean isFirst = true;// �Ƿ��һ�μ�����Ƭ
	boolean isExist = false;// ��Ƭ�Ƿ���������
	int book_num = 0;// �Ѿ���ȡ�����������
	Advertisement advertisements[];
	List<Book> books = new ArrayList<Book>();
	int currentItem = 0;// ViewPager��ǰ��item
	Timer timer;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		private int count_ad = 0;// ���ع��ͼƬ��ɵļ�����
		private int count_all = 0;// ����Banner��Book�Ƿ���ɵļ�������ֻ�������¶�����ˣ����ܽ��ȴ���ȡ��
		String content;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				content = (String) msg.obj;

				// ��������������ʾˢ��ʧ�ܻ�ӱ��ػ�ȡBanner
				if (content.equals("INTERNET_ERROR")) {
					if (pullrefreshscrollview.isRefreshing()) {
						pullrefreshscrollview.onRefreshComplete();
						Toast.makeText(activity, "ˢ��ʧ�ܣ�����������",
								Toast.LENGTH_SHORT).show();
					} else {
						if (!loadAdvertisementsFromFile())
							Log.e("HomeFragment",
									"Load Banner From File Error!");
						// ��Advertisement���õ�ViewPager��
						initSpotAndBanner();

						++count_all;
						if (count_all == 2) {
							count_all = 0;
							progress_dialog.dismiss();
							Toast.makeText(activity, "���������������", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					try {
						JSONArray jsonarray = new JSONArray(content);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						int result_code = jsonobject.getInt("result_code");

						// ���result_code==0�����ɹ������ȡJSON���ݣ�������������
						// ���result_code==1����ʾ����ʲô�ĳ����ͼ���Ѿ�������
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
								new Thread(new AdvertisementRunnable(i - 1,
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
				++count_ad;
				if (count_ad == advertisements.length) {
					count_ad = 0;
					// ��Advertisement���õ�ViewPager��
					initSpotAndBanner();

					// ���浽����
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

				// ��������������ʾˢ��ʧ�ܻ�ӱ��ػ�ȡBook
				if (content.equals("INTERNET_ERROR")) {
					if (pullrefreshscrollview.isRefreshing()) {
						pullrefreshscrollview.onRefreshComplete();
						Toast.makeText(activity, "ˢ��ʧ�ܣ�����������",
								Toast.LENGTH_SHORT).show();
					} else {
						if (!loadBooksFromFile())
							Log.e("HomeFragment", "Load Book From File Error!");

						// ��Book��ӽ�ȥ
						addBooks();

						++count_all;
						if (count_all == 2) {
							count_all = 0;
							progress_dialog.dismiss();
							Toast.makeText(activity, "���������������", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					try {
						JSONArray jsonarray = new JSONArray(content);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						int result_code = jsonobject.getInt("result_code");

						// ���result_code�����ɹ������ȡJSON���ݣ������ͼ������
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

								// ����ͼ������ֺͼ������
								books.add(new Book(
										jsonobject.getString("name"),
										jsonobject.getString("description")));
								// �������̶߳�ȡͼ���ͼƬ��ע��Ҫ����book_num
								new Thread(new BookRunnable(i - 1 + book_num,
										jsonobject.getString("picture")))
										.start();
							}
						} else if (result_code == 1){
							Log.e("HomeFragment", "Get Book Error!");
							// �п����Ǻ�̨���ݿⲻ��ͼ����
							if (pullrefreshscrollview.isRefreshing())
								pullrefreshscrollview.onRefreshComplete();
						}else
							Log.e("HomeFragment", "result_code error!");
					} catch (JSONException e) {
						Toast.makeText(activity, "JSON��������", Toast.LENGTH_LONG)
								.show();
					}
				}
				break;
			case 500:
				++book_num;
				if (book_num == books.size()) {
					// ��ͼ����ӽ�ȥ
					addBooks();

					// ��һ����ӵ�6���飬�򱣴浽����
					if (books.size() == 6 && !saveToFile("books", books))
						Log.e("HomeFragment", "Save Books To File Error!");

					if (pullrefreshscrollview.isRefreshing()) {
						pullrefreshscrollview.onRefreshComplete();
						// �����ˢ����ӵģ���ɺ�ҳ�����������棬�ӳ�0.2�룬��Ȼ̫���ˣ��������ֵֹ�
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

		// ��һ������ز��֣��ڶ��ο�ʼ�Ͳ����ˣ�ֱ�ӷ��ص�һ�μ��غõ�
		if (isFirst) {
			isFirst = false;

			view = inflater.inflate(R.layout.fragment_home, container, false);
			activity = getActivity();// �����ڹ��캯���е��ã��᷵��null��������fragment��activity������ϵ����ò���Ч

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

			// �ӷ�������ȡͼ����Ϣ��ȡ6��ͼ��
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

			// PullRefreshScrollView����ˢ���¼�
			pullrefreshscrollview
					.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
						@Override
						public void onPullDownToRefresh(
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

						@Override
						public void onPullUpToRefresh(
								PullToRefreshBase<ScrollView> refreshView) {
							// �ӷ�������ȡͼ����Ϣ
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
						if (arg0 == view_banner[i]) {
							Intent intent = new Intent(activity,
									WebActivity.class);
							intent.putExtra("url", advertisements[i].getUrl());
							startActivity(intent);
						}
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

	// ���ؾ�ѡ�Ķ������ͼ��
	private void addBooks() {
		int child_num = lly_books.getChildCount();// �ʼ��һ��TextView����
		for (int i = 0; i != books.size() / 2 - child_num + 1; ++i) {
			View view = LayoutInflater.from(activity).inflate(
					R.layout.linearlayout_selected_read, null);

			((ImageView) view.findViewById(R.id.img_book_left_picture))
					.setImageBitmap(books.get((i + child_num - 1) * 2)
							.getPicture());
			((TextView) view.findViewById(R.id.txt_book_left_description))
					.setText(books.get((i + child_num - 1) * 2)
							.getDescription());

			// ��ÿһ��imageview��һ����������������Ϊbooks���±�ֵ���Դ����ֲ�ͬ��imageview
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

			// ��ÿһ��imageview��һ����������������Ϊbooks���±�ֵ���Դ����ֲ�ͬ��imageview
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

	// ���ӷ�������ȡ���Ķ��󱣴浽����
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

	// ������ȡAdvertisement
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

	// ������ȡBook
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

	// �ڲ��࣬�������߳�ʱ�����������ȡ����ͼƬ
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

	// �ڲ��࣬�������߳�ʱ�����������ȡͼ���ͼƬ
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
