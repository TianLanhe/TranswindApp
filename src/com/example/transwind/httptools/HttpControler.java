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

	public static final int INTERNET_ERROR = 1; // �������
	public static final int HTTP_RESULT_OK = 0; // ����������OK
	public static final int HTTP_RESULT_ERROR = -1; // ����������ERROR
	public static final int DATA_ERROR = 2; // ���ݴ������
	private static final int OK = 0;
	private static final int ERROR = -1;

	// ����·����(������Э��ͷ������)���򿪲�����HTTP����
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

	// ����POST����
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

	// ��������ύ������JSON����
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

	// �����˺����룬��¼�����ؽ������
	public static int login(String phonenum, String password) {
		// ������ַ
		HttpURLConnection connection = connectURL("/transwind/app/login.php");
		if (connection == null)
			return INTERNET_ERROR;

		// ����POST����
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

		// ���󲢴����ؽ��
		try {
			// ��÷�������
			String content = getJSONString(connection);
			if (content == null)
				return INTERNET_ERROR;

			// JSON����
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

	// �����˺š����롢�û����͡���֤�룬ע�ᣬ���ؽ������
	public static int regist(String phonenum, String password, String type) {
		// ������ַ
		HttpURLConnection connection = connectURL("/transwind/app/regist.php");
		if (connection == null)
			return INTERNET_ERROR;

		// ���û�����ת������Ҫ�ĸ�ʽ
		if (type.equals("�̻�"))
			type = "0";
		else
			type = "1";

		// ����POST����
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

		// ���󲢴����ؽ��
		try {
			// ��÷�������
			String content = getJSONString(connection);
			if (content == null)
				return INTERNET_ERROR;

			// JSON����
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

	// �����ֻ�����֤�룬�����һأ����ؽ������
	public static int findback(String phonenum) {
		// ������ַ
		HttpURLConnection connection = connectURL("/transwind/app/findback.php");
		if (connection == null)
			return INTERNET_ERROR;

		// ����POST����
		String str = null;
		try {
			str = "phonenum=" + URLEncoder.encode(phonenum, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		int result = setPost(connection, str);
		if (result == ERROR)
			return INTERNET_ERROR;

		// ���󲢴����ؽ��
		try {
			// ��÷�������
			String content = getJSONString(connection);
			if (content == null)
				return INTERNET_ERROR;

			// JSON����
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

	// �����˺����룬�������룬���ؽ������
	public static int reset(String phonenum, String password) {
		// ������ַ
		HttpURLConnection connection = connectURL("/transwind/app/reset.php");
		if (connection == null)
			return INTERNET_ERROR;

		// ����POST����
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

		// ���󲢴����ؽ��
		try {
			// ��÷�������
			String content = getJSONString(connection);
			if (content == null)
				return INTERNET_ERROR;

			// JSON����
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
