package com.siwuxie095.spring.cloud.extend2nd.example1st;

/**
 * @author Jiajing Li
 * @date 2021-06-27 13:57:33
 */
public class Main {

    /**
     * OAuth2 授权类型
     *
     * 在阅读第七部分时，你可能会认为 OAuth2 看起来不太复杂。毕竟有一个验证服务，用于检查用户的凭据并
     * 颁发令牌给用户。每次用户想要调用由 OAuth2 服务器保护的服务时，都可以依次出示令牌。
     *
     * 遗憾的是，现实世界从来都不是简单的。由于 Web 应用程序和基于云的应用程序具有相互关联的性质，用户
     * 期望可以安全地共享自己的数据，并在不同服务所拥有的不同应用程序之间整合功能。这从安全角度来看，是
     * 一个独特的挑战，因为开发人员希望跨不同的应用程序进行整合，而不是强迫用户与他们想要集成的每个应用
     * 程序共享他们的凭据。
     *
     * 幸运的是，OAuth2 是一个灵活的授权框架，它为应用程序提供了多种机制来对用户进行验证和授权，而不用
     * 强制他们共享凭据。但是，这也是 OAuth2 被认为是复杂的原因之一。这些验证机制被称为验证授权
     * （authentication grant）。OAuth2 有四种模式的验证授权，客户端应用程序可以使用它们来对用户进
     * 行验证、接收访问令牌，然后确认该令牌。这些授权分别是：
     * （1）密码授权（password grant）；
     * （2）客户端凭据授权（client credentials grant）；
     * （3）授权码授权（authorization code grant）；
     * （4）隐式授权（implicit grant）。
     *
     * 后续将介绍在执行每个 OAuth2 授权流程期间发生的活动，同时会谈到何时适合使用何种授权类型。
     */
    public static void main(String[] args) {

    }

}
