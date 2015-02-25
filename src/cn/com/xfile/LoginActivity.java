package cn.com.xfile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.util.HttpRequest;
import cn.com.util.MyApp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 登录
 */
public class LoginActivity extends Activity{
    //首次登录
    private SharedPreferences preferences;
    private Editor editor;
    //控件
    private EditText account, password;
    //Loading
    static ProgressDialog progressDialog;
    
    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_login);
        
        account = (EditText)findViewById(R.id.login_input_name);
        password = (EditText)findViewById(R.id.login_input_password);

        TextView forgetPass = (TextView)findViewById(R.id.forgetPass);
        forgetPass.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(LoginActivity.this, MoreActivity.class);
                startActivity(intent);
            }
        });

    }
   
    //登录任务
    class LoginTask extends AsyncTask<Void, Integer, Integer> {
        private JSONObject response;
        
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            NameValuePair pair1 = new BasicNameValuePair("account", account.getText().toString());
            NameValuePair pair2 = new BasicNameValuePair("password", password.getText().toString());
            
            list.add(pair1);
            list.add(pair2);
            
            String uri = "http://www.xpcms.net/mobile.php/member/login";
            
            response = HttpRequest.post(uri, list);
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progressDialog.dismiss();
            
            if (response!=null) {
                try {
                    int code = Integer.parseInt(response.getString("code"));
                    if (code == 200) {//登录成功
                        String  token = response.getString("data");
                        //存入全局变量
                        MyApp appCookies = (MyApp) getApplication();
                        appCookies.setData("token", token);
                        
                        //启动判断
                        preferences = getSharedPreferences("phone", Context.MODE_PRIVATE); 
                        if (preferences.getBoolean("firststart", true)) {//首次启动进行欢迎页面
                            editor = preferences.edit();
                            editor.putBoolean("firststart", false);
                            editor.commit();
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        String msg = response.getString("msg");
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            } else {
                Toast.makeText(LoginActivity.this, "服务器内部错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    //登录
    public void login(View v) {
        progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
        new LoginTask().execute();
    }
    
    //注册
    public void register(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
