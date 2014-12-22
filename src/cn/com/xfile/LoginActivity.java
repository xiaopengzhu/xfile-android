package cn.com.xfile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
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
    //控件
    private EditText account, password;
    //Loading
    static ProgressDialog progressDialog;
    //主线程
    private Handler handler;
	//接口返回
    private HttpResponse response;
    
    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        account = (EditText)findViewById(R.id.login_input_name);
        password = (EditText)findViewById(R.id.login_input_password);
        handler = new MsgHandler(this);

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
    
    //静态线程
    static class MsgHandler extends Handler {
    	private WeakReference<LoginActivity> mActivity;  
    	MsgHandler(LoginActivity activity) {  
            mActivity = new WeakReference<LoginActivity>(activity);  
        } 
		@Override
		public void handleMessage(Message msg) {  
			LoginActivity activity = mActivity.get();  
	        if (activity != null) {  
	        	activity.handleMessage(msg);
	        }  
	    } 
    	
    }

    //登录后处理
    public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		try {
			Bundle data = msg.getData();  
	        String str = data.getString("value");  
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
			e.printStackTrace();
		}
		//关闭loading
		progressDialog.dismiss();
	}
    
    //异步登录任务
    Runnable runnable = new Runnable() {
		
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
				Message message = new Message();
				Bundle data = new Bundle();  
		        try {
					data.putString("value",EntityUtils.toString(response.getEntity()).toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		        message.setData(data);
				handler.sendMessage(message);
			}
		}
	};
    
	//响应登录按钮
    public void login(View v) {
        //加载Loading界面
    	progressDialog = ProgressDialog.show(this, "加载中...", "请稍候", true, false);
		
        //启动新线程
		new Thread(runnable).start();
    }
    
    //响应注册按钮
    public void register(View v) {
    	Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
    	startActivity(intent);
    }
}
