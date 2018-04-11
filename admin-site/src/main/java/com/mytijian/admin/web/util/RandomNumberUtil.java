package com.mytijian.admin.web.util;

import java.util.Random;

public class RandomNumberUtil {
	/**
	 * 随机生成n位数字
	 */
	public static String generate(int n){
		
		Random random = new Random();
		StringBuffer sb=new StringBuffer();
		
		for(int i=0;i<n;i++){
			
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}
}
