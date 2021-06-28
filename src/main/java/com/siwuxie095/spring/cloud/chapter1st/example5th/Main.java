package com.siwuxie095.spring.cloud.chapter1st.example5th;

/**
 * @author Jiajing Li
 * @date 2021-05-07 21:57:09
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使用 Spring Boot 来构建微服务
     *
     * 笔者一直以来都持有这样一个观点：如果一个软件开发框架通过了被亲切地称为 "卡内尔猴子测试" 的试验，就认为
     * 它是经过深思熟虑和易于使用的。如果一只像笔者这样的 "猴子" 能够在 10 min 或者更少时间内弄明白一个框架，
     * 那么这个框架就通过了这个试验。这就是第一次写 Spring Boot 服务示例的感觉。希望你也有同样的体验和快乐，
     * 所以，下面来花一点儿时间，看看如何使用 Spring Boot 编写一个简单的 "Hello World" REST 服务。
     *
     * PS："卡内尔猴子测试" 对应的英文为 "Carnell Monkey Test"，是作者 John Carnell 设想出来的一个判断
     * 框架是否易于使用的试验。
     *
     * 如下展示了这个服务将会做什么，以及 Spring Boot 微服务将会如何处理用户请求的一般流程。
     * （1）客户端：客户端发送一个 HTTP GET 请求到 Hello 微服务。
     * （2）路由映射：Spring Boot 将解析 HTTP 请求，并根据 HTTP 谓词、URL 和 URL 定义的潜在参数映射路由。
     * 路由映射到 Spring RestController 类中的方法。
     * （3）参数解构：Spring Boot 识别出路由后，将路由中定义的所有参数映射到执行该工作的 Java 方法中。
     * （4）JSON 到 Java 对象映射：对于 HTTP PUT 或 POST 请求，在 HTTP 主体中传递的 JSON 被映射到
     * Java 类。
     * （5）业务逻辑执行：映射完所有数据后，Spring Boot 就会执行业务逻辑。
     * （6）Java 到 JSON 对象映射：执行完业务逻辑后，Spring Boot 将 Java 对象转换为 JSON。
     * （7）客户端：客户端以 JSON 接收来自服务的响应。调用的成功或失败以 HTTP 状态码返回。
     *
     * PS：Spring Boot 抽象出了常见的 REST 微服务任务（路由到业务逻辑、从 URL 中解析 HTTP 参数、JSON
     * 与对象相互映射），并让开发人员专注于服务的业务逻辑。
     *
     * 这个例子并不详尽，甚至没有说明应该如何构建一个生产级别的微服务，但它同样值得注意，因为它只需要写很少的
     * 代码。
     *
     * 在这个例子中，创建一个名为 Application 的 Java 类，它公开了一个名为 /hello 的 REST 端点。
     *
     * Application 类的代码，如下所示。
     *
     * // 告诉 Spring Boot 框架，该类是 Spring Boot 服务的入口点
     * @SpringBootApplication
     * // 告诉 Spring Boot，要将该类中的代码公开为 Spring RestController 类
     * @RestController
     * // 此应用程序中公开的所有 URL 将以 /hello 前缀开头
     * @RequestMapping(value="hello")
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     *     // Spring Boot 公开为一个基于 GET 方法的 REST 端点，它将使用两个参数，即 firstName 和 lastName
     *     @RequestMapping(value="/{firstName}/{lastName}",method = RequestMethod.GET)
     *     // 将 URL 中传入的 firstName 和 lastName 参数映射为传递给 hello 方法的两个变量
     *     public String hello( @PathVariable("firstName") String firstName,
     *                          @PathVariable("lastName") String lastName) {
     *         // 返回一个手动构建的简单 JSON 字符串
     *         return String.format("{\"message\":\"Hello %s %s\"}", firstName, lastName);
     *     }
     *
     * }
     *
     * 这段代码中主要公开了一个 GET HTTP 端点，该端点将在 URL 上取两个参数（firstName 和 lastName），
     * 然后返回一个包含消息 "Hello firstName lastName" 的净荷的简单 JSON 字符串。如果在服务上调用了
     * 端点 /hello/john/carnell，返回的结果（马上展示）将会是：
     *
     * {"message":"Hello john carnell"}
     *
     * 下面启动服务。为此，请转到命令提示符并输入以下命令：
     *
     * mvn spring-boot:run
     *
     * 这条 mvn 命令将使用 Spring Boot 插件，然后使用嵌入式 Tomcat 服务器启动应用程序。Spring Boot
     * 服务将通过控制台与公开的端点和服务端口进行通信。
     *
     *
     * PS：Java 与 Groovy 以及 Maven 与 Gradle
     *
     * Spring Boot 框架对 Java 和 Groovy 编程语言提供了强力的支持。可以使用 Groovy 构建微服务，而无
     * 需任何项目设置。Spring Boot 还支持 Maven 和 Gradle 构建工具。这里的例子将限制只使用 Java 和
     * Maven。
     *
     *
     * 通过启动日志，注意两件事。首先，端口 8080 上启动了一个 Tomcat 服务器；其次，在服务器上公开了
     * /hello/{firstName}/{lastName} 的 GET 端点。
     *
     * 这里将使用名为 POSTMAN 的基于浏览器的 REST 工具来调用服务。许多工具（包括图形和命令行）都可用于
     * 调用基于 REST 的服务，但是这里的所有示例都使用 POSTMAN。
     *
     * 在 POSTMAN 中调用 http://localhost:8080/hello/john/carnell 端点，将看到从服务返回的结果。
     *
     * 显然，这个简单的例子并不能演示 Spring Boot 的全部功能。但是，应该注意到，在这里只使用了 25 行
     * 代码就编写了一个完整的 HTTP JSON REST 服务，其中带有基于 URL 和参数的路由映射。正如所有经验
     * 丰富的 Java 开发人员都会告诉你的那样，在 25 行 Java 代码中编写任何有意义的东西都是非常困难的。
     * 虽然 Java 是一门强大的编程语言，但与其他编程语言相比，它却获得了啰嗦冗长的名声。
     *
     * 完成了 Spring Boot 的简短介绍，现在必须提出这个问题：可以使用微服务的方式编写应用程序，这是否
     * 意味着就应该这么做呢？后续将介绍为什么以及何时适合使用微服务方法来构建应用程序。
     */
    public static void main(String[] args) {

    }

}
