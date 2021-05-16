package com.siwuxie095.spring.cloud.chapter2nd.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-05-16 22:11:26
 */
public class Main {

    /**
     * 架构师的故事：设计微服务架构
     *
     * 架构师在软件项目中的角色是提供需要解决的问题的工作模型。架构师的工作是提供开发人员用于构建代码的脚手架，
     * 以便应用程序的所有组件能够组合在一起。
     *
     * 在建立一个微服务架构的时候，一个项目的架构聚焦在三个关键任务：
     * （1）分解业务问题
     * （2）确定服务粒度
     * （3）定义服务接口
     *
     *
     *
     * 1、分解业务问题
     *
     * 面对复杂性，大多数人试图分解他们正在处理的问题。他们这样做，就不必试图把问题的所有细节都放在脑子里。相
     * 反，他们将问题抽象地分解成几个关键部分，然后寻找这些部分之间存在的关系。
     *
     * 在微服务架构，架构师分解业务问题成块，它表示活动离散域。这些块封装了业务规则和与业务域特定部分相关联的
     * 数据逻辑。
     *
     * 虽然你想微服务封装进行一个事务的所有业务规则，但这并不总是可行的。你经常会遇到这种情况，你需要有一组微
     * 服务跨业务领域的不同的部分来完成整个事务。一个架构师梳理一套微服务的服务界限，看看哪里的数据域不适合在
     * 一起。
     *
     * 例如，架构师可能会查看一个由代码执行的业务流，并意识到它们需要客户和产品信息。两个离散数据域的存在是一
     * 个很好的迹象，即 多微服务在起作用。业务事务的两个不同部分如何相互作用通常成为微服务的服务接口。
     *
     * 分解业务领域是一种艺术形式，而不是一门黑白科学。使用下列准则确定和分解业务问题成为微服务候选者：
     * （1）描述业务问题，依你用的名词来描述问题：在描述问题时反复使用相同的名词，通常是一个核心业务领域和一
     * 个微服务契机的象征。例如 EagleEye 域中的目标名词，看起来如：合同、许可证和资产。
     * （2）注意动词：动词突出动作，通常表示问题域的自然轮廓。如果你发现自己在说 "事务 X 需要从 A 和 B 类
     * 获得数据"，那通常意味着有多个服务在起作用。如果你运用 EagleEye 看动词的方法，你可能会寻找这样的语句，
     * “当迈克使用桌面服务正在安装一台新电脑，他查看了可供软件 X 的可用许可证数量，如果许可证可用，安装软件。
     * 然后，他更新跟踪电子表格中已使用的许可证数量。”，这里的关键动词是 looks 和 updates。
     * （3）寻找数据内聚性：当你把你的业务问题分解成离散的部分时，寻找那些彼此高度相关的数据块。如果突然间，
     * 在你人机对话的过程中，你正在阅读或更新不你之前讨论过的根本不同的数据，你可能会有另一个候选服务。微服务
     * 应该完全拥有自己的数据。
     *
     * 下面把这些指导原则应用到现实世界的问题中去。之前介绍了现有的软件产品称为 EagleEye，它用于管理软件资产，
     * 如软件许可证和安全套接字层（SSL）证书。这些项目部署到整个组织的各个服务器中。
     *
     * EagleEye 是一个传统的单体 Web 应用程序，它被部署到客户数据中心的 J2EE 应用服务器。你的目标是将现有
     * 的单体应用程序分解成一组服务。
     *
     * PS：EagleEye 的数据库的数据模型是共享和高度集成的。
     *
     * 你要开始与 EagleEye 应用程序的所有用户面谈和与他们讨论他们如何使用 EagleEye。通过看 EagleEye 的用
     * 户如何与应用程序交互和应用程序的数据模型如何被分解，你可以分解 EagleEye 的问题域为以下候选微服务。
     *
     * PS：可以突出与业务用户对话期间出现的一些名词和动词。因为这是一个已有的应用程序，你可以查看应用程序并将
     * 主要的名词映射到物理数据模型中的表。现有的应用程序可能有数百个表，但是每个表通常会映射到一组逻辑实体。
     *
     * 这里得到了一个在与 EagleEye 客户交谈的基础上简化的数据模型。基于企业访谈和数据模型，候选微服务是：组
     * 织、许可证、合同、资产服务。
     *
     *
     *
     * 2、确定服务粒度
     *
     * 一旦你有了一个简化的数据模型，你可以开始定义过程，你将在应用中需要什么微服务。 根据数据模型，你基于以
     * 下要素可以看到潜在的四个微服务：
     * （1）资产
     * （2）许可证
     * （3）合同
     * （4）组织
     *
     * 这里的目标是将这些主要功能部分提取出来，并将它们提取成完全独立的单元，这些单元可以独立地构建和部署。但
     * 从数据模型中提取服务不仅仅是重新包装成单独的项目代码。它还涉及到梳理服务访问的实际数据库表，只允许每个
     * 单独的服务访问特定域中的表。即 通过应用程序代码和数据模型将 "块" 变成独立的组件。
     *
     * EagleEye 应用从单体应用中被分解成更小的个体服务，它们彼此独立部署。每个服务都拥有在其域内的所有数据。
     * 这并不意味着每个服务都有自己的数据库。它只是意味着叧有拥有该域的服务才能访问它内部的数据库表。
     *
     * 在将一个问题域分解为离散块之后，你常常发现自己在努力确定是否已经为服务实现了正确的粒度级别。这里不久将
     * 讨论，一个微服务太粗或太细会有一些明显的特征。
     *
     * 当你建立一个微服务架构，粒度的问题是重要的，但你可以用以下的概念来确定正确的解决方案：
     * （1）最好开始广泛的使用微服务和重构为更小的服务：很容易走极端，当你开始你的微服务旅程，做一切事情都使
     * 用微服务。但分解问题域为小服务，往往导致过早的复杂性，因为微服务仅仅变成细粒度的数据服务。
     * （2）首先关注你的服务如何与其它服务交互：这将有助于建立问题域的粗粒度接口。这使从太粗太细重构变得更容
     * 易。
     * （3）随着你对问题域的理解不断增长，服务职责将随时间而变化：通常，当新的应用功能被要求的时候，一个微服
     * 务增加职责。一开始，一个单一的微服务可能成长为多个服务，原始的微服务作为对这些新的服务业务流程层和封装
     * 来自应用程序其它部分的功能。
     *
     *
     * PS：有害微服务的气味
     *
     * 你如何知道你的微服务是否是合适的大小？如果一个微服务太粗，你可能会看到如下：
     * （1）服务太多的职责：服务中业务逻辑的一般流程是复杂的，并且似乎在执行一系列过于多样化的业务规则。
     * （2）服务通过大量的表管理数据：一个微服务是其管理数据的记录系统。如果你发现将数据持久保存到多个表，或
     * 者直接访问外部数据库的表，这表明服务太大了。微服务应该拥有不超过三至五个表。而且，你的服务可能有太多的
     * 职责。
     * （3）测试用例太多：随着时间的推移，服务的规模和职责也会增加。如果你有一个服务，它们开始于小数量小的测
     * 试用例，结束于成百上千的单元和集成测试用例，你可能需要重构。
     *
     * 那一个微服务粒度太细会怎么样？
     * （1）在问题域的一部分微服务像兔子一样繁殖：如果一切都变成一个微服务，构成业务逻辑的服务变得复杂和困难，
     * 因为要得到一块工作服务的数量的难度与日俱增。一个共同的气味，是你在应用程序中有很多的微服务和每一个服务
     * 只与一个单一的数据库表交互。
     * （2）微服务彼此严重依赖：你发现的问题域的一部分微服务保持相互之间来回调用来完成一个用户请求。
     * （3）微服务成为一个简单的 CRUD（创建、替换、更新、删除）服务集：微服务是一种业务逻辑表述，并不是你数
     * 据源的一个抽象层。如果微服务除了 CRUD 相关的逻辑，什么也不做，他们可能太细。
     *
     *
     * 微服务架构将用进化的思维过程发展，你知道你不会在第一次就得到正确的设计。这就是为什么从第一组服务开始，
     * 粗粒度比细粒度更好。不要对自己的设计过于武断也是很重要的。你需要做一个将数据聚合在一起服务，可能在该
     * 服务上会遇到物理约束，因为两个单独的服务太多了，或者在服务域之间没有明确的界限。
     *
     * 最后，与其浪费时间试图使设计完美，然后在你的努力面前没有成绩可言，倒不如采取一种务实的态度交付。
     *
     *
     *
     * 3、互相交谈：服务接口
     *
     * 架构投入的最后一部分，要谈一谈关于如何在你的应用程序定义微服务。使用微服务构建业务逻辑，服务接口应该是
     * 直观的，开发者通过学习应用中的一个或两个服务，将获得运行在应用中所有服务的规律。
     *
     * 在一般情况下，下列准则可作为服务接口的设计思想：
     * （1）拥抱 REST 哲学：服务的 REST 方法本质上是把 HTTP 作为服务的调用协议和使用标准 HTTP 动词（GET,
     * PUT, POST 和 DELETE）。围绕这些 HTTP 动词对你的基本行为建模。
     * （2）使用 URI 来传达意图：作为服务端点的 URI 应该描述你的问题域中的不同资源，并为你的问题域中的资源
     * 关系提供基本机制。
     * （3）为请求和响应使用 JSON：JavaScript Object Notation（换句话说，JSON）是一种非常轻量级的数据
     * 序列化协议，并且比 XML 更容易消费。
     * （4）使用 HTTP 状态代码来传递结果：HTTP 协议有大量的标准响应代码来表明服务的成功或失败。学习这些状
     * 态代码，最重要的是在所有的服务中始终如一地使用它们。
     *
     * 所有的基本原则都驱动着一件事，使你的服务接口易于理解和可消费。你希望开发人员坐下来查看服务接口并开始
     * 使用它们。如果一个微服务不易消费，开发人员将以自己的方式工作，颠覆架构的意图。
     */
    public static void main(String[] args) {

    }

}
