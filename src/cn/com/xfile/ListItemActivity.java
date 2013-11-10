package cn.com.xfile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class ListItemActivity extends Activity{
    private ListView listview;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        
      //获取转入数据
        Intent intent = getIntent();
        String id = intent.getStringExtra("pid");
        String url = "http://www.xpcms.net/mobile.php/api/getTypes/pid/" + id;
        
        SimpleAdapter simpleadapter = new SimpleAdapter(this, getData(url),
                R.layout.activity_list_item,
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
        
        listview = (ListView)findViewById(R.id.listview);
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
                map.put("name", line.getJSONObject(i).getString("name"));
                map.put("icon", getBitMap("http://www.xpcms.net/public/upload/type/"+line.getJSONObject(i).getString("icon")));
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
    
    private Bitmap getBitMap(String str) {
        Bitmap img = null;
        try {
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            img = BitmapFactory.decodeStream(is); 
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return img;
    }
}
