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
     * 这里将会看到两大类别的 Hystrix 实现。在第一个类别中，将使用 Hystrix 断路器包装许可证服务和组织服务中所有
     * 对数据库的调用。在第二个类别中，将使用 Hystrix 包装许可证服务和组织服务之间的内部服务调用。虽然这是两个不
     * 同类别的调用，但是 Hystrix 的用法是完全一样的。
     *
     * 如下展示了使用 Hystrix 断路器来包装的远程资源。
     * （1）第一个类别：Hystrix 包装的所有数据库调用。
     * （2）第二个类别：Hystrix 包装的内部服务调用。
     *
     * PS：Hystrix 位于每个远程资源调用之间并保护客户端。远程资源调用
     * 是数据库调用还是基于 REST 的服务调用无关紧要。
     *
     * 这里将先展示如何使用同步 Hystrix 断路器从许可数据库中检索许可服务数据，以此开始对 Hystrix 的讨论。许可证
     * 服务将通过同步调用来检索数据，但在继续处理之前会等待 SQL 语句完成或断路器超时。
     *
     * Hystrix 和 Spring Cloud 使用 @HystrixCommand 注解来将 Java 类方法标记为由 Hystrix 断路器进行管理。
     * 当 Spring 框架看到 @HystrixCommand 时，它将动态生成一个代理，该代理将包装该方法，并通过专门用于处理远程
     * 调用的线程池来管理对该方法的所有调用。
     *
     * 这里将包装 LicenseService 类中的 getLicensesByOrg() 方法，如下所示。
     *
     * // @HystrixCommand 注解会使用 Hystrix 断路器包装 getLicenseByOrg() 方法
     * @HystrixCommand
     * public List<License> getLicensesByOrg(String organizationId){
     *     return licenseRepository.findByOrganizationId(organizationId);
     * }
     *
     * 这看起来代码并不多，但在这一个注解中却有很多功能。
     *
     * 使用 @HystrixCommand 注解，在任何时候调用 getLicensesByOrg() 方法时，Hystrix 断路器都将包装这个调用。
     * 每当调用时间超过 1000 ms 时，断路器将中断对 getLicensesByOrg() 方法的调用。
     *
     * 如果数据库正常工作，这个代码示例就显得很无聊。因此，通过让调用时间稍微超过 1 s（每 3 次调用中大约有 1 次），
     * 下面来模拟 getLicensesByOrg() 方法执行慢数据库查询。如下所示。
     *
     * // randomlyRunLong() 方法提供了 1/3 的概率运行耗时较长的数据库调用
     * private void randomlyRunLong() {　
     *     Random rand = new Random();
     *
     *     int randomNum = rand.nextInt((3 - 1) + 1) + 1;
     *
     *     if (randomNum == 3) {
     *         sleep();
     *     }
     * }
     *
     * private void sleep() {
     *     try {
     *         // 休眠 11 000 ms（即 11 s），Hystrix 的默认调用时间是 1 s
     *         Thread.sleep(11000);
     *     } catch (InterruptedException e) {
     *         e.printStackTrace();
     *     }
     * }
     *
     * @HystrixCommand
     * public List<License> getLicensesByOrg(String organizationId) {
     *     randomlyRunLong();
     *     return licenseRepository.findByOrganizationId(organizationId);
     * }
     *
     * 如果访问 http://localhost/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a/licenses/ 端点
     * 的次数足够多，那么应该会看到从许可证服务返回的超时错误消息。
     *
     * PS：当远程调用花费时间过长时，会抛出一个 HystrixRuntimeException 异常。
     *
     * 现在，有了 @HystrixCommand 注解，如果查询花费的时间过长，许可证服务将中断其对数据库的调用。如果需要超过
     * 1000 ms 的时间来执行 Hystrix 代码包装的数据库调用，那么服务调用将抛出一个 com.nextflix.hystrix
     * .exception.HystrixRuntimeException 异常。
     *
     *
     *
     * 1、对组织微服务的调用超时
     *
     * 这里可以使用方法级注解使被标记的调用拥有断路器功能，其优点在于，无论是访问数据库还是调用微服务，它都是相同
     * 的注解。
     *
     * 例如，在许可证服务中，需要查找与许可证关联的组织的名称。如果要使用断路器来包装对组织服务的调用的话，一个简
     * 单的方法就是将 RestTemplate 调用分解到自己的方法，并使用 @HystrixCommand 注解进行标注：
     *
     * @HystrixCommand
     * private Organization getOrganization(String organizationId) {
     *     return organizationRestClient.getOrganization(organizationId);
     * }
     *
     * 虽然使用 @HystrixCommand 很容易实现，但在使用没有任何配置的默认的 @HystrixCommand 注解时要特别小心。
     * 在默认情况下，在指定不带属性的 @HystrixCommand 注解时，这个注解会将所有远程服务调用都放在同一线程池下。
     * 这可能会导致应用程序中出现问题。后续讨论如何实现舱壁模式时，将展示如何将这些远程服务调用隔离到它们自己的
     * 线程池中，并配置线程池的行为以相互独立。
     *
     *
     *
     * 2、定制断路器的超时时间
     *
     * 在与新的开发人员合作使用 Hystrix 进行开发时，经常遇到的第一个问题是，他们如何定制 Hystrix 中断调用之前
     * 的时间。这一点通过将附加的参数传递给 @HystrixCommand 注解可以轻松完成。如下代码演示了如何定制 Hystrix
     * 在超时调用之前等待的时间。
     *
     *     @HystrixCommand(
     *             // commandProperties 属性允许开发人员提供附加的属性来定制 Hystrix
     *             commandProperties = {
     *                     @HystrixProperty(
     *                     // execution.isolation.thread.timeoutInMilliseconds 用于设置断路器的超时
     *                     // 时间（以毫秒为单位）
     *                     name = "execution.isolation.thread.timeoutInMilliseconds",
     *                     value = "12000")}
     *     )
     *     public List<License> getLicensesByOrg(String organizationId) {
     *         randomlyRunLong();
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     * Hystrix 允许通过 commandProperties 属性来定制断路器的行为。
     *
     * commandProperties 属性接受一个 HystrixProperty 对象数组，它可以传入自定义属性来配置 Hystrix 断路器。
     * 这段代码使用 execution.isolation.thread.timeoutInMilliseconds 属性设置 Hystrix 调用的最大超时时
     * 间为 12 s。现在，如果重新构建并重新运行这个代码示例，则永远都不会出现超时错误，因为人工超时时间为 11 s，
     * 而 @HystrixCommand 注解现在配置为 12 s 后才会超时。
     *
     *
     * PS：服务超时
     *
     * 显然，12 s 的断路器超时只是这里用来作为教学的一个例子。在分布式环境中，如果开始听到开发团队反馈，说远程
     * 服务调用上的 1 s 超时时间太少了，因为他们的服务 X 平均需要 5～6 s 的时间，那么就应该感到紧张。
     *
     * 这些反馈通常说明，被调用的服务存在未解决的性能问题。开发人员应避免在 Hystrix 调用上增加默认超时的诱惑，
     * 除非实在无法解决运行缓慢的服务调用。
     *
     * 如果确实遇到一些比其他服务调用需要更长时间的服务调用，务必将这些服务调用隔离到单独的线程池中。
     */
    public static void main(String[] args) {

    }

}
