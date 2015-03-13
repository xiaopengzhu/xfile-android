package cn.com.lib;

import android.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class CustomeProgressDialog extends ProgressDialog{
	
	private Context mContent; 
	
	public CustomeProgressDialog(Context context) {
		super(context);
		mContent = context;
	}
	
	public CustomeProgressDialog(Context context, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);  
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
