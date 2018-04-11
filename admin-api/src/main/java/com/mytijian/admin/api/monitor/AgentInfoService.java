package com.mytijian.admin.api.monitor;

import java.util.Map;

import com.mytijian.base.page.Page;
import com.mytijian.base.page.PageView;


public interface AgentInfoService {
	
	@SuppressWarnings("rawtypes")
	PageView<Map> getAgentInfo(Page page);
}
