package cn.com.xfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{
    private Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //登录
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                EditText account = (EditText)findViewById(R.id.login_input_name);
                EditText password = (EditText)findViewById(R.id.login_input_password);
                
                NameValuePair pair1 = new BasicNameValuePair("account", account.getText().toString());
                NameValuePair pair2 = new BasicNameValuePair("password", password.getText().toString());
                
                list.add(pair1);
                list.add(pair2);
                try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://www.xpcms.net/mobile.php/api/login");
                    post.setEntity(entity);
                    HttpResponse response = httpclient.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String str = EntityUtils.toString(response.getEntity());
                        if (str!=null) {
                            JSONObject line = new JSONObject(new JSONTokener(str));
                            int code = Integer.parseInt(line.getString("code")); 
                            if (code ==1) {
                                int id = Integer.parseInt(line.getJSONObject("data").getString("id"));
                                //存入全局变量
                                MyApp appCookies = (MyApp) getApplication();
                                appCookies.setData("id", id);
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, XfileActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                        }
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
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

}
