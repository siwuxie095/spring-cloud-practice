package com.siwuxie095.spring.cloud.chapter5th.example7th;

/**
 * @author Jiajing Li
 * @date 2021-06-06 15:28:10
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 后备处理
     *
     * 断路器模式的一部分美妙之处在于，由于远程资源的消费者和资源本身之间存在 "中间人"，因此开发人员有机会拦截
     * 服务故障，并选择替代方案。
     *
     * 在 Hystrix 中，这被称为后备策略（fallback strategy），并且很容易实现。下面看看如何为许可数据库构建
     * 一个简单的后备策略，该后备策略简单地返回一个许可对象，这个许可对象表示当前没有可用的许可信息。如下代码
     * 展示了上述讨论的内容。
     *
     *     // fallbackMethod 属性定义了类中的一个方法，如果来自 Hystrix 的调用失败，那么就会调用该方法
     *     @HystrixCommand(fallbackMethod = "buildFallbackLicenseList")
     *     public List<License> getLicensesByOrg(String organizationId) {
     *         randomlyRunLong();
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     *     // 在后备方法中，返回了一个硬编码的值
     *     private List<License> buildFallbackLicenseList(String organizationId) {
     *         List<License> fallbackList = new ArrayList<>();
     *         License license = new License()
     *                 .withId("0000000-00-00000")
     *                 .withOrganizationId(organizationId)
     *                 .withProductName("Sorry no licensing information currently available");
     *
     *         fallbackList.add(license);
     *         return fallbackList;
     *     }
     *
     * 要使用 Hystrix 实现一个的后备策略，开发人员必须做两件事情。第一件是，需要在 @HystrixCommand 注解中
     * 添加一个名为 fallbackMethod 的属性。该属性将包含一个方法的名称，当 Hystrix 因为调用耗费时间太长而
     * 不得不中断该调用时，该方法将会被调用。
     *
     * 第二件是，需要定义一个待执行的后备方法。此后备方法必须与由 @HystrixCommand 保护的原始方法位于同一个
     * 类中，并且必须具有与原始方法完全相同的方法签名，因为传递给由 @HystrixCommand 保护的原始方法的所有参
     * 数都将传递给后备方法。
     *
     * 在这段代码中，后备方法 buildFallbackLicenseList() 只是简单构建一个包含虚拟信息的单个 License 对
     * 象。可以使用后备方法从备用数据源读取这些数据，但这里出于演示的目的，将构建一个列表，该列表由原始的方法
     * 调用返回。
     *
     *
     * PS：后备
     *
     * 在微服务检索数据并且调用失败的情况下，后备策略非常有效。在确定是否要实施后备策略时，要注意以下两点。
     * （1）后备是一种在资源超时或失败时提供行动方案的机制。如果发现自己使用后备来捕获超时异常，然后只做
     * 日志记录错误，就应该在服务调用周围使用标准的 try..catch 块，捕获 HystrixRuntimeException 异
     * 常，并将日志记录逻辑放在 try..catch 块中。
     * （2）注意使用后备方法所执行的操作。如果在后备服务中调用另一个分布式服务，就可能需要使用 @Hystrix
     * Command 注解来包装后备方法。记住，在主要行动方案中经历的相同的失败有可能也会影响次要的后备方案。
     * 要进行防御性编码。如果在使用后备的时候没有考虑到这个问题，最终可能会吃很大苦头。
     *
     *
     * 现在拥有了后备方案，接下来继续访问端点。这一次，当访问端点并遇到一个超时错误（有 1/3 的机会）时，不会
     * 从服务调用中得到一个返回的异常，而是得到虚拟的许可证值。
     */
    public static void main(String[] args) {

    }

}
