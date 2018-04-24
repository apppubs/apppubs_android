package com.apppubs.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by siger on 2018/4/20.
 */

public class NetHelper<T> {
    T t;
    public void amyMethod(T t){
        ParameterizedType tt = (ParameterizedType) getClass()
                .getGenericSuperclass();

        /**
         * 这里如果传递的是User.那么就是class com.example.bean.User
         * 如果传递的是Shop.       那么就是class com.example.bean.Shop
         * */
        Class<T> clazz = (Class<T>) tt.getActualTypeArguments()[0];
        System.out.println("名字："+clazz.getName());
        final Method[] methods = NetHelper.this.getClass().getMethods();
        for (final Method m : methods) {
            System.out.println("方法名称"+m.getName());
            Type[] types =  m.getGenericParameterTypes();
            for (Type type : types){
                if (type instanceof ParameterizedType){
                    ParameterizedType pt = (ParameterizedType)type;
                    Type[] ats = pt.getActualTypeArguments();
                    for (Type at : ats){
                        if (at instanceof ParameterizedType){
                            ParameterizedType npt = (ParameterizedType)at;

                            System.out.println("泛型参数实际类型："+npt.getRawType().toString());
                        }
                    }
                }
            }
        }
    }
}
