package com.siwuxie095.spring.cloud.chapter4th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-05-30 15:57:31
 */
public class Main {

    /**
     * 关于云端服务发现的研究
     *
     * 一个基于云的微服务环境的解决方案是使用一个服务发现机制，即：
     * （1）高可用性：服务发现需要能够支持 "热" 集群环境，在服务发现集群里服务查找可以跨多个节点共享。如果
     * 某个节点不可用，则集群中的其他节点应该能够接管。
     * （2）对等的：服务发现集群中的每个节点共享服务实例的状态。
     * （3）负载均衡：服务发现需要动态负载均衡请求到所有服务实例，来确保服务调用遍布所有受管的服务实例。在
     * 许多方面，服务发现取代了在许多早期的 Web 应用程序的实现中使用的静态、手工管理的负载均衡器。
     * （4）有弹性的：服务发现客户端应该在本地 "缓存" 服务信息。本地缓存允许逐步降低服务发现特性，这样，如
     * 果服务发现服务变得不可用，应用程序仍然可以基于本地缓存中维护的信息来对服务保留原始功能和进行定位。
     * （5）容错性：服务发现需要检测服务实例不健康的情况，并将实例从可接受客户请求的可用服务列表中删除。它
     * 应该用服务来检测这些故障，并在没有人工干预的情况下采取行动。
     *
     * 在下面的部分中，将要：
     * （1）了解云服务发现代理如何工作的概念架构。
     * （2）显示客户端缓存和负载均衡允许服务在服务发现代理不可用时如何继续运行。
     * （3）了解使用 Spring Cloud 和 Netflix 的 Eureka 服务发现代理如何实现服务发现。
     *
     *
     *
     * 1、服务发现架构
     *
     * 为了展开围绕服务发现架构的讨论，需要理解四个概念。这些一般概念在所有服务发现实现中共享：
     * （1）服务注册：服务如何向服务发现代理注册？
     * （2）客户端查找服务地址：服务客户端查找服务信息的方法是什么？
     * （3）信息共享：服务信息如何跨节点共享？
     * （4）健康监控：服务如何将其健康状态返回到服务发现代理？
     *
     * 客户端应用程序从来不直接知道服务的 IP 地址。相反，它们从一个服务发现代理获取。在添加/删除服务实例时，
     * 它们将更新服务发现代理，并可用以处理用户请求。当已经启动了一个或多个服务发现节点。这些服务发现实例通
     * 常是唯一的，并且没有负载均衡器位于它们前面。
     *
     * 如下显示了这四个部分的流程，以及服务发现模式实现中通常发生的情况。
     * （1）服务位置可以由服务发现代理的逻辑名称查找。
     * （2）当一个服务上线时，它会用一个服务发现代理注册它的 IP 地址。
     * （3）服务发现节点彼此共享服务实例健康信息。
     * （4）服务向服务发现代理发送一个心跳。如果服务死亡，服务发现层将删除 "死" 实例的 IP。
     *
     * 当服务实例启动时，它们将通过一个或多个服务发现实例来注册它们的物理位置、路径和端口。虽然服务的每个实
     * 例都有唯一的 IP 地址和端口，但是每次启动的服务实例都会在同一个服务 ID 下注册。服务 ID 不过是唯一标
     * 识同一个服务实例组的键。
     *
     * 一个服务通常只注册到一个服务发现服务实例。大多数服务发现实现使用数据传播的对等模型，其中每个服务实例
     * 中的数据被传送到集群中的所有其他节点。
     *
     * 根据服务发现实现，传播机制可能使用一个硬编码的服务列表来传播或使用诸如 "gossip" 或 "infection-style"
     * 协议的多播协议，以允许其他节点 "发现" 集群中的更改。
     *
     * 最后，每个服务实例将通过服务发现服务推送或拉出其状态。任何未能返回健康检查的服务将从可用服务实例池中
     * 删除。
     *
     * 一旦服务注册到服务发现服务，它就可以由需要使用其功能的应用程序或服务使用。不同的模型存在于客户端 "发
     * 现" 服务中。客户端可以只依赖于服务发现引擎在每次调用服务时解析服务位置。通过这种方法，服务发现引擎将
     * 被调用，每次一个调用由已注册的微服务实例发起。不幸的是，这种方法很脆弱，因为服务客户端完全依赖于正在
     * 运行的服务发现引擎来查找和调用服务。
     *
     * 更健壮的方法是使用所谓的客户端负载均衡。在这个模型中，当一个消费参与者需要调用一个服务时：
     * （1）它将与服务发现服务（服务消费者要求的所有服务实例）通信，然后在服务消费者的机器上本地缓存数据。
     * （2）每次客户端要调用服务时，服务消费者都会从缓存中查找服务的位置信息。通常，客户端缓存将使用一种简单
     * 的负载均衡算法，如 "循环轮询" 负载均衡算法，以确保服务调用在多个服务实例之间传播。
     * （3）然后，客户端将定期与服务发现服务通信，并刷新服务实例的缓存。客户端缓存最终是一致的，但总有这样的
     * 风险：当客户端与服务发现实例进行刷新和调用时，调用可能指向不健康的服务实例。如果在调用服务时，服务调
     * 用失败，本地服务发现缓存无效，服务发现客户端将试图从服务发现代理刷新其条目。
     *
     * 客户端负载均衡缓存服务的位置，以便服务客户机在每次调用时不必与服务发现通信。即：
     * （1）当服务客户机需要调用服务时，它将检查服务实例 IPs 的本地缓存。服务实例之间的负载均衡将发生在服务
     * 上。
     * （2）如果客户端在缓存中找到服务 IP，它将使用它。否则，它将定位到服务发现。
     * （3）客户端缓存将周期性地被服务发现层刷新。
     *
     * 下面将把通用的服务发现模式运用到 EagleEye 的问题域。
     *
     *
     *
     * 2、使用 Spring 和 Netflix Eureka 实现服务发现
     *
     * 现在你将通过设置服务发现代理来实现服务发现，然后通过代理注册两个服务。然后，通过使用服务发现取回的信
     * 息，将有一个服务调用另一个服务。Spring Cloud 提供了从服务发现代理查找信息的多种方法。这里还将了解
     * 每种方法的优点和缺点。
     *
     * 再者，Spring Cloud 项目使这种类型的安装变得微不足道。你将使用 Spring Cloud 和 Netflix 的 Eureka
     * 服务发现引擎来实现服务发现模式。对于客户端负载均衡，你将使用 Spring Cloud 和 Netflix 的 Ribbon 库。
     *
     * 当调用许可服务时，它将调用组织服务来检索不指定的组织 ID 相关联的组织信息。组织服务的实际位置将保存在
     * 服务发现注册信息中。对于本例，你将使用服务发现注册信息注册组织服务的两个实例，然后使用客户端负载均衡
     * 查找和缓存每个服务实例中的注册信息。
     *
     * 通过实现客户端缓存和使用 Eureka 的许可和组织服务，如果 Eureka 不可用的话，你可以减少 Eureka 服务
     * 器上的负载，并提高客户端的稳定性。过程如下：
     * （1）当服务实例开始时，他们将用 Eureka 注册它们的 IP：
     * 当服务启动的时候，许可服务和组织服务也将把它们自己注册为 Eureka 服务。这个注册过程将告诉 Eureka 每
     * 个服务实例的物理位置和端口号以及启动服务的服务 ID。
     * （2）当许可服务调用组织服务时，它将使用 Ribbon 查找组织服务 IP 是否在本地缓存：
     * 当许可服务调用组织服务时，它将使用 Netflix 的 Ribbon 库提供客户端负载均衡。Ribbon 将与 Eureka 服
     * 务通信，检索服务位置信息，然后在本地缓存。
     * （3）Ribbon 将周期性地刷新其 IP 地址缓存：
     * Netflix 的 Ribbon 库将周期性地 ping Eureka 服务并刷新其本地缓存服务位置。
     *
     * 任何新的组织服务实例现在都可以对本地许可服务可见，而任何非健康实例都将从本地缓存中删除。
     *
     * 后续将通过设置 Spring Cloud Eureka 服务来实现此设计。
     */
    public static void main(String[] args) {

    }

}
