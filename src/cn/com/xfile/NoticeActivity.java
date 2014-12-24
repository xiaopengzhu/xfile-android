package cn.com.xfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NoticeActivity  extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_notice);
    }
    
    public void add(View v) {
    	Intent intent = new Intent(NoticeActivity.this, NoticeAddActivity.class);
    	startActivity(intent);
    }
}
