## 项目介绍

### 组织结构
```
paicoding
├── paicoding-api -- 定义一些通用的枚举、实体类，定义 DO\DTO\VO 等
├── paicoding-core -- 核心工具/组件相关模块，如工具包 util， 通用的组件都放在这个模块（以包路径对模块功能进行拆分，如搜索、缓存、推荐等）
├── paicoding-service -- 服务模块，业务相关的主要逻辑，DB 的操作都在这里
├── paicoding-ui -- HTML 前端资源（包括 JavaScript、CSS、Thymeleaf 等）
├── paicoding-web -- Web模块、HTTP入口、项目启动入口，包括权限身份校验、全局异常处理等
```

#### 环境配置说明

资源配置都放在 `paicoding-web` 模块的资源路径下，通过maven的env进行环境选择切换

当前提供了四种开发环境
- resources-env/dev: 本地开发环境，也是默认环境
- resources-env/test: 测试环境
- resources-env/pre: 预发环境
- resources-env/prod: 生产环境


#### 配置文件说明

- resources
  - application.yml: 主配置文件入口
  - application-config.yml: 全局的站点信息配置文件
  - logback-spring.xml: 日志打印相关配置文件
  - liquibase: 由liquibase进行数据库表结构管理
- resources-env
  - xxx/application-dal.yml: 定义数据库相关的配置信息
  - xxx/application-image.yml: 定义上传图片的相关配置信息
  - xxx/application-web.yml: 定义web相关的配置信息



### 开发环境

|      工具       | 版本        | 下载                                                                                                                     |
|:-------------:|:----------|------------------------------------------------------------------------------------------------------------------------|
|      jdk      | 1.8+      | [https://www.oracle.com/java/technologies/downloads/#java8](https://www.oracle.com/java/technologies/downloads/#java8) |
|     maven     | 3.4+      | [https://maven.apache.org/](https://maven.apache.org/)                                                                 |
|     mysql     | 5.7+/8.0+ | [https://www.mysql.com/downloads/](https://www.mysql.com/downloads/)                                                   |
|     redis     | 5.0+      | [https://redis.io/download/](https://redis.io/download/)                                                               |
| elasticsearch | 8.0.0+    | [https://www.elastic.co/cn/downloads/elasticsearch](https://www.elastic.co/cn/downloads/elasticsearch)                 |
|     nginx     | 1.10+     | [https://nginx.org/en/download.html](https://nginx.org/en/download.html)                                               |
|   rabbitmq    | 3.10.14+  | [https://www.rabbitmq.com/news.html](https://www.rabbitmq.com/news.html)                                               |
|    ali-oss    | 3.15.1    | [https://help.aliyun.com/document_detail/31946.html](https://help.aliyun.com/document_detail/31946.html)               |
|      git      | 2.34.1    | [http://github.com/](http://github.com/)                                                                               |
|    docker     | 4.10.0+   | [https://docs.docker.com/desktop/](https://docs.docker.com/desktop/)                                                   |
| let's encrypt | https证书   | [https://letsencrypt.org/](https://letsencrypt.org/)                                                                   |

