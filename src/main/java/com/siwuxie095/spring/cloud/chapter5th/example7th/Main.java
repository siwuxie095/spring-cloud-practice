package com.siwuxie095.spring.cloud.chapter5th.example7th;

/**
 * @author Jiajing Li
 * @date 2021-06-06 15:28:10
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 回退处理
     *
     * 断路器模式的一个优点是，一个 "中间人" 位于远程资源的消费者和资源本身之间，有机会让开发人员拦截服务
     * 失败并选择一种替代的操作方案。
     *
     * 在 Hystrix，这被称为回退策略，很容易实现。下面看看如何为你的许可数据库构建一个简单的回退策略，该
     * 策略只简单返回一个许可对象，该对象表示当前没有可用的许可信息。如下代码演示了返一点。
     *
     *     @HystrixCommand(fallbackMethod = "buildFallbackLicenseList")
     *     public List<License> getLicensesByOrg(String organizationId){
     *         randomlyRunLong();
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     *     private List<License> buildFallbackLicenseList(String organizationId){
     *         List<License> fallbackList = new ArrayList<>();
     *         License license = new License()
     *                 .withId("0000000-00-00000")
     *                 .withOrganizationId( organizationId )
     *                 .withProductName("Sorry no licensing information currently available");
     *
     *         fallbackList.add(license);
     *         return fallbackList;
     *     }
     *
     * 使用 Hystrix 实现一个回退策略你必须做两件事。首先，你需要给 @HystrixCommand 注解添加一个叫做
     * fallbackMethod 的属性。此属性将包含一个方法的名称，该方法将在 Hystrix 必须中断调用时调用，因
     * 为它占用的时间太长。
     *
     * 你需要做的第二件事是定义要执行的回退方法。此回退方法必须在同一个类，被 @HystrixCommand 保护的
     * 原始方法。回退方法必须与原始方法具有相同的方法参数定义，所有参数传递到由 @HystrixCommand 保护
     * 的原始方法将传递给回退。
     *
     * 这里的 buildFallbackLicenseList() 回退方法就是简单构建一个单一的许可对象包含模拟的信息。你
     * 可以让回退方法从其他数据源中读取这些数据，但为了演示目的，你将构建一个列表，该列表将由原始方法
     * 调用返回。
     *
     *
     * PS：回退
     *
     * 回退策略工作非常好的情况下，你的微服务正在检索数据且调用失败。在工作的一个组织中，将客户信息存储在
     * 操作数据存储（ODS）中，并在数据仓库中进行汇总。
     *
     * 高兴的是总是能检索最新的数据，并根据它计算摘要信息。然而，一个特别讨厌的停电，一个缓慢的数据库连接
     * 了多个服务后，决定使用 Hystrix 回退实现保护检索和汇总客户信息的服务调用。如果由于性能问题或错误
     * 而对 ODS 的调用失败，将使用回退从数据仓库表中检索汇总数据。
     *
     * 业务团队决定，给客户旧的数据比让客户看到错误或整个应用程序崩溃要好得多。当选择是否使用回退策略时，
     * 关键是你的客户对他们的数据生命周期的容忍程度，以及从不让他们看到应用程序有问题的重要性。
     *
     * 在决定是否要实现回退策略时，需要记住以下几点：
     * （1）回退是一种机制，当资源超时或失败时提供一种处理的方式。如果你发现自己使用回退捕获一个超时异常
     * 并且仅仅是记录错误日志，那么你应该在你的服务调用中使用一个标准的 try..catch 块，捕获
     * HystrixRuntimeException 异常，然后在 try..catch 块中添加日志记录逻辑。
     * （2）注意你的回退功能所采取的操作。如果在回退服务中调用另一个分布式服务，可能需要用 @HystrixCommand
     * 注解包装回退。记住，同样的失败也会影响你的次要选择。代码的防御。当使用回退时，就能体会到可能没有
     * 考虑到这一点。
     *
     *
     * 现在你的回退方法已经就位，你可以继续再次调用你的端点。这一次当你点击它并遇到超时错误时，你不应该从
     * 服务调用中获得异常，而是返回模拟许可证值。
     */
    public static void main(String[] args) {

    }

}
