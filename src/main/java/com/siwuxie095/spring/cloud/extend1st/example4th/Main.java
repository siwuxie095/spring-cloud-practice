package com.siwuxie095.spring.cloud.extend1st.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-27 12:36:31
 */
public class Main {

    /**
     * 剖析每一部分
     *
     * 这里的每一部分都有一个或多个与之相关联的服务。这里的每个服务都有自己的项目目录。例如，如果你
     * 查看第六部分，会发现里面有以下七个服务。
     * （1）config-server(confsvr)：Spring Cloud Config 服务器。
     * （2）eureka-server(eurekasvr)：使用 Eureka 的 Spring Cloud 服务。
     * （3）licensing-service(licensing-service)：EagleEye 的许可证服务。
     * （4）organization-service(organization-service)：EagleEye 的组织服务。
     * （5）organization-service-new(orgservice-new)：EagleEye 的组织服务的新测试版本。
     * （6）special-routes-service(specialroutes-service)：A/B 路由服务。
     * （7）zuul-server(zuulsvr)：EagleEye 的 Zuul 服务。
     *
     * 每一部分中的每个服务目录都是作为基于 Maven 的构建项目组织的。每个项目里面都有一个 src/main
     * 目录，其中包含以下子目录。
     * （1）java：这个目录包含用于构建服务的 Java 源代码。
     * （2）docker：这个目录包含两个文件，用于为每个服务构建一个 Docker 镜像。第一个文件
     * 总是被称为 Dockerfile，它包含 Docker 用来构建 Docker 镜像的步骤指导。第二个文件
     * run.sh 是一个在 Docker 容器内部运行的自定义 Bash 脚本。此脚本确保服务在某些关键
     * 依赖项（如数据库已启动并正在运行）可用之前不会启动。
     * （3）resources：resources 目录包含所有服务的 application.yml 文件。虽然应用程
     * 序配置存储在 Spring Cloud Config 中，但所有服务都在 application.yml 中拥有本
     * 地存储的配置。此外，resources 目录还包含一个 schema.sql 文件，它包含所有 SQL 命
     * 令，用于创建表以及将这些服务的数据预加载到 Postgres 数据库中。
     */
    public static void main(String[] args) {

    }

}
