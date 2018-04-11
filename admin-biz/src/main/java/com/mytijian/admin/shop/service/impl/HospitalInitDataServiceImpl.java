package com.mytijian.admin.shop.service.impl;

import org.springframework.stereotype.Service;

import com.mytijian.admin.shop.param.InitDataReq;
import com.mytijian.admin.shop.service.HospitalInitDataService;
import com.mytijian.calculate.CalculatorServiceEnum;
import com.mytijian.organization.dto.OrganizationManagerDto;
import com.mytijian.organization.enums.OrganizationMediatorTypeEnum;
import com.mytijian.organization.model.OrganizationMediatorInfo;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.util.AssertUtil;

/**
 * 类HospitalInitDataServiceImpl.java的实现描述：医院化服务接口，收拢医院初始化数据业务
 * @author ljx 2018年1月31日 下午3:45:57
 */
@Service
public class HospitalInitDataServiceImpl implements HospitalInitDataService {
	
	@Override
	public OrganizationManagerDto initData(InitDataReq initDataReq) {
		OrganizationManagerDto organMana = new OrganizationManagerDto();
		//初始化医院数据
		Hospital hospital = initHospital(initDataReq.getHospital(), initDataReq.getSettings(),
				initDataReq.getAddress(),initDataReq.getOpsManagerKeys());
		organMana.setHospital(hospital);
		//检查业务联系人
		OrganizationMediatorInfo mediatorInfo = initOrganizationMediator(initDataReq.getMediatorInfo());
		organMana.setMediatorInfo(mediatorInfo);
		//时段
		organMana.setPeriodSettingList(initDataReq.getPeriodSettingList());
		//站点
		initDataReq.getSite().setTemplate(initDataReq.getSiteTemplate());
		organMana.setSite(initDataReq.getSite());
		organMana.setLimitNumMap(initDataReq.getLimitNumMap());
		//设置客户经理
		organMana.setManager(initDataReq.getManager());


		return organMana;
	}

	private Hospital initHospital(Hospital hospital, HospitalSettings settings, Address address,
			String opsManagerKeys) {
	
		hospital.setAddress(address);
		initHospitalSetting(settings);
		hospital.setSettings(settings);
		hospital.setOpsManagerKeys(opsManagerKeys);
		return hospital;
	}

	/**
	 * 初始化设置，增加默认值
	 * @param settings
	 */
	private void initHospitalSetting(HospitalSettings setting) {
		setting.setAutoConfirmOrder(true);
		setting.setAutoExportOrder(true);
		setting.setAutoReleaseDays(5);
		setting.setDeliveryPrice(0);
		setting.setEnableDatePeriod(0);
		setting.setExportWithXls(true);
		setting.setGuestOfflineCompAlias("个人");
		setting.setGuestOnlineCompAlias("网上预约个人");
		setting.setIsSendMessage(true);
		setting.setMakeOutInvoice(false);
		setting.setManualExportOrder(true);
		setting.setmGuestCompAlias("每天健康");
		setting.setMobileFieldOrder(true);
		setting.setOnlyLocalePay(false);
		setting.setPreviousExportTime("09:00");
		setting.setRefundRefusedItem(true);
		setting.setReserveDayAvailable(false);
		setting.setSendExamSms(false);
		setting.setSendExamSmsDays(1);
		setting.setSendExamSmsTime("10:00");
		setting.setSettlementMode(0);
		setting.setShowCompanyReport(true);
		setting.setExamreportIntervalTime(0);
		setting.setShowExamReport(false);
		setting.setShowInvoice(2);
		setting.setShowItemPrice(false);
		setting.setSupportExtDiscount(false);
		setting.setVipPrice(300000);
		setting.setSecondSiteSwitch(true);
		setting.setOpenSyncCompany(false);
		setting.setOpenSyncMeal(false);
		setting.setAdvanceExportCompanyOrder(true);
		setting.setOpenPrintExamGuide(false);
		setting.setOpenQueue(false);
		setting.setShowNonPlatformExamReport(false);
		// 是否自动导出订单到邮件
		setting.setIsAutoExportOrderToEmails(false);
		// 是否提供个性化套餐 1是 0 否
		setting.setProvideIndividuationMeal(true);
		// 开票要求，0:普通，1:高
		setting.setInvoiceRequired(0);
		setting.setPromptPageUrl(null);
		setting.setBookPromptText(null);
		setting.setPayTipText(null);
		setting.setIsAdvanceExportCompanyOrder(true);
		setting.setPlatformGuestDiscount(1.00);
		setting.setPlatformCompDiscount(1.00);
		setting.setGuestOnlineCompDiscount(1.00);
		setting.setGuestOfflineCompDiscount(1.00);
		setting.setHospitalCompDiscount(1.00);
		// 转换精度
		setting.setCalculatorService(CalculatorServiceEnum.getNameByCode(Integer.valueOf(setting.getCalculatorService())));
		if (setting.getAccountPay() == null) {
			setting.setAccountPay(false);
		}
		if (setting.getAliPay() == null) {
			setting.setAliPay(false);
		}
		if (setting.getWeiXinPay() == null) {
			setting.setWeiXinPay(false);
		}
		if (setting.getAcceptOfflinePay() == null) {
			setting.setAcceptOfflinePay(false);
		}

	}

	private OrganizationMediatorInfo initOrganizationMediator(OrganizationMediatorInfo mediatorInfo) {
		// 检查业务联系人
		if (mediatorInfo != null
				&& (AssertUtil.isNotEmpty(mediatorInfo.getName()) || AssertUtil.isNotEmpty(mediatorInfo.getMobile()) || AssertUtil
						.isNotEmpty(mediatorInfo.getMail()))) {
			mediatorInfo.setType(OrganizationMediatorTypeEnum.BUSINESS_CONTACTS.getCode());
			return mediatorInfo;
		}
		return null;
	}

}
