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

import cn.com.util.HttpRequest;
import cn.com.util.MyApp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 添加记录
 */
public class RecordAddActivity extends Activity{
	private String tid;
	private ArrayList<String> data;
	private AutoCompleteTextView title;
	private JSONObject line;
	private String id;
	static  ProgressDialog progressDialog;
	private EditText account, password, remark;
	private TextView titleText, record_id;
	private Button sub_btn;
	private ArrayAdapter<String> adapter;
	private MyApp myapp;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_record_add);
        
        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        id = intent.getStringExtra("id");
        
        sub_btn = (Button)findViewById(R.id.sub_btn);
    	titleText = (TextView)findViewById(R.id.titleText);
    	record_id = (TextView)findViewById(R.id.record_id);
        account = (EditText)findViewById(R.id.account);
        password = (EditText)findViewById(R.id.password);
        remark = (EditText)findViewById(R.id.remark);
        title = (AutoCompleteTextView)findViewById(R.id.title);
        myapp = (MyApp) getApplication();
        
        //异步加载
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        new LoadingTask().execute();
        
        //提交
        sub_btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	new SubmitTask().execute();
            }
        });
        
        //自动提示
        title.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String str = s.toString();
				if (start > 0) {
					new AutocompleteTask().execute(str);
				}
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
        
    }
    
    //输入提示
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
	            HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/record/autocomplete");
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
						data.add(tmp.getString("name"));
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
    
    //异步加载
    class LoadingTask extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//类型列表拉取
			data = getData("http://www.xpcms.net/mobile.php/record/index/pid/"+tid);
	        
			//记录拉取
			if (id!=null) {
		        HttpGet get = new HttpGet("http://www.xpcms.net/mobile.php/record/get/id/"+id+"/token/"+myapp.getData("token"));
		        HttpClient client = new DefaultHttpClient();
		        
				try {
					HttpResponse response = client.execute(get);
			        String  str = EntityUtils.toString(response.getEntity());
					line = new JSONObject(new JSONTokener(str)); 
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
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
	        
            if (line!=null) {//编辑模式
                titleText.setText("编辑");
                try {
                	
                	title.setText(line.getString("title"));
					record_id.setText(line.getString("id"));
					account.setText(line.getString("account"));
	                password.setText(line.getString("password"));
	                remark.setText(line.getString("remark"));
	            	
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
	        progressDialog.dismiss();
		}
    	
    }
    
    //提交异步任务
    class SubmitTask extends AsyncTask<Void, Integer, Integer> {
    	private int code;
    	
		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
            
            NameValuePair pair0 = new BasicNameValuePair("id", id);
            NameValuePair pair1 = new BasicNameValuePair("token", myapp.getData("token").toString());
            NameValuePair pair2 = new BasicNameValuePair("tid", tid);
            NameValuePair pair3 = new BasicNameValuePair("title", title.getText().toString());
            NameValuePair pair5 = new BasicNameValuePair("account", account.getText().toString());
            NameValuePair pair6 = new BasicNameValuePair("password", password.getText().toString());
            NameValuePair pair7 = new BasicNameValuePair("remark", remark.getText().toString());
            
            
            list.add(pair0);
            list.add(pair1);
            list.add(pair2);
            list.add(pair3);
            list.add(pair5);
            list.add(pair6);
            list.add(pair7);
            
            String uri = "http://www.xpcms.net/mobile.php/record/add";
			JSONObject response = HttpRequest.post(uri, list);
            
			try {
				code = response.getInt("code");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            return code;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			if (code == 200) {
				finish();
				Toast.makeText(RecordAddActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RecordAddActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
			}
		}
    	
    }
    
    private ArrayList<String> getData(String url) {
        ArrayList<String> list = new ArrayList<String>();
        
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONArray line = new JSONArray(new JSONTokener(str));
            for (int i =0; i<line.length(); i++) {
                list.add(line.getJSONObject(i).getString("name"));
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
        return list;
        
    }
}
