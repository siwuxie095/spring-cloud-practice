package com.siwuxie095.spring.cloud.chapter1st.example5th;

/**
 * @author Jiajing Li
 * @date 2021-05-07 21:57:09
 */
public class Main {

    /**
     * 使用 Spring Boot 构建一个微服务
     *
     * 一个软件开发框架应该是深思熟虑的和易于使用的，如果能通过 "猴子身上测试"，也就是说，如果像猴子一样的人
     * 能在 10 分钟以内找到一个框架（即 傻瓜式），那么它就有希望是这样的框架。这是第一次写 Spring Boot 服
     * 务示例时的感受。希望你有同样的经历和快乐，所以下面来花一点时间看看如何使用 Spring Boot 编写一个简单
     * 的 "Hello World" REST 服务。
     *
     * 这个例子绝不是详尽说明你应该如何建立一个生产水平的微服务，但它应该让你停一下，因为它能够教会你怎样编写
     * 更少的代码。
     *
     * Spring Boot 抽象了 REST 微服务的共同功能（路由到业务逻辑，从 URL 解析 HTTP 参数，JSON/Java 对象
     * 的数据转换），并让开发人员专注于服务的业务逻辑。
     *
     * Spring Boot 微服务处理用户请求的基本流程如下：
     * （1）客户端发出 HTTP GET 请求访问你的 Hello 微服务。
     * （2）路由映射：Spring Boot 解析 HTTP 请求和根据 HTTP 谓词、URL 和 URL 定义的参数映射路由。在
     * Spring RestController 类中，有路由映射到的方法。
     * （3）参数解析：一旦 Spring Boot 已经确定路由，它将映射里面定义的任何参数的路由到一个 Java 方法，并
     * 进行工作。
     * （4）JSON -> Java 对象映射：通过 HTTP 的 PUT 或 Post 操作，HTTP 报文体内合法的 JSON 数据被映射
     * 到 Java 类。
     * （5）执行业务逻辑：一旦所有的数据都被映射，Spring Boot 将执行业务逻辑。
     * （6）Java 对象 -> JSON 映射：执行业务逻辑之后，Spring Boot 将 Java 对象转换为 JSON。
     * （7）客户端接收到服务 JSON 格式的响应。调用的成功或失败被作为一个 HTTP 状态码返回。
     *
     *
     * 这个例子，将有一个 Java 类 com.siwuxie095.spring.cloud.simpleservice.Application，它将用于把
     * 访问路径为 /hello 的 REST 端点暴露出来。
     *
     * 这里主要暴露一个 HTTP GET 方式的端点，URL 传递两个参数（firstName 和 lastName），然后返回一个简单
     * 的 JSON 字符串，它的有效载荷包含消息 "Hello firstName lastName"。如果你使用 /hello/scott/lee
     * 端点访问服务，调用返回的将是 {"message":"Hello scott lee"}。
     *
     * 下面启动服务。进入命令提示符并发出以下命令：
     *
     * mvn spring-boot:run
     *
     * 这个 mvn 命令，将使用一个 Spring Boot 插件，启动内嵌 Tomcat 服务器的应用程序。Spring Boot 服务将
     * 通过控制台的服务端口，与暴露的端点通信。
     *
     * 通过启动日志可以注意到两件事情。首先，一个 Tomcat 服务器使用端口 8080 启动。其次，/hello/{firstName}
     * /{lastName} 端点以 GET 方式暴露在服务器上。
     *
     * 你使用基于浏览器的 REST 工具 POSTMAN（https://www.getpostman.com/）调用你的服务。许多工具，包括
     * 图形和命令行，都可以调用的基于 REST 的服务，这里使用的是 POSTMAN。
     *
     * 输入：http://localhost:8080/hello/scott/lee，将会看到 {"message":"Hello scott lee"}。
     *
     * 显然，这个简单的例子没有证明 Spring Boot 的功能强大。但它所要表明的是，你可以在 Java 中只要写 25 行
     * 代码，就能编写一个带 URL 参数路由映射，并基于 HTTP 协议 JSON 格式的 REST 服务。有经验的 Java 开发
     * 人员会告诉你，在 Java 中使用 25 行代码编写任何有意义的东西都是非常困难的。Java 是一门功能强大的语言，
     * 在与其它语言广泛的比较中，已经获得良好的声誉。
     */
    public static void main(String[] args) {

    }

}
