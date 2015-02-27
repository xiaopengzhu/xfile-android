package cn.com.xfile;

import java.io.IOException;
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

import cn.com.util.EncryptString;
import cn.com.util.HttpRequest;
import cn.com.util.MyApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 添加记录
 */
public class RecordAddActivity extends Activity{
    private String id, tid, second_password;
    private ArrayList<String> data;
    private AutoCompleteTextView title;
    private JSONObject ret;
    static  ProgressDialog progressDialog;
    private EditText  account,password, remark;
    private TextView titleText, record_id;
    private Button sub_btn;
    private ArrayAdapter<String> adapter;
    private MyApp myapp;
    
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    
    private int notice_type;
    private EncryptString acc_encrypt, pass_encrypt;
    private int acc_position, pass_position;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_record_add);
        
        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        id = intent.getStringExtra("id");
        second_password = intent.getStringExtra("second_password");
        
        sub_btn = (Button)findViewById(R.id.sub_btn);
        titleText = (TextView)findViewById(R.id.titleText);
        record_id = (TextView)findViewById(R.id.record_id);
        account = (EditText)findViewById(R.id.account);
        password = (EditText)findViewById(R.id.password);
        remark = (EditText)findViewById(R.id.remark);
        title = (AutoCompleteTextView)findViewById(R.id.title);
        myapp = (MyApp) getApplication();
        
        //异步加载
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        new LoadingTask().execute();
        
        //提交
        sub_btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new SubmitTask().execute();
            }
        });
        
        //自动提示
        title.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String str = s.toString();
                if (start > 0) {
                    new AutocompleteTask().execute(str);
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }
        });
        
        //侦测输入
        account.addTextChangedListener(new TextWatcher() {
            
            @Override
            /**
             * s 为变化后字符串
             * start 为变化后的光标位置
             * before 为变化前的光标位置
             * count 为新增量
             */
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
                CharSequence str = null;

                if (before > 0 && count == 0) {//删除模式, setText会先执行一行清空，此时不能调用delete
                    acc_encrypt.delete(start+before, before);
                }
                //一次插入多个字符情况，Notice塞入时start会重置为0, count变为整个长度
                if (count > 0) {//插入模式，分键盘插入和Notice提示插入
                    if (start == 0 && before == 0 && s.subSequence(0,1).toString().equals("■")) {//首位Notice插入
                    } else if (start == 0 && before == 0 && !s.subSequence(0,1).toString().equals("■")) {//首位Keybord插入
                        str = s.subSequence(0, count);
                        if (str.toString().equals(acc_encrypt.showString)) {//编辑状态时还原的情况,此时start处于首位，且showString就有值
                            
                        } else {
                            acc_encrypt.add(start, str.toString());
                        }
                    } else if (start == 0 && s.length() == count) {//非首位Notice插入
                    } else {
                        str = s.subSequence(start, start+count);
                        acc_encrypt.add(start, str.toString());
                    }

                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }
        });
        
        //侦测输入
        password.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                CharSequence str = null;

                if (before > 0 && count == 0) {//删除模式, setText会先执行一行清空，此时不能调用delete
                    pass_encrypt.delete(start+before, before);
                }
                //一次插入多个字符情况，Notice塞入时start会重置为0, count变为整个长度
                if (count > 0) {//插入模式，分键盘插入和Notice提示插入
                    if (start == 0 && before == 0 && s.subSequence(0,1).toString().equals("■")) {//首位Notice插入
                    } else if (start == 0 && before == 0 && !s.subSequence(0,1).toString().equals("■")) {//首位Keybord插入
                        str = s.subSequence(0, count);
                        if (str.toString().equals(pass_encrypt.showString)) {//编辑状态时还原的情况,此时start处于首位，且showString就有值
                            
                        } else {
                            pass_encrypt.add(start, str.toString());
                        }
                    } else if (start == 0 && s.length() == count) {//非首位Notice插入
                    } else {
                        str = s.subSequence(start, start+count);
                        pass_encrypt.add(start, str.toString());
                    }

                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }
        });
        
    }

    public void insertAccNotice(View v) {
        notice_type = 1;
        acc_position = account.getSelectionStart();
        new NoticeTask().execute();
    }
    
    public void insertPassNotice(View v) {
        notice_type = 2;
        pass_position = password.getSelectionStart();
        new NoticeTask().execute();
    }
    
    private ArrayList<String> getData(String url) {
        ArrayList<String> list = new ArrayList<String>();
        
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONArray line = new JSONArray(new JSONTokener(str));
            for (int i =0; i<line.length(); i++) {
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
    
    private List<HashMap<String, Object>> getNotice() {
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
    
    //加载密码提示
    class NoticeTask extends AsyncTask<Void, Integer, Integer> {
        private List<HashMap<String, Object>> data;
        
        
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            data = getNotice();
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
       
            SimpleAdapter simpleadapter = new SimpleAdapter(getApplicationContext(), data,
                    R.layout.xfile_recordadd_notice_item,
                    new String[]{"id", "sort", "title"}, 
                    new int[]{R.id.item_id, R.id.sort, R.id.item_title});
            if (dialog != null && dialog.isShowing()) return;
            
            builder = new AlertDialog.Builder(RecordAddActivity.this);
            builder.setAdapter(simpleadapter, new OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    String str = "[" + data.get(which).get("id").toString() +"]";
                    if (notice_type == 1) {
                        acc_encrypt.add(acc_position, str);
                        
                        account.setText(acc_encrypt.showString);
                        account.setSelection(acc_position+1);
                    }
                    if (notice_type == 2) {
                        pass_encrypt.add(pass_position, str);
                        password.setText(pass_encrypt.showString);
                        password.setSelection(pass_position+1);
                    }
                }
            });
            dialog = builder.create();
            dialog.show();
        }
        
    }
    
    //输入提示
    class AutocompleteTask extends AsyncTask<String, Integer, Integer> {
        JSONArray ary = null;
        
        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            NameValuePair pair0 = new BasicNameValuePair("str", params[0]);
            list.add(pair0);
            
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/record/autocomplete");
                post.setEntity(entity);
                HttpResponse response = httpclient.execute(post);

                String  str = EntityUtils.toString(response.getEntity());
                
                if (!str.equals("null")) {
                    ary = new JSONArray(new JSONTokener(str)); 
                    return 1;
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
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            if (ary!=null) {
                data.clear();
                for (int i=0; i<ary.length(); i++) {
                    try {
                        JSONObject tmp = (JSONObject) ary.get(i);
                        data.add(tmp.getString("name"));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                }
                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.xfile_notice_add_dropdown, data);
                title.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }
    
    //异步加载
    class LoadingTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            //类型列表拉取
            data = getData("http://www.xpcms.net/mobile.php/record/index/pid/"+tid);
            
            //记录拉取
            if (id!=null) {
                HttpGet get = new HttpGet("http://www.xpcms.net/mobile.php/record/get/id/"+id+"/token/"+myapp.getData("token")+"/second_password/" + second_password);
                HttpClient client = new DefaultHttpClient();
                try {
                    HttpResponse response = client.execute(get);
                    String  str = EntityUtils.toString(response.getEntity());
                    ret = new JSONObject(new JSONTokener(str)); 
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
        
            if (ret!=null) {//编辑模式
                titleText.setText("编辑");
                try {
                    int code = Integer.parseInt(ret.getString("code"));
                    String msg = ret.getString("msg");
                    if (code!=200) {
                        Toast.makeText(RecordAddActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    
                    JSONObject line = ret.getJSONObject("data");
                    
                    title.setText(line.getString("title"));
                    record_id.setText(line.getString("id"));
                    
                    acc_encrypt = new EncryptString();
                    acc_encrypt.contruct(line.getString("account"));
                    account.setText(acc_encrypt.showString);
                    
                    pass_encrypt = new EncryptString();
                    pass_encrypt.contruct(line.getString("password"));
                    password.setText(pass_encrypt.showString);
                    
                    remark.setText(line.getString("remark"));
                    
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                acc_encrypt = new EncryptString();
                acc_encrypt.contruct("");
                pass_encrypt = new EncryptString();
                pass_encrypt.contruct("");
            }
            progressDialog.dismiss();
        }
        
    }
    
    //提交异步任务
    class SubmitTask extends AsyncTask<Void, Integer, Integer> {
        private int code;
        
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            
            NameValuePair pair0 = new BasicNameValuePair("id", id);
            NameValuePair pair1 = new BasicNameValuePair("token", myapp.getData("token").toString());
            NameValuePair pair2 = new BasicNameValuePair("tid", tid);
            NameValuePair pair3 = new BasicNameValuePair("title", title.getText().toString());
            NameValuePair pair5 = new BasicNameValuePair("account", acc_encrypt.trueString);
            NameValuePair pair6 = new BasicNameValuePair("password", pass_encrypt.trueString);
            NameValuePair pair7 = new BasicNameValuePair("remark", remark.getText().toString());
            
            list.add(pair0);
            list.add(pair1);
            list.add(pair2);
            list.add(pair3);
            list.add(pair5);
            list.add(pair6);
            list.add(pair7);
            
            String uri = "http://www.xpcms.net/mobile.php/record/add";
            JSONObject response = HttpRequest.post(uri, list);
            
            try {
                code = response.getInt("code");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return code;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            if (code == 200) {
                finish();
                Toast.makeText(RecordAddActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RecordAddActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
        
    }

}
