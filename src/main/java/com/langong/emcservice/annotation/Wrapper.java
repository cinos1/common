package com.langong.emcservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Wrapper {

    // Dong ZhaoYang 2017/8/7 查询方式
    String SubTableName() default "";
    String FK() default "";
    String PK() default "id";

    boolean ASC() default false;
    boolean DESC() default false;

    Type type() default Type.EQ;

    boolean Nullable() default true;

    enum Type {
        EQ, // 等于 =
        NE, //不等于 <>
        GT, //大于 >
        GE, // 大于等于 >=
        LT, //小于 <
        LE, //小于等于 <=
        BETWEEN, //BETWEEN 值1 AND 值2
        NOT_BETWEEN, //NOT BETWEEN 值1 AND 值2
        LIKE, //LIKE '%值%'
        NOT_LIKE, //NOT LIKE '%值%'
        LIKE_LEFT, // LIKE '%值'
        LIKE_RIGHT, //LIKE '值%'
        IS_NULL, //字段 IS NULL
        IS_NOT_NULL, // 字段 IS NOT NULL
        IN, //字段 IN (value.get(0), value.get(1), ...)
        NOT_IN,// 字段 NOT IN (value.get(0), value.get(1), ...)

        ONE2ONE, //1对1
        ONE2MANY,//1对多
    }
}
