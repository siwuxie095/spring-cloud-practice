package com.siwuxie095.spring.cloud.licenses.hystrix;


import com.siwuxie095.spring.cloud.licenses.utils.UserContext;
import com.siwuxie095.spring.cloud.licenses.utils.UserContextHolder;

import java.util.concurrent.Callable;

/**
 * @author Jiajing Li
 * @date 2021-06-14 20:44:51
 */
@SuppressWarnings("all")
public final class DelegatingUserContextCallable<V> implements Callable<V> {
    private final Callable<V> delegate;
    private UserContext originalUserContext;

    public DelegatingUserContextCallable(Callable<V> delegate,
                                         UserContext userContext) {
        this.delegate = delegate;
        this.originalUserContext = userContext;
    }

    @Override
    public V call() throws Exception {
        UserContextHolder.setContext(originalUserContext);

        try {
            return delegate.call();
        }
        finally {
            this.originalUserContext = null;
        }
    }

    public static <V> Callable<V> create(Callable<V> delegate,
                                         UserContext userContext) {
        return new DelegatingUserContextCallable<V>(delegate, userContext);
    }
}
