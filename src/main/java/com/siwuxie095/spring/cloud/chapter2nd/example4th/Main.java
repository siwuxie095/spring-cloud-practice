package com.siwuxie095.spring.cloud.chapter2nd.example4th;

/**
 * @author Jiajing Li
 * @date 2021-05-20 22:00:00
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 开发人员的故事：用 Spring Boot 和 Java 构建微服务
     *
     * 在构建微服务时，从概念到实现，需要视角的转换。具体来说，开发人员需要建立一个实现应用程序中每个微服务的
     * 基本模式。虽然每项服务都将是独一无二的，但这里希望确保使用的是一个移除样板代码的框架，并且微服务的每个
     * 部分都采用相同的布局。
     *
     * 在这里，将探讨开发人员从 EagleEye 域模型构建许可证微服务的优先事项。许可证服务将使用 Spring Boot
     * 编写。Spring Boot 是标准 Spring 库之上的一个抽象层，它允许开发人员快速构建基于 Groovy 和 Java
     * 的 Web 应用程序和微服务，比成熟的 Spring 应用程序能够节省大量的配置。
     *
     * 对于许可证服务示例，这里将使用 Java 作为核心编程语言并使用 Apache Maven 作为构建工具。
     *
     * 接下来，将要完成以下几项工作。
     * （1）构建微服务的基本框架并构建应用程序的 Maven 脚本。
     * （2）实现一个 Spring 引导类，它将启动用于微服务的 Spring 容器，并启动类的所有初始化工作。
     * （3）实现映射端点的 Spring Boot 控制器类，以公开服务的端点。
     *
     *
     *
     * 1、从骨架项目开始
     *
     * 首先，要为许可证服务创建一个骨架项目。你可以从 GitHub 存储库拉取源代码，也可以创建具有以下目录结构的
     * 许可证服务项目目录：
     * （1）licensing-service
     * （2）src/main/java/com/siwuxie095/spring/cloud/licenses
     * （3）controllers
     * （4）model
     * （5）services
     * （6）resources
     *
     * 一旦拉取或创建了这个目录结构，就可以开始为项目编写 Maven 脚本。这就是位于项目根目录下的 pom.xml 文
     * 件。如下代码展示了许可证服务的 Maven POM 文件。
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <project xmlns=http://maven.apache.org/POM/4.0.0
     *     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
     *     ➥  http://maven.apache.org/xsd/maven-4.0.0.xsd">
     *   <modelVersion>4.0.0</modelVersion>
     *
     *   <groupId>com.thoughtmechanix</groupId>
     *   <artifactId>licensing-service</artifactId>
     *   <version>0.0.1-SNAPSHOT</version>
     *   <packaging>jar</packaging>
     *
     *   <name>EagleEye Licensing Service</name>
     *   <description>Licensing Service</description>
     *
     *   <parent>
     *     <groupId>org.springframework.boot</groupId>
     *     <artifactId>spring-boot-starter-parent</artifactId>    ⇽---  告诉 Maven 包含 Spring Boot 起步工具包依赖项
     *     <version>1.4.4.RELEASE</version>
     *     <relativePath/>
     *   </parent>
     *   <dependencies>
     *     <dependency>
     *       <groupId>org.springframework.boot</groupId>
     *       <artifactId>spring-boot-starter-web</artifactId>    ⇽---  告诉 Maven 包含 Spring Boot Web 依赖项
     *     </dependency>
     *     <dependency>
     *       <groupId>org.springframework.boot</groupId>
     *       <artifactId>spring-boot-starter-actuator</artifactId>    ⇽---  告诉 Maven 包含 Spring Actuator 依赖项
     *     </dependency>
     * </dependencies>
     * <!--
     *      注意：某些构建属性和 Docker 构建插件已从此 pom 中的 pom.xml 中排除掉了（GitHub 存储库的
     *      源代码中并没有移除），因为它们与这里的讨论无关。
     *     -->
     *
     * <build>
     *   <plugins>
     *      <plugin>
     *        <groupId>org.springframework.boot</groupId>
     *        <artifactId>spring-boot-maven-plugin</artifactId>    ⇽---  告诉 Maven 包含 Spring 特定的 Maven 插件，
     *                                                                   用于构建和部署 Spring Boot 应用程序
     *      </plugin>
     *     </plugins>
     *   </build>
     * </project>
     *
     * 这里不会详细讨论整个脚本，但是在开始的时候要注意几个关键的地方。Spring Boot 被分解成许多个独立的项目。
     * 其理念是，如果不需要在应用程序中使用 Spring Boot 的各个部分，那么就不应该 "拉取整个世界"。这也使不
     * 同的 Spring Boot 项目能够独立地发布新版本的代码。为了简化开发人员的开发工作，Spring Boot 团队将相
     * 关的依赖项目收集到各种 "起步"（starter）工具包中。Maven POM 的第一部分告诉 Maven 需要拉取 Spring
     * Boot 框架的 1.4.4 版本。
     *
     * Maven 文件的第二部分和第三部分确定了要拉取 Spring Web 和 Spring Actuator 起步工具包。这两个项目
     * 几乎是所有基于 Spring Boot REST 服务的核心。你会发现，服务中构建功能越多，这些依赖项目的列表就会变
     * 得越长。
     *
     * 此外，Spring Source 还提供了 Maven 插件，可简化 Spring Boot 应用程序的构建和部署。第四部分告诉
     * Maven 构建脚本安装最新的 Spring Boot Maven 插件。此插件包含许多附加任务（如spring-boot:run ），
     * 可以简化 Maven 和 Spring Boot 之间的交互。
     *
     * 最后，你将看到一条注释，说明 Maven 文件的哪些部分已被删除。为了简化，这里没有在上述代码中包含 Spotify
     * Docker 插件。
     *
     *
     *
     * 2、引导 Spring Boot 应用程序：编写引导类
     *
     * 这里的目标是在 Spring Boot 中运行一个简单的微服务，然后重复这个步骤以提供功能。为此，需要在许可证服
     * 务微服务中创建以下两个类。
     * （1）一个 Spring 引导类，可被 Spring Boot 用于启动和初始化应用程序。
     * （2）一个 Spring 控制器类，用来公开可以被微服务调用的 HTTP 端点。
     *
     * Spring Boot 使用注解来简化设置和配置服务。在查看引导类时，这一点就变得显然易见。如下所示。
     *
     * // 告诉 Spring Boot 框架，这是项目的引导类
     * @SpringBootApplication
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         // 调用以启动整个 Spring Boot 服务
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 在这段代码中需要注意的第一件事是 @SpringBootApplication 的用法。Spring Boot 使用这个注解来告诉
     * Spring 容器，这个类是在 Spring 中使用的 bean 定义的源。在 Spring Boot 应用程序中，可以通过以下
     * 方法定义 Spring Bean。
     * （1）用 @Component 、@Service 或 @Repository 注解标签来标注一个 Java 类。
     * （2）用 @Configuration 注解标签来标注一个类，然后为每个想要构建的 Spring Bean 定义一个构造器方
     * 法并为方法添加上 @Bean 标签。
     *
     * 在幕后，@SpringBootApplication 注解将 Application 类标记为配置类，然后开始自动扫描 Java 类路径
     * 上所有的类以形成其他的 Spring Bean。
     *
     * 第二件需要注意的事是 Application 类的 main() 方法。在 main() 方法中，Spring Application.run
     * (Application.class, args) 调用启动了 Spring 容器，然后返回了一个 Spring ApplicationContext
     * 对象（这里没有使用 ApplicationContext 做任何事情，因此它没有在代码中展示）。
     *
     * 关于 @SpringBootApplication 注解及其对应的 Application 类，最容易记住的是，它是整个微服务的引导
     * 类。服务的核心初始化逻辑应该放在这个类中。
     *
     *
     *
     * 3、构建微服务的入口：Spring Boot 控制器
     *
     * 现在已经有了构建脚本，并实现了一个简单的 Spring Boot 引导类，接下来就可以开始编写第一个代码来做一些
     * 事情。这个代码就是控制器类。在 Spring Boot 应用程序中，控制器类公开了服务端点，并将数据从传入的 HTTP
     * 请求映射到将处理该请求的 Java 方法。
     *
     *
     * PS：遵循 REST
     *
     * 这里的所有微服务都遵循 REST 方法来构建。对 REST 的深入讨论超出了这里的范围，但对于这里，构建的所有服
     * 务都将具有以下特点。
     * （1）使用 HTTP 作为服务的调用协议：服务将通过 HTTP 端点公开，并使用 HTTP 协议传输进出服务的数据。
     * （2）将服务的行为映射到标准 HTTP 动词：REST 强调将服务的行为映射到 POST、GET、PUT 和 DELETE
     * 这样的 HTTP 动词上。这些动词映射到大多数服务中的 CRUD 功能。
     * （3）使用 JSON 作为进出服务的所有数据的序列化格式：对基于 REST 的微服务来说，这不是一个硬性原则，但
     * 是 JSON 已经成为通过微服务提交和返回数据的通用语言。当然也可以使用 XML，但是许多基于 REST 的应用程
     * 序大量使用 JavaScript 和 JSON。JSON 是基于 JavaScript 的 Web 前端和服务对数据进行序列化和反序列
     * 化的原生格式。
     * （4）使用 HTTP 状态码来传达服务调用的状态：HTTP 协议开发了一组丰富的状态码，以指示服务的成功或失败。
     * 基于 REST 的服务利用这些 HTTP 状态码和其他基于 Web 的基础设施，如反向代理和缓存，可以相对容易地与
     * 微服务集成。
     *
     * HTTP 是 Web 的语言，使用 HTTP 作为构建服务的哲学框架是构建云服务的关键。
     *
     *
     * 第一个控制器类是 LicenseSerriceController，这个类将公开 4 个 HTTP 端点，这些端点将映射到 POST、
     * GET、PUT 和 DELETE 动词。
     *
     * 下面看一下控制器类，看看 Spring Boot 如何提供一组注解，以保证花最少的努力公开服务端点，使开发人员能
     * 够集中精力构建服务的业务逻辑。这里将从没有任何类方法的基本控制器类定义开始。如下代码展示了为许可证服务
     * 构建的控制器类。
     *
     * // @Restcontroller 告诉 Spring Boot 这是一个基于 REST 的服务，它将自动序列化/反序列化服务请求/响应到 JSON
     * @RestController
     * // 在这个类中使用 /v1/organizations{organizationId}/licenses 的前缀，公开所有 HTTP 端点
     * @RequestMapping(value="/v1/organizations/{organizationId}/licenses")
     * public class LicenseServiceController {
     *   // 为了简洁，省略了该类的内容
     * }
     *
     * 这里通过查看 @RestController 注解来开始探索。@RestController 是一个类级 Java 注解，它告诉 Spring
     * 容器这个 Java 类将用于基于 REST 的服务。此注解自动处理以 JSON 或 XML 方式传递到服务中的数据的序列化
     * （在默认情况下，@RestController 类将返回的数据序列化为 JSON）。与传统的 Spring @Controller 注解
     * 不同，@RestController 注解并不需要开发者从控制器类返回 ResponseBody 类。这一切都由 @RestController
     * 注解进行处理，它包含了 @ResponseBody 注解。
     *
     *
     * PS：为什么是 JSON
     *
     * 在基于 HTTP 的微服务之间发送数据时，其实有多种可选的协议。由于以下几个原因，JSON 已经成为事实上的标准。
     * （1）首先，与其他协议（如基于 XML 的 SOAP（Simple Object Access Protocol，简单对象访问协议））相
     * 比，它非常轻量级，可以在没有太多文本开销的情况下传递数据。
     * （2）其次，JSON 易于人们阅读和消费。这在选择序列化协议时往往被低估。当出现问题时，开发人员可以快速查看
     * 一大堆 JSON，直观地处理其中的内容。JSON 协议的简单性让这件事非常容易做到。
     * （3）最后，JSON 是 JavaScript 使用的默认序列化协议。由于 JavaScript 作为编程语言的急剧增长以及依赖
     * 于 JavaScript 的单页互联网应用程序（Single Page Internet Application，SPIA）的同样快速增长，JSON
     * 已经天然适用于构建基于 REST 的应用程序，因为前端 Web 客户端用它来调用服务。
     *
     * 其他机制和协议能够比 JSON 更有效地在服务之间进行通信。Apache Thrift 框架允许构建使用二进制协议相互通
     * 信的多语言服务。Apache Avro 协议是一种数据序列化协议，可在客户端和服务器调用之间将数据转换为二进制格式。
     *
     * 如果你需要最小化通过线路发送的数据的大小，建议查看这些协议。但是根据经验，在微服务中使用直接的 JSON 就
     * 可以有效地工作，并且不会在服务消费者和服务客户端间插入另一层通信来进行调试。
     *
     *
     * 这段代码中展示的第二个注解是 @RequestMapping。可以使用 @RequestMapping 作为类级注解和方法级注解。
     * @RequestMapping 注解用于告诉 Spring 容器该服务将要公开的 HTTP 端点。使用类级的 @RequestMapping
     * 注解时，将为该控制器公开的所有其他端点建立 URL 的根。
     *
     * 在这里，@RequestMapping(value="/v1/organizations/{organizationId}/licenses") 使用 value
     * 属性为控制器类中公开的所有端点建立 URL 的根。在此控制器中公开的所有服务端点将以 /v1/organizations
     * /{organizationId}/licenses 作为其端点的根。{organizationId} 是一个占位符，表明如何使用在每个
     * 调用中传递的 organizationId 来参数化 URL。在 URL 中使用 organizationId 可以区分使用服务的不同
     * 客户。
     *
     * 现在将添加控制器的第一个方法。这一方法将实现 REST 调用中的 GET 动词，并返回单个 License 类实例，
     * 如下所示（为了便于讨论，将实例化一个名为 License 的 Java 类）。
     *
     *     // 使用值创建一个 GET 端点 v1/organizations/{organizationId}/licenses/{licenseId}
     *     @RequestMapping(value="/{licenseId}",method = RequestMethod.GET)
     *     // 从 URL 映射两个参数（organizationId 和 licenseId）到方法参数
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
     * 这段代码中完成的第一件事是，使用方法级的 @RequestMapping 注解来标记 getLicenses() 方法，将两个
     * 参数传递给注解，即 value 和 method。通过方法级的 @RequestMapping 注解，再结合类顶部指定的根级
     * 注解，将所有传入该控制器的 HTTP 请求与端点 /v1/organizations/{organizationId}/licences
     * /{licensedId} 匹配起来。该注解的第二个参数 method 指定该方法将匹配的 HTTP 动词。在这里，以
     * RequestMethod.GET 枚举的形式匹配 GET 方法。
     *
     * 这段代码中需要注意的第二件事是 getLicenses() 方法的参数体中使用了 @PathVariable 注解。这个注解
     * 用于将在传入的 URL 中传递的参数值（由 {parameterName} 语法表示）映射为方法的参数。在这里，将两个
     * 参数 organizationId 和 licenseId 映射到方法中的两个参数级变量：
     *
     * @PathVariable("organizationId") String organizationId,
     * @PathVariable("licenseId") String licenseId)
     *
     *
     * PS：端点命名问题
     *
     * 在编写微服务之前，要确保（以及组织中的其他可能的团队）为服务公开的端点建立标准。应该使用微服务的 URL
     * （Uniform Resource Locator，统一资源定位器）来明确传达服务的意图、服务管理的资源以及服务内管理的
     * 资源之间存在的关系。以下指导方针有助于命名服务端点。
     * （1）使用明确的 URL 名称来确立服务所代表的资源：使用规范的格式定义 URL 将有助于 API 更直观，更易于
     * 使用。要在命名约定中保持一致。
     * （2）使用 URL 来确立资源之间的关系：通常，在微服务中会存在一种父子关系，在这些资源中，子项不会存在于
     * 父项的上下文之外（因此可能没有针对该子项的单独的微服务）。使用 URL 来表达这些关系。但是，如果发现 URL
     * 嵌套过长，可能意味着微服务尝试做的事情太多了。
     * （3）尽早建立 URL 的版本控制方案：URL 及其对应的端点代表了服务的所有者和服务的消费者之间的契约。一
     * 种常见的模式是使用版本号作为前缀添加到所有端点上。尽早建立版本控制方案，并坚持下去。在几个消费者使用
     * 它们之后，对 URL 进行版本更新是非常困难的。
     *
     *
     * 现在，可以将刚刚创建的东西称为服务。在命令行窗口中，转到代码的项目目录，然后执行以下 Maven 命令：
     *
     * mvn spring-boot:run
     *
     * 一旦按下回车键，应该会看到 Spring Boot 启动一个嵌入式 Tomcat 服务器，并开始监听 8080 端口。
     *
     * 服务启动后就可以直接访问公开的端点了。因为公开的第一个方法是 GET 调用，可以使用多种方法来调用这一服务。
     * 这里的首选方法是使用基于 Chrome 的工具，如 POSTMAN 或 CURL 来调用该服务。
     *
     * 可以试着在 http://localhost:8080/v1/organizations/e254f8c-c442-4ebe-a82a-e2fc1d1ff78a
     * /licenses/f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a 端点上完成的一个 GET 请求。
     *
     * PS：当调用 GET 端点时，将返回包含许可证数据当 JSON 净荷。
     *
     * 现在已经有了一个服务的运行骨架。但从开发的角度来看，这服务还不完整。良好的微服务设计不可避免地将服务分
     * 成定义明确的业务逻辑和数据访问层。后续将继续对此服务进行迭代，并进一步深入了解如何构建该服务。
     */
    public static void main(String[] args) {

    }

}
