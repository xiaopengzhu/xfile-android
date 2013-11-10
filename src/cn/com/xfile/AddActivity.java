package cn.com.xfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class AddActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		String url = "http://www.xpcms.net/mobile.php/api/getTypes";
		
		Spinner type = (Spinner)findViewById(R.id.type);
		type.setAdapter(getData(url));
		
		
	}
	
    private SpinnerAdapter getData(String url) {
        
    	String[] map;

        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            String str = EntityUtils.toString(response.getEntity());
            JSONArray line = new JSONArray(new JSONTokener(str));
            for (int i =0; i<line.length();i++) {
                line.getJSONObject(i).getString("name");
            }
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
        SpinnerAdapter list = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item, map);
        return list;
        
    }
}
