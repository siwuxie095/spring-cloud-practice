package com.siwuxie095.spring.cloud.chapter8th.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-06-19 16:34:56
 */
public class Main {

    /**
     * 为什么使用消息传递、EDA 和微服务
     *
     * 为什么消息传递在构建基于微服务的应用程序中很重要？为了回答这个问题，下面从一个例子开始。这里将使用两项服务：
     * 许可证服务和组织服务。想象一下，将这些服务部署到生产环境之后，会发现，从组织服务中查找组织信息时，许可证服
     * 务调用花费了非常长的时间。在查看组织数据的使用模式时，会发现组织数据很少会更改，并且组织服务中读取的大多数
     * 数据都是按照组织记录的主键完成的。如果可以为组织数据缓存读操作从而节省访问数据库的成本，那么就可以极大地改
     * 善许可证服务调用的响应时间。
     *
     * 在实施缓存解决方案时，会意识到有以下三个核心要求。
     * （1）缓存的数据需要在许可证服务的所有实例之间保持一致 —— 这意味着不能在许可证服务本地中缓存数据，因为要保
     * 证无论服务实例如何都能读取相同的组织数据。
     * （2）不能将组织数据缓存在托管许可证服务的容器的内存中 —— 托管服务的运行时容器通常受到大小限制，并且可以使
     * 用不同的访问模式来对数据进行访问。本地缓存可能会带来复杂性，因为必须保证本地缓存与集群中的所有其他服务同步。
     * （3）在更新或删除一个组织记录时，开发人员希望许可证服务能够识别出组织服务中出现了状态更改 —— 许可证服务应
     * 该使该组织的所有缓存数据失效，并将它从缓存中删除。
     *
     * 下面来看看实现这些要求的两种方法。第一种方法将使用同步请求-响应模型来实现上述要求。在组织状态发生变化时，
     * 许可证服务和组织服务通过它们的 REST 端点进行通信。第二种方法是组织服务发出异步事件（消息），该事件将通
     * 报组织服务数据已经发生了变化。使用第二种方法，组织服务将发布一条组织记录已被更新或删除的消息到队列。许可
     * 证服务将监听中介，了解到一个组织事件已发生，并清除其缓存中的组织数据。
     *
     *
     *
     * 1、使用同步请求-响应方式来传达状态变化
     *
     * 对于组织数据缓存，这里将使用分布式的键值存储数据库 Redis。如下提供了一个高层次概览，讲述如何使用传统的同步
     * 请求-响应编程模型构建高速缓存解决方案。
     * （1）许可证服务用户（许可证服务客户端）发出对许可证服务的调用以检索许可证数据。
     * （2）许可证服务首先检查 Redis 缓存，以查找组织数据。
     * （3）如果 Redis 缓存中没有该组织数据，许可证服务调用组织服务去检索它。
     * （4）组织数据（组织服务客户端）可以通过对组织服务的调用进行更新。
     * （5）当组织服务更新时，组织服务要么调用许可证服务端点，并告诉它使其缓存失效，要么直接与许可证服务的缓存进行
     * 联系。
     *
     * PS：在同步请求-响应模型中，紧密耦合的服务带来复杂性和脆弱性。
     *
     * 当用户调用许可证服务时，许可证服务同样需要查找组织数据。许可证服务首先会检查通过组织 ID 从 Redis 集群中检
     * 索的所需的组织数据。如果许可证服务找不到组织数据，它将使用基于 REST 的端点调用组织服务，然后在将组织数据返
     * 回给用户之前，将返回的数据存储在 Redis 中。现在，如果有人使用组织服务的 REST 端点来更新或删除组织记录，组
     * 织服务将需要调用在许可证服务上公开的端点，以通知许可证服务使它缓存中的组织数据无效。
     *
     * 如果查看组织服务调用许可证服务以使 Redis 缓存失效的地方，那么至少可以看到以下三个问题：
     * （1）组织服务和许可证服务紧密耦合。
     * （2）耦合带来了服务之间的脆弱性。如果用于使缓存无效的许可证服务端点发生了更改，则组织服务必须要进行更改。
     * （3）这种方法是不灵活的，因为如果想要为组织服务添加新的消费者，就必须修改组织服务的代码，才能让它知道需要
     * 调用其他的服务以通知数据变更。
     *
     *
     * 1.1、服务之间的紧密耦合
     *
     * 从以上方案，可以看到许可证服务和组织服务之间存在紧密耦合。许可证服务始终依赖于组织服务来检索数据。然而，通
     * 过让组织服务在组织记录被更新或删除时直接与许可证服务进行通信，就已经将耦合从组织服务引入许可证服务了。为了
     * 使 Redis 缓存中的数据失效，组织服务需要许可证服务公开的端点，该端点可以被调用以使许可证服务的 Redis 缓存
     * 无效，或者组织服务必须直接与许可证服务所拥有的 Redis 服务器进行通信以清除其中的数据。
     *
     * 让组织服务与 Redis 进行通信有其自身的问题，因为开发人员正直接与另一个服务拥有的数据存储进行通信。在微服务
     * 环境中，这是一个很大的禁忌。虽然可以认为组织数据理所当然地属于组织服务，但是许可证服务在特定的上下文中使用
     * 这些数据，并且可能潜在地转换数据，或者围绕这些数据构建业务规则。让组织服务直接与 Redis 服务进行通信，可能
     * 会意外地破坏拥有许可证服务的团队所实现的规则。
     *
     *
     * 1.2、服务之间的脆弱性
     *
     * 许可证服务与组织服务之间的紧密耦合也带来了这两种服务之间的脆弱性。如果许可证服务关闭或运行缓慢，那么组织服
     * 务可能会受到影响，因为组织服务正在与许可证服务进行直接通信。同样，如果组织服务直接与许可证服务的 Redis 数
     * 据存储进行对话，那么就会在组织服务和 Redis 之间创建一个依赖关系。在这种情况下，共享 Redis 服务器出现任何
     * 问题都有可能拖垮这两个服务。
     *
     *
     * 1.3、在修改组织服务以增加新的消费者方面是不灵活的
     *
     * 这种架构的最后一个问题是，它是不灵活的。使用这种模型，如果有其他服务对组织数据发生的变化感兴趣，则需要添加
     * 另一个从组织服务到该其他服务的调用。这意味着需要更改代码并重新部署组织服务。如果使用同步的请求-响应模型来
     * 通知状态更改，则会在应用程序中的核心服务和其他服务之间出现网状的依赖关系模式。这些网络的中心会成为应用程序
     * 中的主要故障点。
     *
     *
     * PS：另一种耦合
     *
     * 虽然消息传递在服务之间增加了一个间接层，但是使用消息传递仍然会在两个服务之间引入紧密耦合。后续读者将在组织
     * 服务和许可证服务之间发送消息。这些消息将使用 JSON 作为消息的传输协议，序列化以及反序列化为 Java 对象。如
     * 果两个服务不能优雅地处理同一消息类型的不同版本，则在转换为 Java 对象时，对 JSON 消息的结构的变更会造成问
     * 题。JSON 本身不支持版本控制，但如果读者需要版本控制，那么可以使用 Apache Avro。Avro 是一个二进制协议，
     * 它内置了版本控制。Spring Cloud Stream 支持 Apache Avro 作为消息传递协议。使用 Avro 不在这里的讨论范
     * 围之内，但是这里确实希望让你意识到，如果真的担心消息版本控制的话，Avro 确实会有帮助。
     *
     *
     *
     * 2、使用消息传递在服务之间传达状态更改
     *
     * 使用消息传递方式将会在许可证服务和组织服务之间注入队列。该队列不会用于从组织服务中读取数据，而是由组织服务
     * 用于在组织服务管理的组织数据内发生状态更改时发布消息。
     *
     * 当组织状态更改时，消息将被写入位于两个服务之间的消息队列之中，如下：
     * （1）当组织服务传达状态更改时，它将消息发布到队列中。
     * （2）许可证服务监视队列中由组织服务发布的所有消息，并可根据需要使 Redis 缓存数据失效。
     *
     * 在这种模型中，每次组织数据发生变化，组织服务都发布一条消息到队列中。许可证服务正在监视消息队列，并在消息进
     * 入时将相应的组织记录从 Redis 缓存中清除。当涉及传达状态时，消息队列充当许可证服务和组织服务之间的中介。这
     * 种方法提供了以下四个好处：
     * （1）松耦合；
     * （2）耐久性；
     * （3）可伸缩性；
     * （4）灵活性。
     *
     *
     * 2.1、松耦合
     *
     * 微服务应用程序可以由数十个小型的分布式服务组成，这些服务彼此交互，并对彼此管理的数据感兴趣。正如在前面提到
     * 的同步设计中所看到的，同步 HTTP 响应在许可证服务和组织服务之间产生一个强依赖关系。尽管不能完全消除这些依
     * 赖关系，但是通过仅公开直接管理服务所拥有的数据的端点，可以尝试最小化依赖关系。消息传递的方法允许开发人员解
     * 耦两个服务，因为在涉及传达状态更改时，两个服务都不知道彼此。当组织服务需要发布状态更改时，它会将消息写入队
     * 列，而许可证服务只知道它得到一条消息，却不知道谁发布了这条消息。
     *
     *
     * 2.2、耐久性
     *
     * 队列的存在让开发人员可以保证，即使服务的消费者已经关闭，也可以发送消息。即使许可证服务不可用，组织服务也可
     * 以继续发布消息。消息将存储在队列中，并将一直保存到许可证服务可用。另一方面，通过将缓存和队列方法结合在一起，
     * 如果组织服务关闭，许可证服务可以优雅地降级，因为至少有部分组织数据将位于其缓存中。有时候，旧数据比没有数据
     * 好。
     *
     *
     * 2.3、可伸缩性
     *
     * 因为消息存储在队列中，所以消息发送者不必等待来自消息消费者的响应，它们可以继续工作。同样地，如果一个消息消
     * 费者没有足够的能力处理从消息队列中读取的消息，那么启动更多消息消费者，并让它们处理从队列中读取的消息则是一
     * 项非常简单的任务。这种可伸缩性方法适用于微服务模型，因为这里强调事情之一是，启动微服务的新实例应该是很简单
     * 的，让这些追加的微服务处理持有消息的消息队列亦是如此。这就是水平伸缩的一个示例。从队列中读取消息的传统伸缩
     * 机制涉及增加消息消费者可以同时处理的线程数。遗憾的是，这种方法最终会受消息消费者可用的 CPU 数量的限制。微
     * 服务模型则没有这样的限制，因为它是通过增加托管消费消息的服务的机器数量来进行扩大的。
     *
     *
     * 2.4、灵活性
     *
     * 消息的发送者不知道谁将会消费它。这意味着开发人员可以轻松添加新的消息消费者（和新功能），而不影响原始发送服
     * 务。这是一个非常强大的概念，因为可以在不必触及现有服务的情况下，将新功能添加到应用程序。新的代码可以监听正
     * 在发布的事件，并相应地对它们做出反应。
     *
     *
     *
     * 3、消息传递架构的缺点
     *
     * 与任何架构模型一样，基于消息传递的架构也有折中。基于消息传递的架构可能是复杂的，需要开发团队密切关注一些关
     * 键的事情，包括：
     * （1）消息处理语义；
     * （2）消息可见性；
     * （3）消息编排。
     *
     *
     * 3.1、消息处理语义
     *
     * 在基于微服务的应用程序中使用消息，需要的不只是了解如何发布和消费消息。它要求开发人员了解应用程序消费有序消
     * 息时的行为是什么，以及如果消息没有按顺序处理会发生什么情况。例如，如果严格要求来自单个客户的所有订单都必须
     * 按照接收的顺序进行处理，那么开发人员必须有区别地建立和构造消息处理方式，而不是每条消息都可以被独立地使用。
     *
     * 这还意味着，如果开发人员正在使用消息传递来执行数据的严格状态转换，那么就需要在设计应用程序时考虑到消息抛出
     * 异常或者错误按无序方式处理的场景。如果消息失败，是重试处理错误，还是就这么让它失败？如果其中一个客户消息失
     * 败，那么如何处理与该客户有关的未来消息？这些都是需要考虑的问题。
     *
     *
     * 3.2、消息可见性
     *
     * 在微服务中使用消息，通常意味着同步服务调用与异步处理服务的混合。消息的异步性意味着消息在发布或消费时，它们
     * 可能不会被立刻接收或处理。此外，像关联 ID 这些在 Web 服务调用和消息之间用于跟踪用户事务的信息，对于理解和
     * 调试应用程序中发生的事情是至关重要的。你可能还记得在之前，关联 ID 是在用户事务开始时生成的唯一编号，并与每
     * 个服务调用一起传递，此外，它还应该在每条消息被发布和消费时被传递。
     *
     *
     * 3.3、消息编排
     *
     * 正如在消息可见性的那部分中提到的，基于消息传递的应用程序更难按照应用程序的执行顺序进行业务逻辑推理，因为它
     * 们的代码不再以简单的块请求-响应模型的线性方式进行处理。相反，调试基于消息的应用程序可能涉及多个不同服务的
     * 日志，在这些服务中，用户事务可以在不同的时间不按顺序执行。
     *
     *
     * PS：消息传递可能很复杂但很强大
     *
     * 介绍消息传递架构的缺点并不是为了吓跑大家，让大家远离在应用程序中使用消息传递。相反，这里的目的是强调在服务
     * 中使用消息传递需要深谋远虑。笔者最近完成了一个主要的项目，需要为每个客户开启和关闭有状态的 AWS 服务器实例
     * 集。这里必须使用 AWS 简单排队服务（Simple Queuing Service，SQS）和 Kafka 来集成微服务调用和消息的组
     * 合。虽然这个项目很复杂，但是在项目结束时，笔者亲眼看到了消息传递的强大功能。整个团队意识到需要处理的问题是，
     * 在服务器被终止之前，必须确保从服务器上提取某些文件。这一步骤占据大约 75% 的用户工作流程，并且整个流程只有
     * 在这一步完成之后才能继续进行。幸运的是，已经有一个微服务（称为文件恢复服务），它会检查正在退出的服务器是否
     * 已将文件提取出来。由于服务器通过事件传递了所有的状态变化（包括它们正在退出），所以只需要将文件恢复服务器插
     * 入来自正在退出的服务器的事件流中，并让它们监听 "olecommissioning" 事件。
     *
     * 如果整个过程都是同步的，那么增加这个文件排查的步骤将是非常痛苦的。但是在最后，只需要一个在生产中已存在的现
     * 有服务，来监听来自现有消息队列的事件并作出反应。这项工作是在几天内完成的，而且在项目交付过程中从没出过任何
     * 差错。通过消息，开发人员可以将服务挂钩在一起，而不需要将服务在基于代码的工作流中硬编码到一起。
     */
    public static void main(String[] args) {

    }

}