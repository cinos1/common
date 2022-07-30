package com.langong.emcservice.util;

import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

public class MapToBean {
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }
}
