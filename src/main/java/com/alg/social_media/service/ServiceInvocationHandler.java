package com.alg.social_media.service;

import com.alg.social_media.utils.DBUtils;
import jakarta.transaction.Transactional;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ServiceInvocationHandler implements InvocationHandler {
    private final Object service;
    private final DBUtils dbUtils;

    public ServiceInvocationHandler(final Object service, final DBUtils dbUtils) {
        this.service = service;
        this.dbUtils = dbUtils;
    }

    public static <T> T wrap(T target, DBUtils dbUtils) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new ServiceInvocationHandler(target, dbUtils)
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

            return dbUtils.executeWithTransactionResultPropagation(operation);
        }

        // execute the class method and return the result if annotation not present
        return method.invoke(service, args);
    }
}
