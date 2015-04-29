package cn.com.xfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.com.lib.XListView;
import cn.com.lib.XProgressDialog;
import cn.com.lib.XListView.IXListViewListener;
import cn.com.util.EncryptString;
import cn.com.util.HttpRequest;
import cn.com.util.MyApp;
import cn.com.util.Tools;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * 查看列表中的一项
 * @author Administrator
 *
 */
@SuppressLint("InflateParams")
public class RecordListActivity extends Activity implements IXListViewListener{
    private XListView listview;
    private Button add_btn;
    private SimpleAdapter simpleadapter;
    private List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    
    private Handler mHandler;
    private Runnable refresh, getmore, delete, checkSecondPass;
    private String token, tid, id;
    private XProgressDialog xProgressDialog;
    
    public static int page = 1, pagesize = 8;
    
    private int index, optype;
    
    private String second_password;
    
    //长按菜单
    private String[] opts = new String[]{"查看", "编辑", "删除"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_record_list);
        
        initView();
        
        //刷新
        refresh = new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                page = 1;
                data = getData();
                simpleadapter = new SimpleAdapter(getApplicationContext(), data,
                        R.layout.xfile_record_list_item,
                        new String[]{"id", "title",  "account", "multi", "icon"}, 
                        new int[]{R.id.item_id, R.id.item_title, R.id.item_account, R.id.item_multi, R.id.item_icon});
                simpleadapter.setViewBinder(new ViewBinder() {
                    
                    @Override
                    public boolean setViewValue(View view, Object data,
                            String textRepresentation) {
                        
                        switch (view.getId()) {
                            case R.id.item_icon:
                                ImageView iv = (ImageView) view;
                                iv.setImageBitmap((Bitmap)data);
                                return true;
                            case R.id.item_multi:
                                TextView tv = (TextView)view;
                                tv.setText(Html.fromHtml(data.toString()));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                
                mHandler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        listview.setAdapter(simpleadapter);
                        stopProgressDialog();
                        onLoad();
                    }
                });
            }
        };
        
        //删除
        delete = new Runnable() {
            
            @Override
            public void run() {
                 // TODO Auto-generated method stub
                 List<NameValuePair> list = new ArrayList<NameValuePair>();
                 MyApp myapp = (MyApp) getApplication();
                 NameValuePair pair1 = new BasicNameValuePair("token", myapp.getData("token").toString());
                 NameValuePair pair2 = new BasicNameValuePair("id", id);
                 NameValuePair pair3 = new BasicNameValuePair("second_password", second_password);
                 list.add(pair1);
                 list.add(pair2);
                 list.add(pair3);
                 
                 String uri = "http://www.xpcms.net/mobile.php/record/del";
                 JSONObject response = HttpRequest.post(uri, list);

                 if (response!=null) {
                    try {
                        final int code = Integer.parseInt(response.getString("code"));
                        final String msg = response.getString("msg");
                        
                        mHandler.post(new Runnable() {
                             
                             @Override
                             public void run() {
                                 // TODO Auto-generated method stub
                                 if (code == 200) {
                                 data.remove(index);
                                simpleadapter.notifyDataSetChanged();
                                
                                Toast.makeText(RecordListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                 } else {
                                     Toast.makeText(RecordListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                                          
                 } else {
                     Toast.makeText(RecordListActivity.this, "服务器内部错误", Toast.LENGTH_SHORT).show();
                 }
            }
        };
        
        //更多
        getmore = new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                data.addAll(getData());
                
                mHandler.postDelayed(new Runnable() {
                     
                     @Override
                     public void run() {
                         // TODO Auto-generated method stub
                         simpleadapter.notifyDataSetChanged();
                         onLoad();
                     }
                 }, 2000);
            }
        };
        
        //检测二次密码
        checkSecondPass = new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                MyApp myapp = (MyApp) getApplication();
                NameValuePair pair1 = new BasicNameValuePair("token", myapp.getData("token").toString());
                NameValuePair pair2 = new BasicNameValuePair("second_password", second_password);
                list.add(pair1);
                list.add(pair2);
                
                String uri = "http://www.xpcms.net/mobile.php/member/checkSecondPass";
                JSONObject response = HttpRequest.post(uri, list);

                if (response!=null) {
                   try {
                       final int code = Integer.parseInt(response.getString("code"));
                       
                       mHandler.post(new Runnable() {
                            
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (code == 200) {
                                    Intent intent;
                                    switch (optype) {
                                        //查看
                                        case 0:
                                            intent = new Intent();
                                            intent.putExtra("id", id);
                                            intent.putExtra("second_password", second_password);
                                            intent.setClass(RecordListActivity.this, RecordViewActivity.class);
                                            startActivity(intent);
                                            
                                            break;
                                        //编辑
                                        case 1:
                                            intent = new Intent();
                                            intent.putExtra("id", id);
                                            intent.putExtra("tid", tid);
                                            intent.putExtra("second_password", second_password);
                                            intent.setClass(RecordListActivity.this, RecordAddActivity.class);
                                            startActivity(intent);
                                            break;
                                        //删除
                                        case 2:
                                            new Thread(delete).start();
                                            break;

                                        default:
                                            break;
                                    }
                                } else {
                                    Toast.makeText(RecordListActivity.this, "二级密码错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                   } catch (JSONException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                   }
                } else {
                    Toast.makeText(RecordListActivity.this, "服务器内部错误", Toast.LENGTH_SHORT).show();
                }
            }
        };
        
        //添加
        add_btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(RecordListActivity.this, RecordAddActivity.class);
                intent.putExtra("tid", tid);
                startActivity(intent);
            }
        });
        
        //点击
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                String link = data.get(arg2-1).get("link").toString().trim();
                if (!link.equals("")) {
                    Intent intent = new Intent();
                    intent.putExtra("url", link);
                    intent.setClass(RecordListActivity.this, WebViewActivity.class);
                    startActivity(intent);
                }
                
            }
        });
        
        //长按
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                   index = arg2-1; 
                id = data.get(index).get("id").toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordListActivity.this, R.style.alert_dialog);
                builder.setTitle("选择操作");//.setView(LayoutInflater.from(RecordListActivity.this).inflate(R.layout.alert_dialog, null));
                builder.setItems(opts, new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            secondPass(which);
                        }
                    }).show();
                
                //返回true则不会再次触发ItemClick
                return true;
            }
        });
    
    }
    
    private void secondPass(final int type) {
        LayoutInflater lf = LayoutInflater.from(this);
        final View view = lf.inflate(R.layout.xfile_recordlist_secondpass, null);

        new AlertDialog.Builder(RecordListActivity.this).
        	setTitle("请输入二级密码").setView(view).
        	setPositiveButton("确定", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                EditText secondPass = (EditText)view.findViewById(R.id.seconed_password);
                second_password = secondPass.getText().toString();
                
                optype = type;
                new Thread(checkSecondPass).start();

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                
            }
        }).show();
    }
    
    private void initView() {
        //控件线程
        mHandler = new Handler();
        add_btn = (Button)findViewById(R.id.add_btn);
        listview = (XListView)findViewById(R.id.refreshable_view);
        
        //获取转入数据
        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        
        //获取UID
        MyApp myapp = (MyApp)getApplication();
        token = myapp.getData("token").toString();
        
        //Loading
        startProgressDialog();
        
        //初始化
        listview.setXListViewListener(this);
        listview.setPullLoadEnable(false);
        
    }

    // 返回时调用此方法刷新
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        new Thread(refresh).start();
    }
    
    private void onLoad() {
        listview.stopRefresh();
        listview.stopLoadMore();
        listview.setRefreshTime("刚刚");
        if (data.size() > 6) {
            listview.setPullLoadEnable(true);
        }
    }

    public void onRefresh() {
        new Thread(refresh).start();
    }

    public void onLoadMore() {
        new Thread(getmore).start();
    }

    private List<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> map = null;
        
        String url = "http://www.xpcms.net/mobile.php/record/gets/tid/" + tid + "/token/" + token+"/page/" + page + "/pagesize/" + pagesize;
        
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(new JSONTokener(str));
            int code = Integer.parseInt(obj.getString("code"));
            if (code!=200) return null;
            
            JSONArray line = obj.getJSONArray("data");
            EncryptString encryptString;
            for (int i =0; i<line.length();i++) {
                map = new HashMap<String, Object>();
                map.put("id", line.getJSONObject(i).getString("id"));
                map.put("type_id", line.getJSONObject(i).getJSONObject("type").getString("id"));
                map.put("title", line.getJSONObject(i).getString("title"));
                
                encryptString = new EncryptString();
                encryptString.contruct(line.getJSONObject(i).getString("account"));
                map.put("account", encryptString.showString);
                
                encryptString = new EncryptString();
                encryptString.contruct(line.getJSONObject(i).getString("password"));
                map.put("password", encryptString.showString);
                
                JSONObject type = line.getJSONObject(i).getJSONObject("type");
                map.put("icon", Tools.getBitMap("http://www.xpcms.net/public/upload/type/"+type.getString("icon")));
                
                //Multi设置
                String ad = line.getJSONObject(i).getJSONObject("type").getString("ad");
                String link = line.getJSONObject(i).getJSONObject("type").getString("link");
                String remark = line.getJSONObject(i).getString("remark");
                
                if (ad.equals("")) {
                    map.put("multi", remark);
                } else {
                    map.put("multi", "<font color=\"red\">" + ad + "</font>");
                }
                map.put("link", link);
                
                list.add(map);
            }
            page += 1;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
        
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
