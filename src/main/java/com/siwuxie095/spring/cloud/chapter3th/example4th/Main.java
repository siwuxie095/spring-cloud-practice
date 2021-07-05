package com.siwuxie095.spring.cloud.chapter3th.example4th;

/**
 * @author Jiajing Li
 * @date 2021-05-28 20:49:30
 */
@SuppressWarnings("all")
public class Main {

    /**
     * 将 Spring Cloud Config 与 Spring Boot 客户端集成
     *
     * 之前构建了一个简单的许可证服务框架，这个框架只是返回一个代表数据库中单个许可记录的硬编码 Java 对象。在
     * 下面一个示例中，将构建许可证服务，并与持有许可数据的 Postgres 数据库进行交流。
     *
     * 这里将使用 Spring Data 与数据库进行通信，并将数据从许可证表映射到保存数据的 POJO。数据库连接和一条简
     * 单的属性将从 Spring Cloud 配置服务器中读出。如下展示了许可证服务和 Spring Cloud 配置服务之间的交互。
     * （1）许可证服务实例：将 Spring Profile 和端点信息传递给许可证服务。
     * （2）Spring Cloud 配置服务：许可证服务联系 Spring Cloud 配置服务。
     * （3）配置服务存储库：从存储库检索特定 profile 的配置信息。
     * （4）许可证服务实例：属性值传回给许可证服务。
     *
     * 当许可证服务首次启动时，将通过命令行传递两条信息：Spring 的 profile 和许可证服务用于与 Spring Cloud
     * 配置服务通信的端点。Spring 的 profile 值映射到为 Spring 服务检索属性的环境。当许可证服务首次启动时，
     * 它将通过从 Spring 的 profile 传入构建的端点与 Spring Cloud Config 服务进行联系。然后，Spring
     * Cloud Config 服务将会根据 URI 上传递过来的特定 Spring profile，使用已配置的后端配置存储库（文件系
     * 统、Git、Consul 或 Eureka）来检索相应的配置信息，然后将适当的属性值传回许可证服务。接着，Spring
     * Boot 框架将这些值注入应用程序的相应部分。
     *
     *
     *
     * 1、建立许可证服务对 Spring Cloud Config 服务器的依赖
     *
     * 现在把焦点从配置服务器转移到许可证服务。这里需要做的第一件事，就是在许可证服务中为 Maven 文件添加更多
     * 的条目。如下代码展示了需要添加的条目。
     *
     * <dependency>
     *   <groupId>org.springframework.boot</groupId>
     *   <artifactId>spring-boot-starter-data-jpa</artifactId>    ⇽---  告诉 Spring Boot 将要在服务
     *                                                           中使用 Java Persistence API（JPA）
     * </dependency>
     * <dependency>
     *   <groupId>postgresql</groupId>
     *   <artifactId>postgresql</artifactId>    ⇽---  告诉 Spring Boot 拉取 Postgres JDBC 驱动程序
     *   <version>9.1-901.jdbc4</version>
     * </dependency>
     * <dependency>
     *   <groupId>org.springframework.cloud</groupId>
     *   <artifactId>spring-cloud-config-client</artifactId>    ⇽---  告诉 Spring Boot 拉取 Spring
     *                                                          Cloud Config 客户端所需的所有依赖项
     * </dependency>
     *
     * 第一个和第二个依赖项 spring-boot-starter-data-jpa 和 postgresql 导入了 Spring Data Java
     * Persistence API（JPA）和 Postgres JDBC 驱动程序。最后一个依赖项是 spring-cloud-config-client，
     * 它包含与 Spring Cloud 配置服务器交互所需的所有类。
     *
     *
     *
     * 2、配置许可证服务以使用 Spring Cloud Config
     *
     * 在定义了 Maven 依赖项后，需要告知许可证服务在哪里与 Spring Cloud 配置服务器进行联系。在使用 Spring
     * Cloud Config 的 Spring Boot 服务中，配置信息可以在 bootstrap.yml 和 application.yml 这两个配
     * 置文件之一中设置。
     *
     * 在其他所有配置信息被使用之前，bootstrap.yml 文件要先读取应用程序属性。一般来说，bootstrap.yml 文件
     * 包含服务的应用程序名称、应用程序 profile 和连接到 Spring Cloud Config 服务器的 URI。希望保留在本
     * 地服务（而不是存储在 Spring Cloud Config 中）的其他配置信息，都可以在服务中的 application.yml 文
     * 件中进行本地设置。通常情况下，即使 Spring Cloud Config 服务不可用，也会希望存储在 application.yml
     * 文件中的配置数据可用。bootstrap.yml 和 application.yml 保存在项目的 src/main/resources 文件夹
     * 中。
     *
     * 要使许可证服务与 Spring Cloud Config 服务进行通信，需要添加一个 bootstrap.yml 文件，并设置三个属
     * 性，即 spring.application.name、spring.profiles.active 和 spring.cloud.config.uri。
     *
     * 如下代码展示了许可证服务的 bootstrap.yml 文件。
     *
     * spring:
     *   application:
     *     name: licensingservice    ⇽---  指定许可证服务的名称，以便 Spring Cloud Config 客户端知道
     *                                     正在查找哪个服务
     *   profiles:
     *     active:
     *       default    ⇽---  指定服务应该运行的默认 profile oprofile 映射到环境
     *   cloud:
     *     config:
     *       uri: http://localhost:8888    ⇽---  指定 Spring Cloud Config 服务器的位置
     *
     * 注意：Spring Boot 应用程序支持两种定义属性的机制：YAML（Yet another Markup Language）和使用 "."
     * 分隔的属性名称。这里选择 YAML 作为配置应用程序的方法。YAML 属性值的分层格式直接映射到 spring.
     * application.name、spring.profiles.active 和 spring.cloud.config.uri 名称。
     *
     * 第一个属性 spring.application.name 是应用程序的名称（如 licensingservice）并且必须直接映射到
     * Spring Cloud 配置服务器中的目录的名称。对于许可证服务，需要在 Spring Cloud 配置服务器上有一个名
     * 为 licensingservice 的目录。
     *
     * 第二个属性 spring.profiles.active 用于告诉 Spring Boot 应用程序应该运行哪个 profile。profile
     * 是区分 Spring Boot 应用程序要使用哪个配置数据的机制。对于许可证服务的 profile，这里将支持服务的环
     * 境直接映射到云配置环境中。例如，通过传入开发环境作为 profile，Spring Cloud 配置服务器将使用开发环
     * 境的属性。如果没有设置 profile，许可证服务将使用默认 profile。
     *
     * 第三个也是最后一个属性 spring.cloud.config.uri 是许可证服务查找 Spring Cloud 配置服务器端点的
     * 位置。在默认情况下，许可证服务将在 http://localhost:8888 上查找配置服务器。后续你将看到如何在应
     * 用程序启动时覆盖 boostrap.yml 和 application.yml 文件中定义的不同属性，这样可以告知许可证微服务
     * 应该运行哪个环境。
     *
     * 现在，如果启动 Spring Cloud 配置服务，并在本地计算机上运行相应的 Postgres 数据库，那么就可以使用
     * 默认 profile 启动许可证服务。这可以通过切换到许可证服务的目录并执行以下命令来完成：
     *
     * mvn spring-boot: run
     *
     * 通过运行此命令而不设置任何属性，许可证服务器将自动尝试使用端点（http://localhost:8888）和在许可证
     * 服务的 bootstrap.yml 文件中定义的活跃 profile（默认），连接到 Spring Cloud 配置服务器。
     *
     * 如果要覆盖这些默认值并指向另一个环境，可以通过将许可证服务项目编译到 JAR，然后使用 -D 系统属性来运行
     * 这个 JAR 来实现。下面的命令行演示了如何使用非默认 profile 启动许可证服务：
     *
     * java  -Dspring.cloud.config.uri=http://localhost:8888 \
     *       -Dspring.profiles.active=dev \
     *       -jar target/licensing-service-0.0.1-SNAPSHOT.jar
     *
     * 使用上述命令行将覆盖两个参数，即 spring.cloud.config.uri 和 spring.profiles.active。使用
     * -Dspring.cloud.config.uri=http://localhost:8888 系统属性将指向一个本地运行的配置服务器。
     *
     * 注意：如果你尝试从自己的台式机上使用上述的 Java 命令来运行从 GitHub 存储库下载的许可证服务，将
     * 会运行失败，这是因为没有运行桌面 Postgres 服务器，并且 GitHub 存储库中的源代码在配置服务器上
     * 使用了加密。
     *
     * 使用 -Dspring.profiles.active=dev 系统属性，可以告诉许可证服务使用开发环境 profile（从配置
     * 服务器读取），从而连接到开发环境的数据库的实例。
     *
     *
     * PS：使用环境变量传递启动信息
     *
     * 在这些示例中，将这些值硬编码传递给 -D 参数值。在云中所需的大部分应用程序配置数据都将位于配置服务
     * 器中。但是，对于启动服务所需的信息（如配置服务器的数据），则需要启动 VM 实例或 Docker 容器并传
     * 入环境变量。
     *
     * 这里的所有代码示例都可以在 Docker 容器中完全运行。使用 Docker，可以通过特定环境的 Docker-
     * compose 文件来模拟不同的环境，从而协调所有服务的启动。容器所需的特定环境值作为环境变量传递到容器。
     * 例如，要在开发环境中启动许可证服务，docker/dev/docker-compose.yml 文件要包含以下用于许可证服
     * 务的条目：
     *
     * licensingservice:
     *   image: ch3-thoughtmechanix/licensing-service
     *   ports:
     *     - "8080:8080"
     *   environment:     ⇽---  指定许可证服务容器的环境变量的开始
     *     PROFILE: "dev"    ⇽---  PROFILE 环境变量被传递给 Spring Boot 服务命令行，告诉 Spring
     *                             Boot 应该运行哪个 profile
     *     CONFIGSERVER_URI: http://configserver:8888    ⇽---  配置服务的端点
     *     CONFIGSERVER_PORT: "8888"
     *     DATABASESERVER_PORT: "5432"
     *
     * 该文件中的环境条目包含两个变量 PROFILE 的值，这是许可证服务将要运行的 Spring Boot profile。
     * CONFIGSERVER_URI 被传递给许可证服务，该属性定义了 Spring Cloud 配置服务器实例的地址，服务
     * 将从该 URI 读取其配置数据的。
     *
     * 在由容器运行的启动脚本中，这里将这些环境变量以 -D 参数传递到启动应用程序的 JVM。在每个项目中，
     * 可以制作一个 Docker 容器，然后该 Docker 容器使用启动脚本启动该容器中的软件。对于许可证服务，
     * 容器中的启动脚本位于 src/main/docker/run.sh 中。在 run.sh 脚本中，以下条目负责启动许可证
     * 服务的 JVM：
     *
     * echo  "********************************************************"
     * echo  "Starting License Server with Configuration Service :
     *       $CONFIGSERVER_URI";
     * echo  "********************************************************"
     * java -Dspring.cloud.config.uri=$CONFIGSERVER_URI
     * -Dspring.profiles.active=$PROFILE -jar /usr/local/licensingservice/
     *      licensing-service-0.0.1-SNAPSHOT.jar
     *
     *
     * 因为这里是通过 Spring Boot Actuator 来增强服务的自我检查能力的，所以可以通过访问 http://
     * localhost:8080/env 来确认正在运行的环境。/env 端点将提供有关服务的配置信息的完整列表，包括
     * 服务启动的属性和端点。
     *
     * PS：可以通过调用 /env 端点来检查许可证服务加载的配置。
     *
     * 其中要注意的关键是，许可证服务的活跃 profile 是 dev。通过观察返回的 JSON，还可以看到被返回的
     * Postgres 数据库 URI 是开发环境 URI：jdbc:postgresgl://database:5432/eagle-eye-dev。
     *
     *
     * PS：暴露太多的信息
     *
     * 围绕如何为服务实现安全性，每个组织都会有自己的规则。许多组织认为，服务不应该广播任何有关自己的
     * 信息，也不允许像 /env 端点这样的东西在服务上存在，因为他们相信（这是理所当然的）这样会为潜在
     * 的黑客提供太多的信息。Spring Boot 为配置 Spring Actuator 端点返回的信息提供了丰富的功能，
     * 具体可参考相关文档。
     *
     *
     *
     * 3、使用 Spring Cloud 配置服务器连接数据源
     *
     * 至此已将数据库配置信息直接注入微服务中。数据库配置设置完毕后，配置许可证微服务就变成使用标准 Spring 组
     * 件来构建和从 Postgres 数据库中检索数据的练习。许可证服务已被重构成不同的类，每个类都有各自独立的职责。
     * 这些类如下所示。
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
     * License 类是模型类，它将持有从许可数据库检索的数据。如下所示。
     *
     * @SuppressWarnings("all")
     * // @Entity 注解告诉 Spring 这是一个 JPA 类
     * @Entity
     * // @Table 映射到数据库的表
     * @Table(name = "licenses")
     * public class License {
     *
     *     // @Id 将该字段标记为主键
     *     @Id
     *     // @Column 将该字段映射到特定数据库表中的列
     *     @Column(name = "license_id", nullable = false)
     *     private String licenseId;
     *
     *     @Column(name = "organization_id", nullable = false)
     *     private String organizationId;
     *
     *     @Column(name = "product_name", nullable = false)
     *     private String productName;
     *
     *     // ...
     *
     * }
     *
     * 这个类使用了多个 Java 持久化注解（Java Persistence Annotations，JPA），帮助 Spring Data 框架将
     * Postgres 数据库中的 licenses 表中的数据映射到 Java 对象。
     * （1）@Entity 注解让 Spring 知道这个 Java POJO 将要映射保存数据的对象。
     * （2）@Table 注解告诉 Spring JPA 应该映射哪个数据库表。
     * （3）@Id 注解标识数据库的主键。
     * （4）@Column 注解标记数据库中的每一列将被映射到的各个属性。
     *
     * Spring Data 和 JPA 框架提供访问数据库的基本 CRUD 方法。如果要构建其他方法，可以使用 Spring Data
     * 存储库接口和基本命名约定来进行构建。Spring 将在启动时从 Repository 接口解析方法的名称，并将它们转换
     * 为基于名称的 SQL 语句，然后在幕后生成一个动态代理类来完成这项工作。如下所示。
     *
     * // 告诉 Spring Boot 这是一个 JPA 存储库类
     * @Repository
     * // 定义正在扩展 Spring CrudRepository
     * public interface LicenseRepository extends CrudRepository<License,String> {
     *
     *     // 每个查询方法被 Spring 解析为 SELECT...FROM 查询
     *     public List<License> findByOrganizationId(String organizationId);
     *
     *     public License findByOrganizationIdAndLicenseId(String organizationId,
     *                                                     String licenseId);
     *
     * }
     *
     * 存储库接口 LicenseRepository 用 @Repository 注解标记，这个注解告诉 Spring 应该将这个接口视为存储
     * 库并为它生成动态代理。Spring 提供不同类型的数据访问存储库。这里选择使用 Spring CrudRepository 基类
     * 来扩展 LicenseRepository 类。CrudRepository 基类包含基本的 CRUD 方法。除了从 CrudRepository
     * 扩展的 CRUD 方法外，这里还添加了两个用于从许可表中检索数据的自定义查询方法。Spring Data 框架将拆开这
     * 些方法的名称以构建访问底层数据的查询。
     *
     * 注意：Spring Data 框架提供各种数据库平台上的抽象层，并不仅限于关系数据库。该框架还支持 NoSQL 数据库，
     * 如 MongoDB 和 Cassandra。
     *
     * 与之前的许可证服务不同，现在已将许可证服务的业务逻辑和数据访问逻辑从 LicenseController 中分离出来，
     * 并划分在名为 LicenseService 的独立服务类中。如下所示。
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
     *         License license = licenseRepository
     *                 .findByOrganizationIdAndLicenseId(organizationId, licenseId);
     *         return license.withComment(config.getExampleProperty());
     *     }
     *
     *     public List<License> getLicensesByOrg(String organizationId) {
     *         return licenseRepository.findByOrganizationId(organizationId);
     *     }
     *
     *     public void saveLicense(License license) {
     *         license.withId(UUID.randomUUID().toString());
     *         licenseRepository.save(license);
     *     }
     *
     *     // ...
     *
     * }
     *
     * 这里使用标准的 Spring @Autowired 注解将控制器、服务和存储库类连接到一起。
     *
     *
     *
     * 4、使用 @Value 注解直接读取属性
     *
     * 在上面的 LicenseService 类中，getLicense() 方法中使用了来自 config.getExampleProperty() 的值
     * 来设置 license.withComment() 的值。所指的代码如下：
     *
     *     public License getLicense(String organizationId, String licenseId) {
     *         License license = licenseRepository
     *                 .findByOrganizationIdAndLicenseId(organizationId, licenseId);
     *         return license.withComment(config.getExampleProperty());
     *     }
     *
     * 如果查看 ServiceConfig 类，将看到使用 @Value 注解标注的属性。如下代码展示了 @Value 注解的用法。
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
     * 虽然 Spring Data "自动神奇地" 将数据库的配置数据注入数据库连接对象中，但所有其他属性都必须使用 @Value
     * 注解进行注入。在上述示例中，@Value 注解从 Spring Cloud 配置服务器中提取 example.property 并将其注入
     * ServiceConfig 类的 example.property 属性中。
     *
     * 注意：虽然可以将配置的值直接注入各个类的属性中，但这里发现将所有配置信息集中到一个配置类，然后将配置类注入
     * 需要它的地方是很有用的。
     *
     *
     *
     * 5、使用 Spring Cloud 配置服务器和 Git
     *
     * 如前所述，使用文件系统作为 Spring Cloud 配置服务器的后端存储库，对基于云的应用程序来说是不切实际的，
     * 因为开发团队必须搭建和管理所有挂载在云配置服务器实例上的共享文件系统。
     *
     * Spring Cloud 配置服务器能够与不同的后端存储库集成，这些存储库可以用于托管应用程序配置属性。这里成功地
     * 使用过 Spring Cloud 配置服务器与 Git 源代码控制存储库集成。
     *
     * 通过使用 Git，可以获得将配置管理属性置于源代码管理下的所有好处，并提供一种简单的机制来将属性配置文件的
     * 部署集成到构建和部署管道中。
     *
     * 要使用 Git，需要在配置服务的 bootstrap.yml 文件中使用如下代码所示的配置替换文件系统的配置。
     *
     * server:
     *   port: 8888
     * spring:
     *   cloud:
     *     config:
     *       server:
     *         git:     ⇽---  告诉 Spring Cloud Config 使用 Git 作为后端存储库
     *           uri: https://github.com/carnellj/config-repo/    ⇽---  告诉 Spring Cloud Git 服务器
     *                                                                  和 Git 存储库的 URL
     *           searchPaths: licensingservice,organizationservice    ⇽---  告诉 Spring Cloud Config
     *                                                                      在 Git 中查找配置文件的路径
     *           username: native-cloud-apps
     *           password: 0ffended
     *
     * 上述示例中的3个关键配置部分是 spring.cloud.config.server、spring.cloud.config.server.git.uri
     * 和 spring.cloud.config.server.git.searchPaths 属性。
     *
     * spring.cloud.config.server 属性告诉 Spring Cloud 配置服务器使用非基于文件系统的后端存储库。在上
     * 述例子中，将要连接到基于云的 Git 存储库 GitHub。
     *
     * spring.cloud.config.server.git.uri 属性提供要连接的存储库 URL。
     *
     * 最后，spring.cloud.config.server.git.searchPaths 属性告诉 Spring Cloud Config 服务器在云配置
     * 服务器启动时应该在 Git 存储库中搜索的相对路径。与配置的文件系统版本一样，spring.cloud.config.server
     * .git.seachPaths 属性中的值是以逗号分隔的由配置服务托管的服务列表。
     *
     *
     *
     * 6、使用 Spring Cloud 配置服务器刷新属性
     *
     * 开发团队想要使用 Spring Cloud 配置服务器时，遇到的第一个问题是，如何在属性变化时动态刷新应用程序。
     * Spring Cloud 配置服务器始终提供最新版本的属性，通过其底层存储库，对属性进行的更改将是最新的。
     *
     * 但是，Spring Boot 应用程序只会在启动时读取它们的属性，因此 Spring Cloud 配置服务器中进行的属性
     * 更改不会被 Spring Boot 应用程序自动获取。Spring Boot Actuator 提供了一个 @RefreshScope 注
     * 解，允许开发团队访问 /refresh 端点，这会强制 Spring Boot 应用程序重新读取应用程序配置。如下代码
     * 展示了 @RefreshScope 注解的作用。
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
     * 这里需要注意一些有关 @RefreshScope 注解的事情。首先，注解只会重新加载应用程序配置中的自定义
     * Spring 属性。Spring Data 使用的数据库配置等不会被 @RefreshScope 注解重新加载。要执行刷新，
     * 可以访问 http://<yourserver>:8080/refresh 端点。
     *
     *
     * PS：关于刷新微服务
     *
     * 将微服务与 Spring Cloud 配置服务一起使用时，在动态更改属性之前需要考虑的一件事是，可能
     * 会有同一服务的多个实例正在运行，需要使用新的应用程序配置刷新所有这些服务。有几种方法可以
     * 解决这个问题。
     *
     * Spring Cloud 配置服务确实提供了一种称为 Spring Cloud Bus 的 "推送" 机制，使 Spring
     * Cloud 配置服务器能够向所有使用服务的客户端发布有更改发生的消息。Spring Cloud 配置需要
     * 一个额外的中间件（RabbitMQ）运行。这是检测更改的非常有用的手段，但并不是所有的 Spring
     * Cloud 配置后端都支持这种 "推送" 机制（也就是 Consul 服务器）。
     *
     * 后续将使用 Spring Service Discovery 和 Eureka 来注册所有服务实例。曾经用过的用于处
     * 理应用程序配置刷新事件的一种技术是，刷新 Spring Cloud 配置中的应用程序属性，然后编写一
     * 个简单的脚本来查询服务发现引擎以查找服务的所有实例，并直接调用 /refresh 端点。
     *
     * 最后一种方法是重新启动所有服务器或容器来接收新的属性。这项工作很简单，特别是在 Docker
     * 等容器服务中运行服务时。重新启动 Docker 容器差不多需要几秒，然后将强制重新读取应用程序
     * 配置。
     *
     * 记住，基于云的服务器是短暂的。不要害怕使用新配置启动服务的新实例，直接使用新服务，然后
     * 拆除旧的服务。
     */
    public static void main(String[] args) {

    }

}
