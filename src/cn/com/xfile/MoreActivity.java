package cn.com.xfile;

import cn.com.tools.MediaPlayActivity;
import cn.com.tools.UploadActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;

/**
 * 更多
 * @author Administrator
 *
 */
public class MoreActivity  extends Activity{
	private TableRow upload_btn, mediaplay, db;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        
        upload_btn = (TableRow)findViewById(R.id.tools_upload_btn);
        upload_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MoreActivity.this, UploadActivity.class);
				startActivity(intent);
			}
		});
        
        mediaplay = (TableRow)findViewById(R.id.mediaplay);
        mediaplay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MoreActivity.this, MediaPlayActivity.class);
				startActivity(intent);
			}
		});
        
        db = (TableRow)findViewById(R.id.db);
        db.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v("TEST", "DB");
			}
		});
    }
}
