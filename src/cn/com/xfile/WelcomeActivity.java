package cn.com.xfile;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 登录欢迎ListView
 * @author Administrator
 *
 */
public class WelcomeActivity extends Activity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //获取控件
        ViewPager vp = (ViewPager)findViewById(R.id.welcome_viewpager);
        LayoutInflater lif = LayoutInflater.from(this);
        //图标获取
        final ImageView page0 = (ImageView)findViewById(R.id.welcome_page0);
        final ImageView page1 = (ImageView)findViewById(R.id.welcome_page1);
        final ImageView page2 = (ImageView)findViewById(R.id.welcome_page2);
        final ImageView page3 = (ImageView)findViewById(R.id.welcome_page3);
        //视图获取
        View view0 = lif.inflate(R.layout.new1, null);
        View view1 = lif.inflate(R.layout.new2, null);
        View view2 = lif.inflate(R.layout.new3, null);
        View view3 = lif.inflate(R.layout.new4, null);
        //view 装填
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view0);
        views.add(view1);
        views.add(view2);
        views.add(view3);
        
        //数据适配
        vp.setAdapter(new PagerAdapter() {
            
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }
            
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return views.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                    Object object) {
                // TODO Auto-generated method stub
                ((ViewPager)container).removeView(views.get(position));
            }

            @Override 
            //初始化函数
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                ((ViewPager)container).addView(views.get(position));
                return views.get(position);
            }
            
        });
        
        //初始化
        vp.setCurrentItem(0);
        
        //监听事件
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                switch (arg0) {
                case 0:
                    page0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    page1.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;
                case 1:
                    page1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    page0.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    page2.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;
                case 2:
                    page2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    page1.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    page3.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;
                case 3:
                    page3.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    page2.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;

                default:
                    break;
                }
                Log.v("debug", arg0 + "");
            }
            
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    public void startButton(View v) {
        Intent intent = new Intent();
        intent.setClass(WelcomeActivity.this, XfileActivity.class);
        startActivity(intent);
        this.finish();
    }

}
