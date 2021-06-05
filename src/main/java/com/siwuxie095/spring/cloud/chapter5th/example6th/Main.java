package com.siwuxie095.spring.cloud.chapter5th.example6th;

/**
 * @author Jiajing Li
 * @date 2021-06-05 21:29:30
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 使用 Hystrix 实现断路器
     *
     * 这里来看看两大类型的 Hystrix 实现。在第一种类型中，你将使用 Hystrix 断路器包装许可服务和组织服务
     * 对数据库所有的调用。然后你在许可服务和组织服务之间使用 Hystrix 包装 inter-service 调用。虽然这
     * 是两个不同的类型调用，你会看到，Hystrix 的使用将完全相同。
     *
     * PS：Hystrix 位于每个远程资源的调用之间，并保护客户端。远程资源调用是数据库调用还是基于 REST 的服
     * 务调用，这并不重要。使用 Hystrix 实现一个断路器有两种方式：
     * （1）第一类：用 Hystrix 包装所有对数据库的调用。
     * （2）第二类:使用 Hystrix 包装 inter-service 的调用。
     *
     *
     * 下面从通过展示如何使用一个同步的 Hystrix 断路器包装从许可数据库检索许可服务数据来开始对 Hystrix
     * 的讨论。通过同步调用，许可服务将检索其数据，但在继续处理之前，将等待 SQL 语句完成或断路器超时。
     *
     * Hystrix 和 Spring Cloud 使用 @HystrixCommand 注解来标记 Java 类方法，通过一个 Hystrix 断路
     * 器来管理。当 Spring 框架看到 @HystrixCommand，它会动态生成一个代理，将包装方法和管理所有的调用，
     * 该方法通过专门预留处理远程调用线程的线程池。
     *
     * 你将在 LicenseService 类包装 getLicensesByOrg() 方法，如下所示。
     *
     *     @HystrixCommand
     *     private Organization getOrganization(String organizationId) {
     *         return organizationRestClient.getOrganization(organizationId);
     *     }
     *
     * 这看起来不像是很多代码，但是这个注解里面有很多功能。
     *
     * 使用 @HystrixCommand 注解，任何时候 getLicensesByOrg() 方法被调用，调用将被使用一个 Hystrix
     * 断路器包装。断路器将中断任何对 getLicensesByOrg() 方法任何时候调用时间长于 1000 毫秒的调用。
     *
     * 如果数据库正常工作，这个代码示例会很乏味。可以模拟 getLicensesByOrg() 方法通过调用运行一个缓慢的
     * 数据库查询，大约每三个调用消耗一秒钟多一点。如下代码演示了这一点。
     *
     *     @HystrixCommand
     *     private Organization getOrganization(String organizationId) {
     *          randomlyRunLong();
     *         return organizationRestClient.getOrganization(organizationId);
     *     }
     *
     *     private void randomlyRunLong(){
     *         Random rand = new Random();
     *
     *         int randomNum = rand.nextInt((3 - 1) + 1) + 1;
     *
     *         if (randomNum == 3) {
     *             sleep();
     *         }
     *     }
     *
     *     private void sleep(){
     *         try {
     *             Thread.sleep(11000);
     *         } catch (InterruptedException e) {
     *             e.printStackTrace();
     *         }
     *     }
     *
     * 如果你点击 http://localhost/v1/organizations/e254f8c-c442-4ebea82a-e2fc1d1ff78a
     * /licenses/ 端点足够多的次数，你应该会看到从许可服务返回的超时错误消息。
     *
     * 现在，在 @HystrixCommand 注解的地方，如果查询时间过长，许可服务将中断对数据库的调用。如果数据库
     * 调用需要花费超过 1000 毫秒将执行 Hystrix 代码包装，你的服务调用将抛出一个 com.nextflix.hystrix
     * .exception.HystrixRuntimeException 异常。
     *
     *
     *
     * 1、调用微服务超时
     *
     * 具有断路器行为的方法级注解标签调用的优点就是无论你访问一个数据库还是调用一个微服务都用相同的注解。
     *
     * 例如，在你的许可服务中，你需要查找与许可证相关联的组织的名称。如果你希望用断路器将你的调用包装到组织
     * 服务，这很简单，就是把 RestTemplate 调用分解成自己的方法和使用 @HystrixCommand 注解注释它。
     *
     *     @HystrixCommand
     *     private Organization getOrganization(String organizationId) {
     *         return organizationRestClient.getOrganization(organizationId);
     *     }
     *
     * 注意：而使用 @HystrixCommand 是容易实现的，你需要小心使用在注解里没有配置的默认 @HystrixCommand
     * 注解。默认情况下，当你指定一个没有属性的 @HystrixCommand 注解时，注解会将所有远程服务调用放置在同
     * 一个线程池下。这会在应用程序中引入问题。后续将讨论如何实现舱壁模式，并向你展示如何将这些远程服务调用
     * 隔离到自己的线程池中，并配置线程池的行为彼此独立。
     *
     *
     *
     * 2、自定义断路器上的超时时间
     *
     * 当与新的开发人员一起工作时，经常遇到的第一个问题是在 Hystrix 中断调用之前，它是如何自定义时间。通过
     * 向 @HystrixCommand 注解中传递额外的参数，很容易做到这一点。下面的代码演示了在调用超时前，如何自定
     * 义 Hystrix 等待的时间。
     *
     *     @HystrixCommand(
     *             commandProperties={
     *                     @HystrixProperty(
     *                     name="execution.isolation.thread.timeoutInMilliseconds",
     *                     value="12000")
     *             }
     *     )
     *     public List<License> getLicensesByOrg(String organizationId){
     *         randomlyRunLong();
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     * Hystrix 允许你通过 commandProperties 属性自定义的断路器的行为。commandProperties 属性接受一个
     * HystrixProperty 对象数组，它可以通过自定义属性来配置 Hystrix 断路器。这段代码里使用 execution
     * .isolation.thread.timeoutInMilliseconds 属性设置最大超时时间为在 Hystrix 调用失败前 12 秒。
     *
     * 现在，如果你重建并重新运行代码示例，你将永远不会得到超时错误，因为你调用时的人工超时为 11 秒，而你的
     * @HystrixCommand 注解现在被配置为仅在 12 秒后才超时。
     *
     *
     * PS：服务超时
     *
     * 很明显，这里用断路器超时 12 秒作为一个教学例子。在分布式环境中，如果开始听取开发团队的意见，就会感到
     * 紧张，因为远程服务调用的 1 秒超时太低，因为它们的服务 X 平均需要 5-6 秒。
     *
     * 很显然，被调用的服务存在未解决的性能问题。所以需要避免在 Hystrix 调用中增加默认超时，除非你完全不能
     * 解决缓慢运行的服务调用。
     *
     * 如果你确实存在这样的情况，你的服务调用中的一部分将消耗比其他服务调用更长的时间，一定要考虑将这些服务
     * 调用隔离到单独的线程池。
     */
    public static void main(String[] args) {

    }

}
