package cn.com.xfile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 登录
 * @author Administrator
 *
 */
public class LoginActivity extends Activity{
    //首次登录
    private SharedPreferences preferences;
    private Editor editor;
    //主线程
    private Handler mHandler;
    //控件
    private EditText account, password;
    //Loading
    static ProgressDialog progressDialog;
    
	//接口返回
    private HttpResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mHandler = new Handler();
        account = (EditText)findViewById(R.id.login_input_name);
        password = (EditText)findViewById(R.id.login_input_password);

    }

    public void login(View v) {
        //加载Loading界面
    	progressDialog = ProgressDialog.show(this, "Loading...", "please wait", true, false);
    	
    	//设计任务
        Runnable login = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					List<NameValuePair> list = new ArrayList<NameValuePair>();
			    	NameValuePair pair1 = new BasicNameValuePair("account", account.getText().toString());
			        NameValuePair pair2 = new BasicNameValuePair("password", password.getText().toString());
			        
			        list.add(pair1);
			        list.add(pair2);
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
	                DefaultHttpClient httpclient = new DefaultHttpClient();
	                HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/api/login");
	                post.setEntity(entity);
	                response = httpclient.execute(post);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (response.getStatusLine().getStatusCode() == 200) {

					//线程回调
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {

							// TODO Auto-generated method stub
							try {
								String str = EntityUtils.toString(response.getEntity());
			                    if (str!=null) {
			                        JSONObject line = new JSONObject(new JSONTokener(str));
			                        int code = Integer.parseInt(line.getString("code")); 
			                        if (code ==1) {//登录成功
			                            int id = Integer.parseInt(line.getJSONObject("data").getString("id"));
			                            //存入全局变量
			                            MyApp appCookies = (MyApp) getApplication();
			                            appCookies.setData("id", id);
			                            
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
			                            	intent.setClass(LoginActivity.this, XfileActivity.class);
			                            	startActivity(intent);
			                            }
			                        } else {
			                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
			                        }
			                    } else {
			                        Toast.makeText(LoginActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
			                    }
								
							} catch (Exception e) {
								// TODO: handle exception
							}
							//关闭loading
							progressDialog.dismiss();
						}
					});
				}
			}
		};
		
        //启动新线程
		new Thread(login).start();
    }
}
