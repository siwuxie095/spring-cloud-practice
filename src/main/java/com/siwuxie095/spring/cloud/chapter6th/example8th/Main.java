package com.siwuxie095.spring.cloud.chapter6th.example8th;

/**
 * @author Jiajing Li
 * @date 2021-06-12 22:47:11
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 创建动态路由过滤器
     *
     * 最后的 Zuul 过滤器，要看的是 Zuul 路由过滤器。如果没有一个自定义的路由过滤器，Zuul 将使用之前看到的基于
     * 映射的定义来完成路由。然而，通过构建一个 Zuul 路由过滤器，你可以为服务客户端调用将如何路由增加智能。
     *
     * 在这一部分中，你将通过创建一个路由过滤器了解 Zuul 的路由过滤器，这将将允许你做服务新版本的 A/B 测试。A/B
     * 测试是在你推出一个新功能，然后有一部分用户使用该特性，其余的用户仍然使用旧服务。在本例中，你将模拟出一个组
     * 织服务的新版本，你希望其中 50% 的用户转到旧服务，50% 的用户转到新服务。
     *
     * 为此你需要创建一个称为 SpecialRoutesZuulFilter 的 Zuul 路由过滤器，它将获取通过 Zuul 被调用的服务的
     * Eureka 服务 ID，并调用其它称为 SpecialRoutes 的微服务。SpecialRoutes 服务将检查一个内部数据库看服务
     * 名称是否存在。如果目标服务名称存在，它将返回一个权重和服务的另一个位置的目标目的地。SpecialRoutesFilter
     * 将获取返回的权重，并根据权重随机生成一个数，这个数将被用来确定用户的调用将被路由到其它的组织服务或在 Zuul
     * 路由映射定义的组织服务。
     *
     * 在服务客户端通过 Zuul 调用一个 "前端" 服务之后，SpecialRoutesFilter 采取会以下行动：
     * （1）SpecialRoutesFilter 检索正在被调用的服务的服务 ID。
     * （2）SpecialRoutesFilter 调用 SpecialRoutes 服务。SpecialRoutes 服务检查是否有为目标端点替代的端
     * 点定义。如果记录被发现，它包含的权重将会告诉 Zuul，应按服务调用的百分比分别发送到旧服务和新服务。
     * （3）然后，SpecialRoutesFilter 生成一个随机数，并将其与 SpecialRoutes 服务返回的权重进行比较。如果
     * 随机生成的数字小于替代端点的权重，SpecialRoutesFilter 发送请求到服务的新版本。
     * （4）如果 SpecialRoutesFilter 将请求发送到服务的新版本，Zuul 保持原有预定义的管道和通过任何定义的后
     * 置过滤器发送从替代服务端点返回的响应。
     *
     *
     *
     * 1、构建路由过滤器的框架
     *
     * 下面开始介绍你用来创建 SpecialRoutesFilter 的代码。到目前为止，看到的所有过滤器，实现一个 Zuul 路由过
     * 滤器需要最多的编码工作，因为由一个路由过滤器接管 Zuul 的核心部分功能，路由和使用你自己的功能替代它。这里
     * 不打算详细讨论整个类，而是仔细研究相关的细节。
     *
     * SpecialRoutesFilter 遵循与其它 Zuul 过滤器一样的基本模式。它扩展了 ZuulFilter 类并设置 filterType()
     * 方法返回 "route" 值。这里不会去考虑任何更多关于 filterOrder() 和 shouldFilter() 方法的解释，因为它们
     * 不同于之前讨论的过滤器。下面的代码显示了路由过滤器框架。
     *
     * @Component
     * public class SpecialRoutesFilter extends ZuulFilter {
     *
     *     @Override
     *     public String filterType() {
     *         return FilterUtils.ROUTE_FILTER_TYPE;
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
     *         return null;
     *     }
     *
     * }
     *
     *
     *
     * 2、实现 run 方法
     *
     * SpecialRoutesFilter 真正工作的开始是在 run() 方法中。如下所示。
     *
     *     @Override
     *     public Object run() {
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *
     *         AbTestingRoute abTestRoute = getAbRoutingInfo(filterUtils.getServiceId());
     *
     *         if (abTestRoute!=null && useSpecialRoute(abTestRoute)) {
     *             String route = buildRouteString(ctx.getRequest().getRequestURI(),
     *                     abTestRoute.getEndpoint(),
     *                     ctx.get("serviceId").toString());
     *             forwardToSpecialRoute(route);
     *         }
     *
     *         return null;
     *     }
     *
     * 这段代码的一般流程是：当一个路由请求触发 SpecialRoutesFilter 的 run() 方法，它将执行一个向
     * SpecialRoutes 服务的 REST 调用。此服务将执行查找，并确定被调用的目标服务的 Eureka 服务 ID
     * 是否在路由记录中存在。在 getAbRoutingInfo() 方法中调用 SpecialRoutes 服务。
     *
     * getAbRoutingInfo() 方法如下所示。
     *
     *     private AbTestingRoute getAbRoutingInfo(String serviceName){
     *         ResponseEntity<AbTestingRoute> restExchange = null;
     *         try {
     *             restExchange = restTemplate.exchange(
     *                     "http://specialroutesservice/v1/route/abtesting/{serviceName}",
     *                     HttpMethod.GET,
     *                     null, AbTestingRoute.class, serviceName);
     *         }
     *         catch(HttpClientErrorException ex){
     *             if (ex.getStatusCode()== HttpStatus.NOT_FOUND) {
     *                 return null;
     *             }
     *             throw ex;
     *         }
     *         return restExchange.getBody();
     *     }
     *
     * 一旦你已经确定有一个目标服务的路由记录，你需要确定你是否应该路由目标服务请求到替代服务的位置或由
     * Zuul 路由集合管理的静态默认服务位置。为了做这个决定，你调用 useSpecialRoute() 方法。如下所示。
     *
     *     public boolean useSpecialRoute(AbTestingRoute testRoute){
     *         Random random = new Random();
     *
     *         if (testRoute.getActive().equals("N")) {
     *             return false;
     *         }
     *
     *         int value = random.nextInt((10 - 1) + 1) + 1;
     *
     *         if (testRoute.getWeight()<value) {
     *             return true;
     *         }
     *
     *         return false;
     *     }
     *
     * 这个方法做了两件事。首先，该方法检查从 SpecialRoutes 服务返回的 AbTestingRoute 记录上的有效
     * 域。如果记录设置为 "N"，useSpecialRoute() 方法不做任何事情，因为在这一刻你不想做任何路由。其
     * 次，该方法生成一个在 1 到 10 之间的随机数。然后，该方法将检查返回路由的权重是否小于随机生成的数
     * 字。如果条件为真，则 useSpecialRoute 方法返回 true，表示你希望使用该路由。
     *
     * 一旦你确定要路由服务请求进入 SpecialRoutesFilter，你就要将请求转发到目标服务。
     *
     *
     *
     * 3、转发路由
     *
     * 实际传递到下游服务的路由是大多数工作发生在 SpecialRoutesFilter 中的地方。而 Zuul 确实提供了辅助功能，
     * 使这项工作更容易，大部分的工作仍在开发者。forwardToSpecialRoute() 方法为你提供转发工作。此方法中的代
     * 码大量借鉴了 Spring Cloud SimpleHostRoutingFilter 类的源代码。虽然这里不打算讨论该方法中调用的所有
     * 辅助方法，但还是要浏览这个方法中的代码，如下所示。
     *
     *     private void forwardToSpecialRoute(String route) {
     *         RequestContext context = RequestContext.getCurrentContext();
     *         HttpServletRequest request = context.getRequest();
     *
     *         MultiValueMap<String, String> headers = this.helper
     *                 .buildZuulRequestHeaders(request);
     *         MultiValueMap<String, String> params = this.helper
     *                 .buildZuulRequestQueryParams(request);
     *         String verb = getVerb(request);
     *         InputStream requestEntity = getRequestBody(request);
     *         if (request.getContentLength() < 0) {
     *             context.setChunkedRequestBody();
     *         }
     *
     *         this.helper.addIgnoredHeaders();
     *         CloseableHttpClient httpClient = null;
     *         HttpResponse response = null;
     *
     *         try {
     *             httpClient  = HttpClients.createDefault();
     *             response = forward(httpClient, verb, route, request, headers,
     *                     params, requestEntity);
     *             setResponse(response);
     *         }
     *         catch (Exception ex ) {
     *             ex.printStackTrace();
     *
     *         }
     *         finally{
     *             try {
     *                 httpClient.close();
     *             }
     *             catch(IOException ex){}
     *         }
     *     }
     *
     * 这段代码的关键之处在于，你将从传入的 HTTP 请求（头参数、HTTP 谓词和报文体）中复制所有值到在目标服务上
     * 被调用的新请求。forwardToSpecialRoute() 方法获取从目标服务返回的响应并将其设置在用于 Zuul HTTP 请
     * 求上下文。这是通过 setResponse() 辅助方法完成的。Zuul 使用 HTTP 请求上下文返回从服务客户端调用返回
     * 的响应。
     *
     *
     *
     * 4、把代码整合在一起
     *
     * 现在你已经实现了 SpecialRoutesFilter，你可以通过调用许可服务来看一看它的行为。即通过许可服务调用组织
     * 服务来检索组织的联系人数据。
     *
     * 在代码示例中，specialroutesservice 有一条组织服务的数据库记录，将到组织服务的调用请求 50% 路由到现
     * 有的组织服务（在 Zuul 映射）和 50% 路由到另一个组织服务。从 SpecialRoutes 服务返回的替代的组织服务
     * 路由将是 http://orgservice-new，并不会直接从 Zuul 访问。为了区分两个服务，这里已经修改了组织服务在
     * 组织服务返回的联系人名字值前面添加 "OLD::" 和 "NEW::" 文本。
     *
     * 如果你现在通过 Zuul 点击许可服务端点 http://localhost:5555/api/licensing/v1/organizations
     * /e254f8c-c442-4ebe-a82ae2fc1d1ff78a/licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a
     * 你应该看到从许可服务调用返回的 contactName 值在 OLD:: 和 NEW:: 之间翻转。
     *
     * 一个 Zuul 路由过滤器的实现与前置过滤器或后置过滤器相比需要做更多的工作，但它也是 Zuul 最强大的一部分，
     * 因为你可以轻松地将智能添加到服务路由的方式中。
     */
    public static void main(String[] args) {

    }

}
