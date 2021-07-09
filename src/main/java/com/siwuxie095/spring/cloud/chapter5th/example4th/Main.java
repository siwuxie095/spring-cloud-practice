package com.siwuxie095.spring.cloud.chapter5th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-03 21:45:47
 */
public class Main {

    /**
     * 进入 Hystrix
     *
     * 构建断路器模式、后备模式和舱壁模式的实现需要对线程和线程管理有深入的理解。编写健壮的线程代码是一门艺术，并且
     * 正确地做到这一点很困难。高质量地实现断路器模式、后备模式和舱壁模式需要做大量的工作。幸运的是，开发人员可以使
     * 用 Spring Cloud 和 Netflix 的 Hystrix 库，这些库每天都在 Netflix 的微服务架构中使用，因此它们久经考验。
     *
     * 后续将讨论如下内容。
     * （1）如何配置许可证服务的 Maven 构建文件（pom.xml）以包含 Spring Cloud/Hystrix 包装器。
     * （2）如何通过 Spring Cloud/Hystrix 注解来运用断路器模式包装远程调用。
     * （3）如何在远程资源上定制断路器，以便为每个调用使用定制超时。这里还将演示如何配置断路器，以便控制断路器在
     * "跳闸" 之前发生的故障次数。
     * （4）如何在调用失败或断路器必须中断调用时实现后备策略。
     * （5）如何在服务中使用单独的线程池来隔离服务调用，并在被调用的不同远程资源之间构建舱壁。
     */
    public static void main(String[] args) {

    }

}
