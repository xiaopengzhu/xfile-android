package cn.com.xfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.lib.XProgressDialog;
import cn.com.util.HttpRequest;
import cn.com.util.MyApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class NoticeActivity  extends Activity{
	private XProgressDialog xProgressDialog;
    private ListView listview;
    private List<HashMap<String, Object>> data;
    private SimpleAdapter simpleadapter;
    private MyApp myapp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_notice);
        myapp = (MyApp) getApplication();
        
        startProgressDialog();
        
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
    
    private List<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> map = null;
        
        String uri = "http://www.xpcms.net/mobile.php/notice/index/token/" + myapp.getData("token").toString();
        JSONObject response = HttpRequest.get(uri);
        
        try {
            JSONArray line = response.getJSONArray("data");
            for (int i =0; i<line.length();i++) {
                map = new HashMap<String, Object>();
                map.put("id", line.getJSONObject(i).getString("id"));
                map.put("sort", (i+1));
                map.put("title", line.getJSONObject(i).getString("title"));
                list.add(map);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
    
    class DeleteTask extends AsyncTask<Integer, Integer, Integer> {
        private int index;
        private int code;
        private String msg;
        
        @Override
        protected Integer doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            
            index = params[0];
            
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            NameValuePair pair1 = new BasicNameValuePair("id", data.get(params[0]).get("id").toString());
            NameValuePair pair2 = new BasicNameValuePair("token", myapp.getData("token").toString());
            list.add(pair1);
            list.add(pair2);
            
            String uri = "http://www.xpcms.net/mobile.php/notice/delete";
            JSONObject response = HttpRequest.post(uri, list);
            
            try {
                code = Integer.parseInt(response.get("code").toString());
                msg = response.get("msg").toString();
            } catch (NumberFormatException e) {
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
            super.onPostExecute(result);
            if (code == 200) {
                data.remove(index);
                simpleadapter.notifyDataSetChanged();
                Toast.makeText(NoticeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
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

            stopProgressDialog();
            
            simpleadapter = new SimpleAdapter(getApplicationContext(), data,
                    R.layout.xfile_notice_item,
                    new String[]{"id", "sort", "title"}, 
                    new int[]{R.id.item_id, R.id.sort, R.id.item_title});
            listview.setAdapter(simpleadapter);
        }
        
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
