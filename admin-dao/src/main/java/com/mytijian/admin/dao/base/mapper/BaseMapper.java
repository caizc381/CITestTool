package com.mytijian.admin.dao.base.mapper;

public interface BaseMapper<T> {
	
	/**
	 * 增加
	 * @param t
	 * @return
	 */
	public int insert(T t);
	
	
	/**
	 * 修改
	 * @param t
	 * @return
	 */
	public int update(T t);
	
	/**
	 * 根据主键删除
	 * @param id
	 */
	public void deleteById(Integer id);

	/*@Deprecated
	public T queryById(Integer id);*/
	
	/**
	 * 根据主键Id查询
	 * @param id 主键Id
	 * @return
	 */
	T selectById(Integer id);
}
