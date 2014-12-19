package cn.com.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.xfile.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost.Settings;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MediaPlayActivity extends Activity{

	private ListView listview;
	private ArrayList<HashMap<String,Object>> files;
	private MediaPlayer mp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools_mediaplay);
		
		listview = (ListView)findViewById(R.id.listview);
		mp = new MediaPlayer();
		
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String path = files.get(position).get("media").toString();
				try {
					mp.setDataSource(path);
					mp.prepare();
					mp.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	//选择图片
	public void select(View v) {
		files = getFile();
		SimpleAdapter listAdapter = new SimpleAdapter(this, files, R.layout.tools_mediaplay_item, new String[]{"media"},new int[]{R.id.media});
		listview.setAdapter(listAdapter);
	}
	
	//获取定位
	public void gps(View v) {
		LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		String provider = lm.getBestProvider(criteria, true);
		Location location = lm.getLastKnownLocation(provider);
		
		TextView show = (TextView)findViewById(R.id.show);
		show.setText("纬度:" + location.getLatitude() + " 经度:" + location.getLongitude());
		
	}
	
	//选择文件
	public ArrayList<HashMap<String,Object>> getFile(){
		String filePath = Environment.getExternalStorageDirectory().getPath() + "/Music/";
		File file = new File(filePath);
		File[] files = file.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				return filename.endsWith(".mp3");
			}
		});
		
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map = null;
		for (File f : files) {
			map = new HashMap<String, Object>();
			map.put("media", filePath + f.getName());
			list.add(map);
		}
		return list;
	}

}
