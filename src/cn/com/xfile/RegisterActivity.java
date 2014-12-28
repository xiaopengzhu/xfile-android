package cn.com.xfile;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.util.HttpRequest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity{
	private EditText account, password, password2, nickname;
	static ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xfile_register);
		account = (EditText)findViewById(R.id.account);
		password = (EditText)findViewById(R.id.password);
		password2 = (EditText)findViewById(R.id.password2);
		nickname = (EditText)findViewById(R.id.nickname);
	}
	
	class RegisterTask extends AsyncTask<Void, Integer, Integer> {
		private JSONObject response;
		
		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			NameValuePair pair1 = new BasicNameValuePair("account", account.getText().toString());
			NameValuePair pair2 = new BasicNameValuePair("password", password.getText().toString());
			NameValuePair pair3 = new BasicNameValuePair("nickname", nickname.getText().toString());
			list.add(pair1);
			list.add(pair2);
			list.add(pair3);
			
			String uri = "http://www.xpcms.net/mobile.php/member/register";
			response = HttpRequest.post(uri, list);
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (response!=null) {
				try {
					int code = Integer.parseInt(response.getString("code"));
					if (code == 200) {
						Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
	                } else {
	                    Toast.makeText(RegisterActivity.this, "注册失败,请稍后再试", Toast.LENGTH_SHORT).show();
	                }
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            } else {
                Toast.makeText(RegisterActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
            }
			
			finish();
		}
		
	}
	
	public void submit(View v) {
		if (!password.getText().toString().equals(password2.getText().toString())) {
			Toast.makeText(this, "密码二次输入不匹配", Toast.LENGTH_SHORT).show();
		} else {
			new RegisterTask().execute();
		}
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
