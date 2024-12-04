package com.itheima.anno;

import com.auth0.jwt.interfaces.Payload;
import com.itheima.validation.StateValidation;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotEmpty;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented//元注解，表示该注解会被包含在javadoc中
@Constraint(validatedBy = {StateValidation.class})//指定校验逻辑处理类
@Target({ FIELD})
@Retention(RUNTIME)

public @interface State {
    //提供校验失败后的提示信息
    String message() default "State参数的值只能是已发布或者草稿";
    //指定分组
    Class<?>[] groups() default {};
    //负载 获取到State注解的附加信息
    Class<? extends Payload>[] payload() default {};
}
