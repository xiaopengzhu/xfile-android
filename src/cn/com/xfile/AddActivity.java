package cn.com.xfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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










import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 添加记录
 * @author Administrator
 *
 */
public class AddActivity extends Activity{
	private String tid;
	private ArrayList<String> data;
	private Handler mHandler;
	private Spinner type;
	private JSONObject line;
	private String id;
	static ProgressDialog progressDialog;
	private EditText account, password,remark;
	private TextView titleText, record_id;
	private MyApp myapp;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        id = intent.getStringExtra("id");
        type = (Spinner)findViewById(R.id.type);
        mHandler = new Handler();
        Button sub_btn = (Button)findViewById(R.id.sub_btn);
    	titleText = (TextView)findViewById(R.id.titleText);
    	record_id = (TextView)findViewById(R.id.record_id);
        account = (EditText)findViewById(R.id.account);
        password = (EditText)findViewById(R.id.password);
        remark = (EditText)findViewById(R.id.remark);
        myapp = (MyApp) getApplication();
        
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        
        //异步加载
        Runnable ajax = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//类型列表拉取
				data = getData("http://www.xpcms.net/mobile.php/api/getTypes/pid/"+tid);
		        
				//记录拉取
				if (id!=null) {
			        MyApp myapp = (MyApp)getApplication();
			        HttpGet get = new HttpGet("http://www.xpcms.net/mobile.php/api/getRecord/id/"+id+"/mid/"+myapp.getData("id"));
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
		    
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, data);
				        adapter.setDropDownViewResource(R.layout.activity_add_spinner_item);
				        type.setAdapter(adapter);
			            			            
			            if (line!=null) {//编辑模式
			                titleText.setText("编辑");
			                try {
								record_id.setText(line.getString("id"));
								account.setText(line.getString("account"));
				                password.setText(line.getString("password"));
				                remark.setText(line.getString("remark"));
				                String type_name = line.getString("type");
				                //下拉选中
				            	int index = Arrays.binarySearch(data.toArray(), type_name);
				            	if (index >= 0) {
				            		type.setSelection(index);
				            	}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            }
				        progressDialog.dismiss();
					}
				});
			}
		};
        new Thread(ajax).start();
        
        //提交
        sub_btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                
                NameValuePair pair0 = new BasicNameValuePair("id", record_id.getText().toString());
                NameValuePair pair1 = new BasicNameValuePair("account", account.getText().toString());
                NameValuePair pair2 = new BasicNameValuePair("password", password.getText().toString());
                NameValuePair pair3 = new BasicNameValuePair("type", type.getSelectedItem().toString());
                NameValuePair pair4 = new BasicNameValuePair("remark", remark.getText().toString());
                NameValuePair pair5 = new BasicNameValuePair("mid", myapp.getData("id").toString());
                
                list.add(pair0);
                list.add(pair1);
                list.add(pair2);
                list.add(pair3);
                list.add(pair4);
                list.add(pair5);
                
                try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/api/addRecord");
                    post.setEntity(entity);
                    HttpResponse response = httpclient.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        //返回
	                    finish();
	                    Toast.makeText(AddActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
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
                }
            }
        });
    }
    
    private ArrayList<String> getData(String url) {
        ArrayList<String> list = new ArrayList<String>();
        
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONArray line = new JSONArray(new JSONTokener(str));
            for (int i =0; i<line.length();i++) {
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
