package com.mytijian.admin.web.util;

import com.google.common.collect.Maps;
import com.mytijian.util.AssertUtil;

import java.util.Map;

public class WxEventKeyMatcher {
	
	public static void main(String[] args) {
		Map<String, String> map = matcher("WXQ,action_spread,spread_197051"/*, WxConsts.EventKeyPatterns.WX_QRCODE_RULE*/);
		System.out.println(map);
	}
	
	public static Map<String, String> matcher(String eventKey) {
		Map<String, String> res = null;
		
		if (AssertUtil.isNotEmpty(eventKey)) {
				res = Maps.newHashMap();
				String[] kvArray = eventKey.split(",");
				for (String kv : kvArray) {
					String[] kvPairs = kv.split("_");
					res.put(kvPairs[0], kvPairs.length>1?kvPairs[1]:"");
				}
		}
		return res;
	}

}
