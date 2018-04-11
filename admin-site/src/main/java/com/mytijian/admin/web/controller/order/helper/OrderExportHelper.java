package com.mytijian.admin.web.controller.order.helper;

import com.alibaba.fastjson.JSON;
import com.mongodb.DBObject;
import com.mytijian.account.enums.GenderEnum;
import com.mytijian.account.service.AccountService;
import com.mytijian.admin.web.util.OrderUtils;
import com.mytijian.offer.examitem.constant.enums.ExamItemToMealEnum;
import com.mytijian.offer.examitem.exception.ItemSelectException.ConflictType;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.model.ExamItemSnap;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.offer.meal.model.Meal;
import com.mytijian.offer.meal.model.MealSnap;
import com.mytijian.offer.meal.service.MealService;
import com.mytijian.order.enums.OrderStatusEnum;
import com.mytijian.order.model.Order;
import com.mytijian.order.service.OrderService;
import com.mytijian.util.DateUtils;
import com.mytijian.util.MoneyUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("orderExportHelper")
public class OrderExportHelper {
    
    private final static Logger logger = LoggerFactory
            .getLogger(OrderExportHelper.class);
    
    @Resource(name = "orderService")
    private OrderService orderService;
    
    @Resource(name = "examItemService")
    private ExamItemService examItemService;
    
    @Resource(name = "mealService")
    private MealService mealService;
    
    @Resource(name = "accountService")
    private AccountService accountService;
    
    private final int HZLYY = 1;
    
    private final int ARMY117 = 2;

    public String getValue(String key, Map orderMap, Integer hospitalId, Map<String, String> itemMap) {
        
        if (key == null) {
            return null;
        }
        String[] keyArr = key.split("\\.");
        
        if (keyArr.length == 1) {
            if ("exportDiscount".equals(keyArr[0])) {
                return resolveExportDiscount(orderMap, hospitalId);
            }

            if ("mealName".equals(keyArr[0])) {
                //杭疗不需要套餐名称
                if (hospitalId.intValue() == HZLYY) {
                    return "";
                }
                return orderMap.get(keyArr[0]) == null ? null : orderMap.get(
                        keyArr[0]).toString();
            }
            
            if ("addItems".equals(keyArr[0])) {
				String addItems = null;
				// 处理拼接字段
				List<ExamItemSnap> ExamItemSnaps = JSON.parseArray((String) orderMap.get("itemsDetail"),
						ExamItemSnap.class);
				StringBuilder sBAddItems = new StringBuilder();
				for (ExamItemSnap eIS : ExamItemSnaps) {
					int typeToMeal = eIS.getTypeToMeal();
					if (typeToMeal == ExamItemToMealEnum.addToMeal.getCode()) {
						toItemString(sBAddItems, eIS);
					}
				}
				addItems = sBAddItems.toString();
				return addItems;
			}
			// 减项
			if ("minusItems".equals(keyArr[0])) {
				String minusItems = null;
				// 处理拼接字段
				List<ExamItemSnap> ExamItemSnaps = JSON.parseArray((String) orderMap.get("itemsDetail"),
						ExamItemSnap.class);
				StringBuilder sBMinusItems = new StringBuilder();
				for (ExamItemSnap eIS : ExamItemSnaps) {
					int typeToMeal = eIS.getTypeToMeal();
					if (typeToMeal == ExamItemToMealEnum.outMeal.getCode()) {
						toItemString(sBMinusItems, eIS);
					}
				}
				minusItems = sBMinusItems.toString();
				return minusItems;
			}
			// 套餐项
			if ("mealItems".equals(keyArr[0])) {
				String mealItems = null;
				// 处理拼接字段
				List<ExamItemSnap> ExamItemSnaps = JSON.parseArray((String) orderMap.get("itemsDetail"),
						ExamItemSnap.class);
				StringBuilder sBMealItems = new StringBuilder();
				for (ExamItemSnap eIS : ExamItemSnaps) {
					int typeToMeal = eIS.getTypeToMeal();
					if ((typeToMeal == ExamItemToMealEnum.inMeal.getCode())
							|| (typeToMeal == ExamItemToMealEnum.outMeal.getCode())) {
						toItemString(sBMealItems, eIS);
					}
				}
				mealItems = sBMealItems.toString();
				return mealItems;
			}

            if ("orderItems".equals(keyArr[0])) {
                return itemMap.get("orderItems");
            }
            
            if ("selfMoney".equals(keyArr[0])) {
                return orderMap.get("selfMoney") == null ? "0.00"
                        : orderMap.get("selfMoney").toString();
            }
            if ("offlinePayMoney".equals(keyArr[0])) {
                return orderMap.get("offlinePayMoney") == null ? "0.00"
                        : orderMap.get("offlinePayMoney").toString();
            }
            
            if ("mealPrice".equals(keyArr[0])) {
                Order order = orderService.getNormalOrderByOrderId(Integer.parseInt(orderMap.get("id").toString()));
                MealSnap mealSnap = JSON.parseObject(order.getMealDetail(), MealSnap.class);
                Meal meal = mealService.getMealById(mealSnap.getId());
                if (meal == null) {
                    return null;
                } else {
                    return "¥" + MoneyUtil.formatMoney(meal.getInitPrice());
                }
            }

            if ("status".equalsIgnoreCase(keyArr[0])) {
                Integer status = 0;
                if (StringUtils.isNotBlank(String.valueOf(orderMap.get("status")))) {
                    status = Double.valueOf(String.valueOf(orderMap.get("status"))).intValue();
                }
                if (orderMap.containsKey("isExport") && (Boolean) orderMap.get("isExport")
                        && orderMap.containsKey("status")
                        && status == OrderStatusEnum.appointmentSuccess.getCode()) {
                    return OrderUtils.converStatus(99);
                } else {
                    return OrderUtils.converStatus(status);
                }
            } else if ("orderPrice".equalsIgnoreCase(keyArr[0])) {
                String f1 = "0.00";
                try {
                    Double value = Double.valueOf(orderMap.get(keyArr[0]).toString());
                    if (value != null) {
                        f1 = new DecimalFormat("#############0.00").format(value / 100);
                    }
                } catch (Exception e) {
                    logger.error(
                            "getExportCheckOrderValue  Double.valueOf : key is {} and orderMap is {}",
                            e, key, orderMap);
                }
                return f1;
            } else if ("examDate".equalsIgnoreCase(keyArr[0])) {
                if (orderMap.containsKey(keyArr[0])) {
                    Date examDate = (Date) orderMap.get(keyArr[0]);
                    if (examDate == null) {
                        return null;
                    }
                    String examDateStr = DateUtils.format(examDate);
                    if (StringUtils.isNotBlank((String) orderMap.get("examTimeIntervalName"))) {
                        examDateStr = examDateStr + " " + orderMap.get("examTimeIntervalName");
                    }
                    return examDateStr;
                }
            } else if ("insertTime".equalsIgnoreCase(keyArr[0])) {
                if (orderMap.containsKey(keyArr[0])) {
                    Date insertTime = (Date) orderMap.get(keyArr[0]);
                    return DateUtils.format(DateUtils.YYYY_MM_DD_HMS, insertTime);
                }
            } 

            return orderMap.get(keyArr[0]) == null ? null : orderMap.get(keyArr[0])
                    .toString();
        } else if (keyArr.length == 2) {
            DBObject dbObject2 = (DBObject) orderMap.get(keyArr[0]);
            if (dbObject2 == null) {
                return null;
            }
            Map innerMap = (Map)orderMap.get(keyArr[0]);
            if("gender".equalsIgnoreCase(keyArr[1])) {
                if(innerMap.containsKey("gender") && (Integer)innerMap.get("gender")== GenderEnum.FEMALE.getCode()) {
                    return "女";  
                } 
                if(innerMap.containsKey("gender") && (Integer)innerMap.get("gender")== GenderEnum.MALE.getCode()) {
                    return "男";  
                } 
            }
            
            // 体检人去掉空格
            if ("accountRelation.name".equals(key)) {
                return dbObject2.get(keyArr[1]) == null ? null : dbObject2.get(
                        keyArr[1]).toString().replaceAll("\\s+","");
            }
            return dbObject2.get(keyArr[1]) == null ? null : dbObject2.get(
                    keyArr[1]).toString();
        }
        return null;
    }
    
    private String resolveExportDiscount(Map orderMap, Integer hospitalId) {
        Object vo = orderMap.get("exportDiscount");
        if (vo == null) {
            return null;
        }

        double discount = Double.parseDouble(vo.toString());

        String s = "";
        // 杭疗exportDiscount *10
        if (hospitalId.intValue() == HZLYY) {
            s = String.valueOf(discount * 10);
        } else {
            s = String.valueOf(discount);
        }

        return new BigDecimal(s).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

    }
    
 // 拼接 加项;减项;套餐项 公共方法
 	private void toItemString(StringBuilder builder, ExamItemSnap examItemSnap) {

 		String hisId = examItemSnap.getHisId();
 		// 判断hisId是否为空，为空子查询拆分套餐项，不为空 ，
 		if (StringUtils.isBlank(hisId)) {
 			List<Integer> itemList = examItemService.getConflictItems(examItemSnap.getId(), ConflictType.COMPOSE);
 			// 健壮性，
 			if (CollectionUtils.isEmpty(itemList)) {
 				return;
 			} else {
 				List<ExamItem> examItems = examItemService.getExamItemsBySelected(itemList);
 				if (CollectionUtils.isEmpty(examItems)) {
 					return;
 				} else {
 					for (ExamItem examItem : examItems) {
 						builder.append(examItem.getName()).append(" ").append("(").append(examItem.getHisItemId())
 								.append(",")
 								.append("¥"
 										+ MoneyUtil.formatMoney(examItem.getPrice() == null ? 0 : examItem.getPrice()))
 								.append(")").append(System.lineSeparator());
 					}

 				}
 			}
 		} else {
 			builder.append(examItemSnap.getName()).append(" ").append("(").append(examItemSnap.getHisId()).append(",")
 					.append("¥" + MoneyUtil.formatMoney(examItemSnap.getOriginalPrice() == null ? 0
 							: examItemSnap.getOriginalPrice()))
 					.append(")").append(System.lineSeparator());

 		}

 	}
}
