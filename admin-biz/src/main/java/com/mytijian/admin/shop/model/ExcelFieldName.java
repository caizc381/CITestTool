package com.mytijian.admin.shop.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelFieldName {

	/**
	 * 体检中心导入模板文件，头字段名字
	 * @return
	 */
	String fieldName() default "";
	
	/**
	 * 字段错误提示信息
	 * @return
	 */
	String errorTip() default "";



}
