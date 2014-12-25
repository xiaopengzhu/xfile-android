package cn.com.xfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import cn.com.util.MyApp;
import cn.com.util.Tools;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class NoticeActivity  extends Activity{
	static ProgressDialog progressDialog;
	private ListView listview;
	private List<HashMap<String, Object>> data;
	private SimpleAdapter simpleadapter;
	private MyApp myapp;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_notice);
        myapp = (MyApp) getApplication();
        
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        listview = (ListView)findViewById(R.id.listview);
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
                // TODO Auto-generated method stub
				
				new AlertDialog.Builder(NoticeActivity.this).setTitle("删除记录").
	                setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                    
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        // TODO Auto-generated method stub
	                    	new DeleteTask().execute(position);
	                    }
	                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
	                    
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        // TODO Auto-generated method stub
	                        
	                    }
	                }).show();
                
                //返回true则不会再次触发ItemClick
                return true;
			}
		});
        
        new LoadingTask().execute();
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new LoadingTask().execute();
	}

	public void add(View v) {
    	Intent intent = new Intent(NoticeActivity.this, NoticeAddActivity.class);
    	startActivity(intent);
    }
    
    class DeleteTask extends AsyncTask<Integer, Integer, Integer> {
    	private int index;
    	
		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
            NameValuePair pair1 = new BasicNameValuePair("id", data.get(params[0]).get("id").toString());
            list.add(pair1);
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/notice/delete");
                post.setEntity(entity);
                HttpResponse response = httpclient.execute(post);
                int result = response.getStatusLine().getStatusCode();
                index = params[0];
                return result;
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
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == 200) {
				data.remove(index);
				simpleadapter.notifyDataSetChanged();
            	Toast.makeText(NoticeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(NoticeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
			}
		}
    	
    }
    
    class LoadingTask extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			data = getData();
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			simpleadapter = new SimpleAdapter(getApplicationContext(), data,
	                R.layout.xfile_notice_item,
	                new String[]{"id", "sort", "title"}, 
	                new int[]{R.id.item_id, R.id.sort, R.id.item_title});
			listview.setAdapter(simpleadapter);
		}
    	
    }
    
    private List<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> map = null;
        
        String url = "http://www.xpcms.net/mobile.php/notice/index/mid/" + myapp.getData("id").toString();
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONArray line = new JSONArray(new JSONTokener(str));
            for (int i =0; i<line.length();i++) {
                map = new HashMap<String, Object>();
                map.put("id", line.getJSONObject(i).getString("id"));
                map.put("sort", (i+1));
                map.put("title", line.getJSONObject(i).getString("title"));
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
