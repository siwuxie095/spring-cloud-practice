package com.siwuxie095.spring.cloud.chapter1st.example2nd;

/**
 * @author Jiajing Li
 * @date 2021-05-07 08:14:05
 */
public class Main {

    /**
     * 什么是微服务
     *
     * 在微服务的概念逐步形成之前，绝大部分基于 Web 的应用都是使用单体架构的风格来进行构建的。在单体
     * 架构中，应用程序作为单个可部署的软件制品交付，所有的 UI（用户接口）、业务、数据库访问逻辑都被
     * 打包在一个应用程序制品中并且部署在一个应用程序服务器上。
     *
     * 虽然应用程序可能是作为单个工作单元部署的，但大多数情况下，会有多个开发团队开发这个应用程序。每
     * 个开发团队负责应用程序的不同部分，并且他们经常用自己的功能部件来服务特定的客户。例如，笔者在一
     * 家大型的金融服务公司工作时，公司有一个内部定制的客户关系管理（CRM）应用，它涉及多个团队之间的
     * 合作，包括 UI 团队、客户主团队、数据仓库团队以及共同基金团队。
     *
     * 如下阐示了这个应用程序的基本架构。
     * （1）每个团队都有自己的职责领域，有自己的要求和交付需求。
     * （2）他们的所有工作都被同步到单一的代码库中。
     * （3）通过持续集成管道，构建典型的基于 Spring 的 Web 应用程序，其中，Java 应用服务器
     * 可能使用了：JBoss、Websphere、WebLogic、Tomcat。
     * （4）整个应用程序都了解应用程序中使用的所有数据源，还具有对数据源的访问权。
     *
     * PS：单体应用程序强迫开发团队人工同步他们的交付，因为他们的代码需要被作为一个整体单元进行构建、
     * 测试和部署。
     *
     * 这里的问题在于，随着单体的 CRM 应用的规模和复杂度的增长，在该应用程序上进行开发的各个团队的
     * 沟通与合作成本没有减少。每当各个团队需要修改代码时，整个应用程序都需要重新构建、重新测试和重
     * 新部署。
     *
     * 微服务的概念最初是在 2014 年前后悄悄蔓延到软件开发社区的意识中，它是对在技术上和组织上扩大大
     * 型单体应用程序所面临的诸多挑战的直接回应。记住，微服务是一个小的、松耦合的分布式服务。微服务
     * 允许将一个大型的应用分解为具有严格职责定义的便于管理的组件。微服务通过将大型代码分解为小型的
     * 精确定义的部分，帮助解决大型代码库中传统的复杂问题。在思考微服务时，一个需要信奉的重要概念就
     * 是：分解和分离应用程序的功能，使它们完全彼此独立。
     *
     * 如果以上面的 CRM 应用程序为例，将其分解为微服务，那么使用微服务架构，CRM 应用将会被分解成一
     * 系列完全彼此独立的微服务，让每个开发团队都能够按各自的步伐前进。
     *
     * 而且每个功能团队完全拥有自己的服务代码和服务基础设施。他们可以彼此独立地去构建、部署和测试，
     * 因为他们的代码、源码控制仓库和基础设施（应用服务器和数据库）现在是完全独立于应用的其他部分
     * 的。
     *
     * 微服务架构具有以下特征。
     * （1）应用程序逻辑分解为具有明确定义了职责范围的细粒度组件，这些组件互相协调提供解决方案。
     * （2）每个组件都有一个小的职责领域，并且完全独立部署。微服务应该对业务领域的单个部分负责。
     * 此外，一个微服务应该可以跨多个应用程序复用。
     * （3）微服务通信基于一些基本的原则（注意，这里说的是原则而不是标准），并采用 HTTP 和
     * JSON（JavaScript Object Notation）这样的轻量级通信协议，在服务消费者和服务提供者
     * 之间进行数据交换。
     * （4）服务的底层采用什么技术实现并没有什么影响，因为应用程序始终使用技术中立的协议（JSON
     * 是最常见的）进行通信。这意味着构建在微服务之上的应用程序能够使用多种编程语言和技术进行构建。
     * （5）微服务利用其小、独立和分布式的性质，使组织拥有明确责任领域的小型开发团队。这些团队
     * 可能为同一个目标工作，如交付一个应用程序，但是每个团队只负责他们在做的服务。
     *
     * 开玩笑的说，微服务是构建云应用程序的 "诱人上瘾的毒药"。你开始构建微服务是因为它们能够为你的
     * 开发团队提供高度的灵活性和自治权，但你和你的团队很快就会发现，微服务的小而独立的特性使它们
     * 可以轻松地部署到云上。一旦服务运行在云中，它们小型化的特点使启动大量相同服务的实例变得很容易，
     * 应用程序瞬间变得更具可伸缩性，并且显而易见也会更有弹性。
     */
    public static void main(String[] args) {

    }

}
