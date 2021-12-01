package com.cyneck.workflow.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description:swagger枚举类解析 </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/20 23:14
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerDisplayEnum {

    String value() default "value";

    String name() default "name";
}
