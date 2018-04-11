package com.mytijian.admin.web.controller.wx;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.alipay.api.domain.QRcode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.account.enums.SystemTypeEnum;
import com.mytijian.account.model.Account;
import com.mytijian.account.model.User;
import com.mytijian.account.service.AccountService;
import com.mytijian.account.service.UserService;
import com.mytijian.admin.web.util.WxEventKeyMatcher;
import com.mytijian.admin.web.util.WxProps;
import com.mytijian.admin.web.vo.wx.WxConfigVO;
import com.mytijian.admin.web.vo.wx.WxQrcodeDTO;
import com.mytijian.gotone.api.SmsTemplateService;
import com.mytijian.gotone.api.model.SmsTemplateAddReq;
import com.mytijian.gotone.api.model.SmsTemplateReq;
import com.mytijian.gotone.api.model.SmsTemplateUpdateReq;
import com.mytijian.gotone.api.model.beans.SmsTemplate;
import com.mytijian.gotone.api.model.enums.MsgTypeEnum;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.site.model.Site;
import com.mytijian.site.service.SiteService;
import com.mytijian.util.AssertUtil;
import com.mytijian.web.intercepter.Token;
import com.mytijian.wx.model.WxArticle;
import com.mytijian.wx.model.WxAutoReply;
import com.mytijian.wx.model.WxConfig;
import com.mytijian.wx.model.WxOpenAPIConfig;
import com.mytijian.wx.model.WxQrcode;
import com.mytijian.wx.sdk.Wx;
import com.mytijian.wx.sdk.WxConsts;
import com.mytijian.wx.sdk.WxException;
import com.mytijian.wx.service.WxArticleService;
import com.mytijian.wx.service.WxAutoReplyService;
import com.mytijian.wx.service.WxConfigService;
import com.mytijian.wx.service.WxOpenAPIConfigService;
import com.mytijian.wx.service.WxQrcodeService;

/**
 *
 * @author linzhihao
 */
@Controller
@RequestMapping("/wx")
public class WxConfigController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	@Resource(name = "wxConfigService")
	private WxConfigService wxConfigService;

	@Resource(name = "wxArticleService")
	private WxArticleService wxArticleService;

	@Resource(name = "siteService")
	private SiteService siteService;

	@Resource(name = "smsTemplateService")
	private SmsTemplateService smsTemplateService;

	@Resource(name = "wxQrcodeService")
	private WxQrcodeService wxQrcodeService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "accountService")
	private AccountService accountService;

	@Resource(name = "wxProps")
	private WxProps props;
	
	@Value("${wxOauth2redirectUrl}")
	private String wxOauth2redirectUrl;
	
	@Value("${mobileSiteBasePath}")
	private String mobileSiteBasePath;
	
	@Resource(name = "wxOpenAPIConfigService")
	private WxOpenAPIConfigService wxOpenAPIConfigService;

	/**
	 * 获取微信接入配置列表
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public Map<String, Object> list() {

		List<Hospital> hospitals = hospitalService.getAllOrganizations();
		List<WxConfig> wxConfigs = wxConfigService.listWxConfig();

		Map<String, Object> pageInfo = new HashMap<>();
		pageInfo.put("hospitals", hospitals);
		pageInfo.put("wxConfigs", wxConfigs);
		pageInfo.put("wxOauth2redirectUrl", wxOauth2redirectUrl);

		return pageInfo;
	}

	/**
	 * 增加微信接入配置
	 * 
	 * @param hospitalId
	 * @param appid
	 * @param appsecret
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/config/{hospitalId}", method = RequestMethod.POST)
	public Map<String, Object> init(@PathVariable("hospitalId") Integer hospitalId, String appid, String appsecret,
                                    HttpSession session) throws Exception {

		String wxMenuFilePath = session.getServletContext().getRealPath("/WEB-INF/wx_default_menu.txt");
		File file = new File(wxMenuFilePath);
		String temp = null;
		StringBuilder sb = new StringBuilder();
		if (file.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((temp = reader.readLine()) != null) {
				sb.append(temp.replace("\r\n", "").replace("\n", ""));
			}
			reader.close();
		}

		WxConfig config = wxConfigService.initWxConfig(hospitalId, appid, appsecret, sb.toString());

		initTepmlate(config);
		//
		// String templateId = Wx.addTemplate(config.getAccessToken(),
		// "OPENTM200438992");
		// Wx.getAllTemplate(config.getAccessToken());

		Map<String, Object> pageInfo = new HashMap<>();
		pageInfo.put("hid", config.getHid());
		pageInfo.put("token", config.getToken());

		return pageInfo;
	}

	private void initTepmlate(WxConfig config) throws Exception {

		List<SmsTemplate> templates = wxConfigService.initTemplate(config);

		if (AssertUtil.isNotEmpty(templates)) {
			SmsTemplateReq smsTemplateReq = new SmsTemplateReq();
			smsTemplateReq.setHospitalId(config.getHospitalId());
			smsTemplateReq.setPageNo(1);
			smsTemplateReq.setPageSize(1000);

			List<SmsTemplate> smsList = smsTemplateService.query(smsTemplateReq).getSmsTemplateList();
			templates.forEach((wxTemplate) -> {
				Optional<SmsTemplate> optional = smsList.stream()
						.filter((sms) -> wxTemplate.getCode().equalsIgnoreCase(sms.getCode())).findFirst();
				if (optional.isPresent()) {
					SmsTemplate existsTemplate = optional.get();
					SmsTemplateUpdateReq smsTemplateUpdateReq = new SmsTemplateUpdateReq();
					BeanUtils.copyProperties(existsTemplate, smsTemplateUpdateReq);
					smsTemplateUpdateReq.setWeixinMsgId(wxTemplate.getWeixinMsgId());
					smsTemplateUpdateReq.setUrl(wxTemplate.getUrl());
					smsTemplateUpdateReq.setMsgTypeEnum(MsgTypeEnum.getByValue(existsTemplate.getPriority()));
					this.smsTemplateService.update(smsTemplateUpdateReq);
				} else {
					SmsTemplateAddReq addReq = new SmsTemplateAddReq();
					BeanUtils.copyProperties(wxTemplate, addReq);
					addReq.setHospitalId(config.getHospitalId());
					addReq.setCustomized(0);
					this.smsTemplateService.add(addReq);
				}
			});
		}
	}

	/**
	 * 删除微信公众号接入
	 * 
	 * @param hospitalId
	 */
	@ResponseBody
	@RequestMapping(value = "/config/{hospitalId}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("hospitalId") Integer hospitalId) {
		this.wxConfigService.delete(hospitalId);
	}

	/**
	 * 修改微信菜单
	 * 
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/config/updateMenus", method = RequestMethod.POST)
	public void updateMenus(@RequestBody WxConfigVO vo) throws Exception {
		this.wxConfigService.updateMenus(vo.getHospitalId(), vo.getButtons());
	}

	/**
	 * 保存微信欢迎消息
	 * 
	 * @param vo
	 */
	@ResponseBody
	@RequestMapping(value = "/config/articles", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateArticle(@RequestBody WxConfigVO vo) {
		this.wxArticleService.overlay(vo.getHid(), vo.getArticles(), WxConsts.Events.SUBSCRIBE);
	}

	@ResponseBody
	@RequestMapping(value = "/config/articles/{hid}", method = RequestMethod.GET)
	public List<WxArticle> updateArticles(@PathVariable("hid") String hid) {
		List<WxArticle> list = this.wxArticleService.getArticlesByHid(hid, WxConsts.Events.SUBSCRIBE);
		return list;
	}

	@Token
	@ResponseBody
	@RequestMapping(value = "/config/template/{hid}", method = RequestMethod.POST)
	public void updateTemplate(@PathVariable("hid") String hid) throws Exception {
		WxConfig config = this.wxConfigService.findEnabledWxConfigByHid(hid);
		initTepmlate(config);
	}

	/**
	 * 公众号 启用、停用
	 * 
	 * @param hid
	 * @param enable
	 */
	@ResponseBody
	@RequestMapping(value = "/config/enable/{hid}/{enable}", method = RequestMethod.POST)
	public void updateEnable(@PathVariable("hid") String hid, @PathVariable("enable") boolean enable) {
		wxConfigService.updateEnable(hid, enable);
	}

	/**
	 * 清除公众号警告信息
	 * 
	 * @param hid
	 */
	@ResponseBody
	@RequestMapping(value = "/config/clearwarn/{hid}", method = RequestMethod.POST)
	public void clearWarn(@PathVariable("hid") String hid) {
		wxConfigService.clearWarn(hid);
	}

	/**
	 * 获取公众号二维码列表
	 * 
	 * @param hid
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/config/listqrcode/{hid}")
	public Map<String, Object> listQrcode(@PathVariable("hid") String hid) {
			WxQrcode query = new WxQrcode();
			query.setHid(hid);
			query.setCodeAction(WxConsts.QrcodeActions.Spread);
		List<WxQrcode> qrcodes = wxQrcodeService.listWxQrcodes(query);
		Map<String, Object> result = Maps.newHashMap();
		List<Account> accounts = Lists.newArrayList();
		qrcodes.forEach(code -> {
			String scene = code.getSceneStr();
			Map<String, String> map = WxEventKeyMatcher.matcher(scene);
			Integer accountId = Integer.parseInt(map.get("spread"));
			accounts.add(accountService.getAccountById(accountId));
		});

		result.put("qrcodes", qrcodes);
		result.put("accounts", accounts);
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/config/getAccountId/{hid}/{username}", method = RequestMethod.GET)
	public Map<String, Object> findUserName(@PathVariable("hid") String hid,
			@PathVariable("username") String username) {
		Map<String, Object> result = Maps.newHashMap();

		WxConfig config = wxConfigService.findEnabledWxConfigByHid(hid);
		if (config == null) {
			result.put("success", false);
			result.put("errmsg", "微信配置错误");
			return result;
		}
		Hospital hospital = hospitalService.getHospitalBaseInfoById(config.getHospitalId());

		int systemRoleId = SystemTypeEnum.CRM_LOGIN.getCode();
		if (OrganizationTypeEnum.CHANNEL.getCode() == hospital.getOrganizationType()) {
			systemRoleId = SystemTypeEnum.CHANNEL_LOGIN.getCode();
		}

		User user = userService.getUserBySystemType(username, systemRoleId);
		if (user != null) {
			result.put("success", true);
			result.put("accountId", user.getAccountId());
		} else {
			result.put("success", false);
			result.put("errmsg", "用户名不存在");
		}

		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/config/qrcode/{hid}/{scene}", method = RequestMethod.POST)
	public Map<String, Object> addWxQrcode(@PathVariable("hid") String hid, @PathVariable("scene") String scene) {
		Map<String, Object> result = Maps.newHashMap();
		WxQrcode qrcode = wxQrcodeService.builderNewWxQrcode(hid, scene, WxConsts.QrcodeActions.Spread, null);
		if (qrcode != null) {
			result.put("qrcode", qrcode);
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/config/uak/{hid}", method = RequestMethod.POST)
	public WxConfig uak(@PathVariable("hid") String hid) throws WxException {
		wxConfigService.updateAccessToken(hid);
		return wxConfigService.findEnabledWxConfigByHid(hid);
	}

	@ResponseBody
	@RequestMapping(value = "/config/deleteqrcode/{hid}/{scene}", method = RequestMethod.POST)
	public void deleteqrcode(@PathVariable("hid") String hid, @PathVariable("scene") String scene) {
		wxQrcodeService.delete(hid, scene);
	}

	@ResponseBody
	@RequestMapping(value = "/config/getsiteinfo")
	public Site getBaseUtrl(Integer hospitalId) {

		Site site = siteService.getSiteByHospitalId(hospitalId);
		if (site != null) {
			StringBuffer sbUrl = new StringBuffer(props.getMobileSiteBasePath());
			sbUrl.append("/").append(site.getUrl()).append("/");
			sbUrl.toString();
			site.setUrl(sbUrl.toString());
		}
		return site;
	}
	
	
	/**
	 * 图片消息
	 * @param hid
	 * @param title
	 * @param image
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/config/imagemsgqrcode/{hid}/{title}")
	public Map<String, Object> addImageWxWrCode(@PathVariable("hid") String hid, @PathVariable("title") String title, @RequestParam("image")MultipartFile image) throws Exception {
		Map<String, Object> result = new HashMap<>();
		
		if (image!=null) {
			WxConfig config = wxConfigService.findEnabledWxConfigByHid(hid);
			String mediaId = Wx.uploadImage(config.getAccessToken(), image.getBytes(), image.getOriginalFilename());
			if (mediaId!=null) {
				String scene =  WxConsts.Prefixs.Qrcode + "action_msg,msg_" + UUID.randomUUID().toString().replaceAll("-", "");
				
				System.out.println("scene: "+scene);
				
				WxQrcode qrcode = wxQrcodeService.builderNewWxQrcode(hid, scene, WxConsts.QrcodeActions.ImageMsg, title, mediaId);
				result.put("qrcode", qrcode);
				result.put("ticket", qrcode.getTicket());
			}
		} else {
			System.out.println("没收到");
		}
		return result;
	}
	
	@RequestMapping("/config/rest/{hid}/{target}")
	public Map<String, String> reset(@PathVariable("hid") String hid, @PathVariable("target") String target) {
		WxOpenAPIConfig api = wxOpenAPIConfigService.get();
		
		Map<String, String> map = new HashMap<>();
		if ("welcome".equals(target)) {
			
		}
		
		if ("menu".equals(target)) {
			WxConfig config = wxConfigService.findEnabledWxConfigByHid(hid);
			Site site = siteService.getSiteByHospitalId(config.getHospitalId());
			String menuString = api.getDefaultMenu();
			menuString = String.format(menuString, mobileSiteBasePath+"/"+site.getUrl());
			map.put("menu", menuString);
		}
		return map;
	}

	/**
	 * 图文消息、图片消息二维码
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/config/msgqrcode/{hid}")
	public Map<String, Object> addWxMsgQrcode(@PathVariable("hid") String hid, @RequestBody WxQrcodeDTO vo) {

		System.out.println("进入 controller");
		
		Map<String, Object> result = Maps.newHashMap();
		
		String scene =  WxConsts.Prefixs.Qrcode + "action_msg,msg_" + UUID.randomUUID().toString().replaceAll("-", "");
		
		Integer hospitalId =  wxConfigService.getHospitalIdByHid(hid);
		
		WxQrcode qrcode = wxQrcodeService.builderNewWxQrcode(hid, scene,vo.getCodeAction(), vo.getTitle());
	
		
		if (vo.getCodeAction() == WxConsts.QrcodeActions.TextMsg) {
			WxAutoReply reply = new WxAutoReply();
			reply.setHospitalId(hospitalId);
			reply.setRequest(scene);
			reply.setContent(vo.getMsg());
			wxAutoReplyService.save(reply);
		}
		
		if (vo.getCodeAction() == WxConsts.QrcodeActions.ArticleMsg) {
			wxArticleService.overlay(hid, vo.getArticles(), scene);
		}
		
		result.put("qrcode", qrcode);
		return result;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/config/updatemsgqrcode/{hid}", method = RequestMethod.POST)
	public void update (@PathVariable("hid") String hid, @RequestBody WxQrcodeDTO vo) {
		
		WxQrcode code = this.wxQrcodeService.getWxQrcode(hid, vo.getScene());
		code.setTitle(vo.getTitle());
		wxQrcodeService.update(code);
		
		if (code.getCodeAction()==WxConsts.QrcodeActions.TextMsg) {
			Integer hospitalId =  wxConfigService.getHospitalIdByHid(hid);
			WxAutoReply reply = new WxAutoReply();
			reply.setHospitalId(hospitalId);
			reply.setRequest(vo.getScene());
			reply.setContent(vo.getMsg());
			wxAutoReplyService.update(reply);
		}
		
		if (code.getCodeAction() == WxConsts.QrcodeActions.ArticleMsg) {
			wxArticleService.overlay(hid, vo.getArticles(), code.getSceneStr());
		}
		
	}

	@Resource(name = "wxAutoReplyService")
	private WxAutoReplyService wxAutoReplyService;
	
	@ResponseBody
	@RequestMapping(value = "/config/listmsgqrcode/{hid}")
	public List<WxQrcode> listMsgQrcodes(@PathVariable("hid")String hid) {
		WxQrcode qrcode = new WxQrcode();
		qrcode.setHid(hid);
		qrcode.setCodeAction(WxConsts.QrcodeActions.TextMsg);
		List<WxQrcode> codes = wxQrcodeService.listWxQrcodes(qrcode);
		// 获取 图文消息和图片消息
		qrcode.setCodeAction(WxConsts.QrcodeActions.ArticleMsg);
		codes.addAll(wxQrcodeService.listWxQrcodes(qrcode));
		qrcode.setCodeAction(WxConsts.QrcodeActions.ImageMsg);
		codes.addAll(wxQrcodeService.listWxQrcodes(qrcode));
		return codes;
	}
	
	/**
	 * @param hid
	 * @param scene
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/config/getqrcodemsg/{hid}/{scene}")
	public WxQrcodeDTO getQrcodeMsg(@PathVariable("hid") String hid, @PathVariable("scene")String scene) {
		
		WxQrcode qrcode = wxQrcodeService.getWxQrcode(hid, scene);
		WxQrcodeDTO result = new WxQrcodeDTO();
		result.setCodeAction(qrcode.getCodeAction());
		result.setTitle(qrcode.getTitle());
		result.setScene(qrcode.getSceneStr());
		
		// 文本消息二维码
		if (qrcode.getCodeAction() == WxConsts.QrcodeActions.TextMsg) {
			Integer hospitalId =  wxConfigService.getHospitalIdByHid(hid);
			WxAutoReply reply =  wxAutoReplyService.getWxAutoReply(hospitalId, scene);
			if (reply!=null) {
				result.setMsg(reply.getContent());
			}
		}
		
		// 图文消息二维码
		if(qrcode.getCodeAction() == WxConsts.QrcodeActions.ArticleMsg) {
			List<WxArticle> articles = wxArticleService.getArticlesByHid(hid, qrcode.getSceneStr());
			result.setArticles(articles);
		}
		
		// 图片消息二维码
		if (qrcode.getCodeAction() == WxConsts.QrcodeActions.ImageMsg) {
			Map<String, String> map = WxEventKeyMatcher.matcher(qrcode.getSceneStr());
			result.setMediaId(map.get("msg"));
		}
		return result;
	}
	

	@ResponseBody
	@RequestMapping(value = "/config/autoreplys/{hid}")
	public List<WxAutoReply> getAutoReplays(@PathVariable("hid") String hid) {
		Integer hospitalId =  wxConfigService.getHospitalIdByHid(hid);
		
		//
		// XXX 后期使用 字段标识 自动回复记录是系统的还是用户配置的
		//
		List<WxAutoReply> list = wxAutoReplyService.listWxAutoReplys(hospitalId, "^((?!WXQ,).)*$");
		return list;
	}
	
	@ResponseBody
	@RequestMapping(value = "/config/saveautoreply")
	public void saveAutoReply(@RequestBody WxAutoReply reply) {
		wxAutoReplyService.save(reply);
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/config/updateautoreply")
	public void updateAutoReply(@RequestBody WxAutoReply reply) {
		wxAutoReplyService.updateById(reply);
	}
	
	/**
	 * 开启关闭 微信客服功能
	 * @param hid
	 */
	@ResponseBody
	@RequestMapping(value = "/config/toggletransfercustomerservice/{hid}")
	public WxConfig toggletransfercustomerservice(@PathVariable("hid") String hid) {
		WxConfig config = wxConfigService.findEnabledWxConfigByHid(hid);
		boolean toggle = !AssertUtil.areEquals(config.getTransferCustomerService(), WxConfig.TRANSFER_CUSTOMER_SERVICE);
		wxConfigService.toggleTransferCustomerService(config.getHospitalId(), toggle);
		return wxConfigService.findEnabledWxConfigByHid(hid);
	}
	

}
