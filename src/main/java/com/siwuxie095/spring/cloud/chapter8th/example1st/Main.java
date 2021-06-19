package com.siwuxie095.spring.cloud.chapter8th.example1st;

/**
 * @author Jiajing Li
 * @date 2021-06-18 23:23:53
 */
public class Main {

    /**
     * Spring Cloud Stream 的事件驱动架构
     *
     * 还记得最后一次和别人坐下来聊天是什么时候吗？回想一下你是如何与那个人进行互动的。你完全专注于信息交换（就是在
     * 你说完之后，在等待对方完全回复之前什么都没有做）吗？当你说话的时候，你完全专注于谈话，而不让外界的东西分散自
     * 己的注意力吗？如果这场谈话中有两位以上的参与者，你重复了你对每位对话参与者所说的话，然后依次等待他们的回应吗？
     * 如果你对上述问题的回答都是 "是"，那就说明你已经得道开悟，超越了凡人，那么你应该停止你正在做的事情，因为你现
     * 在可以回答这个古老的问题："一只手鼓掌的声音是什么？另外，猜你没有孩子。
     *
     * 事实上，人类总是处于一种运动状态，与周围的环境相互作用，同时发送信息给周围的事物并接收信息。在家里，一个典型
     * 的对话可能是这样的：在和老婆说话的时候你正忙着洗碗，你正在向她描述你的一天，此时，她正玩着她的手机，并聆听着、
     * 处理着你说的话，然后偶尔给予回应。当你在洗碗的时候，听到隔壁房间里有一阵骚动。你停下手头的事情，冲进隔壁房间
     * 去看看出了什么问题，然后你就看到你们那只九个月大的小狗维德咬住了你三岁大的儿子的鞋，像拿着战利品般在客厅里到
     * 处跑，而你三岁的儿子对此情此景感到不满。你满屋子追狗，直到把鞋子拿回来。然后你回去洗碗，继续和老婆聊天。
     *
     * 这里跟大家说这件事并不是想告诉大家生活中普通的一天，而是想要指出人类与世界的互动不是同步的、线性的，不能狭义
     * 地定义为一个请求-响应模型。它是消息驱动的，在这里，人类不断地发送和接收消息。当人类收到消息时，会对这些消息
     * 作出反应，同时经常打断正在处理的主要任务。
     *
     * 这里将介绍如何设计和实现基于 Spring 的微服务，以便与其他使用异步消息的微服务进行通信。使用异步消息在应用程
     * 序之间进行通信并不新鲜，新鲜的是使用消息实现事件通信的概念，这些事件代表了状态的变化。这个概念称为事件驱动架
     * 构（Event Driven Architecture，EDA），也被称为消息驱动架构（Message Driven Architecture，MDA）。基
     * 于 EDA 的方法允许开发人员构建高度解耦的系统，它可以对变更作出反应，而不需要与特定的库或服务紧密耦合。当与微
     * 服务结合后，EDA 通过仅让服务监听由应用程序发出的事件流（消息）的方式，允许开发人员迅速地向应用程序中添加新
     * 功能。
     *
     * Spring Cloud 项目通过 Spring Cloud Stream 子项目使构建基于消息传递的解决方案变得轻而易举。Spring Cloud
     * Stream 允许开发人员轻松实现消息发布和消费，同时屏蔽与底层消息传递平台相关的实现细节。
     */
    public static void main(String[] args) {

    }

}
