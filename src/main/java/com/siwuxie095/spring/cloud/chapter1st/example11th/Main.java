package com.siwuxie095.spring.cloud.chapter1st.example11th;

/**
 * @author Jiajing Li
 * @date 2021-05-14 20:43:57
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 通过示例来介绍 Spring Cloud
     *
     * 在这里，来概要回顾一下要使用的各种 Spring Cloud 技术。因为每一种技术都是独立的服务，要详细介绍这些服务，这里的
     * 内容肯定不够，所以仅作简要介绍。与此同时，留下一个小小的代码示例，它再次演示了将这些技术集成到微服务开发工作中是
     * 多么容易。
     *
     * 值得注意的是，这个代码示例不能运行，因为它需要设置和配置许多支持服务才能使用。不过，不要担心，在设置服务方面，这
     * 些 Spring Cloud 服务（配置服务，服务发现）的设置是一次性的。一旦设置完成，微服务就可以不断使用这些功能。而在这
     * 里的开头部分，无法将所有的精华都融入一个代码示例中。
     *
     * 如下代码快速演示了如何将远程服务的服务发现、断路器、舱壁以及客户端负载均衡集成到 "Hello World" 示例中。
     *
     * // 为了简洁，省略了其他 import 语句
     * import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
     * import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
     * import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
     * import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
     *
     * @SpringBootApplication
     * @RestController
     * @RequestMapping(value="hello")
     * // 使服务能够使用 Hystrix 和 Ribbon 库
     * @EnableCircuitBreaker
     * // 告诉服务，它应该使用 Eureka 服务发现代理注册自身，并且服务调用是使用服务发现来 "查找" 远程服务的位置的
     * @EnableEurekaClient
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     *     // 包装器使用 Hystrix 断路器调用 helloRemoteServiceCall 方法
     *     @HystrixCommand(threadPoolKey = "helloThreadPool")　
     *     public String helloRemoteServiceCall(String firstName, String lastName){
     *     // 使用一个装饰好的 RestTemplate 类来获取一个 "逻辑" 服务 ID，Eureka 在幕后查找服务的物理位置
     *     ResponseEntity<String> restExchange =
     *        restTemplate.exchange(
     *            "http://logical-service-id/name/[ca]{firstName}/{lastName}",
     *            HttpMethod.GET,
     *            null, String.class, firstName, lastName);
     *
     *     return restExchange.getBody();
     *
     *     }
     *     @RequestMapping(value="/{firstName}/{lastName}", method = RequestMethod.GET)
     *     public String hello(@PathVariable("firstName") String firstName,
     *        @PathVariable("lastName") String lastName) {
     *         return helloRemoteServiceCall(firstName, lastName);
     *     }
     * }
     *
     * 这段代码包含了很多内容，下面来慢慢分析。
     *
     * 开发人员首先应该要注意的是 @EnableCircuitBreaker 和 @EnableEurekaClient 注解。@EnableCircuitBreaker
     * 注解告诉 Spring 微服务，将要在应用程序使用 Netflix Hystrix 库。@EnableEurekaClient 注解告诉微服务使用
     * Eureka 服务发现代理去注册它自己，并且将要在代码中使用服务发现去查询远程 REST 服务端点。注意，配置是在一个属
     * 性文件中的，该属性文件告诉服务要进行通信的 Eureka 服务器的地址和端口号。
     *
     * 你第一次看到使用 Hystrix 是在声明 hello 方法时：
     *
     * @HystrixCommand(threadPoolKey = "helloThreadPool")
     * public String helloRemoteServiceCall(String firstName，String lastName)
     *
     * @HystrixCommand 注解做两件事。
     * （1）第一件事是，在任何时候调用 helloRemoteService Call 方法，该方法都不会被直接调用，这个调用会被委派给由
     * Hystrix 管理的线程池。如果调用时间太长（默认为 1 s），Hystrix 将介入并中断调用。这是断路器模式的实现。
     * （2）第二件事是创建一个由 Hystrix 管理的名为 helloThreadPool 的线程池。所有对 helloRemoteServiceCall
     * 方法的调用只会发生在此线程池中，并且将与正在进行的任何其他远程服务调用隔离。
     *
     *
     * 最后要注意的是 helloRemoteServiceCall 方法中发生的事情。@EnableEurekaClient 的存在告诉 Spring Boot，
     * 在使用 REST 服务调用时，使用修改过的 RestTemplate 类（这不是标准的 Spring RestTemplate 的工作方式）。
     * 这个 RestTemplate 类允许用户传入自己想要调用的服务的逻辑服务 ID：
     *
     * ResponseEntity<String> restExchange = restTemplate.exchange
     * ➥  (http://logical-service-id/name/{firstName}/{lastName}
     *
     * 在幕后，RestTemplate 类将与 Eureka 服务进行通信，并查找一个或多个 "name" 服务实例的实际位置。作为服务的
     * 消费者，开发人员的代码永远不需要知道服务的位置。
     *
     * 另外，RestTemplate 类使用 Netflix 的 Ribbon 库。Ribbon 将会检索与服务有关的所有物理端点的列表。每当客户
     * 端调用该服务时，它不必经过集中式负载均衡器就可以对客户端上不同服务实例进行轮询（round-robin）。通过消除集中
     * 式负载均衡器并将其移动到客户端，可以消除应用程序基础设施中的其他故障点（故障的负载均衡器）。
     *
     * 希望此刻会给你留下深刻印象，因为只需要几个注解就可以为微服务添加大量的功能。这就是 Spring Cloud 背后真正的美。
     * 开发者可以利用 Netflix 和 Consul 等知名的云计算公司的微服务功能，这些功能是久经考验的。如果在 Spring Cloud
     * 之外使用这些功能，可能会很复杂并且难以设置。Spring Cloud 简化了它们的使用，仅仅是使用一些简单的 Spring Cloud
     * 注解和配置条目。
     */
    public static void main(String[] args) {

    }

}
