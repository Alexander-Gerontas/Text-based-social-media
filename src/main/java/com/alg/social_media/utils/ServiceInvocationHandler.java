package com.alg.social_media.utils;

import jakarta.transaction.Transactional;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ServiceInvocationHandler implements InvocationHandler {
    private final Object service;

    public ServiceInvocationHandler(final Object service) {
        this.service = service;
    }

    public static <T> T wrap(T target) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new ServiceInvocationHandler(target)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Check if the method has the transactional annotation
        if (service.getClass().isAnnotationPresent(Transactional.class)) {
            DBUtils.DbTransactionResultOperation<Object> operation =
                entityManager -> {
                    // Call the original method using reflection
                    var result = method.invoke(service, args);

                    return result;
                };

            return DBUtils.executeWithTransactionResultPropagation(operation);
        }

        // execute the class method and return the result if annotation not present
        return method.invoke(service, args);
    }
}
