package cn.com.lib;

import cn.com.xfile.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class XProgressDialog extends Dialog{
	
	private static XProgressDialog xProgressDialog = null;

	public XProgressDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static XProgressDialog createDialog(Context context) {  
        xProgressDialog = new XProgressDialog(context, R.style.loading_dialog);  
        xProgressDialog.setContentView(R.layout.loading_dialog);  
        xProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;  
        
        ImageView imageView = (ImageView) xProgressDialog.findViewById(R.id.loadingImageView);  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation); // 加载动画  
        imageView.startAnimation(hyperspaceJumpAnimation);
        
        return xProgressDialog;  
    }  


}
