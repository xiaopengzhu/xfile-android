package cn.com.xfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity{
	private EditText account, password, password2;
	private Handler mHandler;
	static ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		setContentView(R.layout.xfile_register);
		account = (EditText)findViewById(R.id.account);
		password = (EditText)findViewById(R.id.password);
		password2 = (EditText)findViewById(R.id.password2);
		mHandler = new Handler();
	}
	
	public void submit(View v) {
		if (!password.getText().toString().equals(password2.getText().toString())) {
			Toast.makeText(this, "密码二次输入不匹配", Toast.LENGTH_SHORT).show();
		} else {
			final List<NameValuePair> list = new ArrayList<NameValuePair>();
			NameValuePair pair1 = new BasicNameValuePair("account", account.getText().toString());
			NameValuePair pair2 = new BasicNameValuePair("password", password.getText().toString());
			list.add(pair1);
			list.add(pair2);
			//异步提交
			progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					final boolean tag = post(list);
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							progressDialog.dismiss();
							if (tag) {
								finish();
								Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "注册失败,请稍后再试", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}).start();
			
		}
	}
	
	public boolean post(List<NameValuePair> list) {
		boolean rs = false;
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
	        DefaultHttpClient httpclient = new DefaultHttpClient();
	        HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/member/register");
	        post.setEntity(entity);
	        HttpResponse response = httpclient.execute(post);
	        if (response.getStatusLine().getStatusCode() == 200) {
                //返回
	        	String  str = EntityUtils.toString(response.getEntity());
	        	if (Integer.parseInt(str) > 0) {
	        		return true;
	        	} else {
	        		return false;
	        	}
            }
		} catch(UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public static String MD5(String str) {
		byte[] hash = null;
		try {
			hash = MessageDigest.getInstance("md5").digest(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for(byte b : hash){
			 if ((b & 0xFF) < 0x10) hex.append("0");
			 hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

}
