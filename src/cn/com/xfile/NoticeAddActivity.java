package cn.com.xfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.com.util.MyApp;
import android.app.Activity;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class NoticeAddActivity extends Activity{
	private AutoCompleteTextView title;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> data = new ArrayList<String>();
	private MyApp myapp;
	private Button sub_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xfile_notice_add);
		myapp = (MyApp) getApplication();
		
		title = (AutoCompleteTextView)findViewById(R.id.title);
		title.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String str = s.toString();
				new AutocompleteTask().execute(str);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		sub_btn = (Button)findViewById(R.id.sub_btn);
		sub_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SubmitTask().execute();
			}
		});
	}
	
	class SubmitTask extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
            
            NameValuePair pair0 = new BasicNameValuePair("title", title.getText().toString());
            NameValuePair pair1 = new BasicNameValuePair("mid", myapp.getData("id").toString());
            
            list.add(pair0);
            list.add(pair1);
            
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
				DefaultHttpClient httpclient = new DefaultHttpClient();
	            HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/notice/add");
	            post.setEntity(entity);
	            HttpResponse response = httpclient.execute(post);
	            return response.getStatusLine().getStatusCode();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == 200) {
				finish();
				Toast.makeText(NoticeAddActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(NoticeAddActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	class AutocompleteTask extends AsyncTask<String, Integer, Integer> {
		JSONArray ary = null;
		
		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			NameValuePair pair0 = new BasicNameValuePair("str", params[0]);
			list.add(pair0);
	        
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
				DefaultHttpClient httpclient = new DefaultHttpClient();
	            HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/notice/autocomplete");
	            post.setEntity(entity);
	            HttpResponse response = httpclient.execute(post);

		        String  str = EntityUtils.toString(response.getEntity());
		        
		        if (!str.equals("null")) {
		        	ary = new JSONArray(new JSONTokener(str)); 
		        	return 1;
		        }
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

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			if (ary!=null) {
				data.clear();
				for (int i=0; i<ary.length(); i++) {
					try {
						JSONObject tmp = (JSONObject) ary.get(i);
						data.add(tmp.getString("title"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.xfile_notice_add_dropdown, data);
				title.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		}
	}

}
