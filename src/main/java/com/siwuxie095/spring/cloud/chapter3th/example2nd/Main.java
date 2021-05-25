package com.siwuxie095.spring.cloud.chapter3th.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-05-25 22:05:09
 */
public class Main {

    /**
     * 管理配置（和复杂性）
     *
     * 管理应用程序的配置对于运行在云端的微服务是至关重要的，因为微服务实例需要以最小的人为干预，迅速启动。
     * 每当一个人需要手动配置或获取某个服务来部署它时，配置漂移、意外中断和响应时间延迟会给应用程序的可伸
     * 缩性带来挑战。
     *
     * 下面通过建立想要遵循的四个原则来开始关于应用程序配置管理的讨论：
     * （1）隔离：希望将服务配置信息与服务的实际物理部署完全分离。应用程序配置不应该部署到服务实例中。相
     * 反，在服务启动时，配置信息应该作为环境变量传递到正在启动的服务或从中央仓库中读取。
     * （2）抽象：抽象服务接口背后的配置数据的访问。与其编写直接访问服务存储库的代码（即 读取文件的数据或
     * 使用 JDBC 访问数据库），不如让应用程序使用基于 REST 的 JSON 服务检索配置数据。
     * （3）集中:因为基于云的应用程序可能有数百个服务，因此最小化保存配置信息的不同存储库的数量是至关重要
     * 的。将应用程序配置集中到尽可能少的存储库中。
     * （4）稳定：因为你的应用程序的配置信息将与你部署的服务完全隔离和中心化，至关重要的是，无论你使用何
     * 种解决方案，都可以实现高可用和冗余。
     *
     * 要记住的关键一点是，当将配置信息分离到实际代码之外时，你将创建一个需要管理和版本控制的外部依赖项。
     * 这里再次强调，应用程序配置数据需要跟踪和版本控制，因为管理不善的应用配置是不易察觉的错误和意外中断
     * 的一个肥沃的滋生地。
     *
     *
     *
     * 1、配置管理架构
     *
     * 在引导一个微服务的阶段，会发生一个微服务配置管理的加载。下面针对前面所阐述的四个原则（隔离、抽象、
     * 集中和稳定），看看这四个原则在服务引导时是如何应用的。
     *
     * 在引导过程中，配置服务起着关键的作用。
     * （1）微服务实例启动并获得配置信息。
     * （2）实际配置驻留在存储库中。
     * （3）来自开发人员的更改被通过构建和部署管道推送到配置存储库。
     * （4）配置更改的应用程序被通知刷新自己。
     *
     * 配置管理概念体系结构中，发生了这样几个活动：
     * （1）当微服务实例启动时，它会调用一个服务端点读取其操作的特定环境中的配置信息。在微服务启动时，配
     * 置管理的连接信息（连接凭据、服务端点等等）会被传递到微服务。
     * （2）实际配置将驻留在存储库中。基于配置存储库的实现，你可以选择使用不同的实现来保存配置数据。实现
     * 选项可以包括源代码控制的文件、关系数据库或键值数据存储。
     * （3）应用程序配置数据的实际管理独立于应用程序部署的方式。配置管理的更改通常通过构建和部署管道来处
     * 理，其中配置的更改可以通过版本信息进行标记，并通过不同的环境进行部署。
     * （4）当进行配置管理更改时，必须通知使用该应用程序配置数据的服务，并刷新应用程序数据的副本。
     *
     * 在这一点上，已经完成了概念架构，它演示了配置管理模式的不同部分，以及这些组件是如何组合在一起的。
     * 现在将继续为该模式研究不同解决方案，然后看看具体的实现。
     *
     *
     *
     * 2、实现选择
     *
     * 下面看看几种不同的选择，并比较它们。如下是实现配置管理系统的开源项目。
     * （1）
     * 项目名称：Etcd
     * 描述：编写于 Go 语言的开源项目。用于服务发现和键值管理。采用 raft（https://raft.github.io/）
     * 协议的分布式计算模型。
     * 特性：非常快速和可伸缩；分布式的；命令行驱动；易于使用和设置。
     * （2）
     * 项目名称：Eureka
     * 描述：由 Netflix 撰写。经过充分的测试。用于服务发现和键值管理。
     * 特性：分布式键值存储；柔性；通过配置提供动态客户端刷新的开箱即用功能。
     * （3）
     * 项目名称：Consul
     * 描述：由 Hashicorp 编写。特性与 Etcd 和 Eureka 类似，但使用不同的算法的分布式计算模型（SWIM
     * protocol：https://www.cs.cornell.edu/~asdas/research/dsn02-swim.pdf）。
     * 特性：快速；提供直接与 DNS 集成的本地服务发现；不提供客户端动态刷新权限；开箱即用。
     * （4）
     * 项目名称：ZooKeeper
     * 描述：提供分布式锁定功能的 Apache 项目。通常用作访问键值数据的配置管理解决方案。
     * 特性：最古老，经过充分测试的解决方案；使用起来最复杂的配置管理，但应该考虑除非你已经使用 ZooKeeper
     * 作为你架构的一部分。
     * （5）
     * 项目名称：Spring Cloud configuration server
     * 描述：提供不同后端的通用配置管理解决方案的开放源代码项目。它可以与作为后端的 Git、Eureka 和 Consul
     * 整合。
     * 特性：非分布式键值存储；为 Spring 和非 Spring 服务提供紧密集成；可以使用多个后端存储配置数据，
     * 包括共享的文件系统、Eureka、Consul 和 Git。
     *
     * 上面所有解决方案都可以很容易地用于构建配置管理解决方案。这里将选择使用 Spring Cloud 配置服务器。
     * 之所以选择这个解决方案有几个原因，包括以下几点：
     * （1）Spring Cloud 配置服务器很容易设置和使用。
     * （2）Spring Cloud 配置与 Spring Boot 紧密集成。你可以通过几个简单的注解来读取应用程序的所有配
     * 置数据。
     * （3）Spring Cloud 配置服务器提供多个后端来存储配置数据。如果你已经使用了像 Eureka 和 Consul
     * 这样的工具，你可以把它们整合到 Spring Cloud 配置服务器中。
     * （4）在上面所有解决方案中，Spring Cloud 服务器可以直接与 Git 源代码控制平台集成。Spring Cloud
     * 配置与 Git 的集成消除了解决方案中的额外依赖性，并使应用程序配置数据的版本化成为可能。而其他工具
     * （Etcd、Consul、Eureka）不提供任何一种原生的版本，如果你想要，你必须自己建立它。如果你使用 Git，
     * Spring Cloud 配置服务器的使用是一个诱人的选择。
     *
     * 在此基础之上，后续你将要：
     * （1）配置一个 Spring Cloud 配置服务器，并演示两种不同的机制：一种是使用文件系统，另一种是使用
     * Git 存储库来为应用程序配置数据提供服务。
     * （2）继续构建许可服务以从数据库检索数据。
     * （3）将 Spring Cloud 配置服务集成到许可服务中，以提供应用程序配置数据。
     */
    public static void main(String[] args) {

    }

}
