package com.siwuxie095.spring.cloud.extend1st.example5th;

/**
 * @author Jiajing Li
 * @date 2021-06-27 13:12:09
 */
public class Main {

    /**
     * 构建和编译项目
     *
     * 因为这里的所有部分都遵循相同的结构，并使用 Maven 作为构建工具，所以构建源代码变得非常简单。
     * 每一部分都在目录的根目录中有一个 pom.xml，作为所有子模块的父 pom。如果你想要编译源代码并
     * 在单个部分中为所有项目构建 Docker 镜像，则需要在这一部分的根目录下运行 mvn clean package
     * docker:build 命令。
     *
     * 这将在每个服务目录中执行 Maven pom.xml 文件，并在本地构建 Docker 镜像。
     *
     * 如果你要在某一部分中构建单个服务，则可以切换到特定的服务目录，然后再运行 mvn clean package
     * docker:build 命令。
     */
    public static void main(String[] args) {

    }

}
