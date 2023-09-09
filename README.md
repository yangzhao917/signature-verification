# signature-verification

> 一个用于等级保护安全测评3.0完整性检查的工具（签名-验签）

用于对Linux环境下对系统指定的配置文件、日志目录、和可执行文件进行签名和验签处理，并记录结果。

## 技术方案

|     名称     |     版本      |                说明                |
| :----------: | :-----------: | :--------------------------------: |
|  SpringBoot  | 2.3.2.RELEASE | 基于Spring的应用框架，用于简化开发 |
| mybatis-plus |     3.5.2     |             持久层框架             |
|    hutool    |    5.8.16     |      一个小而全的Java工具类库      |
|   knife4j    |     2.0.2     |             UI接口文档             |
|  达梦数据库  |       8       |            国产化数据库            |
|     JDK      |      1.8      |            Java开发工具            |
|    Maven     |     3.5.2     |        用于管理项目版本依赖        |

## 项目主要文件说明

> 项目遵循标准的maven项目结构，采用MVC方式编写

```shell
signature-verification/
├── LICENSE
├── README.md
├── log
├── script
│   ├── distribute_script.sh
│   └── signature.sh
├── sql
│   └── SIGN_VERIFY.sql
└── src
    ├── main
    └── test
```

- `log`：程序运行时所产生的日志文件目录
- `script`：程序所需要的脚本
  - `distribute_script.sh`：用于向服务器分发签名/验签脚本
  - `signature.sh`：签名/验签脚本
- `sql`：达梦数据库表结构
  - `SIGN_VERIFY.sql`：签名、验签表结构SQL
- `src`：项目源码目录
  - `main`：源码
  - `test`：测试源码

## 快速使用

1. 构建项目

使用`maven`和开发工具（我使用的是idea）构建打包项目，得到`signature-verification-0.0.1-SNAPSHOT.jar`文件。

或者也可以使用`target`目录下构建好的signature-verification-0.0.1-SNAPSHOT.jar文件

2. 部署程序和脚本

- 一台服务器

服务器需要安装JDK工具，版本请参考**技术方案**的jdk版本，安装过程和环境配置可自行百度；

通过启动参数启动jar应用，通过`signature.sh`创建定时任务执行签名、验签

```shell
#每天晚上11点验签
00 23 * * * /bin/bash /home/wsupport/Downloads/signature_script/signature.sh --verify

#每天凌晨3点签名
0 3 * * * /bin/bash /home/wsupport/Downloads/signature_script/signature.sh --sign
```

- 2台以上服务器

如果有多台服务器，请保证所有机器的iP可互通，并指定一台为应用服务器和数据库服务器，用来存储签名验签数据的结果。

> 服务端配置

1. 在达梦数据库上创建模式`SIGN_VERIFY`，密码自行设置，并修改`src/resources/application.properties`配置文件，将以下配置，修改成你自己的，以下配置仅供参考

```properties
spring.datasource.url=jdbc:dm://10.211.55.32:5236
spring.datasource.username=SIGN_VERIFY
spring.datasource.password=Aa12345678
```

2. 上传脚本到服务器

将`distribute_script.sh`和`signature.sh`上传到服务器，并根据你的情况修改参数配置。在distribute_script.sh中定义你要分发到其他服务器的IP。然后赋予distribute_script.sh可执行权限

```shell
chmod +x distribute_script.sh
```

然后执行分发脚本

```shell
sh distribute_script.sh
```

> 客户端配置

创建`signature.sh`的定时任务执行计划，具体时间可根据你的需求配置

```shell
#每天晚上11点验签
00 23 * * * /bin/bash /home/wsupport/Downloads/signature_script/signature.sh --verify

#每天凌晨3点签名
0 3 * * * /bin/bash /home/wsupport/Downloads/signature_script/signature.sh --sign
```

## 感谢

如果你觉得这个项目不错，请给予一个star。
