package cn.com.xfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

public class ListItemActivity extends Activity{
    private ListView listview;
    private SimpleAdapter simpleadapter;
    private List<HashMap<String, Object>> data;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_list);
        
        //获取转入数据
        Intent intent = getIntent();
        String tid = intent.getStringExtra("tid");
        
        MyApp myapp = (MyApp)getApplication();
        String mid = myapp.getData("id").toString();
        String url = "http://www.xpcms.net/mobile.php/api/getRecords/tid/" + tid + "/mid/" + mid;
        data = getData(url);
        
        simpleadapter = new SimpleAdapter(this, data,
                R.layout.activity_list_list_item,
                new String[]{"id", "account", "password"}, 
                new int[]{R.id.item_id, R.id.item_account, R.id.item_password});
        simpleadapter.setViewBinder(new ViewBinder() {
            
            @Override
            public boolean setViewValue(View view, Object data,
                    String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView iv = (ImageView) view;
                    iv.setImageBitmap((Bitmap)data);
                    return true;
                } else 
                return false;
            }
        });
        
        listview = (ListView)findViewById(R.id.listlistview);
        listview.setAdapter(simpleadapter);

        //添加事件
        Button add_btn = (Button)findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ListItemActivity.this, AddActivity.class);
				startActivity(intent);
			}
		});
        
        //长按删除
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final int index  = arg2;
				// TODO Auto-generated method stub
				TextView tv = (TextView)findViewById(R.id.item_id);
				final String id = tv.getText().toString();
				new AlertDialog.Builder(ListItemActivity.this).setTitle("删除记录").
				setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						List<NameValuePair> list = new ArrayList<NameValuePair>();
						MyApp myapp = (MyApp) getApplication();
		            	NameValuePair pair1 = new BasicNameValuePair("mid", myapp.getData("id").toString());
		            	NameValuePair pair2 = new BasicNameValuePair("id", id);
		            	list.add(pair1);
						list.add(pair2);
						try {
							UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
							DefaultHttpClient httpclient = new DefaultHttpClient();
			                HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/api/delRecord");
			                post.setEntity(entity);
			                HttpResponse response = httpclient.execute(post);
			                if (response.getStatusLine().getStatusCode() == 200) {
			                	
			                	data.remove(index);
			                	simpleadapter.notifyDataSetChanged();
			                	
			                	Toast.makeText(ListItemActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
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
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
				return false;
			}
		});
    }
    
    
    private List<HashMap<String, Object>> getData(String url) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> map = null;
        
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONArray line = new JSONArray(new JSONTokener(str));
            for (int i =0; i<line.length();i++) {
                map = new HashMap<String, Object>();
                map.put("id", line.getJSONObject(i).getString("id"));
                map.put("account", line.getJSONObject(i).getString("account"));
                map.put("password", line.getJSONObject(i).getString("password"));
                list.add(map);
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
