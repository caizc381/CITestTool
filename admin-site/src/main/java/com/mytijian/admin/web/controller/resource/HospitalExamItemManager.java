package com.mytijian.admin.web.controller.resource;

import com.mytijian.admin.web.vo.resource.ExamitemUpdateResult;
import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.offer.examitem.constant.enums.UpdateItemStatusEnum;
import com.mytijian.offer.meal.service.MealManageService;
import com.mytijian.pool.ThreadPoolManager;
import com.mytijian.resource.exceptions.HospitalException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

@Component
public class HospitalExamItemManager {
    private static Logger log = LoggerFactory.getLogger(HospitalExamItemManager.class);

    @Resource(name = "mealManageService")
    private MealManageService mealManageService;
    
//    @RedisClient(nameSpace = "updateItemStatusEnum", timeout = 60 * 60 * 12)   
//    private RedisCacheClient<UpdateItemStatusEnum> redisClient;
    
    @RedisClient(nameSpace = "offer:examitem:update", timeout = 60 * 60 * 12)
    private RedisCacheClient<ExamitemUpdateResult> examitemUpdateClient;
    
    private ExecutorService executorService = ThreadPoolManager.newFixedThreadPool(10, 100);

    /**
     * 执行更新单项对套餐和订单影响的任务
     * 
     * @param hospitalId
     */
    public void exeUpdateItemJob(Integer hospitalId) throws HospitalException {
        if (examitemUpdateClient.get(hospitalId) != null
                && examitemUpdateClient.get(hospitalId).getStatus().equals(UpdateItemStatusEnum.EXECUTING)) {
            throw new HospitalException(HospitalException.UPDATE_ITEM_EXCUTING, "更新单项的任务正在执行中");
        }
        
        ExamitemUpdateResult result = new ExamitemUpdateResult();
		result.setStatus(UpdateItemStatusEnum.EXECUTING);
		result.setMessage("执行中");
		examitemUpdateClient.put(hospitalId, result);

		Runnable sendThread = () -> {
			try {
				mealManageService.effectExamItemChanges(hospitalId);
				result.setStatus(UpdateItemStatusEnum.SUCCESS);
				result.setMessage("执行成功");
				examitemUpdateClient.put(hospitalId, result);
			} catch (Exception e) {
				result.setStatus(UpdateItemStatusEnum.FAIL);
				String message = "";
				Throwable throwable = e.getCause();
				if (throwable != null) {
					message = throwable.getMessage();
					if (StringUtils.isNotBlank(message)) {
						message = ",原因:" + message;
					}
				}
				result.setMessage("更新单项任务失败" + message);
				examitemUpdateClient.put(hospitalId, result);
				log.error("更新单项任务失败", e);
			}
		};

        executorService.submit(sendThread);
    }

	/**
	 * 获取体检中心更新单项任务的状态
	 * @param hospitalId
	 * @return
	 */
	public String getUpdateItemStatus(Integer hospitalId) {
		ExamitemUpdateResult result = examitemUpdateClient.get(hospitalId);
		if (result != null) {
			return result.getMessage();
		}
		return UpdateItemStatusEnum.NONE.getName();
	}
}
