package cn.com.tools;

import cn.com.xfile.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageViewActivity extends Activity{
    private ImageView image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools_imageview);
		
		image = (ImageView)findViewById(R.id.image);
		
		Intent intent = getIntent();
        String src = intent.getStringExtra("src");
        Bitmap bm = BitmapFactory.decodeFile(src);
        image.setImageBitmap(bm);
	}

	//点击关闭
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return true;
	}

}
