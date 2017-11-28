package tech.amath.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpUtil {
	public static class HttpResponse<T> {
		Map<String, List<String>> headers;
		T responseBody;

		public Map<String, List<String>> getHeaders() {
			return headers;
		}

		public void setHeaders(Map<String, List<String>> headers) {
			this.headers = headers;
		}

		public T getResponseBody() {
			return responseBody;
		}

		public void setResponseBody(T responseBody) {
			this.responseBody = responseBody;
		}

	}

	public static final String UTF8 = "UTF-8";
	public static final String GBK = "GBK";
	public static final String GB2312 = "GB2312";
	public static final String ISO8859_1 = "ISO8859-1";
	public static final String user_agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36";
	public static int connectTimeout = 60;
	public static int readTimeout = 30;
	public static int writeTimeout = 60;

	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	private static Interceptor getLogInterceptor() {
		// 自带日志拦截器的使用. 可以不传参数,使用jdk自带的日志.
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

			@Override
			public void log(String message) {
				log.debug(message);
			}
		});
		logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
		return logging;
	}

	protected static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(getLogInterceptor())
			.connectTimeout(connectTimeout, TimeUnit.SECONDS).readTimeout(readTimeout, TimeUnit.SECONDS)
			.writeTimeout(writeTimeout, TimeUnit.SECONDS).build();

	private static Headers setHeaders(Map<String, String> headers) {
		Headers.Builder hBuilder = new Headers.Builder();
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				hBuilder.add(entry.getKey(), entry.getValue());
			}
		}
		return hBuilder.build();
	}

	public static String get(String url, Map<String, String> headers) {
		return get(url, null, headers);
	}

	public static String get(String url, Map<String, String> params, Map<String, String> headers) {
		String query = "";
		if (params != null) {
			query = "?";
			for (Map.Entry<String, String> param : params.entrySet()) {
				String key = param.getKey();
				String value = param.getValue();
				query = query + key + "=" + value + "&";
			}
			if (query.endsWith("&")) {
				query = query.substring(0, query.length() - 1);
			}
		}

		String requestUrl = url + query;
		String responseBody = "";
		try {
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}
			Request request = reqBuild.url(requestUrl).build();
			Response response = client.newCall(request).execute();
			responseBody = response.body().string();
			response.close();
		} catch (IOException e) {
			log.error("", e);
		}
		return responseBody;
	}
	
	public static HttpResponse<String> getAndResponse(String url, Map<String, String> params, Map<String, String> headers) {
		HttpResponse<String> resp = new HttpResponse<String>();
		String query = "";
		if (params != null) {
			query = "?";
			for (Map.Entry<String, String> param : params.entrySet()) {
				String key = param.getKey();
				String value = param.getValue();
				query = query + key + "=" + value + "&";
			}
			if (query.endsWith("&")) {
				query = query.substring(0, query.length() - 1);
			}
		}

		String requestUrl = url + query;
		String responseBody = "";
		try {
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}
			Request request = reqBuild.url(requestUrl).build();
			Response response = client.newCall(request).execute();
			responseBody = response.body().string();
			resp.setHeaders(response.headers().toMultimap());
			resp.setResponseBody(responseBody);
			response.close();
		} catch (IOException e) {
			log.error("", e);
		}
		return resp;
	}

	public static String post(String url, String content, Map<String, String> headers) {
		String responseBody = "";
		try {
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}
			MediaType contentType = MediaType.parse("application/json; charset=utf-8");
			RequestBody body = RequestBody.create(contentType, content);
			Request request = reqBuild.url(url).post(body).build();
			Response response = client.newCall(request).execute();
			responseBody = response.body().string();
			response.close();
		} catch (IOException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return responseBody;
	}
	
	public static HttpResponse<String> postAndResponse(String url, String content, Map<String, String> headers) {
		HttpResponse<String> resp = new HttpResponse<String>();
		String responseBody = "";
		try {
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}
			MediaType contentType = MediaType.parse("application/json; charset=utf-8");
			RequestBody body = RequestBody.create(contentType, content);
			Request request = reqBuild.url(url).post(body).build();
			Response response = client.newCall(request).execute();
			responseBody = response.body().string();
			resp.setHeaders(response.headers().toMultimap());
			resp.setResponseBody(responseBody);
			response.close();
		} catch (IOException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return resp;
	}

	public static byte[] post(String url, byte[] content, Map<String, String> headers) {
		byte[] responseBody = null;
		try {
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}
			MediaType contentType = MediaType.parse("application/octet-stream; charset=utf-8");
			RequestBody body = RequestBody.create(contentType, content);
			Request request = reqBuild.url(url).post(body).build();
			Response response = client.newCall(request).execute();
			responseBody = response.body().bytes();
			response.close();
		} catch (IOException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return responseBody;
	}
	public  static HttpResponse<String> postAndResponse(String url, Map<String ,String> params, Map<String,String> headers){
		HttpResponse<String> resp = new HttpResponse<String>();
		try {
			String responseBody = "";
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}
			FormBody.Builder formBodyBuild = new FormBody.Builder();
			if (params != null) {
				for(Entry<String,String> entry:params.entrySet()) {
					formBodyBuild.add(entry.getKey(), entry.getValue());
				}
			}
			RequestBody formBody = formBodyBuild.build();
			Request request = reqBuild.url(url).post(formBody).build();
			Response response = client.newCall(request).execute();
			responseBody = response.body().string();
			resp.setHeaders(response.headers().toMultimap());
			resp.setResponseBody(responseBody);
			response.close();
		} catch (IOException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return resp;
		
	}
	public static String post(String url, Map<String, String> params, Map<String, String> headers) {
		String responseBody = "";
		try {
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}
			FormBody.Builder formBodyBuild = new FormBody.Builder();
			if (params != null) {
				for (Entry<String, String> entry : params.entrySet()) {
					formBodyBuild.add(entry.getKey(), entry.getValue());
				}
			}
			RequestBody formBody = formBodyBuild.build();
			Request request = reqBuild.url(url).post(formBody).build();
			Response response = client.newCall(request).execute();
			responseBody = response.body().string();
			response.close();
		} catch (IOException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return responseBody;
	}

	/**
	 * 
	 * @param url
	 * @param data
	 * @param certPath
	 *            证书路径
	 * @param certPass
	 *            证书密码
	 * @return
	 */
	public static String postSSL(String url, String data, String certPath, String certPass) {
		final okhttp3.MediaType CONTENT_TYPE_FORM = okhttp3.MediaType.parse("application/x-www-form-urlencoded");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(CONTENT_TYPE_FORM, data);
		okhttp3.Request request = new okhttp3.Request.Builder().url(url).post(body).build();

		InputStream inputStream = null;
		try {
			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			inputStream = new FileInputStream(certPath);
			char[] passArray = certPass.toCharArray();
			clientStore.load(inputStream, passArray);

			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init((KeyStore) null);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
				throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
			}
			X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore, passArray);
			KeyManager[] kms = kmf.getKeyManagers();
			SSLContext sslContext = SSLContext.getInstance("TLSv1");

			sslContext.init(kms, null, new SecureRandom());

			okhttp3.OkHttpClient httpsClient = new okhttp3.OkHttpClient().newBuilder()
					.addInterceptor(getLogInterceptor()).connectTimeout(10, TimeUnit.SECONDS)
					.writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
					.sslSocketFactory(sslContext.getSocketFactory(), trustManager).build();

			okhttp3.Response response = httpsClient.newCall(request).execute();

			if (!response.isSuccessful())
				throw new RuntimeException("Unexpected code " + response);

			return response.body().string();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	public static String postFile(String url, Map<String, File> files, Map<String, String> params,
			Map<String, String> headers) {
		String responseBody = "";
		try {
			/* form的分割线,自己定义 */
			String boundary = "xx--------------------------------------------------------------xx";
			MultipartBody.Builder bodyBuild = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM);
			for (Entry<String, File> entry : files.entrySet()) {
				RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),
						entry.getValue());
				bodyBuild.addFormDataPart(entry.getKey(), entry.getValue().getName(), fileBody);
			}
			if (params != null) {
				for (Entry<String, String> entry : params.entrySet()) {
					bodyBuild.addFormDataPart(entry.getKey(), entry.getValue());
				}
			}
			MultipartBody mBody = bodyBuild.build();

			/* 下边的就和post一样了 */
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}

			Request request = reqBuild.url(url).post(mBody).build();
			Response response = client.newCall(request).execute();
			responseBody = response.body().string();
			response.close();
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return responseBody;
	}

	public static boolean downloadFile(String url, String filePath, Map<String, String> headers) {
		boolean rst = false;
		InputStream in = null;
		try {
			Request.Builder reqBuild = new Request.Builder();
			if (headers != null) {
				headers.putIfAbsent("User-Agent", user_agent);
				reqBuild.headers(Headers.of(headers));
			} else {
				reqBuild.addHeader("User-Agent", user_agent);
			}
			Request request = reqBuild.url(url).build();
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				in = response.body().byteStream();
				IOUtils.copy(in, new FileOutputStream(filePath));
				response.close();
				rst = true;
			}
		} catch (IOException e) {
			log.error("", e);
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return rst;
	}

	
}
