package cn.com.xfile;

import cn.com.tools.UploadActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;

public class SetActivity  extends Activity{
    private TableRow upload_btn, db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_set);
        
        upload_btn = (TableRow)findViewById(R.id.tools_upload_btn);
        upload_btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(SetActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });
        
        db = (TableRow)findViewById(R.id.db);
        db.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
    }

}
