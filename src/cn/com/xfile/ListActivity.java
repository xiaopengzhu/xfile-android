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

import cn.com.lib.RefreshableView;
import cn.com.lib.RefreshableView.PullToRefreshListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * 记录列表
 * @author Administrator
 *
 */
public class ListActivity extends Activity{
    //数据接口
    private GridView gridview;
    //数据
    private List<HashMap<String, Object>> data = null;
    private SimpleAdapter simpleadapter;
    //主线程
    private Handler mHandler;
    //Loading
    static ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
		
		//初始加载空数据
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        gridview = (GridView)findViewById(R.id.gridview);
        mHandler = new Handler();
        
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
                intent.setClass(ListActivity.this, ListItemActivity.class);
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
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						gridview.setAdapter(simpleadapter);
						progressDialog.dismiss();
					}
				});
			}
		};
		new Thread(run).start();
    }
    
    
    private List<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> map = null;
        
        String url = "http://www.xpcms.net/mobile.php/api/getTypes";
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
            //圆角
            
            Bitmap output = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, img.getWidth(), img.getHeight());
            final RectF rectF= new RectF(rect);
            final float roundPx = 5;
            
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(img, rect, rect, paint);
            img = output;
            
            
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
