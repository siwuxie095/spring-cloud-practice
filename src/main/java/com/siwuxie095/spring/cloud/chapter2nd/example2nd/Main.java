package com.siwuxie095.spring.cloud.chapter2nd.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-05-16 22:11:26
 */
public class Main {

    /**
     * 架构师的故事：设计微服务架构
     *
     * 架构师在软件项目中的作用是提供待解决问题的工作模型。架构师的工作是提供脚手架，开发人员将根据这些脚手架构建
     * 他们的代码，使应用程序所有部件都组合在一起。
     *
     * 在构建微服务架构时，项目的架构师主要关注以下三个关键任务：
     * （1）分解业务问题；
     * （2）建立服务粒度；
     * （3）定义服务接口。
     *
     *
     *
     * 1、分解业务问题
     *
     * 面对复杂性，大多数人试图将他们正在处理的问题分解成可管理的块。因为这样他们就不必努力把问题的所有细节都考虑
     * 进来。他们将问题抽象地分解成几个关键部分，然后寻找这些部分之间存在的关系。
     *
     * 在微服务架构中，架构师将业务问题分解成代表离散活动领域的块。这些块封装了与业务域特定部分相关联的业务规则和
     * 数据逻辑。
     *
     * 虽然这里希望微服务封装执行单个事务的所有业务规则，但这并不总是行得通。在实际中，经常会遇到需要跨业务领域不
     * 同部分的一组微服务来完成整个事务的情况。架构师通过查看数据域中那些不适合放到一起的地方来划分一组微服务的服
     * 务边界。
     *
     * 例如，架构师可能会看到代码执行的业务流程，并意识到它们同时需要客户和产品信息。存在两个离散的数据域时，通常
     * 就意味着需要使用多个微服务。业务事务的两个不同部分如何交互通常成为微服务的服务接口。
     *
     * 分离业务领域是一门艺术，而不是非黑即白的科学。你可以使用以下指导方针将业务问题识别和分解为备选的微服务。
     * （1）描述业务问题，并聆听用来描述问题的名词。在描述问题时，反复使用的同一名词通常意味着它们是核心业务领域
     * 并且适合创建微服务。EagleEye 域的目标名词可能会是合同 、许可证和资产。
     * （2）注意动词。动词突出了动作，通常代表问题域的自然轮廓。如果发现自己说出 "事务 X 需要从事物 A 和事物 B
     * 获取数据" 这样的话，通常表明多个服务正在起作用。如果把注意动词的方法应用到 EagleEye 上，那么就可能会查
     * 找像 "来自桌面服务的 Mike 安装新 PC 时，他会查找软件 X 可用的许可证数量，如果有许可证，就安装软件。然
     * 后他更新了跟踪电子表格中使用的许可证的数量" 这样的陈述句。这里的关键动词是查找和更新。
     * （3）寻找数据内聚。将业务问题分解成离散的部分时，要寻找彼此高度相关的数据。如果在会话过程中，突然读取或更
     * 新与迄今为止所讨论的内容完全不同的数据，那么就可能还存在其他候选服务。微服务应完全拥有自己的数据。
     *
     * 下面将这些指导方针应用到现实世界的问题中。之前介绍了一种名为 EagleEye 的现有软件产品，该软件产品用于管理
     * 软件资产，如软件许可证和安全套接字层（SSL）证书。这些软件资产被部署到组织中的各种服务器上。
     *
     * EagleEye 是一个传统的单体 Web 应用程序，部署在位于客户数据中心内的 J2EE 应用程序服务器。这里的目标是将
     * 现有的单体应用程序梳理成一组服务。
     *
     * 首先，要采访 EagleEye 应用程序的所有用户，并讨论他们是如何交互和使用 EagleEye 的。如下描述了与不同业务
     * 客户进行的对话的总结。通过查看 EagleEye 的用户是如何与应用程序进行交互的，以及如何将应用程序的数据模型分
     * 解出来，可以将 EagleEye 问题域分解为若干备选微服务。
     * （1）Rick（采购）：将合同信息输入 EagleEye；定义软件许可证的类型；输入购买获得的许可证数量。
     * （2）Ruth（财务）：运行每月成本报告；分析每份合同的许可证成本；确定许可证是否已被使用或未充分使用；取消未
     * 使用的软件许可证。
     * （3）Mike（桌面服务）：设置 PC；确定 PC 的软件许可是否可用；更新 EagleEye，决定哪些用户拥有哪些软件。
     *
     * 如上强调了与业务用户对话时出现的一些名词和动词。因为这是现有的应用程序，所以可以查看应用程序并将主要名词映
     * 射到物理数据模型中的表。现有应用程序可能有数百张表，但每张表通常会映射回一组逻辑实体。
     *
     * 如下是基于与 EagleEye 客户对话的简化数据模型。
     * （1）组织与许可证进行交互。
     * （2）许可证与资产进行交互。
     * （3）许可证与合同进行交互。
     *
     * 基于业务对话和数据模型，备选微服务是组织、许可证、合同和资产服务。
     *
     *
     *
     * 2、建立服务粒度
     *
     * 拥有了一个简化的数据模型，就可以开始定义在应用程序中需要哪些微服务。根据数据模型，可以看到潜在的四个微服务
     * 基于以下元素：
     * （1）资产；
     * （2）许可证；
     * （3）合同；
     * （4）组织。
     *
     * 这里的目标是将这些主要的功能部件提取到完全独立的单元中，这些单元可以独立构建和部署。但是，从数据模型中提取
     * 服务需要的不只是将代码重新打包到单独的项目中，还涉及梳理出服务访问的实际数据库表，并且只允许每个单独的服务
     * 访问其特定域中的表。
     *
     * PS：每个服务都拥有域内的所有数据。这并不意味着每个服务都有自己的数据库，而意味着只有拥有该域的服务才能访问
     * 其中的数据库表。
     *
     * 将问题域分解成不同的部分后，开发人员通常会发现自己不确定是否为服务划分了适当的粒度级别。一个太粗粒度或太细
     * 粒度的微服务将具有很多的特征，将在稍后讨论。
     *
     * 构建微服务架构时，粒度的问题很重要，可以采用以下思想来确定正确的解决方案。
     * （1）开始的时候可以让微服务涉及的范围更广泛一些，然后将其重构到更小的服务。在开始微服务旅程之初，容易出现
     * 的一个极端情况就是将所有的事情都变成微服务。但是将问题域分解为小型的服务通常会导致过早的复杂性，因为微服务
     * 变成了细粒度的数据服务。
     * （2）重点关注服务如何相互交互。这有助于建立问题域的粗粒度接口。从粗粒度重构到细粒度是比较容易的。
     * （3）随着对问题域的理解不断增长，服务的职责将随着时间的推移而改变。通常来说，当需要新的应用功能时，微服务
     * 就会承担起职责。最初的微服务可能会发展为多个服务，原始的微服务则充当这些新服务的编排层，负责将应用的其他
     * 部分的功能封装起来。
     *
     *
     * PS：糟糕的微服务的 "味道"
     *
     * 如何知道微服务的划分是否正确？如果微服务过于粗粒度，可能会看到以下现象。
     * （1）服务承担过多的职责。服务中的业务逻辑的一般流程很复杂，并且似乎正在执行一组过于多样化的业务规则。
     * （2）该服务正在跨大量表来管理数据。微服务是它管理的数据的记录系统。如果发现自己将数据持久化存储到多个表或
     * 接触到当前数据库以外的表，那么这就是一条服务过于粗粒度的线索。这里喜欢使用这么一个指导方针：微服务应该不
     * 超过 3～5 个表。再多一点，服务就可能承担了太多的职责。
     * （3）测试用例太多。随着时间的推移，服务的规模和职责会增长。如果一开始有一个只有少量测试用例的服务，到了最
     * 后该服务需要数百个单元测试用例和集成测试用例，那么就可能需要重构。
     *
     * 如果微服务过于细粒度呢？
     * （1）问题域的一部分微服务像兔子一样繁殖。如果一切都成为微服务，将服务中的业务逻辑组合起来会变得复杂和困难，
     * 因为完成一项工作所需的服务数量会快速增长。一种常见的 "坏味道" 出现在应用程序有几十个微服务，并且每个服务
     * 只与一个数据库表进行交互时。
     * （2）微服务彼此间严重相互依赖。在问题域的某一部分中，微服务相互来回调用以完成单个用户请求。
     * （3）微服务成为简单CRUD（Create，Read，Update，Delete）服务的集合。微服务是业务逻辑的表达，而不是数据
     * 源的抽象层。如果微服务除了 CRUD 相关逻辑之外什么都不做，那么它们可能被划分得太细粒度了。
     *
     *
     * 应该通过演化思维的过程来开发一个微服务架构，在这个过程中，你知道不会第一次就得到正确的设计。这就是最好从一
     * 组粗粒度的服务而不是一组细粒度的服务开始的原因。同样重要的是，不要对设计带有教条主义。因为可能会面临两个单
     * 独的服务之间交互过于频繁，或者服务的域之间不存在明确的边界这样的物理约束，当面临这样的约束时，需要创建一个
     * 聚合服务来将数据连接在一起。
     *
     * 最后，采取务实的做法并进行交付，而不是浪费时间试图让设计变得完美，最终导致没有东西可以展现你的努力。
     *
     *
     *
     * 3、互相交流：定义服务接口
     *
     * 架构师需要关心的最后一部分，是应用程序中的微服务该如何彼此交流。使用微服务构建业务逻辑时，服务的接口应该是
     * 直观的，开发人员应该通过学习应用程序中的一两个服务来获得应用程序中所有服务的工作节奏。
     *
     * 一般来说，可使用以下指导方针思考服务接口设计。
     * （1）拥抱 REST 的理念：REST 对服务的处理方式是将 HTTP 作为服务的调用协议并使用标准 HTTP 动词
     * （GET、PUT、POST 和 DELETE）。围绕这些 HTTP 动词对基本行为进行建模。
     * （2）使用 URI 来传达意图：用作服务端点的 URI 应描述问题域中的不同资源，并为问题域内的资源的关系
     * 提供一种基本机制。
     * （3）请求和响应使用 JSON：JavaScript 对象表示法（JavaScript Object Notation，JSON）是一个
     * 非常轻量级的数据序列化协议，并且比 XML 更容易使用。
     * （4）使用 HTTP 状态码来传达结果：HTTP 协议具有丰富的标准响应代码，以指示服务的成功或失败。学习
     * 这些状态码，并且最重要的是在所有服务中始终如一地使用它们。
     *
     * 所有这些指导方针都是为了完成一件事，那就是使服务接口易于理解和使用。这里希望开发人员坐下来查看一下服务接口
     * 就能开始使用它们。如果微服务不容易使用，开发人员就会另辟道路，破坏架构的意图。
     */
    public static void main(String[] args) {

    }

}
