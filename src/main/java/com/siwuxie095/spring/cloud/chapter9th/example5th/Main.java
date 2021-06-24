package com.siwuxie095.spring.cloud.chapter9th.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-24 22:41:38
 */
public class Main {

    /**
     * 小结
     *
     * （1）Spring Cloud Sleuth 可以无缝地将跟踪信息（关联 ID）添加到微服务调用中。
     * （2）关联 ID 可用于在多个服务之间链接日志条目。可以使用关联 ID 查看在单个事务中涉及的所有服务
     * 的事务行为。
     * （3）虽然关联 ID 功能强大，但需要将此概念与日志聚合平台结合使用，以便从多个来源获取日志，然后
     * 搜索和查询它们的内容。
     * （4）虽然存在多个内部部署的日志聚合平台，但基于云的服务可以让开发人员在不必拥有大量基础设施的
     * 情况下，对日志进行管理。此外，它们还可以在应用程序日志记录量增长时轻松扩大。
     * （5）可以将 Docker 容器与日志聚合平台集成，来捕获正在写入容器 stdout/stderr 的所有日志记录
     * 数据。在这里将 Docker 容器、Logspout 以及在线云日志记录供应商 Papertrail 集成，以捕获和查
     * 询日志。
     * （6）虽然统一的日志记录平台很重要，但通过微服务来可视化地跟踪事务的能力也是一个有价值的工具。
     * （7）Zipkin 可以让开发人员在对服务进行调用时查看服务之间存在的依赖关系。
     * （8）Spring Cloud Sleuth 与 Zipkin 集成，Zipkin 可以让开发人员以图形方式查看事务流程，并
     * 了解用户事务中涉及的每个微服务的性能特征。
     * （9）在启用 Spring Cloud Sleuth 的服务中，Spring Cloud Sleuth 将自动捕获 HTTP 调用以及
     * 入站和出站消息通道的跟踪数据。
     * （10）Spring Cloud Sleuth 将每个服务调用映射到一个跨度的概念。可以使用 Zipkin 来查看一个
     * 跨度的性能。
     * （11）Spring Cloud Sleuth 和 Zipkin 还允许开发人员自定义跨度，以便了解基于非 Spring 的
     * 资源（如 Postgres 或 Redis 等数据库服务器）的性能。
     */
    public static void main(String[] args) {

    }

}
