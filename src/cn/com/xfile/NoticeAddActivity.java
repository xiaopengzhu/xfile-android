package cn.com.xfile;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class NoticeAddActivity extends Activity{
	private AutoCompleteTextView title;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> data = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xfile_notice_add);
		
		title = (AutoCompleteTextView)findViewById(R.id.title);
		
		title.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String str = s.toString();
				new AutocompleteTask().execute(str);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	class AutocompleteTask extends AsyncTask<String, Integer, Integer> {
		JSONArray ary = null;
		
		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpGet get = new HttpGet("http://www.xpcms.net/mobile.php/notice/autocomplete/str/" + params[0]);
	        HttpClient client = new DefaultHttpClient();
	        
			try {
				HttpResponse response = client.execute(get);
		        String  str = EntityUtils.toString(response.getEntity());
		        if (!str.equals("null")) {
		        	ary = new JSONArray(new JSONTokener(str)); 
		        	return 1;
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
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			if (ary!=null) {
				data.clear();
				for (int i=0; i<ary.length(); i++) {
					try {
						JSONObject tmp = (JSONObject) ary.get(i);
						data.add(tmp.getString("title"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.xfile_notice_add_dropdown, data);
				title.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
