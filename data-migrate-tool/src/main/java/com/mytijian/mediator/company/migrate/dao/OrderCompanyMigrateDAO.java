package com.mytijian.mediator.company.migrate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.mytijian.order.base.snapshot.model.HospitalExamCompanySnapshot;
import com.mytijian.order.model.Order;
import com.mytijian.resource.model.Hospital;
import com.mytijian.util.AssertUtil;

@Repository("orderCompanyMigrateDAO")
public class OrderCompanyMigrateDAO implements ApplicationContextAware{

	DataSource dataSource;
	
	ApplicationContext applicationContext;
	


	public OrderIterator selectAllOrderNum(String migratehspIds, String notMigratehspIds) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select a.id,a.order_num,a.hospital_company_id,a.hospital_id,a.operator_id,a.batch_id,a.source,a.from_site,a.old_exam_company_id,b.manager_id from tb_order a left join tb_order_relation b on a.id = b.order_id where a.hospital_company_id is not null and a.hospital_company_id=a.old_exam_company_id");
		if ("all".equals(migratehspIds) && AssertUtil.isNotEmpty(notMigratehspIds)) {
			sql.append(" and a.hospital_id not in (").append(notMigratehspIds).append(")");
		}else if(!"all".equals(migratehspIds) && AssertUtil.isNotEmpty(migratehspIds)){
			sql.append(" and a.hospital_id in (").append(migratehspIds).append(")");
		}else{
			throw new SQLException("migratehspIds或notMigratehspIds参数错误，请访问migrateDataInfo接口查看详细说明");
		}
		PreparedStatement pstmt = getConnection()
				.prepareStatement(sql.toString());
		return new OrderIterator(pstmt);
	}

	public Set<Order> selectOrderNum(Integer hospitalId) throws SQLException {
		Connection conn = getConnection();
		Set<Order> platformManagerIdSet = new HashSet();
		String sql ="select order_num,old_exam_company_id from tb_order where hospital_id ="+hospitalId;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet result = pstmt.executeQuery();
		while(result.next()){
			Order order = new Order();
			order.setOrderNum(result.getString(1));
			order.setExamCompanyId(result.getInt(2));
			platformManagerIdSet.add(order);
		}
		close(conn,  pstmt,  result);
		return platformManagerIdSet;
	}

	public void updateOrderDetail(String orderNum,Integer companyId) throws SQLException {
		Connection conn = getConnection();
		// 更新订单详情
		PreparedStatement queryPstmt = conn
				.prepareStatement("select hospital_company from tb_exam_order_details where order_num = ?");
		queryPstmt.setString(1, orderNum);
		ResultSet result = queryPstmt.executeQuery();
		PreparedStatement updateRelPstmt = null;
		if(result.next()){
			HospitalExamCompanySnapshot hspCompany = JSON.parseObject(result.getString(1),
					HospitalExamCompanySnapshot.class);
			hspCompany.setId(companyId);

			updateRelPstmt = conn
					.prepareStatement("update tb_exam_order_details set hospital_company=? where order_num = ?");
			updateRelPstmt.setString(1, JSON.toJSONString(hspCompany));
			updateRelPstmt.setString(2, orderNum);
			updateRelPstmt.execute();
		}

		close(conn,  queryPstmt,  result);
		close(conn,  updateRelPstmt,  result);
	}
	
	public Set<Integer> selectAllPlatformManager() throws SQLException {
		
		Set<Integer> platformManagerIdSet = new HashSet<Integer>();
		String sql ="select account_id from tb_account_role where role_id=4 or role_id =13";
		Connection conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql.toString());
		ResultSet result = pstmt.executeQuery();
		while(result.next()){
			platformManagerIdSet.add(result.getInt(1));
		}
		close(conn,  pstmt,  result);
		return platformManagerIdSet;
	}
	public Set<Integer> selectAllMCompanyId() throws SQLException {

		Set<Integer> platformManagerIdSet = new HashSet<Integer>();
		String sql ="select id from tb_exam_company where type = 4";
		Connection conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql.toString());
		ResultSet result = pstmt.executeQuery();
		while(result.next()){
			platformManagerIdSet.add(result.getInt(1));
		}
		close(conn,  pstmt,  result);
		return platformManagerIdSet;
	}

	private Connection getConnection() throws SQLException{
		if(dataSource == null){
			this.dataSource = applicationContext.getBean("defaultDataSource", DataSource.class);
		}
		return dataSource.getConnection();
	}

	public void updateOrderCompanyId(Integer orderId, Integer orderBatchId, String orderNum, Integer companyId, Integer channelCompanyId) throws SQLException {
		Connection conn = getConnection();
		boolean tranFlag = conn.getAutoCommit();
		conn.setAutoCommit(false);
		// 更新order,order_relation
		StringBuilder orderSql=new StringBuilder("update tb_order set hospital_company_id=?");
		if(channelCompanyId != null ){
			orderSql.append(", channel_company_id=?");
		}
		orderSql.append(" where id = ?");
		PreparedStatement orderPstmt = conn
				.prepareStatement(orderSql.toString());
		orderPstmt.setInt(1, companyId);
		if(channelCompanyId != null ){
			orderPstmt.setInt(2, channelCompanyId);
			orderPstmt.setInt(3, orderId);
		}else{
			orderPstmt.setInt(2, orderId);
		}
		orderPstmt.execute();
		PreparedStatement orderBatchPstmt = conn
				.prepareStatement("update tb_order_batch set company_id=? where id = ?");
		orderBatchPstmt.setInt(1, companyId);
		orderBatchPstmt.setInt(2, orderBatchId);
		orderBatchPstmt.execute();
		PreparedStatement orderRelPstmt = conn
				.prepareStatement("update tb_order_relation set exam_company_id=? where order_id = ?");
		orderRelPstmt.setInt(1, companyId);
		orderRelPstmt.setInt(2, orderId);
		orderRelPstmt.execute();
		// 更新订单详情
		PreparedStatement queryPstmt = conn
				.prepareStatement("select hospital_company from tb_exam_order_details where order_num = ?");
		queryPstmt.setString(1, orderNum);
		ResultSet result = queryPstmt.executeQuery();
		PreparedStatement updateRelPstmt = null;
		if(result.next()){
			HospitalExamCompanySnapshot hspCompany = JSON.parseObject(result.getString(1),
					HospitalExamCompanySnapshot.class);
			hspCompany.setId(companyId);
			
			updateRelPstmt = conn
					.prepareStatement("update tb_exam_order_details set hospital_company=? where order_num = ?");
			updateRelPstmt.setString(1, JSON.toJSONString(hspCompany));
			updateRelPstmt.setString(2, orderNum);
			updateRelPstmt.execute();
		}

		conn.commit();
		conn.setAutoCommit(tranFlag);

		close(conn,  orderPstmt,  result);
		close(conn,  orderRelPstmt,  result);
		close(conn,  queryPstmt,  result);
	}

	public Integer selectHspCompanyId(Integer oldCompanyId, Integer hospitalId) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement queryPstmt = conn
				.prepareStatement("select id from tb_hospital_company where tb_exam_company_id = ? and organization_id = ?");
		queryPstmt.setInt(1, oldCompanyId);
		queryPstmt.setInt(2, hospitalId);
		ResultSet result = queryPstmt.executeQuery();
		Integer hspCompanyId = null;
		if(result.next()){
			hspCompanyId = result.getInt(1);
		}
		close(conn,  queryPstmt,  result);
		return hspCompanyId;
	}

	private void close(Connection conn, PreparedStatement pstmt, ResultSet result) throws SQLException
	{
		if (result != null && !result.isClosed()){
			result.close();
		}
		if (pstmt != null && !pstmt.isClosed()){
			pstmt.close();
		}
		if (conn != null && !conn.isClosed()){
			conn.close();
		}
	}
	public class OrderIterator implements Iterator<Order> {
		
		ResultSet result;
		PreparedStatement pstmt;
		
		public OrderIterator(PreparedStatement pstmt) throws SQLException {
			this.pstmt = pstmt;
			this.result = pstmt.executeQuery();
		}
		
		@Override
		public  boolean hasNext() {
			try {
				if (result.next()) {
					return true;
				} else {
					result.close();
					pstmt.close();
					return false;
				}
			} catch (SQLException e) {
				return false;
			}
		}
		
		@Override
		public Order next() {
			try {
				Order order = new Order();
				order.setId(result.getInt(1));
				order.setOrderNum(result.getString(2));
				order.setExamCompanyId(result.getInt(3));
				Hospital hsp = new Hospital();
				hsp.setId(result.getInt(4));
				order.setHospital(hsp);
				order.setOperatorId(result.getInt(5));
				order.setBatchId(result.getInt(6));
				order.setSource(result.getInt(7));
				order.setFromSite(result.getInt(8));
				order.setOldExamCompanyId(result.getInt(9));
				order.setOwnerId(result.getInt(10));
				return order;
			} catch (SQLException e) {
				return null;
			}
		}
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}

