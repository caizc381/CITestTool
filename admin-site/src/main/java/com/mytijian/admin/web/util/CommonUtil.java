package com.mytijian.admin.web.util;

import java.security.MessageDigest;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class CommonUtil {
	
	public static final String DEFAULT_PWD="123456";
	
	public static final int DEFAULT_PAGESIZE = 12;
	
	public static final int DEFAULT_CURRPAGE = 1;

	/**
	 * 获取MD5加密后的字符串
	 * 
	 * @param str
	 *            加密前的字符串
	 * @return
	 */
	public static String MD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(str.getBytes("UTF-8"));
			byte[] byteArray = md.digest();
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				} else {
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
			return md5StrBuff.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Page getPage(Integer currPage, Integer pageSize) {
		if (currPage == null || currPage.intValue() <= 0) {
			currPage = DEFAULT_CURRPAGE;
		}
		if (pageSize == null || pageSize.intValue() <= 0) {
			pageSize = DEFAULT_PAGESIZE;
		}
		//currPage = Optional.ofNullable(currPage).orElse();
		//pageSize = Optional.ofNullable(pageSize).orElse(CommonUtil.PAGESIZE);
		Page page = new Page();
		page.setCurrPage(currPage);
		page.setPageSize(pageSize);
		page.setOffset((currPage - 1) * pageSize);
		return page;
	}
	
	/**
	 * TODO 暂时salt为1位
	 * @return
	 */
	public static String getSalt() {
		String baseStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		int number = random.nextInt(baseStr.length());
		return String.valueOf(baseStr.charAt(number));
	}
	
	public static boolean isStrFitLength(String str, Integer minLength, Integer maxLength) {
		if (minLength == null) {
			minLength = 0;
		}
		
		if (maxLength == null) {
			maxLength = Integer.MAX_VALUE;
		}
		
		if (StringUtils.isEmpty(str)) {
			if (minLength.intValue() == 0) {
				return true;
			} else {
				return false;
			}
		}
		
		String regEx = String.format("[\\s\\S]{%s,%s}", minLength, maxLength);
	    // 编译正则表达式
	    Pattern pattern = Pattern.compile(regEx);
	    Matcher matcher = pattern.matcher(str);
	    return matcher.matches();
	}
	
	public  static void main(String args[]) {
		String a = "";
		System.out.println(isStrFitLength(a, null, 1));
	}
	
}

