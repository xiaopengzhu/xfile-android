package cn.com.xfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
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

import cn.com.lib.FunctionHelper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddActivity extends Activity{
    //分类类型
    private ArrayList<String> types;
    //当前记录
    private ArrayList<NameValuePair> record;
    //类型URL
    private String typeUrl = "http://www.xpcms.net/mobile.php/api/getTypes/pid/";
    //添加URL
    private String addUrl = "http://www.xpcms.net/mobile.php/api/addRecord";
    //记录URL
    private String recordUrl = "http://www.xpcms.net/mobile.php/api/getRecord";
    //页面控件
    private Spinner type;
    private EditText account, password;
    //登录者ID
    private int mid;
    //Helper
    private FunctionHelper funhelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        
        //记录拉取
        MyApp myapp = (MyApp)getApplication();
        HttpGet get = new HttpGet("http://www.xpcms.net/mobile.php/api/getRecord/id/"+id+"/mid/"+myapp.getData("id"));
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONObject line = new JSONObject(new JSONTokener(str));
            if (line!=null) {
                type.setSelection(1);
                EditText account = (EditText)findViewById(R.id.account);
                account.setText(line.getString("account"));
                EditText password = (EditText)findViewById(R.id.password);
                password.setText(line.getString("password"));
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
        
        //添加
        Button sub_btn = (Button)findViewById(R.id.sub_btn);
        sub_btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                EditText account = (EditText)findViewById(R.id.account);
                EditText password = (EditText)findViewById(R.id.password);
                Spinner type = (Spinner)findViewById(R.id.type);
                
                NameValuePair pair1 = new BasicNameValuePair("account", account.getText().toString());
                NameValuePair pair2 = new BasicNameValuePair("password", password.getText().toString());
                NameValuePair pair3 = new BasicNameValuePair("type", type.getSelectedItem().toString());
                
                MyApp myapp = (MyApp) getApplication();
                NameValuePair pair4 = new BasicNameValuePair("mid", myapp.getData("id").toString());
                
                list.add(pair1);
                list.add(pair2);
                list.add(pair3);
                list.add(pair4);
                try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/api/addRecord");
                    post.setEntity(entity);
                    HttpResponse response = httpclient.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200)
                        Log.v("debug", EntityUtils.toString(response.getEntity()));
                        Toast.makeText(AddActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
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
    
    private void init() {
        //控件获取
        type = (Spinner)findViewById(R.id.type);
        type.setAdapter(getTypes());
        
        account = (EditText)findViewById(R.id.account);
        password = (EditText)findViewById(R.id.password);
    }
    
    private ArrayAdapter<String> getTypes() {
        ArrayAdapter<String> adapter = null;
        Intent intent = getIntent();
        String tid = intent.getStringExtra("tid");
        if (tid != null) {
            typeUrl += tid;
            URI uri;
            try {
                uri = new URI(typeUrl);
                String str = funhelper.getGetData(uri);
                ArrayList<String> list = new ArrayList<String>();
                JSONArray line = new JSONArray(new JSONTokener(str));
                for (int i =0; i<line.length();i++) {
                    String name = line.getJSONObject(i).getString("name");
                    list.add(name);
                }
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return adapter;
    }
    
    private ArrayList<NameValuePair> getRecordData() {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        MyApp myapp = (MyApp)getApplication();
        String mid = myapp.getData("id").toString();
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        NameValuePair pair1 = new BasicNameValuePair("id", id);
        NameValuePair pair2 = new BasicNameValuePair("mid", mid);
        data.add(pair1);
        data.add(pair2);
        funhelper = new FunctionHelper();
        URI uri;
        try {
            uri = new URI(recordUrl);
            String str = funhelper.getPostData(uri, data);
            JSONObject line = new JSONObject(new JSONTokener(str));
            if (line!=null) {
                NameValuePair pair3 = new BasicNameValuePair("account",line.getString("account"));
                account.setText(line.getString("account"));
                EditText password = (EditText)findViewById(R.id.password);
                password.setText(line.getString("password"));
            }
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
