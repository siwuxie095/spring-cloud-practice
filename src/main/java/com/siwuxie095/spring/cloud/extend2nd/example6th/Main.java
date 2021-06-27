package com.siwuxie095.spring.cloud.extend2nd.example6th;

/**
 * @author Jiajing Li
 * @date 2021-06-27 15:14:53
 */
public class Main {

    /**
     * 如何刷新令牌
     *
     * 当 OAuth2 访问令牌被颁发时，其有效时间是有限的，它最终会过期。当令牌到期时，调用应用程序（和用户）
     * 将需要使用 OAuth2 服务重新进行验证。但是，在大多数 OAuth2 授权流程中，OAuth2 服务器将同时颁发
     * 访问令牌和刷新令牌。客户端可以将刷新令牌出示给 OAuth2 验证服务，该服务将确认刷新令牌，然后发出新
     * 的 OAuth2 访问令牌。如下是刷新令牌流程。
     * （1）用户已经登录了 EagleEye，并且早已通过 EagleEye OAuth2 服务进行了验证。用户正在愉快
     * 地工作，但是，他们的令牌已经过期了。
     * （2）用户下一次尝试调用服务（如组织服务）时，EagleEye 应用程序将把过期的令牌传递给组织服务。
     * （3）组织服务将尝试使用 OAuth2 服务确认令牌，OAuth2 服务返回 HTTP 状态码 401（未经授权）
     * 和一个 JSON 净荷，指示该令牌不再有效。组织服务将把 HTTP 状态码 401 返回给调用服务。
     * （4）EagleEye 应用程序收到 HTTP 状态码 401 和 JSON 净荷，指出调用从组织服务失败的原因。
     * EagleEye 应用程序将使用刷新令牌调用 OAuth2 验证服务。OAuth2 验证服务将确认刷新令牌，然
     * 后发回新的访问令牌。
     */
    public static void main(String[] args) {

    }

}
