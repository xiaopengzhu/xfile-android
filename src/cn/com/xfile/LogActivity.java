package cn.com.xfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.lib.XListView;
import cn.com.lib.XListView.IXListViewListener;
import cn.com.util.HttpRequest;
import cn.com.util.MyApp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class LogActivity extends Activity implements IXListViewListener {
    public static int page = 1, pagesize = 8;
    private XListView listview;
    private MyApp myapp;
    static  ProgressDialog progressDialog;
    private SimpleAdapter simpleadapter;
    private List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_log);
        
        listview = (XListView)findViewById(R.id.refreshable_view);
        myapp = (MyApp)getApplication();
        
        //Loading
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        new LoadTask().execute(1);
        
        //初始化
        listview.setPullLoadEnable(true);
        listview.setXListViewListener(this);
        listview.setPullLoadEnable(false);
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        new LoadTask().execute(1);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        new LoadTask().execute(2);
    }
    
    private void onLoad() {
        listview.stopRefresh();
        listview.stopLoadMore();
        listview.setRefreshTime("刚刚");
        if (list.size() > 6) {
            listview.setPullLoadEnable(true);
        }
    }
    
    class LoadTask extends AsyncTask<Integer, Integer, String> {
        
        @Override
        protected String doInBackground(Integer...type) {
            // TODO Auto-generated method stub
            if (type[0] == 1) {//刷新
                page = 1;
            }
            String uri = "http://www.xpcms.net/mobile.php/log/index/token/" + myapp.getData("token").toString() + "/page/" + page + "/pagesize/" + pagesize;
            JSONObject response = HttpRequest.get(uri);
            if (response!=null) {
                try {
                    int code = Integer.parseInt(response.getString("code"));
                    if (code == 200) {
                        HashMap<String, Object> map = null;
                        List<HashMap<String, Object>> temp = new ArrayList<HashMap<String,Object>>();
                        
                        JSONArray data = response.getJSONArray("data");
                        for (int i =0; i<data.length(); i++) {
                            map = new HashMap<String, Object>();
                            map.put("id", data.getJSONObject(i).getString("id"));
                            map.put("ip", data.getJSONObject(i).getString("ip"));
                            map.put("time", data.getJSONObject(i).getString("time"));
                            map.put("address", data.getJSONObject(i).getString("address"));
                            map.put("content", data.getJSONObject(i).getString("content"));
                            map.put("status", data.getJSONObject(i).getString("status"));
                            temp.add(map);
                        }
                        page += 1;

                        if (type[0] == 1) {
                            list = temp;
                            simpleadapter = new SimpleAdapter(getApplicationContext(), list,
                                    R.layout.xfile_log_list_item,
                                    new String[]{"id","time", "address",  "content"}, 
                                    new int[]{R.id.id, R.id.time, R.id.address, R.id.content});
                            return "refresh";
                        } else {
                            list.addAll(temp);
                            return "loadmore";
                        }
                    } else {
                        String msg = response.getString("msg");
                        return msg;
                    }
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            } else {
                return "服务器内部错误";
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(String msg) {
            // TODO Auto-generated method stub
            progressDialog.dismiss();
            if (msg.equals("refresh")) {
                listview.setAdapter(simpleadapter);
            } else if (msg.equals("loadmore")) {
                simpleadapter.notifyDataSetChanged();
            } else {
                Toast.makeText(LogActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            onLoad();
        }
    }
}
