package com.siwuxie095.spring.cloud.organization.hystrix;

import com.siwuxie095.spring.cloud.authentication.utils.UserContext;
import com.siwuxie095.spring.cloud.authentication.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:06:13
 */
@SuppressWarnings("all")
public final class DelegatingUserContextCallable<V> implements Callable<V> {
    private static final Logger logger = LoggerFactory.getLogger(DelegatingUserContextCallable.class);
    private final Callable<V> delegate;



    //private final UserContext delegateUserContext;
    private UserContext originalUserContext;

    public DelegatingUserContextCallable(Callable<V> delegate,
                                         UserContext userContext) {
        Assert.notNull(delegate, "delegate cannot be null");
        Assert.notNull(userContext, "userContext cannot be null");
        this.delegate = delegate;
        this.originalUserContext = userContext;
    }

    public DelegatingUserContextCallable(Callable<V> delegate) {
        this(delegate, UserContextHolder.getContext());
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

    @Override
    public String toString() {
        return delegate.toString();
    }


    public static <V> Callable<V> create(Callable<V> delegate,
                                         UserContext userContext) {
        return new DelegatingUserContextCallable<V>(delegate, userContext);
    }
}
