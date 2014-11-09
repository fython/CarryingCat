package cn.fython.carryingcat.support;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class Utility {

	public static boolean isLandscape(Activity activity) {
		Configuration c = activity.getResources().getConfiguration();
		if (c.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		} else {
			return false;
		}
	}

	public static String sendHttpMessage(String url, String method, String contents) {
		try {
			URL serverUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setConnectTimeout(20000);
			conn.setRequestMethod(method);
			// if (null != refer) conn.addRequestProperty("Referer", refer);

			conn.addRequestProperty("Connection", "Keep-Alive");
			conn.addRequestProperty("Accept-Language", "zh-cn");
			conn.addRequestProperty("Accept-Encoding", "gzip, deflate");
			conn.addRequestProperty("Cache-Control", "no-cache");
			conn.addRequestProperty("Accept-Charset", "UTF-8;");
			conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
			if (method.equalsIgnoreCase("GET")) {
				conn.connect();
			}
			else if (method.equalsIgnoreCase("POST")) {
				conn.setDoOutput(true);
				conn.connect();
				conn.getOutputStream().write(contents.getBytes());
			}
			else throw new RuntimeException("your method is not implement");

			InputStream ins =  conn.getInputStream();

			//处理GZIP压缩的
			if (null != conn.getHeaderField("Content-Encoding")
					&& conn.getHeaderField("Content-Encoding").equals("gzip")) {
				byte[] b = null;
				GZIPInputStream gzip = new GZIPInputStream(ins);
				byte[] buf = new byte[1024*8];
				int num = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((num = gzip.read(buf, 0, buf.length)) != -1) {
					baos.write(buf, 0, num);
				}
				b = baos.toByteArray();
				baos.flush();
				baos.close();
				gzip.close();
				ins.close();
				return new String(b).trim();
			}

			String charset = "UTF-8";
			InputStreamReader inr = new InputStreamReader(ins, charset);
			BufferedReader br = new BufferedReader(inr);

			String line = "";
			StringBuffer sb = new StringBuffer();
			do {
				sb.append(line);
				line = br.readLine();
			}
			while(line != null);

			return sb.toString();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] decryptBase64(String key) {
		return Base64.decode(key, Base64.DEFAULT);
	}

	public static String encryptBase64(byte[] key) {
		return Base64.encodeToString(key, Base64.DEFAULT);
	}

}
