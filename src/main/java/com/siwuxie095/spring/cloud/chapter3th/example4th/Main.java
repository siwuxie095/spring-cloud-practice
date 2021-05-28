package com.siwuxie095.spring.cloud.chapter3th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-05-28 20:49:30
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 将 Spring Cloud 配置与 Spring Boot 客户端集成
     *
     * 之前已经为许可服务建立了一个简单的骨架，只返回一个硬编码的 Java 对象，这个对象代表从数据库获取
     * 的一条许可记录。在下面的例子中，你将增建许可服务并与存储你的许可数据的一个 Postgres 数据库进行
     * 交互。
     *
     * 你将使用 Spring Data 与数据库通信，并将从许可表中获取的数据映射为一个存储数据的 POJO 对象。
     * 你的数据库连接和一个简单属性将从 Spring Cloud 配置服务器读取。
     *
     * 当许可服务第一次启动时，你将通过命令行传递两条信息：Spring 概要文件和许可服务与 Spring Cloud
     * 配置服务进行通信应该使用的端点。Spring 概要值映射为 Spring 服务检索的环境属性。当许可服务第一
     * 次启动时，它将通过一个通过 Spring 概要文件传入和构建的端点与 Spring Cloud 配置服务联系。
     * Spring Cloud 配置服务将使用配置好的后端配置存储库（文件系统、Git、Consul、Eureka）检索 URI
     * 中传递的 Spring 概要文件值的特定配置信息。然后将适当的属性值传递回许可服务。然后，Spring Boot
     * 框架将这些值注入应用程序的适当部分。
     *
     *
     *
     * 1、配置 Spring Cloud Config Server 的依赖
     *
     * 下面将焦点从配置服务器转为许可服务。你需要做的第一件事是在你的许可服务 Maven 文件中添加几项。需
     * 要添加的条目如下面的列表所示。
     *
     *         <dependency>
     *             <groupId>org.springframework.boot</groupId>
     *             <artifactId>spring-boot-starter-data-jpa</artifactId>
     *         </dependency>
     *
     *         <dependency>
     *             <groupId>postgresql</groupId>
     *             <artifactId>postgresql</artifactId>
     *             <version>9.1-901.jdbc4</version>
     *         </dependency>
     *
     *         <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-config-client</artifactId>
     *         </dependency>
     *
     * 第一和第二个依赖 spring-boot-starter-data-jpa 和 PostgreSQL 引入 Spring Data Java 持久
     * 化 API（JPA）和 Postgres 的 JDBC 驱动程序。最后一个依赖项，spring-cloud-config-client，
     * 它包含与 Spring Cloud 配置服务器交互所需的所有类。
     *
     *
     *
     * 2、Spring Cloud Config 使用配置
     *
     * 在 Maven 的依赖已被定义，你需要告诉许可服务联系 Spring Cloud 配置服务器的地址。在一个使用
     * Spring Cloud Config 的 Spring Boot 服务中，配置信息可以设置在其中一个配置文件：
     * bootstrap.yml 和 application.yml。
     *
     * 在任何其他配置信息使用之前，bootstrap.yml 文件读取应用程序的属性。总的来说，bootstrap.yml
     * 文件包含服务的应用程序的名称，应用程序概要文件，和连接到 Spring Cloud 配置服务器的 URI。任
     * 何你想保留在本地的服务配置信息（而不是存储在 Spring Cloud Config）可以设置在本地服务的
     * application.yml 文件。通常，你存储在 application.yml 文件的配置数据，是你可能想即使
     * Spring Cloud 配置服务不可用也有一个可用的服务。bootstrap.yml 和 application.yml 文件都
     * 存储在一个项目 src/main/resources 目录。
     *
     * 为了使许可服务与 Spring Cloud 配置服务通信，你需要添加一个 bootstrap.yml 文件和设置三个
     * 属性：spring.application.name、spring.profiles.active 和 spring.cloud.config.uri。
     * 如下所示：
     *
     * spring:
     *   application:
     *     name: licensing-service
     *   profiles:
     *     active:
     *       default
     *   cloud:
     *     config:
     *       uri: http://localhost:8888
     *
     * 注意:Spring Boot 应用程序支持两种机制来定义一个属性：YAML（Yet another Markup Language）
     * 和一个 "." 分隔的属性名称。这里选择 YAML 为手段配置应用程序。YAML 的属性值直接映射到 spring
     * .application.name、spring.profiles.active 和 spring.cloud.config.uri names 的分层
     * 格式。
     *
     * 第一个属性 spring.application.name 是应用程序的名称（例如：licensing-service）必须直接
     * 映射到在 Spring Cloud 配置服务器该目录的名称。以许可服务为例，在 Spring Cloud 配置服务器
     * 的目录名称为 licensing-service。
     *
     * 第二个属性 spring.profiles.active 用来告诉 Spring Boot 的应用程序应该运行什么文件。配置
     * 文件是一种机制，用来区分 Spring Boot 应用程序所消费的配置数据。对于许可服务的配置文件，你将
     * 支持将服务直接映射到云配置环境中的环境。例如，通过将 dev 作为配置传递，Spring Cloud 配置服
     * 务器将使用 dev 属性。如果设置了配置文件，许可服务将使用默认配置文件。
     *
     * 第三个属性 spring.cloud.config.uri 是位置信息，是许可服务查找 Spring Cloud 配置服务器的
     * 端点。默认情况下，许可服务将在 http://localhost:8888 查找配置服务器。后续将看到在应用启动
     * 时，如何覆盖定义在 boostrap.yml 和 application.yml 文件不同的属性。这将允许你告诉许可微
     * 服务应该运行在那个环境。
     *
     * 现在，如果你启动 Spring Cloud 配置服务，与相应的 Postgres 数据库在本地机器上运行，你可以使
     * 用它的默认配置文件启动许可服务。这是通过切换到 licensing-services 目录并发出以下命令完成的：
     *
     * mvn spring-boot: run
     *
     * 通过运行这个命令而没有任何属性设置，许可服务器会自动尝试使用端点（http://localhost:8888）
     * 连接到 Spring Cloud 配置服务器和在许可服务的 bootstrap.yml 文件中定义的激活状态的概要文件
     * （默认）。
     *
     * 如果你想覆盖这些默认值和指向到另一个环境，你可以通过编译 licensing-service 项目为 JAR 文件，
     * 然后用 -D 系统属性覆盖运行 JAR。下面的命令行演示如何使用非默认配置文件启动许可服务。
     *
     * java -Dspring.cloud.config.uri=http://localhost:8888 \
     *      -Dspring.profiles.active=dev \
     *      -jar target/licensing-service-0.0.1-SNAPSHOT.jar
     *
     * 之前的命令行，最重要的两个参数：spring.cloud.config.uri 和 spring.profiles.active。系统
     * 属性 -Dspring.cloud.config.uri=http://localhost:8888，指向一个脱离本地运行的配置服务器。
     *
     * 使用 -Dspring.profiles.active=dev 系统属性，你告诉许可服务使用 dev 配置文件（从配置服务器
     * 读取）连接到数据库的 dev 实例。
     *
     * 注意：如果你尝试从你的桌面使用之前的 Java 命令，运行从 GitHub（https://github.com/carnellj
     * /spmia-chapter3）下载的许可服务，它会失败，因为你本地没有运行的一个 Postgres 服务器，并且在
     * GitHub 库的源代码在配置服务器上是使用加密的（后续会讨论使用加密）。
     *
     *
     * PS：使用环境变量传递启动信息
     *
     * 在示例中，你硬编码值传递为 -D 参数的值。在云中，你需要的大部分应用程序配置数据将在配置服务器中。
     * 然而，对于启动你的服务所需的信息（如配置服务器的数据），你需要启动 VM 实例或 Docker 容器并传递
     * 环境变量。
     *
     * 这里所有的代码例子，都可以完全运行在 Docker 容器。在 Docker，你模拟不同环境通过特定环境下的
     * Docker-compose 文件，编排你所有的服务启动。容器所需的特定环境值作为环境变量传递到容器中。例如，
     * 在 dev 环境中启动许可服务，例如，在开发环境中启动许可证服务，docker/dev/docker-compose.yml
     * 文件包含 licensing-service 的下列条目：
     *
     *   licensing-service:
     *     image: siwuxie095/licensing-service:chapter3rd-demo1st
     *     ports:
     *       - "8080:8080"
     *     environment:
     *       PROFILE: "dev"
     *       CONFIGSERVER_URI: "http://config-server:8888"
     *       CONFIGSERVER_PORT:   "8888"
     *       DATABASESERVER_PORT: "5432"
     *
     * 文件中的环境条目包含两个变量概要文件的值，这就是许可服务将在下面运行的 Spring Boot 概要文件。
     * CONFIGSERVER_URI 被传入许可服务和定义 Spring Cloud 配置服务器实例，该服务将从 Spring
     * Cloud 配置服务器读取它的配置数据。
     *
     * 在启动脚本（它运行在容器中）里，你传递这些环境变量作为 -D 参数到 JVM 来启动应用程序。在每一
     * 个项目，你制作一个 Docker 容器，而 Docker 容器使用启动脚本启动容器内的软件。对亍许可服务，
     * 制作到容器的启动脚本将被发现在 licensing-service/src/main/docker/run.sh。在 run.sh
     * 脚本，以下进入启动你的 licensing-service JVM：
     *
     * echo "********************************************************"
     * echo "Starting License Server with Configuration Service :
     * 	 $CONFIGSERVER_URI";
     * echo "********************************************************"
     * java -Dspring.cloud.config.uri=$CONFIGSERVER_URI
     * -Dspring.profiles.active=$PROFILE -jar /usr/local/licensing-service/
     * 	licensing-service-0.0.1-SNAPSHOT.jar
     *
     *
     * 因为通过 Spring Boot Actuator 增强了所有具有自省功能的服务，所以你可以通过点击 http://
     * localhost:8080/env 来确认你正在运行的环境。/env 端点将提供关于服务的配置信息的完整列表，
     * 包括服务启动的属性和端点。
     *
     *
     *
     * 3、使用 Spring Cloud Configuration Server 配置数据源
     *
     * 在这一点上，你的数据库配置信息被直接注入到你的微服务。有了数据库配置集，配置许可的微服务就变成了
     * 使用标准 Spring 组件从 Postgres 数据库构建和检索数据的练习。许可服务已被重构为不同的类，每个
     * 类有独立的职责。
     *
     * 许可服务所有类和位置如下：
     * （1）
     * 类名：License
     * 位置：licenses/model
     * （2）
     * 类名：LicenseRepository
     * 位置：licenses/repository
     * （3）
     * 类名：LicenseService
     * 位置：licenses/services
     *
     * License 类是保留从 licensing 数据库检索的数据的模型类。如下：
     *
     * @Entity
     * @Table(name = "licenses")
     * public class License{
     *
     *     @Id
     *     @Column(name = "license_id", nullable = false)
     *     private String licenseId;
     *
     *     @Column(name = "organization_id", nullable = false)
     *     private String organizationId;
     *
     *     @Column(name = "product_name", nullable = false)
     *     private String productName;
     *
     * }
     *
     * 该类使用几个 Java 持久化注解（JPA）帮助 Spring Data 框架将数据从 Postgres 数据库 licenses
     * 表映射到 Java 对象。
     * （1）@Entity 注解让 Spring 知道这个 Java POJO 将被从存储数据映射成对象。
     * （2）@Table 注解告诉 Spring/JPA 什么数据库表应该映射。
     * （3）@Id 注解标识数据库的主键。
     * （4）最后，数据库中的每一列将要映射到都使用 @Column 属性进行标记的单个属性。
     *
     * Spring Data 和 JPA 框架提供了用于访问数据库基本的 CRUD 方法。如果你想在此之外构建方法，可以使
     * 用 Spring Data 存储库接口和基本命名约定来构建这些方法。在启动时，Spring 将从存储库接口解析方法
     * 的名称，将它们转换成基于名称的 SQL 语句，然后生成动态代理类，并在其遮掩下来完成工作。许可服务的存
     * 储库如下所示。
     *
     * @Repository
     * public interface LicenseRepository extends CrudRepository<License,String> {
     *
     *     public List<License> findByOrganizationId(String organizationId);
     *
     *     public License findByOrganizationIdAndLicenseId(String organizationId,
     *     String licenseId);
     *
     * }
     *
     * LicenseRepository 仓库接口，标有 @Repository 注解告诉 Spring，它应该把这个接口作为一个存储
     * 库和为它生成一个动态代理。Spring 提供了不同类型的数据访问仓库类。你可以选择使用 Spring
     * CrudRepository 基类来扩展你的 LicenseRepository 类。CrudRepository 基类包含有基本的 CRUD
     * 方法。除了 CRUD 方法从 CrudRepository 扩展，你已经增加了两个自定义查询方法从 licensing 表中
     * 检索数据。Spring Data 框架将把方法的名称分开，以构建一个查询来访问底层数据。
     *
     * 注意：Spring Data 框架提供了各种数据库平台上的抽象层，而不仅仅局限于关系数据库。NoSQL 数据库，
     * 如 MongoDB 和 Cassandra 也支持。
     *
     * 与许可服务的前身不同，现在你已经将 licensing 服务的业务和数据访问逻辑分开成 LicenseController
     * 和一个类名叫做 LicenseService 的单独服务。LicenseService 如下：
     *
     * @Service
     * public class LicenseService {
     *
     *     @Autowired
     *     private LicenseRepository licenseRepository;
     *
     *     @Autowired
     *     ServiceConfig config;
     *
     *     public License getLicense(String organizationId, String licenseId) {
     *         License license =
     *         licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
     *         return license.withComment(config.getExampleProperty());
     *     }
     *
     *     public List<License> getLicensesByOrg(String organizationId){
     *         return licenseRepository.findByOrganizationId( organizationId );
     *     }
     *
     *     public void saveLicense(License license){
     *         license.withId( UUID.randomUUID().toString());
     *         licenseRepository.save(license);
     *     }
     *
     * }
     *
     * 控制器，服务和存储库类被使用标准的 Spring @Autowired 注解组合在一起。
     *
     *
     *
     * 4、使用 @Value 注解直接读取属性
     *
     * 在 LicenseService 类，你可能会注意到在 getLicense() 方法中使用从 config.getExampleProperty()
     * 获取的值来设置 license.withComment() 的值。此处涉及的代码如下所示：
     *
     *     public License getLicense(String organizationId, String licenseId) {
     *         License license =
     *         licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
     *         return license.withComment(config.getExampleProperty());
     *     }
     *
     * 如果你看看 ServiceConfig 类，你将看到一个带有 @Value 的属性注解。如下代码显示了正在使用的 @Value
     * 注解。
     *
     * @Component
     * public class ServiceConfig {
     *
     *     @Value("${example.property}")
     *     private String exampleProperty;
     *
     *     public String getExampleProperty(){
     *         return exampleProperty;
     *     }
     *
     * }
     *
     * 当 Spring Data "自动神奇" 地将数据库的配置数据注入数据库连接对象时，所有其他属性都必须使用 @Value
     * 注解来注入。注意，@Value 注解可以从 Spring Cloud 配置服务器获取 example.property 值并其注入到
     * ServiceConfig 类的 example.property 属性。
     *
     * 提示：虽然可以直接将配置值注入到单个类的属性中，这里发现将所有配置信息集中到一个配置类中很有用，然后将
     * 配置类注入到需要的地方。
     *
     *
     *
     * 5、通过 Git 使用 Spring Cloud Config Server
     *
     * 使用文件系统作为 Spring Cloud 配置服务器的后端存储库对于基于云的应用程序是不切实际的，因为开发
     * 团队必须建立并管理安装在云配置服务器所有实例上的共享文件系统。
     *
     * Spring Cloud 配置服务器集成了不同的后端存储库，它可用于宿主应用程序配置属性。这里成功使用的一个
     * 方法是使用带有一个 Git 源代码管理存储库的 Spring Cloud 配置服务器。
     *
     * 通过使用 Git，你可以获得将配置管理属性置于源代码控制之下的所有好处，并提供一种简单的机制来集成你
     * 的构建和部署管道中的属性配置文件的部署。
     *
     * 使用 Git，你把文件系统的后置配置换出到以下列出的服务的 bootstrap.yml 文件。如下：
     *
     * server:
     *   port: 8888
     * spring:
     *   cloud:
     *     config:
     *       server:
     *         encrypt.enabled: false
     *         git:
     *           uri: https://github.com/carnellj/config-repo/
     *           searchPaths: licensingservice,organizationservice
     *           username: native-cloud-apps
     *           password: 0ffended
     *
     * 这里配置的三个主要部分是：
     * （1）spring.cloud.config.server 属性。
     * （2）spring.cloud.config.server.git.uri 属性
     * （3）spring.cloud.config.server.git.searchPaths 属性。
     *
     * spring.cloud.config.server 属性告诉 Spring Cloud 配置服务器使用非基于文件系统的后端存储仓
     * 库。这里将连接到基于云的存储库 GitHub。spring.cloud.config.server.git.uri 属性提供你正在
     * 连接的存储库的 URL。最后，spring.cloud.config.server.git.searchPaths 属性告诉 Spring
     * Cloud 配置服务器在 Git 仓库上的相对路径，它在 Spring Cloud 配置服务器启动之后将可以搜索。与
     * 配置文件系统版本类似，spring.cloud.config.server.git.seachPaths 的属性值将为配置服务托管
     * 的每个服务提供一个逗号分隔的列表。
     *
     *
     *
     * 6、通过 Spring Cloud Config Server 刷新属性
     *
     * 开发团队在使用 Spring Cloud 配置服务器时遇到的第一个问题是，当属性更改时，它们如何动态刷新应用
     * 程序。Spring Cloud 配置服务器将始终服务于最新版本的属性。通过底层存储库对属性做出的更改将是最
     * 新的。
     *
     * 然而，Spring Boot 应用程序只在启动时读取它们的属性，所以 Spring Cloud 配置服务器中所做的属性
     * 更改不会被 Spring Boot 应用程序自动拾取。Spring Boot Actuator 确实提供了一个 @RefreshScope
     * 注解，将允许开发团队访问 /refresh 端点，将迫使 Spring Boot 应用程序重新读取它的应用配置。下面
     * 的代码显示在实战中使用 @RefreshScope 注解。
     *
     * @SpringBootApplication
     * @RefreshScope
     * public class Application {
     *
     *     public static void main(String[] args) {
     *         SpringApplication.run(Application.class, args);
     *     }
     *
     * }
     *
     * 注意一些关于 @RefreshScope 注解的东西。首先，注解只会重新加载应用程序配置中的自定义 Spring
     * 属性。如你的数据库配置，使用 @RefreshScope 注解标注，Spring Data 不会重载的。执行刷新，你
     * 可以打开 http://<yourserver>:8080/refresh 端点。
     *
     *
     * PS：刷新微服务
     *
     * 使用 Spring Cloud 配置服务微服务的时候，有一件事你需要在你的动态变化特性之前考虑，你可能有相同
     * 服务的多个实例运行，你需要使用它们新的应用程序配置刷新所有这些服务。有几种方法可以解决这个问题：
     *
     * Spring Cloud 配置服务确实提供了一种基于 "推" 的机制，称为 Spring Cloud Bus，它允许 Spring
     * Cloud 配置服务器使用发生更改的服务向所有客户端发布。Spring Cloud 配置需要一个额外的中间件运行
     * （RabbitMQ）。这是一个非常有用的检测变化的手段，但不是所有的 Spring Cloud 配置的后端都支持
     * "推" 机制（即 Consul 服务器）。
     *
     * 后续你将使用 Spring 服务发现和 Eureka 来注册服务的所有实例。这里用来处理应用程序配置刷新事件的
     * 一种技术是在 Spring Cloud 配置中刷新应用程序属性，然后编写一个简单的脚本来查询服务发现引擎，查
     * 找服务的所有实例，并直接调用 /refresh 端点。
     *
     * 最后，你可以重新启动所有服务器或容器以获取新属性。这是一个微不足道的练习，尤其是如果在一个容器服
     * 务运行你的服务，如 Docker。重新启动 Docker 容器只需要几秒并将强制重读应用程序的配置。
     *
     * 记住，基于云的服务器是短暂的。不要害怕使用新配置启动新的服务实例，对新服务直接通信，然后删除旧的
     * 服务。
     */
    public static void main(String[] args) {

    }

}
