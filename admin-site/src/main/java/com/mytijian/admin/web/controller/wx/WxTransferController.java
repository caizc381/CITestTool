package com.mytijian.admin.web.controller.wx;

import java.net.URLEncoder;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.admin.web.vo.wx.WxTransferVO;
import com.mytijian.wx.component.WxComponentAPIUtil;
import com.mytijian.wx.model.WxConfig;
import com.mytijian.wx.model.WxOpenAPIConfig;
import com.mytijian.wx.service.WxConfigService;
import com.mytijian.wx.service.WxOpenAPIConfigService;

@RestController
@RequestMapping("/wx/transfer")
public class WxTransferController {

	@Autowired
	private WxConfigService wxConfigService;
	
	@Value("${mytijian_main_site}")
	private String mytijianMainSite;

	@Resource(name = "wxOpenAPIConfigService")
	private WxOpenAPIConfigService wxOpenAPIConfigService;
	
	@RequestMapping("/start")
	public WxTransferVO transfer(@RequestParam("hospitalId") Integer hospitalId) throws Exception {
		
		WxConfig config = wxConfigService.findEnabledWxConfigByHospitalId(hospitalId);
		
		if (config == null) {
			return null;
		}
		
		// 当前给公众号已经是使用快速接入
		if (config.getAuthType()!=null && config.getAuthType() == WxConfig.AUTH_TYPE_COMPONENT) {
			return null;
		}
		
		WxOpenAPIConfig openApiConfig = wxOpenAPIConfigService.get();
		String preAuthCode = WxComponentAPIUtil.getPreAuthCode(openApiConfig.getComponentAccessToken(),
				openApiConfig.getAppid());

		String transferAuthUrl =  String.format(
				"https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=%s&pre_auth_code=%s&redirect_uri=%s",
				openApiConfig.getAppid(), preAuthCode,
				URLEncoder.encode(mytijianMainSite+"/action/wx/open/rec/" + config.getHospitalId()+"?state=transfer", "utf-8"));
		
		WxTransferVO vo = new WxTransferVO();
		vo.setTransferAuthUrl(transferAuthUrl);
		
		return vo;
	}
}
