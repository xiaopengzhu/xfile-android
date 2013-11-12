package cn.com.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;

public class FunctionHelper {
    
    public Bitmap getBitMap(URL url, int round) {
        Bitmap img = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            img = BitmapFactory.decodeStream(is); 
            //圆角
            
            Bitmap output = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, img.getWidth(), img.getHeight());
            final RectF rectF= new RectF(rect);
            final float roundPx = round;
            
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(img, rect, rect, paint);
            img = output;
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return img;
    }
    
    public String getGetData(URI uri) {
        String str = null;
        HttpGet get = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = client.execute(get);
            str = EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }
    
    public String getPostData(URI uri, List<NameValuePair> list) {
        String str = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(uri);
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                str = EntityUtils.toString(response.getEntity());
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }
    
}
