package com.siwuxie095.spring.cloud.chapter9th.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-06-23 08:48:24
 */
public class Main {

    /**
     * Spring Cloud Sleuth 与关联 ID
     *
     * 关联 ID 是一个随机生成的、唯一的数字或字符串，它在事务启动时分配给一个事务。当事务流过多个服务时，关联 ID 从
     * 一个服务调用传播到另一个服务调用。在之前示例的上下文中，使用了 Zuul 过滤器检查了所有传入的 HTTP 请求，并且在
     * 关联 ID 不存在的情况下注入关联 ID。
     *
     * 一旦提供了关联 ID，就可以在每个服务上使用自定义的 Spring HTTP 过滤器，将传入的变量映射到自定义的 UserContext
     * 对象。有了 UserContext 对象，现在可以手动地将关联 ID 添加到日志语句中，或者通过少量工作将关联 ID 直接添加
     * 到 Spring 的映射诊断上下文（Mapped Diagnostic Context，MDC）中，从而确保将关联 ID 添加到任何日志语句中。
     * 还可以编写一个 Spring 拦截器，该拦截器通过向出站调用添加关联 ID 到 HTTP 首部中，确保来自服务的所有 HTTP
     * 调用都会传播关联 ID。
     *
     * 对了，还必须施展 Spring 和 Hystrix 的魔法，以确保持有关联 ID 的父线程的线程上下文被正确地传播到 Hystrix。
     * 在最后，这些数量众多的基础设施都是为了某些你希望只有在问题发生时才查看的东西而设置的（使用关联 ID 来跟踪事务
     * 中发生了什么）。
     *
     * 幸运的是，Spring Cloud Sleuth 能够为开发人员管理这些代码基础设施并处理复杂的工作。通过添加 Spring Cloud
     * Sleuth 到 Spring 微服务中，开发人员可以：
     * （1）透明地创建并注入一个关联 ID 到服务调用中（如果关联 ID 不存在）；
     * （2）管理关联 ID 到出站服务调用的传播，以便将事务的关联 ID 自动添加到出站调用中；
     * （3）将关联信息添加到 Spring 的 MDC 日志记录，以便生成的关联 ID 由 Spring Boot 默认的 SL4J 和 Logback
     * 实现自动记录；
     * （4）（可选）将服务调用中的跟踪信息发布到 Zipkin 分布式跟踪平台。
     *
     * 注意：有了 Spring Cloud Sleuth，如果使用 Spring Boot 的日志记录实现，关联 ID 就会自动添加到微服务的日志
     * 语句中。
     *
     * 下面继续，将 Spring Cloud Sleuth 添加到许可证服务和组织服务中。
     *
     *
     *
     * 1、将 Spring Cloud Sleuth 添加到许可证服务和组织服务中
     *
     * 要在两个服务（许可证和组织）中开始使用 Spring Cloud Sleuth，需要在两个服务的 pom.xml 文件中添加一个 Maven
     * 依赖项：
     *
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-starter-sleuth</artifactId>
     * </dependency>
     *
     * 这个依赖项会拉取 Spring Cloud Sleuth 所需的所有核心库。就这样，一旦这个依赖项被拉进来，服务现在就会完成如下
     * 功能。
     * （1）检查每个传入的 HTTP 服务，并确定调用中是否存在 Spring Cloud Sleuth 跟踪信息。如果 Spring Cloud
     * Sleuth 跟踪数据确实存在，则将捕获传递到微服务的跟踪信息，并将跟踪信息提供给服务以进行日志记录和处理。
     * （2）将 Spring Cloud Sleuth 跟踪信息添加到 Spring MDC，以便微服务创建的每个日志语句都添加到日志中。
     * （3）将 Spring Cloud 跟踪信息注入服务发出的每个出站 HTTP 调用以及 Spring 消息传递通道的消息中。
     *
     *
     *
     * 2、剖析 Spring Cloud Sleuth 跟踪
     *
     * 如果一切创建正确，则在服务应用程序代码中编写的任何日志语句现在都将包含 Spring Cloud Sleuth 跟踪信息。例如，
     * 如果要在组织服务上执行 HTTP GET 请求 http://localhost:5555/api/organization/v1/organizations
     * /e254f8c-c442-4ebe-a82a-e2fc1d1ff78a ，服务将输出对应结果，包含四部分。
     * （1）应用程序名称：正在记录的服务的名称。
     * （2）跟踪 ID：用户请求的唯一标识符，将在该请求中的所有服务调用中携带。
     * （3）跨度 ID：在整个用户请求中每个组成部分的唯一标识符。对于多服务调用，在用户事务中每个服务调用都会有一个跨
     * 度 ID。
     * （4）发送到 Zipkin 的标志：指示是否将数据发送到 Zipkin 服务器以进行跟踪。
     *
     * Spring Cloud Sleuth 将向每个日志条目添加以下四条信息（对应以上四条）：
     * （1）服务的应用程序名称：这是创建日志条目时所在的应用程序的名称。在默认情况下，Spring Cloud Sleuth 将应用
     * 程序的名称（spring.application.name ）作为在跟踪中写入的名称。
     * （2）跟踪 ID（trace ID）：跟踪 ID 是关联 ID 的等价术语，它是表示整个事务的唯一编号。
     * （3）跨度 ID（span ID）：跨度 ID 是表示整个事务中某一部分的唯一 ID。参与事务的每个服务都将具有自己的
     * 跨度 ID。当与 Zipkin 集成来可视化事务时，跨度 ID 尤其重要。
     * （4）是否将跟踪数据发送到 Zipkin：在大容量服务中，生成的跟踪数据量可能是海量的，并且不会增加大量的价值。
     * Spring Cloud Sleuth 让开发人员确定何时以及如何将事务发送给 Zipkin。Spring Cloud Sleuth 跟踪块末
     * 尾的 true/false 指示器用于指示是否将跟踪信息发送到 Zipkin。
     *
     * 到目前为止，只查看了单个服务调用产生的日志数据。
     *
     * 下面来看看通过 GET http://localhost:5555/api/licensing/v1/organizations
     * /e254f8c-c442-4ebe-a82a-e2fc1d1ff78a/licenses/f3831f8c-c338-4ebe-a82a-e2fc-1d1ff78a
     * 调用许可证服务时会发生什么。记住，许可证服务还必须向组织服务发出调用。
     *
     * 通过日志可以看出许可证服务和组织服务都具有相同的跟踪 ID（即 事务 ID）：a9e3e1786b74d302。但是，许可证服务
     * 的跨度 ID 是 a9e3e1786b74d302（与事务 ID 的值相同），而组织服务的跨度 ID 是 3867263ed85ffbf4（与事务
     * ID 的值不同）。
     *
     * 只需添加一些 POM 的依赖项，就已经替换了之前构建的所有关联 ID 的基础设施。就个人而言，在这个世界上，没有什么
     * 比用别人的代码代替复杂的、基础设施风格的代码更让人开心的了。
     */
    public static void main(String[] args) {

    }

}
