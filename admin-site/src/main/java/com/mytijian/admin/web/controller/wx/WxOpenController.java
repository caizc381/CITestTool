package com.mytijian.admin.web.controller.wx;

import com.mytijian.wx.component.WxComponentAPIUtil;
import com.mytijian.wx.model.WxConfig;
import com.mytijian.wx.model.WxOpenAPIConfig;
import com.mytijian.wx.sdk.Wx;
import com.mytijian.wx.sdk.WxResult;
import com.mytijian.wx.sdk.WxTempQrcode;
import com.mytijian.wx.service.WxConfigService;
import com.mytijian.wx.service.WxOpenAPIConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/wx/open")
public class WxOpenController {

	@Value("${mytijian_main_site}")
	private String mytijianMainSite;

	@Resource(name = "wxOpenAPIConfigService")
	private WxOpenAPIConfigService wxOpenAPIConfigService;

	@Resource(name = "wxConfigService")
	private WxConfigService wxConfigService;
	
	
	@RequestMapping("/auth")
	@ResponseBody
	public Map<String, String> gotoWxComponentAuth(String hospitalId) throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<>();

		WxOpenAPIConfig openApiConfig = wxOpenAPIConfigService.get();
		String preAuthCode = WxComponentAPIUtil.getPreAuthCode(openApiConfig.getComponentAccessToken(),
				openApiConfig.getAppid());

		String url =  String.format(
				"https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=%s&pre_auth_code=%s&redirect_uri=%s",
				openApiConfig.getAppid(), preAuthCode,
				URLEncoder.encode(mytijianMainSite+"/action/wx/open/rec/" + hospitalId, "utf-8"));
		
		
		map.put("success", "true");
		map.put("url", url);
		
		return map;
	}
	
	@RequestMapping("/afterScanQrcode")
	@ResponseBody
	public Map<String, String> afterScanQrcode(Integer hospitalId) throws Exception {
		Map<String, String> map = new HashMap<>();
		WxConfig config = wxConfigService.getWxConfigByHospitalId(hospitalId);
	
		WxResult wr = Wx.tempQrcode(config.getAccessToken(), WxTempQrcode.test);
		String ticket = wr.get("ticket");
		map.put("success", "true");
		map.put("ticket", ticket);
		return map;
	}
	
}
