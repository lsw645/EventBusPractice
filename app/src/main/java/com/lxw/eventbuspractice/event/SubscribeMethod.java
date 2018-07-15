package com.lxw.eventbuspractice.event;

import java.lang.reflect.Method;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/15
 *     desc   :
 * </pre>
 */
public class SubscribeMethod {
    private String label;
    private Method method;
    private Class<?>[] parameterTypes;

    public SubscribeMethod(String label, Method method,  Class<?>[] parameterizedType) {
        this.label = label;
        this.method = method;
        this.parameterTypes = parameterizedType;
    }

    public String getLabel() {
        return label;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParameterizedType() {
        return parameterTypes;
    }
}
