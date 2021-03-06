package com.siwuxie095.spring.cloud.chapter5th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-06-02 21:36:38
 */
public class Main {

    /**
     * 为什么客户端弹性很重要
     *
     * 之前已经抽象地介绍了这些不同的模式，下面来深入了解一些可以应用这些模式的更具体的例子。针对一个常见场景，看看
     * 为什么客户端弹性模式（如断路器模式）对于实现基于服务的架构至关重要，尤其是在云中运行的微服务架构。
     *
     * 如下展示了一个典型的场景，它涉及使用远程资源，如数据库和远程服务。
     * （1）应用程序 A 和应用程序 B 使用服务 A 来完成工作。应用程序 C 使用服务 C。
     * （2）服务 A 调用服务 B 来完成一些工作。服务 B 调用服务 C 来完成一些工作。
     * （3）服务 A 使用数据源 A 来获取一些数据。
     * （4）服务 B 有多个实例，每个实例都与数据源 B 进行联系。
     * （5）服务 C 会访问 NAS（写入共享文件系统），这就是导火索。对 NAS 对微小更改
     * 会导致服务 C 出现性能问题，最终导致一切分崩离析。
     *
     * PS：应用程序是相互关联依赖的图形结构。如果不管理这些依赖之间的远程调用，那么
     * 一个表现不佳的远程资源可能会拖垮所有服务。
     *
     * 在如上场景中，三个应用程序分别以这样或那样的方式与三个不同的服务进行通信。应用程序 A 和应用程序 B 与服务 A
     * 直接通信。服务 A 从数据库检索数据，并调用服务 B 来为它工作。服务 B 从一个完全不同的数据库平台中检索数据，
     * 并从第三方云服务提供商调用另一个服务 —— 服务 C，该服务严重依赖于内部网络区域存储（Network Area Storage，
     * NAS）设备，以将数据写入共享文件系统。此外，应用程序 C 直接调用服务 C。
     *
     * 在某个周末，网络管理员对 NAS 配置做了一个他认为是很小的调整。这个调整似乎可以正常工作，但是在周一早上，所有
     * 对特定磁盘子系统的读取开始变得非常慢。
     *
     * 编写服务 B 的开发人员从来没有预料到会发生调用服务 C 缓慢的事情。他们所编写的代码中，在同一个事务中写入数据
     * 库和从服务 C 读取数据。当服务 C 开始运行缓慢时，不仅请求服务 C 的线程池开始堵塞，服务容器的连接池中的数据
     * 库连接也会耗尽，因为这些连接保持打开状态，这一切的原因是对服务 C 的调用从来没有完成。
     *
     * 最后，服务 A 耗尽资源，因为它调用了服务 B，而服务 B 的运行缓慢则是因为它调用了服务 C。最后，所有三个应用程
     * 序都停止响应了，因为它们在等待请求完成中耗尽了资源。
     *
     * 如果在调用分布式资源（无论是调用数据库还是调用服务）的每一个点上都实现了断路器模式，则可以避免这种情况。即：
     * 如果使用断路器实现了对服务 C 的调用，那么当服务 C 开始表现不佳时，对服务 C 的特定调用的断路器就会跳闸，并
     * 且快速失败，而不会消耗掉一个线程。如果服务 B 有多个端点，则只有与服务 C 特定调用交互的端点才会受到影响。服
     * 务 B 的其余功能仍然是完整的，可以满足用户的要求。
     *
     * 断路器在应用程序和远程服务之间充当中间人。在上述场景中，断路器实现可以保护应用程序 A、应用程序 B 和应用程序
     * C 免于完全崩溃。
     *
     * 此时，服务 B（客户端）永远不会直接调用服务 C。相反，在进行调用时，服务 B 把服务的实际调用委托给断路器，断路
     * 器将接管这个调用，并将它包装在独立于原始调用者的线程（通常由线程池管理）中。通过将调用包装在一个线程中，客户
     * 端不再直接等待调用完成。相反，断路器会监视线程，如果线程运行时间太长，断路器就可以终止该调用。
     *
     * PS：断路器跳闸，让表现不佳的服务调用迅速而优雅地失败。
     *
     * 在使用断路器时，有三种场景。第一种场景是愉快路径：断路器将维护一个定时器，如果在定时器的时间用完之前完成对远
     * 程服务的调用，那么一切都非常顺利，服务 B 可以继续工作。第二种场景是没有后备的断路器：在部分降级的场景中，服
     * 务 B 将通过断路器调用服务 C。但是，如果这一次服务 C 运行缓慢，在断路器维护的线程上的定时器超时之前无法完成
     * 对远程服务的调用，断路器就会切断对远程服务的连接。
     *
     * 然后，服务 B 将从发出的调用中得到一个错误，但是服务 B 不会占用资源（也就是自己的线程池或连接池）来等待服务
     * C 完成调用。如果对服务 C 的调用被断路器超时中断，断路器将开始跟踪已发生故障的数量。
     *
     * 如果在一定时间内在服务 C 上发生了足够多的错误，那么断路器就会电路 "跳闸"，并
     * 且在不调用服务 C 的情况下，就判定所有对服务 C 的调用将会失败。
     *
     * 电路跳闸将会导致如下三种结果。
     * （1）服务 B 现在立即知道服务 C 有问题，而不必等待断路器超时。
     * （2）服务 B 现在可以选择要么彻底失败，要么执行替代代码（后备）来采取行动。
     * （3）服务 C 将获得一个恢复的机会，因为在断路器跳闸后，服务 B 不会调用它。这使得服务 C 有了喘息的空间，并有
     * 助于防止出现服务降级时发生的级联死亡。
     *
     * 第三种场景是带有后备的断路器：最后，断路器会让少量的请求调用直达一个降级的服务，如果这些调用连续多次成功，断
     * 路器就会自动复位。
     *
     * 以下是断路器模式为远程调用提供的关键能力。
     * （1）快速失败：当远程服务处于降级状态时，应用程序将会快速失败，并防止通常会拖垮整个应用程序的资源耗尽问题的
     * 出现。在大多数中断情况下，最好是部分服务关闭而不是完全关闭。
     * （2）优雅地失败：通过超时和快速失败，断路器模式使应用程序开发人员有能力优雅地失败，或寻求替代机制来执行用户
     * 的意图。例如，如果用户尝试从一个数据源检索数据，并且该数据源正在经历服务降级，那么应用程序开发人员可以尝试
     * 从其他地方检索该数据。
     * （3）无缝恢复：有了断路器模式作为中介，断路器可以定期检查所请求的资源是否重新上线，并在没有人为干预的情况下
     * 重新允许对该资源进行访问。
     *
     * 在大型的基于云的应用程序中运行着数百个服务，这种优雅的恢复能力至关重要，因为它可以显著减少恢复服务所需的时间，
     * 并大大减少因疲劳的运维人员或应用工程师直接干预恢复服务（重新启动失败的服务）而造成更严重问题的风险。
     */
    public static void main(String[] args) {

    }

}
