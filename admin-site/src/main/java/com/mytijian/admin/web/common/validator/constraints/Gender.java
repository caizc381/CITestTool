package com.mytijian.admin.web.common.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.google.common.base.Objects;
import com.mytijian.offer.examitem.constant.enums.GenderEnum;
/**
 * 验证性别合法性
 * @author yuefengyang
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Gender.Validator.class)
@Target(ElementType.FIELD)
public @interface Gender {
	String message() default "性别字段无效";

	boolean allowNull() default false;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	public class Validator implements ConstraintValidator<Gender, Integer> {

		boolean allowNull;

		@Override
		public void initialize(Gender constraintAnnotation) {
			allowNull = constraintAnnotation.allowNull();
		}

		@Override
		public boolean isValid(Integer value, ConstraintValidatorContext context) {
			if (value == null) {
				return allowNull;
			}

			if (Objects.equal(value, GenderEnum.COMMON.getCode())) {
				return true;
			}
			if (Objects.equal(value, GenderEnum.MALE.getCode())) {
				return true;
			}
			if (Objects.equal(value, GenderEnum.FEMALE.getCode())) {
				return true;
			}
			return false;
		}

	}

}
