package com.mytijian.admin.web.controller.order.helper;

import com.alibaba.fastjson.JSON;
import com.mytijian.account.enums.GenderEnum;
import com.mytijian.account.model.AccountRelationInCrm;
import com.mytijian.admin.web.util.OrderUtils;
import com.mytijian.offer.examitem.constant.enums.ExamItemToMealEnum;
import com.mytijian.offer.examitem.exception.ItemSelectException.ConflictType;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.model.ExamItemSnap;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.offer.meal.model.Meal;
import com.mytijian.offer.meal.model.MealSnap;
import com.mytijian.offer.meal.service.MealService;
import com.mytijian.order.base.snapshot.model.OrderExtInfoSnapshot;
import com.mytijian.order.enums.OrderStatusEnum;
import com.mytijian.order.model.MongoOrder;
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
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component("newOrderExportHelper")
public class NewOrderExportHelper {

    private final static Logger logger = LoggerFactory
            .getLogger(NewOrderExportHelper.class);

    @Resource
    private OrderService orderService;

    @Resource(name = "examItemService")
    private ExamItemService examItemService;

    @Resource(name = "mealService")
    private MealService mealService;

    private final int HZLYY = 1;


    public String getValue(String key, MongoOrder mongoOrder, Integer hospitalId, Map<String, String> itemMap) {

        if (key == null) {
            return null;
        }
        String[] keyArr = key.split("\\.");

        if (keyArr.length == 1) {
            if ("exportDiscount".equals(keyArr[0])) {
                return resolveExportDiscount(mongoOrder, hospitalId);
            }

            if ("mealName".equals(keyArr[0])) {
                //杭疗不需要套餐名称
                if (Objects.equals(hospitalId,HZLYY)) {
                    return "";
                }
                return mongoOrder.getMealName();
            }

            if ("addItems".equals(keyArr[0])) {
                String addItems = null;
                // 处理拼接字段
                String itemsDetail = mongoOrder.getItemsDetail();
                List<ExamItemSnap> examItemSnaps = JSON.parseArray(itemsDetail,ExamItemSnap.class);
                StringBuilder sBAddItems = new StringBuilder();
                for (ExamItemSnap eIS : examItemSnaps) {
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
                String itemsDetail = mongoOrder.getItemsDetail();
                List<ExamItemSnap> examItemSnaps = JSON.parseArray(itemsDetail,ExamItemSnap.class);
                StringBuilder sBMinusItems = new StringBuilder();
                for (ExamItemSnap eIS : examItemSnaps) {
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
                String itemsDetail = mongoOrder.getItemsDetail();
                List<ExamItemSnap> examItemSnaps = JSON.parseArray(itemsDetail, ExamItemSnap.class);
                StringBuilder sBMealItems = new StringBuilder();
                for (ExamItemSnap eIS : examItemSnaps) {
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
            if ("refundTotal".equals(keyArr[0])) {
                return itemMap.get("refundTotal");
            }
            if ("onlineRefundTotal".equals(keyArr[0])) {
                return itemMap.get("onlineRefundTotal");
            }

            if ("selfMoney".equals(keyArr[0])) {
                String selfMoney = mongoOrder.getSelfMoney();
                return StringUtils.isBlank(selfMoney) ? "0.00" : selfMoney;
            }
            if ("offlinePayMoney".equals(keyArr[0])) {
                String offlinePayMoney = mongoOrder.getOfflinePayMoney();
                return StringUtils.isBlank(offlinePayMoney) ? "0.00" : offlinePayMoney;
            }
            if("offlineUnpayMoney".equals(keyArr[0])){
                Integer offlineUnpayMoney = mongoOrder.getOfflineUnpayMoney();
                return Objects.isNull(offlineUnpayMoney) ? "0.00" : MoneyUtil.Po2Vo(offlineUnpayMoney);
            }
            if ("mealPrice".equals(keyArr[0])) {
                Order order = orderService.getNormalOrderByOrderId(mongoOrder.getId());
                MealSnap mealSnap = com.alibaba.fastjson.JSON.parseObject(order.getMealDetail(), MealSnap.class);
                Meal meal = mealService.getMealById(mealSnap.getId());
                if (meal == null) {
                    return null;
                } else {
                    return "¥" + MoneyUtil.formatMoney(meal.getInitPrice());
                }
            }

            if ("status".equalsIgnoreCase(keyArr[0])) {
                Integer status = mongoOrder.getStatus();
                Boolean isExport = Objects.isNull(mongoOrder.getIsExport()) ? false : mongoOrder.getIsExport();
                if (isExport && status == OrderStatusEnum.appointmentSuccess.getCode()) {
                    return OrderUtils.converStatus(99);
                } else {
                    return OrderUtils.converStatus(status);
                }
            } else if ("orderPrice".equalsIgnoreCase(keyArr[0])) {
                String f1 = "0.00";
                try {
                    Integer orderPrice = mongoOrder.getOrderPrice();
                    if (Objects.nonNull(orderPrice)) {
                        f1 = MoneyUtil.formatMoney(orderPrice);
                    }
                } catch (Exception e) {
                    logger.error( "getExportCheckOrderValue  Double.valueOf : key is {} and orderMap is {}", e, key, mongoOrder);
                }
                return f1;
            } else if ("examDate".equalsIgnoreCase(keyArr[0])) {
                Date examDate = mongoOrder.getExamDate();
                if (Objects.isNull(examDate)) {
                    return null;
                }
                String examDateStr = DateUtils.format(examDate);
                String examTimeIntervalName = mongoOrder.getExamTimeIntervalName();
                if (StringUtils.isNotBlank(examTimeIntervalName)) {
                    examDateStr = examDateStr + " " + examTimeIntervalName;
                }
                return examDateStr;
            } else if ("insertTime".equalsIgnoreCase(keyArr[0])) {
                Date insertTime = mongoOrder.getInsertTime();
                if (Objects.isNull(insertTime)) {
                    return null;
                }
                return DateUtils.format(DateUtils.YYYY_MM_DD_HMS, insertTime);
            } else if ("itemsOriginalPrice".equalsIgnoreCase(keyArr[0])) {
                String itemsDetail = mongoOrder.getItemsDetail();
                if (StringUtils.isNotBlank(itemsDetail)) {
                    List<ExamItemSnap> examItemSnaps = JSON.parseArray(itemsDetail, ExamItemSnap.class);
                    Integer sumOriginalPrice = 0;
                    for (ExamItemSnap examItemSnap : examItemSnaps) {
                        if (examItemSnap.getTypeToMeal() != ExamItemToMealEnum.outMeal.getCode()) {
                            sumOriginalPrice += examItemSnap.getOriginalPrice();
                        }
                    }
                    return MoneyUtil.formatMoney(sumOriginalPrice);
                }
                return null;
            } else if ("manager".equalsIgnoreCase(keyArr[0])) {
                OrderExtInfoSnapshot orderExtInfo = mongoOrder.getOrderExtInfo();
                if (Objects.nonNull(orderExtInfo)) {
                    String manager = orderExtInfo.getManager();
                    return StringUtils.isNotBlank(manager) ? manager : mongoOrder.getManager();
                }
                return null;
            }

            return getFieldValue(mongoOrder.getClass(),mongoOrder,keyArr[0],null);
        } else if (keyArr.length == 2) {
            if("gender".equalsIgnoreCase(keyArr[1])) {
                String gender = getFieldValue(mongoOrder.getClass(),mongoOrder, keyArr[0], "gender");
                if (StringUtils.equals(gender, String.valueOf(GenderEnum.FEMALE.getCode()))) {
                    return "女";
                }
                if (StringUtils.equals(gender, String.valueOf(GenderEnum.MALE.getCode()))) {
                    return "男";
                }
            }

            // 体检人去掉空格
            if ("accountRelation.name".equals(key)) {
                String name = getFieldValue(mongoOrder.getClass(),mongoOrder, keyArr[0], keyArr[1]);
                return StringUtils.isNotBlank(name) ? name.replaceAll("\\s+", "") : null;
            }
            return getFieldValue(mongoOrder.getClass(),mongoOrder,keyArr[0],keyArr[1]);
        }
        return null;
    }


    private String getFieldValue(Class _class,Object mongoOrder,String fieldName,String childrenFieldName) {
        Field[] mongoOrderClassDeclaredFields = _class.getDeclaredFields();
        for (Field declaredField : mongoOrderClassDeclaredFields) {
            declaredField.setAccessible(true);
            String name = declaredField.getName();
            if (StringUtils.equals(name, fieldName)) {
                Object value = null;
                try {
                    value = declaredField.get(mongoOrder);
                    if (Objects.isNull(value)) {
                        return "";
                    }
                    // 子字段值查询
                    if (StringUtils.isNotBlank(childrenFieldName)) {
                        String fieldType = declaredField.getType().toString();
                        boolean isInclude = fieldType.endsWith("String") || fieldType.endsWith("Integer") || fieldType.endsWith("Double") || fieldType.endsWith("Boolean");
                        if (!isInclude) {
                            Class<?> childrenClass = value.getClass();
                            Field[] childrenClassDeclaredFields = childrenClass.getDeclaredFields();
                            for (Field childrenDeclaredField : childrenClassDeclaredFields) {
                                childrenDeclaredField.setAccessible(true);
                                String declaredFieldName = childrenDeclaredField.getName();
                                if (StringUtils.equals(declaredFieldName, childrenFieldName)){
                                    Object o = childrenDeclaredField.get(value);
                                    return Objects.nonNull(o) ? o.toString() : "";
                                }
                            }
                            // 没有找到就找父类
                            if (childrenClass.getGenericSuperclass() != null) {
                                return getFieldValue(childrenClass.getSuperclass(), value, childrenFieldName, null);
                            }
                        }
                        return "";
                    }

                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return Objects.nonNull(value)?value.toString():"";
            }
        }
        // 没有找到就找父类
        if (_class.getGenericSuperclass() != null) {
            return getFieldValue(_class.getSuperclass(), mongoOrder, fieldName, childrenFieldName);
        }
        return "";
    }

    private String resolveExportDiscount(MongoOrder mongoOrder, Integer hospitalId) {
        Double discount = mongoOrder.getExportDiscount();
        if (discount == null) {
            return null;
        }

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

    /**
     * 静态内部类，用于排序导出excel时使用
     */
    public static class OrderSortDto {

        private Integer orderId;

        private String orderNum;

        private AccountRelationInCrm accountRelationInCrm;

        private Map dbObject;

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        public AccountRelationInCrm getAccountRelationInCrm() {
            return accountRelationInCrm;
        }

        public void setAccountRelationInCrm(AccountRelationInCrm accountRelationInCrm) {
            this.accountRelationInCrm = accountRelationInCrm;
        }

        public String getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(String orderNum) {
            this.orderNum = orderNum;
        }

        public Map getDbObject() {
            return dbObject;
        }

        public void setDbObject(Map dbObject) {
            this.dbObject = dbObject;
        }

    }
}
