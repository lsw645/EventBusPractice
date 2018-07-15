package com.lxw.eventbuspractice.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/15
 *     desc   :
 * </pre>
 */
public class EventBus {
    private static volatile EventBus instance;
    private static final Map<Class, List<SubscribeMethod>> CACHE_METHOD = new HashMap<>();
    private static final Map<String, List<Subscription>> SUBSCRIPTIONS = new HashMap<>();
    private static final Map<Class<?>, List<String>> REGISTERS = new HashMap<>();

    private EventBus() {
    }

    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public void register(Object obj) {
        Class aClass = obj.getClass();
        List<SubscribeMethod> subscribeMethods = findSubsribeMethod(aClass);

        //放置到订阅表
        for (SubscribeMethod subscribeMethod : subscribeMethods) {
            String label = subscribeMethod.getLabel();

            {
                List<String> labelList = REGISTERS.get(aClass);
                if (null == labelList) {
                    labelList = new ArrayList<>();
                }
                if (!labelList.contains(label)) {
                    labelList.add(label);
                }
            }

            List<Subscription> subscriptions = SUBSCRIPTIONS.get(label);
            if (subscriptions == null) {
                subscriptions = new ArrayList<>();
                SUBSCRIPTIONS.put(label, subscriptions);
            }
            subscriptions.add(new Subscription(obj, subscribeMethod.getMethod()));
        }
    }

    public void unregister(Object obj) {
        List<String> labels = REGISTERS.remove(obj.getClass());
        if (null != labels) {
            for (String label : labels) {
                List<Subscription> subscriptions = SUBSCRIPTIONS.get(label);
                if (null != subscriptions) {
                    Iterator<Subscription> iterator = subscriptions.iterator();
                    while (iterator.hasNext()) {
                        Subscription next = iterator.next();
                        if (next.getSubscribeObject() == obj) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    public void post(String label, Object... params) {
        List<Subscription> subscriptions = SUBSCRIPTIONS.get(label);
        for (Subscription subscription : subscriptions) {
            Method subscribeMethod = subscription.getSubscribeMethod();
            Object subscribeObject = subscription.getSubscribeObject();
            Class<?>[] parameterTypes = subscribeMethod.getParameterTypes();
            Object[] realParams = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i < params.length && parameterTypes[i].isInstance(params[i])) {
                    realParams[i] = params[i];
                } else {
                    realParams[i] = null;
                }
            }
            try {
                subscribeMethod.invoke(subscribeObject, realParams);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    //查找缓存的方法
    private List<SubscribeMethod> findSubsribeMethod(Class clazz) {
        List<SubscribeMethod> subscribeMethods = CACHE_METHOD.get(clazz);
        if (subscribeMethods == null) {
            subscribeMethods = new ArrayList<>();
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                Subscribe annotation = declaredMethod.getAnnotation(Subscribe.class);
                if (null != annotation) {
                    for (String s : annotation.value()) {
                        SubscribeMethod subscribeMethod = new SubscribeMethod(s, declaredMethod, declaredMethod.getParameterTypes());
                        subscribeMethods.add(subscribeMethod);
                    }

                }
            }
            CACHE_METHOD.put(clazz, subscribeMethods);
        }
        return subscribeMethods;

    }


}
