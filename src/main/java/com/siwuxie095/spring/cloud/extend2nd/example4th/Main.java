package com.siwuxie095.spring.cloud.extend2nd.example4th;

/**
 * @author Jiajing Li
 * @date 2021-06-27 14:30:41
 */
public class Main {

    /**
     * 授权码授权
     *
     * 授权码授权是迄今为止最复杂的 OAuth2 授权，但它也是最常用的流程，因为它允许来自不同供应商的不同应用程序
     * 共享数据和服务，而无须在多个应用程序间暴露用户凭据。授权码授权不会让调用应用程序立即获得 OAuth2 访问令
     * 牌，而是使用一个 "预访问" 授权码的方式来执行额外的检查。
     *
     * 理解授权码授权的简单方法就是看一个例子。假设有一个 EagleEye 用户，它也使用 Salesforce.com。EagleEye
     * 客户的 IT 部门已经构建了一个 Salesforce 应用程序，它需要 EagleEye 服务（组织服务）的数据。下面来看
     * 看授权码授权是如何使 Salesforce 从 EagleEye 的组织服务中访问数据而无须 EagleEye 客户向 Salesforce
     * 公开他们的 EagleEye 凭据的。
     *
     * （1）EagleEye 用户登录到 EagleEye，并为其 Salesforce 应用程序生成应用程序名称和应用程序密钥。
     * 作为注册过程的一部分，还将提供一个回调 URL，以返回到基于 Salesforce 的应用程序。此回调 URL 是
     * 一个 Salesforce 的 URL，将在 EagleEye OAuth2 服务器验证了用户的 EagleEye 凭据后被调用。
     *
     * （2）用户使用以下信息配置 Salesforce 应用程序：
     * 1）为 Salesforce 创建的应用程序名称；
     * 2）为 Salesforce 生成的密钥；
     * 3）指向 EagleEye OAuth2 登录页面的 URL。
     *
     * 现在，当用户尝试使用 Salesforce 应用程序并通过组织服务访问 EagleEye 数据时，根据上述要点中描
     * 述的 URL，用户将被重定向到 EagleEye 登录页面。用户将提供他们的 EagleEye 凭据。如果提供的
     * EagleEye 凭据有效，则 EagleEye OAuth2 服务器将生成一个授权码，并通过步骤 1 中提供的 URL 将
     * 用户重定向到 Salesforce。授权码将作为回调 URL 的一个查询参数被发送。
     *
     * （3）自定义的 Salesforce 应用程序将对授权码进行持久化。注意，此授权码不是 OAuth2 访问令牌。
     *
     * （4）一旦存储了授权码，自定义的 Salesforce 应用程序就可以向 Salesforce 应用程序出示在注册过
     * 程中生成的密钥，并将授权码返回给 EagleEye OAuth2 服务器。EagleEye OAuth2 服务器将确认授权
     * 码是否有效，然后将 OAuth2 令牌返回给自定义的 Salesforce 应用程序。每次自定义的 Salesforce
     * 应用程序需要对用户进行验证并获取 OAuth2 访问令牌时，都会使用此授权码。
     *
     * （5）Salesforce 应用程序将在 HTTP 首部中传递 OAuth2 令牌以调用 EagleEye 组织服务。
     *
     * （6）组织服务将通过 EagleEye OAuth2 服务来确认传入 EagleEye 服务调用的 OAuth2 访问令牌。
     * 如果令牌有效，组织服务将处理用户的请求。
     *
     * 这真的太令人激动了！应用程序到应用程序的集成是错综复杂的。这整个流程中要注意的是，即使用户登录到
     * Salesforce 并且正在访问 EagleEye 数据，用户的 EagleEye 凭据也不会直接暴露给 Salesforce。
     * 在 EagleEye OAuth2 服务生成并提供初始授权码之后，用户就再也不用向 EagleEye 服务提供他们的凭
     * 据了。
     */
    public static void main(String[] args) {

    }

}
