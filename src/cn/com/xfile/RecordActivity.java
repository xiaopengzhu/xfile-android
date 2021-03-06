package cn.com.xfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import cn.com.lib.XProgressDialog;
import cn.com.util.MyApp;
import cn.com.util.Tools;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class RecordActivity extends Activity{
    //数据接口
    private GridView gridview;
    //数据
    private List<HashMap<String, Object>> data = null;
    private SimpleAdapter simpleadapter;
    //主线程
    private Handler mHandler;
    //Loading
    private XProgressDialog xProgressDialog;
    private MyApp myapp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_record);
        
        //初始加载空数据
        startProgressDialog();
        gridview = (GridView)findViewById(R.id.gridview);
        mHandler = new Handler();
        myapp = (MyApp) getApplication();
        
        //选项点击事件    
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
                @SuppressWarnings("unchecked")
                HashMap<String, Object> map = (HashMap<String, Object>) gridview.getItemAtPosition(arg2);
                String id = (String) map.get("id");
                Intent intent = new Intent();
                intent.setClass(RecordActivity.this, RecordListActivity.class);
                intent.putExtra("tid", id);
                startActivity(intent);
            }
        });
        
        //异步加载服务器数据
        Runnable run = new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                data = getData();
                simpleadapter = new SimpleAdapter(getApplicationContext(), data,
                        R.layout.xfile_record_item,
                        new String[]{"id", "name", "icon"}, 
                        new int[]{R.id.item_id, R.id.item_name, R.id.item_icon});
                
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
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        gridview.setAdapter(simpleadapter);
                        stopProgressDialog();
                    }
                });
            }
        };
        new Thread(run).start();
    }
    
    private List<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> map = null;
        
        String url = "http://www.xpcms.net/mobile.php/record/index/token/"+ myapp.getData("token").toString();
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONArray line = new JSONArray(new JSONTokener(str));
            for (int i =0; i<line.length();i++) {
                map = new HashMap<String, Object>();
                map.put("id", line.getJSONObject(i).getString("id"));
                map.put("name", line.getJSONObject(i).getString("name"));
                map.put("icon", Tools.getBitMap("http://www.xpcms.net/public/upload/type/"+line.getJSONObject(i).getString("icon")));
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
    
    /** 
     * 开启进度对话框 
     */  
    private void startProgressDialog() {  
        if (xProgressDialog == null) {  
        	xProgressDialog = XProgressDialog.createDialog(this);  
        }  
        xProgressDialog.show();  
    }  
      
    /** 
     * 停止进度对话框 
     */  
    private void stopProgressDialog() {  
        if (xProgressDialog != null) {  
        	xProgressDialog.dismiss();  
        	xProgressDialog = null;  
        }  
    } 

}
