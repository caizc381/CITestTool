package com.mytijian.mediator.company.migrate.dao.order;

import com.mytijian.mediator.company.migrate.dao.dataobj.order.OrderDO;
import com.mytijian.pulgin.mybatis.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {

	/**
	 *  查询所有渠道订单
	 * @return
	 */
	List<OrderDO> selectAllChannelOrders();

	/**
	 * 查询所有订单表的order_manager_id为空的记录
	 * @return
	 */
	List<OrderDO> selectAllManagerIdIsNullOrdersByPage(Page page);

	/**
	 * 查询所有订单表的order_manager_id为空的记录数量
	 * @return
	 */
	int selectAllManagerIdIsNullOrdersCount();

	/**
	 * 更新订单do对象
	 * @param orderDO 订单do对象
	 * @return
	 */
	int updateOrder(@Param("orderDO") OrderDO orderDO);


	/**
	 * 获取所有channelCompany是空的
	 * @return
	 */
	List<String> selectOrderExamDetail();


	/**
	 * 根据订单号，获取订单的渠道单位id
	 * @param orderNum 订单号
	 * @return
	 */
	Integer selectChannelCompanyId(String orderNum);


	/**
	 * 更新订单详情
	 * @param orderNum
	 * @param channelCompany
	 */
	void updateOrderDetail(@Param("orderNum") String orderNum,@Param("channelCompany")String channelCompany);

}
