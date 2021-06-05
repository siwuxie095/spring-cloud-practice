package com.siwuxie095.spring.cloud.licenses.utils;

import org.springframework.util.Assert;

/**
 * @author Jiajing Li
 * @date 2021-06-05 22:55:39
 */
@SuppressWarnings("all")
public class UserContextHolder {

    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<UserContext>();

    public static final UserContext getContext(){
        UserContext context = userContext.get();

        if (context == null) {
            context = createEmptyContext();
            userContext.set(context);

        }
        return userContext.get();
    }

    public static final void setContext(UserContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        userContext.set(context);
    }

    public static final UserContext createEmptyContext(){
        return new UserContext();
    }

}

