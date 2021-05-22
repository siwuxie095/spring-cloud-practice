package com.siwuxie095.spring.cloud.chapter2nd.example7th;

/**
 * @author Jiajing Li
 * @date 2021-05-22 08:54:27
 */
public class Main {

    /**
     * 小结
     *
     * （1）要使微服务成功，你需要将架构师、软件开发人员、DevOps 的观点结合在一起。
     * （2）微服务，虽然是强大的架构范式，但也有自己的利益和权衡。不是所有的应用程序都应是
     * 微服务应用。
     * （3）从架构师的视角，微服务是小的、独立的、分布式的。微服务应该有有限的范围和管理一
     * 个小的数据集。
     * （4）从开发者的视角，微服务通常使用 REST 样式设计构建，JSON 作为发送和从服务接收数
     * 据的有效载荷。
     * （5）Spring Boot 是构建微服务的理想框架，因为它让你使用很少的简单注解，就可以创建
     * 一个基于 REST 的 JSON 服务。
     * （6）从 DevOp 的视角，微服务如何打包、部署和监控是至关重要的。
     * （7）开箱即用，Spring Boot 允许你将服务作为一个独立的可执行 JAR 文件交付。生产者
     * JAR 文件中的嵌入式 Tomcat 服务器承载服务。
     * （8）Spring Actuator，它包含在 Spring Boot 框架中，其暴露了关于服务运行健康的信
     * 息以及服务运行时的信息。
     */
    public static void main(String[] args) {

    }

}
