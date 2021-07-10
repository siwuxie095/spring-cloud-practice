package com.siwuxie095.spring.cloud.chapter7th.example7th;

/**
 * @author Jiajing Li
 * @date 2021-06-17 22:17:15
 */
public class Main {

    /**
     * 小结
     *
     * （1）OAuth2 是一个基于令牌的验证框架，用于对用户进行验证。
     * （2）OAuth2 确保每个执行用户请求的微服务不需要在每次调用时都出示用户凭据。
     * （3）OAuth2 为保护 Web 服务调用提供了不同的机制，这些机制称为授权（grant）。
     * （4）要在 Spring 中使用 OAuth2，需要建立一个基于 OAuth2 的验证服务。
     * （5）想要调用服务的每个应用程序都需要通过 OAuth2 验证服务注册。
     * （6）每个应用程序都有自己的应用程序名称和密钥。
     * （7）用户凭据和角色存储在内存或数据存储中，并通过 Spring Security 访问。
     * （8）每个服务必须定义角色可以采取的动作。
     * （9）Spring Cloud Security 支持 JSON Web Token（JWT）规范。
     * （10）JWT 定义了一个签名的 JSON 标准，用于生成 OAuth2 令牌。
     * （11）使用 JWT 可以将自定义字段注入规范中。
     * （12）保护微服务涉及的不仅仅是使用 OAuth2，还应该使用 HTTPS 加密服务之间的所有调用。
     * （13）使用服务网关来缩小可以到达服务的访问点的数量。
     * （14）通过限制运行服务的操作系统上的入站端口和出站端口数来限制服务的攻击面。
     */
    public static void main(String[] args) {

    }

}
