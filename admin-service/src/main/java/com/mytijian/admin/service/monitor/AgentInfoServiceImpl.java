package com.mytijian.admin.service.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mytijian.admin.api.monitor.AgentInfoService;
import com.mytijian.base.page.Page;
import com.mytijian.base.page.PageView;

@Service("agentInfoService")
public class AgentInfoServiceImpl implements AgentInfoService {

	@Resource(name = "mongoTemplate")
	private MongoTemplate mongoTemplate;

	private final String COLLECTION_NAME = "agentInfo";

	@SuppressWarnings("rawtypes")
	@Override
	public PageView<Map> getAgentInfo(Page page) {
		DBCollection coll = mongoTemplate.getCollection(COLLECTION_NAME);
		int rowCount = coll.find().count();
		page.setRowCount(rowCount);
		int count = page.getCurrentPage() < 1 ? 0 : (page.getCurrentPage() - 1) * page.getPageSize();
		List<DBObject> dbo = coll.find().skip(count).limit(page.getPageSize()).toArray();
		List<Map> list = parseToMap(dbo);
		return new PageView<>(list, page);
	}

	@SuppressWarnings("rawtypes")
	private List<Map> parseToMap(List<DBObject> orders) {
		List<Map> list = new ArrayList<>();
		for (DBObject obj : orders) {
			list.add(obj.toMap());
		}
		return list;
	}

}
