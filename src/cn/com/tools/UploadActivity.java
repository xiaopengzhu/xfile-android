package cn.com.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.com.util.UploadFile;
import cn.com.xfile.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class UploadActivity extends Activity{
    private GridView gridview;
    private ArrayList<HashMap<String,Object>> files;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools_upload);
		gridview = (GridView)findViewById(R.id.gridview);
		
		//点击放大
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
                intent.setClass(UploadActivity.this, ImageViewActivity.class);
                intent.putExtra("src", files.get(position).get("image").toString());
                startActivity(intent);
			}
		});
	    
		//长按上传
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Map<String, File> file = new HashMap<String, File>();
				file.put("image", new File(files.get(position).get("image").toString()));
				
				Map<String, String> param = new HashMap<String, String>();
				param.put("name", "file_upload_test");
				
				try {
					UploadFile.upload("http://www.xpcms.net/mobile.php/tools/upload", param, file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return true;
			}
		});
	}
	
	//选择图片
	public void select(View v) {
		files = getFile();
		SimpleAdapter listAdapter = new SimpleAdapter(this, files, R.layout.tools_upload_gridview_item, new String[]{"image"},new int[]{R.id.image});
		gridview.setAdapter(listAdapter);
	}
	
	//选择文件
	public ArrayList<HashMap<String,Object>> getFile(){
		String filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
		File file = new File(filePath);
		File[] files = file.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				return filename.endsWith(".jpeg");
			}
		});
		
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map = null;
		for (File f : files) {
			map = new HashMap<String, Object>();
			map.put("image", filePath + f.getName());
			list.add(map);
		}
		return list;
	}

}
