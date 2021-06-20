package com.siwuxie095.spring.cloud.chapter8th.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-06-20 23:24:15
 */
public class Main {

    /**
     * Spring Cloud Stream 简介
     *
     * Spring Cloud 可以轻松地将消息传递集成到基于 Spring 的微服务中，它是通过 Spring Cloud Stream 项目
     * 来实现这一点的。Spring Cloud Stream 是一个由注解驱动的框架，它允许开发人员在 Spring 应用程序中轻松
     * 地构建消息发布者和消费者。
     *
     * Spring Cloud Stream 还允许开发人员抽象出正在使用的消息传递平台的实现细节。Spring Cloud Stream 可
     * 以使用多个消息平台（包括 Apache Kafka 项目和 RabbitMQ），而平台的具体实现细节则被排除在应用程序代码
     * 之外。在应用程序中实现消息发布和消费是通过平台无关的 Spring 接口实现的。
     *
     * 注意：在这里将使用名为 Kafka 的轻量级消息总线。Kafka 是一种轻量级、高性能的消息总线，允许开发人员异步
     * 地将消息从一个应用程序发送到一个或多个其他应用程序。Kafka 是用 Java 编写的，由于 Kafka 具有高可靠性
     * 和可伸缩性，在许多基于云的应用程序中，它已经成为事实上的标准消息总线。此外，Spring Cloud Stream 还
     * 支持使用 RabbitMQ 作为消息总线。
     *
     * 要了解 Spring Cloud Stream，先从 Spring Cloud Stream 的架构开始讨论，并熟悉 Spring Cloud
     * Stream 的术语。如果你以前从未使用过基于消息传递的平台，那么接下来所涉及的新术语可能会有些令人难以
     * 理解。
     *
     *
     *
     * Spring Cloud Stream 架构
     *
     * 下面以通过消息传递进行通信的两个服务的角度来查看 Spring Cloud Stream 的架构。在这两个服务中，一个是
     * 消息发布者，另一个是消息消费者。
     *
     * 随着消息的发布和消费，它将流经一系列的 Spring Cloud Stream 组件，这些组件抽象出底层消息传递平台。如
     * 下展示了如何使用 Spring Cloud Stream 来帮助消息传递的过程。
     * （1）服务客户端调用服务，然后服务更改它所拥有的数据的状态。这是在服务的业务逻辑中完成的。
     * （2）发射器是发布消息的服务的 Spring 代码。
     * （3）消息发布到通道。
     * （4）绑定器是与特定消息传递系统通信的 Spring Cloud Stream 框架代码。
     * （5）消息代理可以使用任意数量的消息平台实现，包括 Apache Kafka 和 RabbitMQ。
     * （6）消息处理（绑定器、通道、接收器）的顺序随着服务接收消息而发生变化。
     * （7）接收器是特定于服务的代码，它监听一个通道，然后处理传入的消息。
     *
     * 随着 Spring Cloud 中消息的发布和消费，有四个组件涉及发布消息和消费消息，它们是：
     * （1）发射器（source）；
     * （2）通道（channel）；
     * （3）绑定器（binder）；
     * （4）接收器（sink）。
     *
     *
     *
     * 1、发射器
     *
     * 当一个服务准备发布消息时，它将使用一个发射器发布消息。发射器是一个 Spring 注解接口，它接收一个普通
     * Java 对象（POJO），该对象代表要发布的消息。发射器接收消息，然后序列化它（默认的序列化是 JSON）并
     * 将消息发布到通道。
     *
     *
     *
     * 2、通道
     *
     * 通道是对队列的一个抽象，它将在消息生产者发布消息或消息消费者消费消息后保留该消息。通道名称始终与目标队列
     * 名称相关联。然而，队列名称永远不会直接公开给代码，相反，通道名称会在代码中使用。这意味着开发人员可以通过
     * 更改应用程序的配置而不是应用程序的代码来切换通道读取或写入的队列。
     *
     *
     *
     * 3、绑定器
     *
     * 绑定器是 Spring Cloud Stream 框架的一部分，它是与特定消息平台对话的 Spring 代码。Spring Cloud
     * Stream 框架的绑定器部分允许开发人员处理消息，而不必依赖于特定于平台的库和 API 来发布和消费消息。
     *
     *
     *
     * 4、接收器
     *
     * 在 Spring Cloud Stream 中，服务通过一个接收器从队列中接收消息。接收器监听传入消息的通道，并将消息反序
     * 列化为 POJO。从这里开始，消息就可以按照 Spring 服务的业务逻辑来进行处理。
     */
    public static void main(String[] args) {

    }

}
