package com.siwuxie095.spring.cloud.chapter1st.example11th;

/**
 * @author Jiajing Li
 * @date 2021-05-14 20:43:57
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使用 Spring Cloud 举例
     *
     * 这里举一个简单的例子，演示 Spring Cloud 如何将服务发现、断路器、舱壁和远程服务的客户端负载均衡集成到示例中。
     *
     * import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
     * import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
     * import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
     * import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
     *
     *
     * @SpringBootApplication
     * @RestController
     * @RequestMapping(value="hello")
     * @EnableCircuitBreaker
     * @EnableEurekaClient
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     *     @HystrixCommand(threadPoolKey = "helloThreadPool")
     *     public String helloRemoteServiceCall(String firstName,
     *                                          String lastName) {
     *         ResponseEntity<String> restExchange =
     *                 restTemplate.exchange(
     *                         "http://logical-service-id/name/[ca]{firstName}/{lastName}",
     *                         HttpMethod.GET,
     *                         null, String.class, firstName, lastName);
     *         return restExchange.getBody();
     *     }
     *
     *
     *     @RequestMapping(value="/{firstName}/{lastName}", method = RequestMethod.GET)
     *     public String hello(@PathVariable("firstName") String firstName,
     *                          @PathVariable("lastName") String lastName) {
     *         return helloRemoteServiceCall(firstName, lastName);
     *     }
     *
     * }
     *
     * 这段代码挤满了很多事情，你应该注意的第一件事是：@EnableCircuitBreaker 和 @EnableEurekaClient 注解。
     * @EnableCircuitBreaker 注解告诉你，Spring 微服务将在你的应用中使用 Netflix 的 Hystrix 库。
     * @EnableEurekaClient 注解告诉你，Spring 微服务使用 Eureka 服务发现代理注册其本身，你要使用服务发现查
     * 找在你的代码里的远程 REST 服务端点。请注意，配置正在发生在一个属性文件中，它将告诉简单的服务要联系的
     * Eureka 服务器的位置和端口号。当你声明你的 hello 方法时，你将第一次看到 Hystrix 被使用。
     *
     * @HystrixCommand 注解将做两件事情。首先，任何时候 helloRemoteServiceCall 方法被调用时，它不会被直接
     * 调用。相反，该方法将被委派到被 Hystrix 管理的一个线程池。如果调用时间太长(默认是 1 秒)，将进入 Hystrix
     * 并中断调用。这就是断路器模式的实现。其次，该注释的作用是创建一个称为 helloThreadPool 的线程池，它由
     * Hystrix 管理。所有到 helloRemoteServiceCall 方法的调用只会发生在这个线程池，并且由任何其他远程服务发
     * 起的调用将被隔离。
     *
     * 最后要注意的一件事是在 helloRemoteServiceCall 方法内发生了什么。@EnableEurekaClient 的存在告诉
     * Spring Boot，每当你做出一个 REST 服务调用，你要使用一个修改的 RestTemplate 类（这不是标准的 Spring
     * RestTemplate 开箱即用）。RestTemplate 类将允许你为你试图调用的服务引入一个逻辑服务 ID，正如这里看到
     * 的，RestTemplate 类将与 Eureka 服务联系，并查找一个或多个 "名称" 服务实例的物理位置。作为服务的消费者，
     * 你的代码永远不必知道该服务位于何处。
     *
     * 另外，RestTemplate 类使用 Netflix 的 Ribbon 库。Ribbon 将取回与服务相关联的所有物理端点的列表。每次
     * 服务被客户端调用时，它对客户端不同服务实例采用 "round-robins" 调用，而不必经过一个集中的负载均衡器。通
     * 过消除集中式负载均衡器并将其移动到客户端，你将在应用程序基础设施中消除另一个故障点（负载均衡器停止运行）。
     * 在这一点上希望你能留下深刻的印象，因为你已经添加了相当多的微服务能力，而你只用了很少的注解。
     *
     * 这才是 Spring Cloud 的真正优势。你作为一个开发者，从最先的云服务提供商获得身经百战的微服务能力，如
     * Netflix 和 Consul。这些功能，如果在 Spring Cloud 之外使用，可能会很复杂，很难建立。Spring Cloud
     * 简化了它们的使用，你要做的只不过是一些简单的 Spring Cloud 注解和配置条目而已。
     */
    public static void main(String[] args) {

    }

}
