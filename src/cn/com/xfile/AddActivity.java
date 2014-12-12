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


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 添加记录
 * @author Administrator
 *
 */
public class AddActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent = getIntent();
        String tid = intent.getStringExtra("tid");
        String id = intent.getStringExtra("id");
        Spinner type = (Spinner)findViewById(R.id.type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getData("http://www.xpcms.net/mobile.php/api/getTypes/pid/" + tid));
        adapter.setDropDownViewResource(R.layout.activity_add_spinner_item);
        type.setAdapter(adapter);
        
        
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
                        Toast.makeText(AddActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        //返回
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
