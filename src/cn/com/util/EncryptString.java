package cn.com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.util.Log;

public class EncryptString {
	public String trueString, showString;
	private ArrayList<int[]> list;
	
	//构造显示字符和加密字符串
	public void contruct(String str) {
		trueString = str;
		Pattern pattern = Pattern.compile("\\[\\d+\\]");
		Matcher matcher = pattern.matcher(trueString);
		
		list = new ArrayList<int[]>();
		while (matcher.find()) {
			int[] xx = {matcher.start(), matcher.end()};
			list.add(xx);
		}
		showString = matcher.replaceAll("■");
	}
	
	/*
	 * position是删除前的位置，从0开始
	 * 删除位置至少从1开始,count至少也是1，而且起始位置要大于或等于count才能有效删除
	 * 先算出删除时光标的真实位置，再算出删除后光标的真实置
	 */
	public void delete(Integer position, Integer count) {
		if (showString.length() < position) return;
		if (position < 1) return;
		if (position < count) return;
		int before = realposition(position);
		int after = realposition(position-count);
		trueString = cut(trueString, after, before);
		contruct(trueString);
	}
	
	//添加可以从0开始
	public void add(Integer position, String str) {
		int realposition = realposition(position);
		StringBuffer buffer = new StringBuffer(trueString);
		buffer.insert(realposition, str);
		contruct(buffer.toString());
	}
	
	//计算在真实字符串中的光标位置
	public Integer realposition(Integer position) {
		String source = showString.substring(0, position); 
		String target = new String("■");
		int number = 0;  
        int i = 0;  
        while((i=source.indexOf(target, i))!=-1) {  
            number++;  
            i++;  
        }
        for(int j=0; j<number; j++) {
        	int[] x = list.get(j);
        	position+=(x[1] - x[0] -1);
        }
        
        return position;
	}
	
	//剪掉指定位置的字符
	public String cut(String str, Integer start, Integer end) {
		StringBuffer buffer = new StringBuffer(str);
		for (int i = start; i<end; i ++) {
			buffer.deleteCharAt(start);
		}
		return buffer.toString();
	}
	
	public String getTrue() {
		return trueString;
	} 
	
	public String getShow() {
		return showString;
	}

}
