package cn.com.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class HttpRequest {
	public static JSONObject post(String uri, List<NameValuePair> list) {
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
			DefaultHttpClient httpclient = new DefaultHttpClient();
	        HttpPost post = new HttpPost(uri);
	        post.setEntity(entity);
	        HttpResponse response = httpclient.execute(post);
	        if (response.getStatusLine().getStatusCode() == 200) {
	        	String str = EntityUtils.toString(response.getEntity());
	        	JSONObject obj = new JSONObject(new JSONTokener(str));
	        	return obj;
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject get(String uri) {
		HttpGet get = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(get);
			String str = EntityUtils.toString(response.getEntity());
        	JSONObject obj = new JSONObject(new JSONTokener(str));
        	return obj;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return null;
	}
}
