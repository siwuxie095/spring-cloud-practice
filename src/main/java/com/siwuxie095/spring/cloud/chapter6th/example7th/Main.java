package com.siwuxie095.spring.cloud.chapter6th.example7th;

/**
 * @author Jiajing Li
 * @date 2021-06-12 22:41:08
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 构建接收关联 ID 的后置过滤器
     *
     * 记住，Zuul 代表服务客户端执行实际的 HTTP 调用。Zuul 有机会从目标服务调用中检查响应，然后修改响应或以额外
     * 的信息装饰它。当与以前置过滤器捕获数据相结合时，Zuul 后置过滤器是收集指标并完成与用户事务相关联的日志记录
     * 的理想场所。这里将利用这一点，通过将已经传递给微服务的关联 ID 注入回用户。
     *
     * 这里将使用 Zuul 后置过滤器将关联 ID 注入 HTTP 响应首部中，该 HTTP 响应首部传回给服务调用者。这样，就可
     * 以将关联 ID 传回给调用者，而无需接触消息体。如下是构建后置过滤器的代码。
     *
     * @Component
     * public class ResponseFilter extends ZuulFilter {
     *
     *     private static final int  FILTER_ORDER = 1;
     *     private static final boolean  SHOULD_FILTER = true;
     *     private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);
     *
     *     @Autowired
     *     FilterUtils filterUtils;
     *
     *     // 要构建一个后置过滤器，需要设置过滤器的类型为 POST_FILTER_TYPE
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
     *                 filterUtils.getCorrelationId());
     *
     *         // 获取原始 HTTP 请求中传入的关联 ID，并将它注入响应中
     *         ctx.getResponse().addHeader(FilterUtils.CORRELATION_ID,
     *                 filterUtils.getCorrelationId());
     *
     *         // 记录传出的请求 URI，这样就有了 "书挡"，它将显示进入 Zuul 的用户请求的传入和传出条目
     *         logger.debug("Completing outgoing request for {}.",
     *                 ctx.getRequest().getRequestURI());
     *
     *         return null;
     *     }
     *
     * }
     *
     * 实现完 ResponseFilter 之后，就可以启动 Zuul 服务，并通过它调用 EagleEye 许可证服务。服务完成后，就可以
     * 在调用的 HTTP 响应首部上看到一个 tmx-correlation-id。
     *
     * 到目前为止，所有的过滤器示例都是在路由到目的地之前或之后对服务客户端调用进行操作。后续将看看最后一个过滤器示
     * 例，如何动态地更改用户要到达的目标路径。
     */
    public static void main(String[] args) {

    }

}
