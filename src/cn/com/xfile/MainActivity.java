package cn.com.xfile;

import cn.com.util.ExitManager;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
    //底部选项卡
    private TabHost tabHost;
    private RadioButton tab_list, tab_notice, tab_log, tab_more;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_main);
        initTab();
        init();
        ExitManager.getInstance().addActivity(this);
    }
    
    public void init(){
        tab_list=(RadioButton)findViewById(R.id.tab_list);
        tab_notice = (RadioButton) findViewById(R.id.tab_notice);
        tab_log = (RadioButton) findViewById(R.id.tab_log);
        tab_more = (RadioButton) findViewById(R.id.tab_more);
        
        tab_list.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                tabHost.setCurrentTabByTag("list");
            }
        });

        tab_notice.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                tabHost.setCurrentTabByTag("notice");
            }
        });
        
        tab_log.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                tabHost.setCurrentTabByTag("log");
            }
        });
        
        tab_more.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                tabHost.setCurrentTabByTag("more");

            }
        });
    }
    
    public void initTab(){
        tabHost=getTabHost();
        tabHost.addTab(tabHost.newTabSpec("list").setIndicator("list")
                .setContent(new Intent(this, RecordActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("notice").setIndicator("notice")
                .setContent(new Intent(this, NoticeActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("log").setIndicator("log")
                .setContent(new Intent(this, LogActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("more").setIndicator("more")
                .setContent(new Intent(this, MoreActivity.class)));
    }
    
    public boolean dispatchKeyEvent( KeyEvent event) {
        int keyCode=event.getKeyCode();
          if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getRepeatCount() == 0) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialog.setTitle(MainActivity.this
                        .getString(R.string.app_close));
                alertDialog.setPositiveButton(MainActivity.this
                        .getString(R.string.btn_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                ExitManager.getInstance().exit();
                            }
                        });
                alertDialog.setNegativeButton(MainActivity.this
                        .getString(R.string.btn_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        });
                alertDialog.show();
            }
        }
        return super.dispatchKeyEvent(event);
    }

}