package com.siwuxie095.spring.cloud.chapter2nd.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-05-18 07:06:56
 */
public class Main {

    /**
     * 什么时候不使用微服务
     *
     * 之前已经讨论为什么微服务是构建应用程序的一个强大的架构模式。但还没提及到，什么时候你不应该使用微服务
     * 来构建你的应用程序。下面一起来讨论它们：
     * （1）分布式系统构建的复杂性
     * （2）虚拟服务器/容器扩展
     * （3）应用类型
     * （4）数据转换和一致性
     *
     *
     *
     * 1、分布式系统构建的复杂性
     *
     * 因为微服务是分布式的、细粒度的（小），他们将复杂程度引入到你的应用程序，就不会有更多的单体应用。微服
     * 务架构需要高度成熟的运维。不要考虑使用微服务，除非你的组织愿意投资于自动化和高度分布式的应用能够取得
     * 成功的运维工作（监控、伸缩）。
     *
     *
     *
     * 2、服务器扩展
     *
     * 微服务最常见的部署模型之一是将一个微服务实例部署在一台服务器上。在一个以大的微服务为基础的应用程序，
     * 你可能会有 50 到 100 的服务器或容器（通常是虚拟的），都必须在生产中被单独创建和维护。即使在云中运行
     * 这些服务的成本较低，管理和监规这些服务器的操作复杂性也可能是巨大的。
     *
     * 注意：微服务的灵活性需要针对运行所有这些服务器的成本进行权衡。
     *
     *
     *
     * 3、应用类型
     *
     * 微服务是面向可复用，对构建需要高弹性和可扩展的大型应用程序非常有用。这就是为什么如此多的基于云计算的
     * 公司已经采用了微服务。如果你构建小型的、部门级的应用程序或一个小的用户群的应用程序，将它们构建在分布
     * 式模型（如微服务）的相关复杂性可能会导致花费更多的费用。
     *
     *
     *
     * 4、数据转换和一致性
     *
     * 当你开始了解微服务，你需要思考你的服务的数据使用模式和服务消费者如何去使用它们。一个微服务包装和抽象
     * 了一小部分的表和作为执行 "操作" 任务的机制工作良好，如创建，添加，并进行简单的（非复杂）对存储查询。
     *
     * 如果你的应用程序需要在多个数据源做复杂的数据聚合或改造，微服务的分布式特性将使这项工作变得困难。你的
     * 微服务总是承担了太多职责，也容易成为性能问题。
     *
     * 也请记住，通过微服务执行事务不存在标准。如果您需要事务管理，则需要自己构建该逻辑。此外，后续你会看到，
     * 微服务通过使用消息传递可以在它们之间相互通信。在数据更新中引入消息传递，应用程序需要处理最终一致性，
     * 而对于数据的更新可能不会立即出现。
     */
    public static void main(String[] args) {

    }

}