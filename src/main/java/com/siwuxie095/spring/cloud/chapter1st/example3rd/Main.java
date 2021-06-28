package com.siwuxie095.spring.cloud.chapter1st.example3rd;

/**
 * @author Jiajing Li
 * @date 2021-05-07 21:20:18
 */
public class Main {

    /**
     * 什么是 Spring，为什么它与微服务有关
     *
     * 在基于 Java 的应用程序构建中，Spring 已经成为了事实上的标准开发框架。Spring 的核心是建立在依赖注入
     * 的概念上的。在普通的 Java 应用程序中，应用程序被分解成为类，其中每个类与应用程序中的其他类经常有明显
     * 的联系，这些联系是在代码中直接调用类的构造器，一旦代码被编译，这些联系点将无法修改。
     *
     * 这在大型项目中是有问题的，因为这些外部联系是脆弱的，并且进行修改可能会对其他下游代码造成多重影响。依赖
     * 注入框架（如 Spring），允许用户通过约定（以及注解）将应用程序对象之间的关系外部化，而不是在对象内部彼
     * 此硬编码实例化代码，以便更轻松地管理大型 Java 项目。Spring 在应用程序的不同的 Java 类之间充当一个中
     * 间人，管理着它们的依赖关系。Spring 本质上就是让用户像玩乐高积木一样将自己的代码组装在一起。
     *
     * Spring 能够快速引入特性的特点推动了它的实际应用，使用 J2EE 技术栈开发应用的企业级 Java 开发人员迅速
     * 采用它作为一个轻量级的替代方案。J2EE 栈虽然功能强大，但许多人认为它过于庞大，甚至许多特性从未被应用程
     * 序开发团队使用过。此外，J2EE 应用程序强制用户使用成熟的（和沉重的）Java 应用程序服务器来部署自己的应
     * 用程序。
     *
     * Spring 框架的迷人之处在于它能够与时俱进并进行自我改造 —— 它已经向开发社区证明了这一点。Spring 团队
     * 发现，许多开发团队正在从将应用程序的展现、业务和数据访问逻辑打包在一起并部署为单个制品的单体应用程序
     * 模型中迁移，正转向高度分布式的模型，服务能够被构建成可以轻松部署到云端的小型分布式服务。为了响应这种
     * 转变，Spring 开发团队启动了两个项目，即 Spring Boot 和 Spring Cloud。
     *
     * Spring Boot 是对 Spring 框架理念重新思考的结果。虽然 Spring Boot 包含了 Spring 的核心特性，但
     * 它剥离了 Spring 中的许多 "企业" 特性，而提供了一个基于 Java 的、面向 REST 的微服务框架。只需一些
     * 简单的注解，Java 开发者就能够快速构建一个可打包和部署的 REST 微服务，这个微服务并不需要外部的应用
     * 容器。
     *
     * 注意：后续会更详细地介绍 REST，REST 背后最为核心的概念是，服务应该使用 HTTP 动词（GET、POST、PUT
     * 和 DELETE）来代表服务中的核心操作，并且使用轻量级的面向 Web 的数据序列化协议（如 JSON）来从服务请求
     * 数据和从服务接收数据。可参考 Roy Fielding 博士关于构建基于 REST 应用程序的博士论文，它仍然是目前能
     * 找到的关于 REST 的最好说明（https://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm）。
     *
     * 在构建基于云的应用时，微服务已经成为更常见的架构模式之一，因此 Spring 社区为开发者提供了 Spring
     * Cloud。Spring Cloud 框架使实施和部署微服务到私有云或公有云变得更加简单。Spring Cloud 在一个
     * 公共框架之下封装了多个流行的云管理微服务框架，并且让这些技术的使用和部署像为代码添加注解一样简便。
     * 后续将介绍 Spring Cloud 中的不同组件。
     */
    public static void main(String[] args) {

    }

}
