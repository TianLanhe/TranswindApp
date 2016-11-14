package com.example.transwind.httptools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpControler {
	private static String DOMAIN_NAME = "http://192.168.1.101";
	private static final int TIMEOUT = 8000;

	public static final int INTERNET_ERROR = 1; // 网络错误
	public static final int HTTP_RESULT_OK = 0; // 服务器返回OK
	public static final int HTTP_RESULT_ERROR = -1; // 服务器返回ERROR
	public static final int DATA_ERROR = 2; // 数据处理错误
	private static final int OK = 0;
	private static final int ERROR = -1;

	// 传入路径名(不包含协议头和域名)，打开并设置HTTP连接
	private static HttpURLConnection connectURL(String str_url) {
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(DOMAIN_NAME + str_url);
			connection = (HttpURLConnection) url.openConnection();
			Log.d("HttpControler", "URL:" + url.toString());

			connection.setRequestMethod("POST");
			connection.setReadTimeout(TIMEOUT);
			connection.setConnectTimeout(TIMEOUT);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
		} catch (IOException e) {
			return null;
		}
		return connection;
	}

	// 发送POST数据
	private static int setPost(HttpURLConnection connection, String str) {
		try {
			OutputStreamWriter writer;
			writer = new OutputStreamWriter(connection.getOutputStream());
			Log.d("HttpControler", "post:" + str);
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			return ERROR;
		}
		return OK;
	}

	// 向服务器提交并返回JSON数据
	private static String getJSONString(HttpURLConnection connection) {
		String content;
		try {
			if (connection.getResponseCode() == 200) {
				InputStreamReader in = new InputStreamReader(
						connection.getInputStream());
				BufferedReader reader = new BufferedReader(in);
				content = new String();
				String line;
				while ((line = reader.readLine()) != null)
					content += line;
				Log.d("HttpControler", "content:" + content);
			} else
				return null;
		} catch (IOException e) {
			return null;
		}
		return content;
	}

	// 传入账号密码，登录，返回结果代码
	public static int login(String phonenum, String password) {
		// 连接网址
		HttpURLConnection connection = connectURL("/transwind/app/login.php");
		if (connection == null)
			return INTERNET_ERROR;

		// 发送POST数据
		String str = null;
		try {
			str = "phonenum=" + URLEncoder.encode(phonenum, "utf-8")
					+ "&password=" + URLEncoder.encode(password, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		int result = setPost(connection, str);
		if (result == ERROR)
			return INTERNET_ERROR;

		// 请求并处理返回结果
		try {
			// 获得返回数据
			String content = getJSONString(connection);
			if (content == null)
				return INTERNET_ERROR;

			// JSON解析
			JSONArray jsonarray = new JSONArray(content);
			JSONObject jsonobject = jsonarray.getJSONObject(0);
			int result_code = jsonobject.getInt("result_code");
			if (result_code == 0)
				return HTTP_RESULT_OK;
			else if (result_code == 1)
				return HTTP_RESULT_ERROR;
			else
				Log.e("HttpControler", "result_code error!");
		} catch (JSONException e) {
			return DATA_ERROR;
		}
		return HTTP_RESULT_OK;
	}

	// 传入账号、密码、用户类型、验证码，注册，返回结果代码
	public static int regist(String phonenum, String password, String type) {
		// 连接网址
		HttpURLConnection connection = connectURL("/transwind/app/regist.php");
		if (connection == null)
			return INTERNET_ERROR;

		// 将用户类型转换成需要的格式
		if (type.equals("商户"))
			type = "0";
		else
			type = "1";

		// 发送POST数据
		String str = null;
		try {
			str = "phonenum=" + URLEncoder.encode(phonenum, "utf-8")
					+ "&password=" + URLEncoder.encode(password, "utf-8")
					+ "&type=" + URLEncoder.encode(type, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		int result = setPost(connection, str);
		if (result == ERROR)
			return INTERNET_ERROR;

		// 请求并处理返回结果
		try {
			// 获得返回数据
			String content = getJSONString(connection);
			if (content == null)
				return INTERNET_ERROR;

			// JSON解析
			JSONArray jsonarray = new JSONArray(content);
			JSONObject jsonobject = jsonarray.getJSONObject(0);
			int result_code = jsonobject.getInt("result_code");
			if (result_code == 0)
				return HTTP_RESULT_OK;
			else if (result_code == 1)
				return HTTP_RESULT_ERROR;
			else
				Log.e("HttpControler", "result_code error!");
		} catch (JSONException e) {
			return DATA_ERROR;
		}
		return HTTP_RESULT_OK;
	}

	// 传入手机和验证码，密码找回，返回结果代码
	public static int findback(String phonenum) {
		// 连接网址
		HttpURLConnection connection = connectURL("/transwind/app/findback.php");
		if (connection == null)
			return INTERNET_ERROR;

		// 发送POST数据
		String str = null;
		try {
			str = "phonenum=" + URLEncoder.encode(phonenum, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		int result = setPost(connection, str);
		if (result == ERROR)
			return INTERNET_ERROR;

		// 请求并处理返回结果
		try {
			// 获得返回数据
			String content = getJSONString(connection);
			if (content == null)
				return INTERNET_ERROR;

			// JSON解析
			JSONArray jsonarray = new JSONArray(content);
			JSONObject jsonobject = jsonarray.getJSONObject(0);
			int result_code = jsonobject.getInt("result_code");
			if (result_code == 0)
				return HTTP_RESULT_OK;
			else if (result_code == 1)
				return HTTP_RESULT_ERROR;
			else
				Log.e("HttpControler", "result_code error!");
		} catch (JSONException e) {
			return DATA_ERROR;
		}
		return HTTP_RESULT_OK;
	}

	// 传入账号密码，重置密码，返回结果代码
	public static int reset(String phonenum, String password) {
		// 连接网址
		HttpURLConnection connection = connectURL("/transwind/app/reset.php");
		if (connection == null)
			return INTERNET_ERROR;

		// 发送POST数据
		String str = null;
		try {
			str = "phonenum=" + URLEncoder.encode(phonenum, "utf-8")
					+ "&password=" + URLEncoder.encode(password, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		int result = setPost(connection, str);
		if (result == ERROR)
			return INTERNET_ERROR;

		// 请求并处理返回结果
		try {
			// 获得返回数据
			String content = getJSONString(connection);
			if (content == null)
				return INTERNET_ERROR;

			// JSON解析
			JSONArray jsonarray = new JSONArray(content);
			JSONObject jsonobject = jsonarray.getJSONObject(0);
			int result_code = jsonobject.getInt("result_code");
			if (result_code == 0)
				return HTTP_RESULT_OK;
			else if (result_code == 1)
				return HTTP_RESULT_ERROR;
			else
				Log.e("HttpControler", "result_code error!");
		} catch (JSONException e) {
			return DATA_ERROR;
		}
		return HTTP_RESULT_OK;
	}
}
