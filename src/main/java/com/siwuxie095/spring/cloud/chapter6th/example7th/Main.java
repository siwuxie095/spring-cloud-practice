package com.siwuxie095.spring.cloud.chapter6th.example7th;

/**
 * @author Jiajing Li
 * @date 2021-06-12 22:41:08
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 创建 post 类型 Zuul 过滤器接收关联 ID
     *
     * 记住，Zuul 代表服务客户端执行实际的 HTTP 调用。Zuul 有机会检查从目标服务调用返回的响应并修改或使用
     * 附加的信息装饰响应。当加上使用前置过滤器采集数据时，一个 Zuul 后置过滤器是收集数据和完成与用户的事务
     * 相关的任何记录的理想地点。你要利用这个来注入关联 ID，你已经通过你的微服务返回到用户。
     *
     * 你打算通过使用一个 Zuul 后置过滤器注入关联 ID 到正在被传递回给服务调用者的 HTTP 响应头。通过这种方
     * 式，你可以将关联 ID 传递给调用者，而无需接触消息体。如下所示。
     *
     * @Component
     * public class ResponseFilter extends ZuulFilter {
     *     private static final int  FILTER_ORDER=1;
     *     private static final boolean  SHOULD_FILTER=true;
     *     private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);
     *
     *     @Autowired
     *     FilterUtils filterUtils;
     *
     *     @Override
     *     public String filterType() {
     *         return FilterUtils.POST_FILTER_TYPE;
     *     }
     *
     *     @Override
     *     public int filterOrder() {
     *         return FILTER_ORDER;
     *     }
     *
     *     @Override
     *     public boolean shouldFilter() {
     *         return SHOULD_FILTER;
     *     }
     *
     *     @Override
     *     public Object run() {
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *
     *         logger.debug("Adding the correlation id to the outbound headers. {}",
     *         filterUtils.getCorrelationId());
     *         ctx.getResponse().addHeader(FilterUtils.CORRELATION_ID,
     *         filterUtils.getCorrelationId());
     *
     *         logger.debug("Completing outgoing request for {}.",
     *         ctx.getRequest().getRequestURI());
     *
     *         return null;
     *     }
     * }
     *
     * ResponseFilter 实现后，你可以启动你的 Zuul 服务，并通过它调用 EagleEye 的许可服务。一旦服务完成
     * 后，你会在调用的 HTTP 响应头看到一个 tmx-correlation-id。
     *
     * 到目前为此，所有的过滤器示例都处理了在路由到目标目的地之前和之后的服务客户端调用。后续将看看最后一个
     * 过滤器示例，如何动态地更改你要将用户发送到的目标路由。
     */
    public static void main(String[] args) {

    }

}
