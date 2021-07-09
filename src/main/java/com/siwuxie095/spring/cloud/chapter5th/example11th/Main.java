package com.siwuxie095.spring.cloud.chapter5th.example11th;

/**
 * @author Jiajing Li
 * @date 2021-06-06 17:56:59
 */
public class Main {

    /**
     * 小结
     *
     * （1）在设计高分布式应用程序（如基于微服务的应用程序）时，必须考虑客户端弹性。
     * （2）服务的彻底故障（如服务器崩溃）是很容易检测和处理的。
     * （3）一个性能不佳的服务可能会引起资源耗尽的连锁效应，因为调用客户端中的线程被阻塞，以等待服务完成。
     * （4）三种核心客户端弹性模式分别是断路器模式、后备模式和舱壁模式。
     * （5）断路器模式试图杀死运行缓慢和降级的系统调用，这样调用就会快速失败，并防止资源耗尽。
     * （6）后备模式允许开发人员在远程服务调用失败或断路器跳闸的情况下，定义替代代码路径。
     * （7）舱壁模式通过将对远程服务的调用隔离到它们自己的线程池中，使远程资源调用彼此分离。就算一组服务
     * 调用失败，这些失败也不会导致应用程序容器中的所有资源耗尽。
     * （8）Spring Cloud 和 Netflix Hystrix 库提供断路器模式、后备模式和舱壁模式的实现。
     * （9）Hystrix 库是高度可配置的，可以在全局、类和线程池级别设置。
     * （10）Hystrix支持两种隔离模型，即 THREAD 和 SEMAPHORE。
     * （11）Hystrix 默认隔离模型 THREAD 完全隔离 Hystrix 保护的调用，但不会将父线程的上下文传播到
     * Hystrix 管理的线程。
     * （12）Hystrix 的另一种隔离模型 SEMAPHORE 不使用单独的线程进行 Hystrix 调用。虽然这更有效率，
     * 但如果 Hystrix 中断了调用，它也会让服务变得不可预测。
     * （13）Hystrix 允许通过自定义 HystrixConcurrencyStrategy 实现，将父线程上下文注入 Hystrix
     * 管理的线程中。
     */
    public static void main(String[] args) {

    }

}
