package com.siwuxie095.spring.cloud.chapter2nd.example4th;

/**
 * @author Jiajing Li
 * @date 2021-05-20 22:00:00
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 开发：使用 Spring Boot 和 Java 构建一个微服务
     *
     * 构建微服务时，从概念空间转变为实现空间需要换个角度思考。具体来说，作为一个开发者，你需要建立一个
     * 基本的模式，每个应用程序中的微服务将被如何实现。虽然每个服务将是独一无二的，你要确保你使用一个删
     * 除样板代码的框架，微服务的每一个部分都以一致的方法被规划。
     *
     * 在这里，将探讨从你的 EagleEye 域模型创建授权微服务，是开发者优先考虑的事。你的许可服务将使用
     * Spring Boot 编写。Spring Boot 是标准 Spring 库上的抽象层，它允许开发者快速构建基于 Groovy
     * 和 Java 的 Web 应用程序，微服务比成熟的 Spring 应用程序使用更少的配置。
     *
     * 以许可服务为例，你可以使用 Java 作为核心编程语言和 Apache Maven 作为构建工具。
     *
     * 在接下来的几个部分中，你将要：
     * （1）创建微服务的基本骨架和构建该应用程序的 Maven 脚本；
     * （2）实现一个 Spring 引导类，它将为微服务启动 Spring 容器和开始剔除所有为类工作的初始化代码；
     * （3）实现一个 Spring Boot 控制器类，用于映射端点以暴露服务的端点；
     *
     *
     *
     * 1、从骨架项目快速入门
     *
     * 首先，你将为授权创建一个框架项目。你可以使用以下的目录结构创建一个许可服务项目目录：
     * （1）licensing-service
     * （2）src/main/java/com/siwuxie095/spring/cloud/licenses
     * （3）controllers
     * （4）model
     * （5）services
     * （6）resources
     *
     * 一旦创建好目录结构，开始为项目写你的 Maven 脚本。pom.xml 文件将位于项目目录的根目录。
     *
     * 这里不会详细地讨论整个 Maven 脚本（具体可见代码），但是在开始时请注意几个关键的地方。Spring
     * Boot 被分解成许多单独的项目。其原理是，如果你不打算在应用程序中使用不同的 Spring Boot， 就
     * 不必 "拉下整个世界"。这也允许各种 Spring Boot 项目独立地发布新版本的代码。为了帮助简化开发
     * 人员的工作，Spring Boot 团队已经将相关的项目集成到各种 "starter" 工具包中。
     *
     * 这里拉下的是版本为 1.4.4 的 Spring Boot 框架。同时，你确定你还将拉下 Spring Web 和 Spring
     * Actuator 的 starter 工具包。这两部分几乎是所有基于 Spring Boot REST 服务的核心。这两个项目
     * 是几乎所有基于 Spring 的基于 REST 的服务的核心。你会发现，当你在服务中创建更多的功能时，这些依
     * 赖项目的列表就变得更长了。
     *
     * 另外，Spring 源代码提供了 Maven 插件，简化构建和部署 Spring Boot 应用程序。所以这里告诉你的
     * Maven 构建脚本安装最新的 Spring Boot Maven 插件，这个插件包含了一些附加的任务，简化了 Maven
     * 和 Spring Boot 之间的操作（如 spring-boot:run）。
     *
     * PS：这里还包括了用于构建和部署应用程序作为 Docker 容器的 Docker 文件，以及 Maven 脚本中的
     * Docker 插件。
     *
     *
     *
     * 2、编写引导类,启动你的 Spring Boot 应用
     *
     * 你癿目标是获得一个简单的微服务，它在 Spring Boot 运行良好，并且在其上可以迭代来发布功能。为此，
     * 你需要在你的授权微服务项目中创建两个类：
     * （1）一个 Spring 引导类，将用于 Spring Boot 启动并初始化应用程序；
     * （2）一个 Spring 控制器类，将暴露可以在微服务调用的 HTTP 端点；
     *
     * 你很快就会看到，Spring Boot 使用注解来简化服务的设置和配置。这个引导类位于 src/main/java/com
     * /siwuxie095/spring/cloud/licenses/Application.java 文件。
     *
     * 在 Application 类中，第一件事是注意其中使用的 @SpringBootApplication 注解。Spring Boot
     * 使用这个注解告诉 Spring 容器，这个类是在 Spring 中使用的 Bean 定义的源头。在 Spring Boot
     * 应用程序中，您可以通过以下方式定义 Spring Bean：
     * （1）在 Java 中使用 @Component、@Service 或 @Repository 注解。
     * （2）用 @Configuration 注解一个类，然后为每一个你想用一个 @Bean 注解创建的 Spring Bean 定义
     * 一个构造函数方法。
     *
     * 正如所论述的，@SpringBootApplication 注解标记应用程序的类作为一个配置类，然后开始自动扫描其它
     * Spring Bean 在 Java 类路径的所有类。
     *
     * 在 Application 类中，第一件事是要注意应用程序类的 main() 方法。在 main() 方法，
     * SpringApplication.run(Application.class, args)，调用启动 Spring 容器并返回
     * 一个 Spring ApplicationContext 对象。
     *
     * 最简单的事情是记住 @SpringBootApplication 注解和相应的应用程序类，它是整个微服务的引导类。服
     * 务的核心初始化逻辑应该放在这个类中。
     *
     *
     *
     * 3、创建微服务的访问入口：Spring Boot 控制器
     *
     * 现在你已经获得了构建脚本，并实现了一个简单的 Spring Boot 引导类，你可以开始编写你的第一个代码，
     * 它将做一些事情。此代码将是控制器类。在一个 Spring Boot 应用， 一个控制器类暴露服务的端点，从
     * 一个传入的 HTTP 请求映射到一个将处理请求数据的 Java 方法。
     *
     *
     * PS：尝试一下 REST
     *
     * 这里所有的微服务采用 REST 方法来构建你的微服务。对 REST 的深入讨论超出了这里的范围，但为了能够
     * 满足你的要求，你所构建的所有服务都具有以下特性：
     * （1）使用 HTTP 作为服务的调用协议：该服务将通过 HTTP 端点暴露，并将使用 HTTP 协议将数据在服务
     * 间来回传送。
     * （2）将服务的行为映射到标准 HTTP 动词：REST 强调服务将其行为映射到 HTTP 动词（POST、GET、PUT
     * 和 DELETE）中。这些动词在大多数服务中映射到增删改查功能。
     * （3）将 JSON 作为服务间传递的所有数据的序列化格式：这不是一个基于 REST 的微服务癿硬性原则，但
     * JSON 已经成为数据序列化的通用语，数据将通过微服务被提交和返回。可以使用 XML，但许多基于 REST
     * 的应用程序大量使用 JavaScript 和 JSON（JavaScript Object Notation）。JSON 是序列化和反序
     * 列化基于 JavaScript 的 Web 前端和服务消费的数据的原生格式。
     * （4）使用 HTTP 状态代码来传递服务调用的状态：HTTP 协议开发了一组丰富的状态代码来表明服务的成败。
     * 基于 REST 的服务利用这些 HTTP 状态码和其他基于网络的基础设施，如反向代理服务器和缓存，可以相对
     * 容易地与你的微服务集成。
     *
     * HTTP 是 Web 的语言，使用 HTTP 作为构建服务的哲学框架是构建云服务的关键。
     *
     * 你的第一个控制器类位于 src/main/java/com/siwuxie095/spring/cloud/licenses/controllers
     * /LicenseServiceController.java。该类将暴露四个 HTTP 端点，这些端点将映射到动词 POST、GET、
     * PUT 和 DELETE。
     *
     * 下面浏觅一下控制器类，看看 Spring Boot 是如何提供一组注解，这些注解可以不断努力将服务端点暴露
     * 到最低限度，并允许你集中精力构建服务的业务逻辑。这里将从基本控制器类定义开始，而不使用任何类方法。
     *
     * @RestController
     * @RequestMapping(value="v1/organizations/{organizationId}/licenses")
     * public class LicenseServiceController {
     *
     * }
     *
     * 这里将从 @RestController 注解开始探索。@RestController 是一个类级的 Java 注解，它告诉
     * Spring 容器，这个 Java 类将被用于一个基于 REST 的服务。这个注解自动处理服务数据的序列化为
     * JSON 或 XML（默认情况下，@RestController 类将序列化返回的数据为 JSON）。不同于传统的
     * Spring @Controller 注解，@RestController 注解不需要你作为开发者从你的控制器类返回一个
     * ResponseBody 类。@RestController 注解包括 @ResponseBody 注解，它会处理所有的序列化。
     *
     *
     * PS：为什么微服务使用 JSON？
     *
     * 多协议可以用来发送基于 HTTP 的微服务来回之间的数据。由于几个原因，JSON 已经成为事实上的标准。
     *
     * 首先，与其他协议（如基于 XML 的 SOAP【简单对象访问协议】）相比，它非常轻量级，你可以在没有大量
     * 文本开销的情况下传送数据。
     *
     * 其次，它很容易被人阅读和消费。这是一个选择序列化协议被低估的优势。当出现问题时，开发人员查看
     * JSON 块并快速、直观地处理其中的内容是至关重要的。协议的简单性使这项工作非常简单。
     *
     * 最后，JSON 是 JavaScript 中使用的默认序列化协议。由于 JavaScript 的关注度急剧上升，作为
     * 一种编程语言和单页面的互联网应用的关注度同样急剧上升（SPIA），它们在很大程度上依赖于
     * JavaScript，JSON 已经成为构建基于 REST 应用的一种天然契合，因为前端 Web 客户端使用它调用
     * 服务。
     *
     * 其他机制和协议比 JSON 更有效地用于服务间通信。Apache 的 Thrift 框架允许你使用二进制协议构
     * 建能够互相通信的多语言服务。Apache 的 Avro 协议是一个数据序列化协议，它将客户端和服务器之
     * 间的来回传递的数据转换成二进制格式。
     *
     * （1）Thrift：https://thrift.apache.org/
     * （2）Avro：https://avro.apache.org/
     *
     * 如果你需要最小化通过电缆发送的数据的大小，这里建议你看看这些协议。但在微服务中直接使用 JSON
     * 能更有效的工作，不会干预另一层服务消费者和服务客户端之间的通信调试。
     *
     *
     * 这里的第二个注解是 @RequestMapping。你可以使用 @RequestMapping 作为类级或者方法级的注解。
     * @RequestMapping 注解用来告诉 Spring 容器服务的 HTTP 端点将向外界暴露。当你使用类级的
     * @RequestMapping 注解，你将建立通过控制器暴露的所有其它端点的根 URL。
     *
     * @RequestMapping(value="v1/organizations/{organizationId}/licenses")
     *
     * 这里使用 value 属性创建控制器类中暴露的所有端点的根 URL。在这个控制器中暴露的所有服务端点都
     * 将以 /v1/organizations/{organizationId}/licenses 开始，作为其端点的根。
     * {organizationId} 是一个占位符，表示你期待的 URL 在每次调用传递一个 organizationId 参数。
     * URL 中 organizationId 的使用，允许你在不同客户之间区分出谁会使用你的服务。
     *
     * 现在，你将向控制器添加第一个方法。此方法将实现 REST 调用中使用的 GET 谓词并返回单个许可证类
     * 实例，如下所示（讨论的目的是实例化一个称为 License 的 Java 类）。
     *
     *     @RequestMapping(value="/{licenseId}",method = RequestMethod.GET)
     *     public License getLicenses(@PathVariable("organizationId") String organizationId,
     *                                @PathVariable("licenseId") String licenseId) {
     *         return new License()
     *                 .withId(licenseId)
     *                 .withOrganizationId(organizationId)
     *                 .withProductName("Teleco")
     *                 .withLicenseType("Seat")
     *                 .withOrganizationId("TestOrg");
     *     }
     *
     * 这里做的第一件事是：使用方法级的 @RequestMapping 来注解 getlicenses() 方法，并有两个参数：
     * value 和 method。注解的第一个参数 value，你将在顶级类中创建指定的根级注解来匹配所有传入的
     * HTTP 请求到端点为 /v1/organizations/{organizationId}/licences/{licensedId} 的控制器。
     * 注解的第二个参数 method，指定该方法将匹配的 HTTP 谓词。这里通过 RequestMethod.GET 枚举匹
     * 配到 GET 方法。
     *
     * 这里作的第二件事是：在 getLicenses() 方法体中使用 @PathVariable 注解参数。@PathVariable
     * 注解被用于映射传入 URL 的参数值（如用 {parameterName} 语法）到你的方法的参数。这里将从 URL
     * 映射两个参数，organizationId 和 licenseId，到方法里两个参数级变量：
     * （1）@PathVariable("organizationId") String organizationId
     * （2）@PathVariable("licenseId") String licenseId
     *
     *
     * PS：端点的名称问题
     *
     * 在你沿着编写微服务的道路走得太远之前，需要确保你（在你的组织潜在的其他团队）为通过你的服务暴露的
     * 端点建立标准。微服务的 URL（统一资源定位器）应该能够清楚的表达服务的目的，服务资源管理和在服务之
     * 间存在的资源管理关系。下面的准则对于命名服务端点很有用：
     * （1）使用明确的 URL 名称建立服务所代表的资源：定义 URL 的规范格式将使你的 API 更直观，更容易
     * 使用。在命名约定中保持一致。
     * （2）使用 URL 建立资源之间的关系：通常在你的微服务资源之间有父子关系，哪里的孩子不会在父上下文
     * 以外存在（因此你不可能有一个子微服务）。使用 URL 来表示这些关系。但如果你发现你的 URL 往往是太
     * 长和嵌套，那么你的微服务可能试图做太多的事情。
     * （3）为 URL 建立早期版本控制方案：URL 及其相应端点表示服务所有者和服务消费者之间的契约。一个
     * 常见的模式是在所有端点之前使用一个版本号。及早建立你的版本控制计划并坚持下去。在已经有好几个用
     * 户使用它们之后，为 URL 升级版本是非常困难的。
     *
     * 此时，你可以将其作为一个服务进行调用。从一个命令行窗口，执行以下的 Maven 命令：
     *
     * mvn spring-boot:run
     *
     * 当你键入回车键时，你应该看到 Spring Boot 启动一个嵌入式 Tomcat 服务器，并开始监听端口 8080。
     *
     * 一旦服务启动，就可以直接命中暴露的端点。因为第一个方法以 GET 调用暴露，所以可以使用许多种方法来
     * 调用服务。这里的首选方法是使用像 POSTMAN 这样基于 Chrome 的工具或 CURL 来调用服务。
     *
     * 如下是一个 GET 方式的端点：
     *
     * http://localhost:8080/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a
     * /licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a
     *
     * 当 GET 端点被调用时，返回包含许可数据的 JSON 有效载荷。
     *
     * 此时，你有一个运行的服务框架。但是从开发的角度来看，这个服务并不完整。一个好的微服务设计并不回避
     * 将服务分离为定义明确的业务逻辑和数据访问层。后续将继续迭代这个服务并深入研究如何构造它。
     */
    public static void main(String[] args) {

    }

}
