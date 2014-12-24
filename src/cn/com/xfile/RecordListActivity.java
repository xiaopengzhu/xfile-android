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
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.com.lib.XListView;
import cn.com.lib.XListView.IXListViewListener;
import cn.com.util.MyApp;
import cn.com.util.Tools;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * 查看列表中的一项
 * @author Administrator
 *
 */
public class RecordListActivity extends Activity implements IXListViewListener{
    private XListView listview;
    private Button add_btn;
    private String tid;
    private SimpleAdapter simpleadapter;
    private List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    
    private Handler mHandler;
    private Runnable refresh, getmore, delete;
    private String url, id;
    static ProgressDialog progressDialog;
    
    private int index;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_record_list);
        
        initView();
        
        //刷新
        refresh = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				data = getData(url);
				simpleadapter = new SimpleAdapter(getApplicationContext(), data,
		                R.layout.xfile_record_list_item,
		                new String[]{"id", "type_name",  "account", "password", "icon"}, 
		                new int[]{R.id.item_id, R.id.type_name, R.id.item_account, R.id.item_password, R.id.item_icon});
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
						listview.setAdapter(simpleadapter);
						progressDialog.dismiss();
						onLoad();
					}
				});
			}
		};
        
		//删除
		delete = new Runnable() {
			
			@Override
			public void run() {
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
                         
                    	 mHandler.post(new Runnable() {
         					
         					@Override
         					public void run() {
         						// TODO Auto-generated method stub
         						data.remove(index);
                                simpleadapter.notifyDataSetChanged();
                                
                                Toast.makeText(RecordListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
         					}
         				});
                         
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
		};
		
		//更多
		getmore = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.postDelayed(new Runnable() {
 					
 					@Override
 					public void run() {
 						// TODO Auto-generated method stub
 						onLoad();
 					}
 				}, 2000);
			}
		};
		
        //添加事件
        add_btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(RecordListActivity.this, AddActivity.class);
                intent.putExtra("tid", tid);
                startActivity(intent);
            }
        });
        
        //点击编辑
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
                String id = data.get(arg2-1).get("id").toString();
                TextView tv = (TextView)findViewById(R.id.type_id); 
                String tid = tv.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("id", id);
                intent.putExtra("tid", tid);
                intent.setClass(RecordListActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
        
        //长按删除
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
           	
            	//长按
                index  = arg2-1;
                // TODO Auto-generated method stub
                id = data.get(index).get("id").toString();
                new AlertDialog.Builder(RecordListActivity.this).setTitle("删除记录").
	                setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                    
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        // TODO Auto-generated method stub
	                    	new Thread(delete).start();
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
    
    }
	
    private void initView() {
    	//控件线程
        mHandler = new Handler();
        add_btn = (Button)findViewById(R.id.add_btn);
        listview = (XListView)findViewById(R.id.refreshable_view);
        
        //获取转入数据
        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        TextView tv2 = (TextView)findViewById(R.id.type_id);
        tv2.setText(tid);
        
        //获取UID
        MyApp myapp = (MyApp)getApplication();
        String mid = myapp.getData("id").toString();
        url = "http://www.xpcms.net/mobile.php/api/getRecords/tid/" + tid + "/mid/" + mid;
        
        //Loading
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        
        //初始化
        listview.setPullLoadEnable(true);
        simpleadapter = new SimpleAdapter(this, data,
                R.layout.xfile_record_list_item,
                new String[]{"id", "type_name",  "account", "password", "icon"}, 
                new int[]{R.id.item_id, R.id.type_name, R.id.item_account, R.id.item_password, R.id.item_icon});
        listview.setAdapter(simpleadapter);
        listview.setXListViewListener(this);
        listview.setPullLoadEnable(false);
        
    }

	@Override
	// 返回时调用此方法刷新
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(refresh).start();
	}

    
    private void onLoad() {
		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime("刚刚");
		if (data.size() > 6) {
			listview.setPullLoadEnable(true);
		}
	}

	public void onRefresh() {
		// TODO Auto-generated method stub
		new Thread(refresh).start();
	}

	public void onLoadMore() {
		// TODO Auto-generated method stub
		new Thread(getmore).start();
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
                map.put("type_id", line.getJSONObject(i).getJSONObject("type").getString("id"));
                map.put("type_name", line.getJSONObject(i).getJSONObject("type").getString("name"));
                map.put("account", line.getJSONObject(i).getString("account"));
                map.put("password", line.getJSONObject(i).getString("password"));
                JSONObject type = line.getJSONObject(i).getJSONObject("type");
                map.put("icon", Tools.getBitMap("http://www.xpcms.net/public/upload/type/"+type.getString("icon")));
                
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
