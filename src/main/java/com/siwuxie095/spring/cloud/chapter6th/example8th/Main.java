package com.siwuxie095.spring.cloud.chapter6th.example8th;

/**
 * @author Jiajing Li
 * @date 2021-06-12 22:47:11
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 构建动态路由过滤器
     *
     * 这里要介绍的最后一个 Zuul 过滤器是 Zuul 路由过滤器。如果没有自定义的路由过滤器，Zuul 将根据映射定义来完成
     * 所有路由。通过构建 Zuul 路由过滤器，可以为服务客户端的调用添加智能路由。
     *
     * 这里将通过构建一个路由过滤器来学习 Zuul 的路由过滤器，从而允许对新版本的服务进行 A/B 测试。A/B 测试是推出
     * 新功能的地方，在这里有一定比例的用户能够使用新功能，而其余的用户仍然使用旧服务。在本例中，将模拟出一个新的组
     * 织服务版本，并希望 50% 的用户使用旧服务，另外 50% 的用户使用新服务。
     *
     * 为此，需要构建一个名为 SpecialRoutesFilter 的路由过滤器。该过滤器将接收由 Zuul 调用的服务的 Eureka 服
     * 务 ID，并调用另一个名为 SpecialRoutes 的微服务。SpecialRoutes 服务将检查内部数据库以查看服务名称是否存
     * 在。如果目标服务名称存在，它将返回服务的权重以及替代位置的目的地。SpecialRoutesFilter 将接收返回的权重，
     * 并根据权重随机生成一个值，用于确定用户的调用是否将被路由到替代组织服务或 Zuul 路由映射中定义的组织服务。
     *
     * 如下展示了使用 SpecialRoutesFilter 时所发生的流程。
     * （1）服务客户端：服务客户端通过 Zuul 调用服务。
     * （2）Eureka ID：SpecialRoutesFilter 检索服务 ID。
     * （3）SpecialRoutes 服务：SpecialRoutes 服务检查是否有其他新的端点服务，
     * 以及将被发送到新服务和旧服务的调用百分比（权重）。
     * （4）随机数：SpecialRoutesFilter 生成随机数，并检查权重数以确定路由。
     * （5）ResponseFilter：如果请求被路由到其他新的服务端点，则 Zuul 仍然通过
     * 预定义的后置过滤器将响应路由回去。
     *
     * PS：通过 SpecialRoutesFilter 调用组织服务的流程。
     *
     * 在如上过程中，在服务客户端调用 Zuul 背后的服务时，SpecialRoutesFilter 会执行以下操作。
     * （1）SpecialRoutesFilter 检索被调用服务的服务 ID。
     * （2）SpecialRoutesFilter 调用 SpecialRoutes 服务。SpecialRoutes 服务将查询是否有针对目标端点定义的
     * 替代端点。如果找到一条记录，那么这条记录将包含一个权重，它将告诉 Zuul 应该发送到旧服务和新服务的服务调用的
     * 百分比。
     * （3）然后 SpecialRoutesFilter 生成一个随机数，并将它与 SpecialRoutes 服务返回的权重进行比较。如果随机
     * 生成的数字大于替代端点权重的值，那么 SpecialRoutesFilter 会将请求发送到服务的新版本。
     * （4）如果 SpecialRoutesFilter 将请求发送到服务的新版本，Zuul 会维持最初的预定义管道，并通过已定义的后置
     * 过滤器将响应从替代服务端点发送回来。
     *
     *
     *
     * 1、构建路由过滤器的骨架
     *
     * 这里将介绍用于构建 SpecialRoutesFilter 的代码。在迄今为止所看到的所有过滤器中，实现 Zuul 路由过滤器所需
     * 进行的编码工作最多，因为通过路由过滤器，开发人员将接管 Zuul 功能的核心部分 —— 路由，并使用自己的功能替换掉
     * 它。这里不会详细介绍整个类，而会讨论相关的细节。
     *
     * SpecialRoutesFilter 遵循与其他 Zuul 过滤器相同的基本模式。它扩展 ZuulFilter 类，并设置了 filterType()
     * 方法来返回 "route" 的值。如下代码展示了路由过滤器的骨架。
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
     *     public Object run() {}
     *
     * }
     *
     *
     *
     * 2、实现 run() 方法
     *
     * SpecialRoutesFilter 的实际工作从代码的 run() 方法开始。如下展示了此方法的代码。
     *
     *     @Override
     *     public Object run() {
     *         RequestContext ctx = RequestContext.getCurrentContext();
     *
     *         // 执行对 SpecialRoutes 服务的调用，以确定该服务 ID 是否有路由记录
     *         AbTestingRoute abTestRoute = getAbRoutingInfo(filterUtils.getServiceId());
     *
     *         // useSpecialRoute() 方法将会接受路径的权重，生成一个随机数，并确定是否将请求转发到替代服务
     *         if (abTestRoute != null && useSpecialRoute(abTestRoute)) {
     *             // 如果有路由记录，则将完整的 URL（包含路径）构建到由 specialroutes 服务指定的服务位置
     *             String route = buildRouteString(ctx.getRequest().getRequestURI(),
     *                     abTestRoute.getEndpoint(),
     *                     ctx.get("serviceId").toString());
     *             // forwardToSpecialRoute() 方法完成转发到其他服务的工作
     *             forwardToSpecialRoute(route);
     *         }
     *
     *         return null;
     *     }
     *
     * 这段代码的一般流程是，当路由请求触发 SpecialRoutesFilter 中的 run() 方法时，它将对 SpecialRoutes 服务
     * 执行 REST 调用。该服务将执行查找，并确定是否存在针对被调用的目标服务的 Eureka 服务 ID 的路由记录。
     *
     * 对 SpecialRoutes 服务的调用是在 getAbRoutingInfo() 方法中完成的。getAbRoutingInfo() 方法如下所示。
     *
     *     private AbTestingRoute getAbRoutingInfo(String serviceName) {
     *         ResponseEntity<AbTestingRoute> restExchange = null;
     *         try {
     *             // 调用 SpecialRoutesService 端点
     *             restExchange = restTemplate.exchange(
     *                     "http://specialroutesservice/v1/route/abtesting/{serviceName}",
     *                     HttpMethod.GET,
     *                     null, AbTestingRoute.class, serviceName);
     *         } catch(HttpClientErrorException ex) {
     *             // 如果路由服务没有找到记录（它将返回 HTTP 状态码 404），该方法将返回空值
     *             if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
     *                 return null;
     *             }
     *             throw ex;
     *         }
     *         return restExchange.getBody();
     *     }
     *
     * 一旦确定目标服务的路由记录存在，就需要确定是否应该将目标服务请求路由到替代服务位置，或者路由到由 Zuul 路由
     * 映射静态管理的默认服务位置。为了做出这个决定，需要调用 useSpecialRoute() 方法。如下所示。
     *
     *     public boolean useSpecialRoute(AbTestingRoute testRoute) {
     *         Random random = new Random();
     *
     *         // 检查路由是否为活跃状态
     *         if (testRoute.getActive().equals("N")) {
     *             return false;
     *         }
     *
     *         // 确定是否应该使用替代服务路由
     *         int value = random.nextInt((10 - 1) + 1) + 1;
     *
     *         if (testRoute.getWeight() < value) {
     *             return true;
     *         }
     *
     *         return false;
     *     }
     *
     * 这个方法做了两件事。首先，该方法检查从 SpecialRoutes 服务返回的 AbTestingRoute 记录中的 active 字段。
     * 如果该记录设置为 "N" ，则 useSpecialRoute() 方法不应该执行任何操作，因为现在不希望进行任何路由。其次，
     * 该方法生成 1 到 10 之间的随机数。然后，该方法将检查返回路由的权重是否小于随机生成的数。如果条件为 true，
     * 则 useSpecialRoute() 方法将返回 true，表示确实希望使用该路由。
     *
     * 一旦确定要路由进入 SpecialRoutesFilter 的服务请求，就需要将请求转发到目标服务。
     *
     *
     *
     * 3、转发路由
     *
     * SpecialRoutesFilter 中出现的大部分工作是到下游服务的路由的实际转发。虽然 Zuul 确实提供了辅助方法来使这项
     * 任务更容易，但开发人员仍然需要负责大部分工作。forwardToSpecialRoute() 方法负责转发工作。该方法中的代码大
     * 量借鉴了 Spring Cloud 的 SimpleHostRoutingFilter 类的源代码。
     *
     * 虽然这里不会介绍 forwardToSpecialRoute() 方法中调用的所有辅助方法，但是会介绍该方法中的代码，如下所示。
     *
     *     // helper 变量是类 ProxyRequestHelper 类型的一个实例变量。这是 Spring Cloud
     *     // 提供的类，附带有用于代理服务请求的辅助方法
     *     private ProxyRequestHelper helper = new ProxyRequestHelper();
     *
     *     private void forwardToSpecialRoute(String route) {
     *         RequestContext context = RequestContext.getCurrentContext();
     *         HttpServletRequest request = context.getRequest();
     *
     *         // 创建将发送到服务的所有 HTTP 请求首部的副本
     *         MultiValueMap<String, String> headers = this.helper
     *                 .buildZuulRequestHeaders(request);
     *         // 创建所有 HTTP 请求参数的副本
     *         MultiValueMap<String, String> params = this.helper
     *                 .buildZuulRequestQueryParams(request);
     *         String verb = getVerb(request);
     *         // 创建将被转发到替代服务的 HTTP 主体的副本
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
     *             // 使用 forward() 辅助方法（未显示）调用替代服务
     *             response = forward(httpClient, verb, route, request, headers,
     *                     params, requestEntity);
     *             // 通过 setResponse() 辅助方法将服务调用的结果保存回 Zuul 服务器
     *             setResponse(response);
     *         } catch (Exception ex ) {
     *             ex.printStackTrace();
     *         } finally {
     *             try {
     *                 httpClient.close();
     *             } catch (IOException ex) {}
     *         }
     *     }
     *
     * 这段代码中的关键要点是，将传入的 HTTP 请求（首部参数、HTTP 动词和主体）中的所有值复制到将在目标服务上调用的
     * 新请求。然后 forwardToSpecialRoute() 方法从目标服务返回响应，并将响应设置在 Zuul 使用的 HTTP 请求上下文
     * 中。上述过程通过 setResponse() 辅助方法（未显示）完成。Zuul 使用 HTTP 请求上下文从调用服务客户端返回响应。
     *
     *
     *
     * 4、整合
     *
     * 既然已经实现了 SpecialRoutesFilter，就可以通过调用许可证服务来查看它的动作。即 通过许可证服务调用组织服务
     * 来检索组织的联系人数据。
     *
     * 在代码示例中，specialroutesservice 具有用于组织服务的数据库记录，该数据库记录指示有 50% 的概率把对组织服
     * 务的请求路由到现有的组织服务（Zuul 中映射的那个），50% 的概率路由到替代组织服务。从 SpecialRoutes 服务返
     * 回的替代组织服务路径是 http://orgservice-new，并且不能直接从 Zuul 访问。为了区分这两个服务，这里修改了
     * 组织服务，将文本 "OLD::" 和 "NEW::" 添加到组织服务返回的联系人姓名的前面。
     *
     * 如果现在通过 Zuul 访问许可证服务端点，应该看到从许可证服务调用返回的 contactName 在 OLD:: 和 NEW:: 值
     * 之间变化。
     *
     * http://localhost:5555/api/licensing/v1/organizations/e254f8c-c442-4ebe-a82a-
     * ➥  e2fc1d1ff78a/licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a
     *
     * PS：当访问替代组织服务时，将会看到 NEW 被添加到 contactName 前面。
     *
     * 实现 Zuul 路由过滤器确实比实现前置过滤器或后置过滤器需要更多的工作，但它也是 Zuul 最强大的部分之一，因为
     * 开发人员可以轻松地让服务路由方式变得智能。
     */
    public static void main(String[] args) {

    }

}
