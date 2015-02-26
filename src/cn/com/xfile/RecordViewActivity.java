package cn.com.xfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.util.HttpRequest;
import cn.com.util.MyApp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecordViewActivity extends Activity{
    private String id, second_password;
    private MyApp myapp;
    static  ProgressDialog progressDialog;
    private TextView account, password, title, remark;
    private ListView notice;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_record_view);
        
        Intent intent = getIntent();
        intent.getStringExtra("tid");
        id = intent.getStringExtra("id");
        second_password = intent.getStringExtra("second_password");
        myapp = (MyApp) getApplication();
        account = (TextView)findViewById(R.id.account);
        password = (TextView)findViewById(R.id.password);
        title = (TextView)findViewById(R.id.titleText);
        remark = (TextView)findViewById(R.id.remark);
        notice = (ListView)findViewById(R.id.notice);
        
        //异步加载
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        new LoadingTask().execute();
    }
    
    //异步加载
    class LoadingTask extends AsyncTask<Void, Integer, Integer> {
        private JSONObject ret;
        
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String uri = "http://www.xpcms.net/mobile.php/record/get/id/"+id+"/token/"+myapp.getData("token")+"/second_password/" + second_password;
            ret = HttpRequest.get(uri);
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            if (ret!=null) {
                try {
                    int code = Integer.parseInt(ret.getString("code"));
                    String msg = ret.getString("msg");
                    if (code!=200) {
                        Toast.makeText(RecordViewActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    
                    JSONObject line = ret.getJSONObject("data");
                    title.setText(line.getString("title"));
                    account.setText(line.getString("account"));
                    password.setText(line.getString("password"));
                    remark.setText(line.getString("remark"));
                    
                    JSONArray ary = line.getJSONArray("notice");
                    String[] data = new String[ary.length()];
                    for(int i=0; i<ary.length();i++) {
                        data[i] = ary.get(i).toString();
                    }
                    notice.setAdapter(new ArrayAdapter<String>(RecordViewActivity.this, R.layout.xfile_record_view_item, data));
                    
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(RecordViewActivity.this, "服务器内部错误", Toast.LENGTH_SHORT).show();
                finish();
            }
            progressDialog.dismiss();
        }
    }
}
