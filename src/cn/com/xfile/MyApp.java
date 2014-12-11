package cn.com.xfile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;

/**
 * 公共全局
 * @author Administrator
 *
 */
public class MyApp extends Application{
    private Map<String, Object> myData;
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        myData = new HashMap<String, Object>();
        myData = Collections.synchronizedMap(myData);
    }
    
    public Object getData(String key) {
        Object ret = null;
        if (myData!=null) {
            ret = myData.get(key);
        }
        return ret;
    }
    
    public void setData(String key, Object value) {
        if (myData!=null) {
            myData.put(key, value);
        }
    }
    
}
