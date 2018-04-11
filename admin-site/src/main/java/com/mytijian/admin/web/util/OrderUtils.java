package com.mytijian.admin.web.util;

import com.google.common.collect.Lists;
import com.mytijian.account.enums.GenderEnum;
import com.mytijian.order.enums.OrderStatusEnum;
import com.mytijian.util.DateUtils;
import com.mytijian.util.ExcelUtil;
import com.mytijian.util.MoneyUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

public class OrderUtils {

	public static final Logger logger = LoggerFactory.getLogger(OrderUtils.class);
	
	@SuppressWarnings("rawtypes")
	public static void getExportCheckOrder(String templatePath, Collection<Map> orderMaps,
			List<List<String>> orderList) throws FileNotFoundException {
		List<List<String>> template = ExcelUtil.analyzeExcel(templatePath);
		orderList.add(template.get(0));
		List<String> keyList = template.get(1);
		List<String> needKeys = Lists.newArrayList();
		needKeys.add("examDate");
		needKeys.add("accountRelation.name");
		needKeys.add("accountRelation.gender");
		needKeys.add("accountRelation.idCard");
		needKeys.add("examCompany");
		needKeys.add("status");
		needKeys.add("orderPrice");
		needKeys.add("selfMoney");
		needKeys.add("offlinePayMoney");
		needKeys.add("refundTotal");
		needKeys.add("operator");
		needKeys.add("insertTime");
		needKeys.add("mealName");
		for (Map orderMap : orderMaps) { 
			List<String> eachOrder = new ArrayList<String>();
			for (int index = 0; index < keyList.size(); index++) {
				eachOrder = getExportCheckOrderValue(orderMap, needKeys);
			}
			orderList.add(eachOrder);
		}
		counterMoney(orderList);
		
	}
	
	/**
	 * 统计总金额
	 */
	private static void counterMoney(List<List<String>> orderList) {
		List<List<String>> list = Lists.newArrayList(orderList);
		if(orderList==null || orderList.isEmpty()) {
			return ;
		}
		double totalOrderPrice = 0.00;
		double totalSelfMoney = 0.00;
		double totalOfflinePayMoney = 0.00;
		double totalRefundTotal = 0.00;
		for(int i=1;i<list.size();i++) {
			try {
				List<String> properties = list.get(i);
				if(StringUtils.isNotBlank(properties.get(7))) {
					totalOrderPrice += Double.valueOf(properties.get(7));
				}
				if(StringUtils.isNotBlank(properties.get(8))) {
					totalSelfMoney += Double.valueOf(properties.get(8));
				}
				if(StringUtils.isNotBlank(properties.get(9))) {
					totalOfflinePayMoney+= Double.valueOf(properties.get(9));			
				}
				if(StringUtils.isNotBlank(properties.get(10))) {
					totalRefundTotal += Double.valueOf(properties.get(10));
				}
			}catch(Exception e) {
				logger.error("counterMoney error,properties is :"+list.get(i),e);
				continue;
			}
			
		}
		//count
		String[] counterOrder = new String[14];
		counterOrder[0]="合计";
		counterOrder[7]= MoneyUtil.format(totalOrderPrice);
		counterOrder[8]= MoneyUtil.format(totalSelfMoney);
		counterOrder[9]= MoneyUtil.format(totalOfflinePayMoney);
		counterOrder[10]= MoneyUtil.format(totalRefundTotal);
		orderList.add(Arrays.asList(counterOrder));
	}
	
	/**
	 * 导出对账单value处理
	 * @param orderMap
	 * @param needKeys
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String>  getExportCheckOrderValue(Map orderMap,List<String> needKeys) {
    	List<String> eachOrder = Lists.newArrayList();
    	eachOrder.add(null);
    	for(String  key : needKeys) {
    		try {
    			String[] keyArr = key.split("\\.");
        		if(keyArr.length==1) {
        			if(orderMap.containsKey(keyArr[0])) {
        				if("status".equalsIgnoreCase(keyArr[0])) {
        					Integer status = 0;
        					if(StringUtils.isNotBlank(String.valueOf(orderMap.get("status")))) {
        						status = Double.valueOf(String.valueOf(orderMap.get("status"))).intValue();
        					}
        					if(orderMap.containsKey("isExport") && (Boolean)orderMap.get("isExport") 
        							&& orderMap.containsKey("status") 
        							&& status==OrderStatusEnum.appointmentSuccess.getCode()) {
        						eachOrder.add(converStatus(99));  
        					}else {
        						eachOrder.add(converStatus(status)); 
        					}                            
        				}
        				else if("orderPrice".equalsIgnoreCase(keyArr[0])) {
        					String f1 = "0.00";
        					try {
            					Double value = Double.valueOf(orderMap.get(keyArr[0]).toString());
            					if(value!=null) {
            						f1 =new DecimalFormat("#############0.00").format(value/100);
            					}
        					}catch(Exception e) {
        						logger.error("getExportCheckOrderValue  Double.valueOf : key is {} and orderMap is {}",e,key,orderMap);
        					}
        					eachOrder.add(f1);
    					}else if("examDate".equalsIgnoreCase(keyArr[0])) {
        					if(orderMap.containsKey(keyArr[0])) {
        						Date examDate = (Date)orderMap.get(keyArr[0]);
        						String examDateStr = DateUtils.format(examDate);
        						if(StringUtils.isNotBlank((String)orderMap.get("examTimeIntervalName"))) {
        							examDateStr = examDateStr +" " +orderMap.get("examTimeIntervalName");
        						}
            					eachOrder.add(examDateStr);  
        					}
    					}else if("insertTime".equalsIgnoreCase(keyArr[0])) {
        					if(orderMap.containsKey(keyArr[0])) {
        						Date insertTime = (Date)orderMap.get(keyArr[0]);
            					eachOrder.add(DateUtils.format(DateUtils.YYYY_MM_DD_HMS,insertTime));
        					}
    					}else if("examCompany".equalsIgnoreCase(keyArr[0])) {
        					if(orderMap.containsKey(keyArr[0])) {
        						String hospitalName = null;
        						if(orderMap.get("hospital")!=null) {
        							hospitalName = (String)((Map)orderMap.get("hospital")).get("name");
        						}
            					eachOrder.add((String)orderMap.get("examCompany")+"/"+hospitalName);  
        					}
    					}else {
    						eachOrder.add(String.valueOf(orderMap.get(keyArr[0])));  
    					}
        			}else {
        				eachOrder.add("");
        			}
        		}else {
        			if(orderMap.containsKey(keyArr[0])) {
        				Map innerMap = (Map)orderMap.get(keyArr[0]);
        				if(innerMap.containsKey(keyArr[1])) {
        					if("gender".equalsIgnoreCase(keyArr[1])) {
        	    				if(innerMap.containsKey("gender") && (Integer)innerMap.get("gender")== GenderEnum.FEMALE.getCode()) {
        	    					eachOrder.add("女");  
        	    				} 
        	    				if(innerMap.containsKey("gender") && (Integer)innerMap.get("gender")== GenderEnum.MALE.getCode()) {
        	    					eachOrder.add("男");  
        	    				} 
        					}else {
        						eachOrder.add(String.valueOf(innerMap.get(keyArr[1])));  
        					}
            			}else {
            				eachOrder.add("");
            			}
        			}else {
        				eachOrder.add("");
        			}
        		}
    		}catch(Exception e) {
    			logger.error("getExportCheckOrderValue error : key is {} and orderMap is {}",e,key,orderMap);
    		}
    		
    	}
    	return eachOrder;
    }
	
	public static String converStatus(Integer status) {
		switch (status){
	      case 0: return "未支付";
	      case 1: return "已支付";
	      case 2: return "已预约";
	      case 3: return "体检完成";
	      case 4: return "未到检";
	      case 5: return "已撤销";
	      case 6: return "已删除";
	      case 7: return "支付中";
	      case 8: return "已关闭";
	      case 9: return "部分退款";
	      case 10: return "导出错误";
	      case 11: return "现场支付";
	      case 99: return "已导出";
	    }
		return "";
	}
    	
}
