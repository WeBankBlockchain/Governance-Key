# WebankBlockchain-Governance-Key

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

WeBankBlockchain-Governance-Key组件旨在让用户便捷、安全的使用私钥，覆盖私钥颁发、托管、使用，覆盖私钥全生命周期，并支持国密标准。Gov-key包含key-core和key-mgr两个组件，key-core用于私钥的生成、加密、分片还原、常规密码学操作，适合个人级用户使用。key-mgr用于私钥保管，适合企业级用户使用。


## 关键特性

- 多种主流密钥生成方式

- 支持私钥加密导出

- 支持多种密钥托管方案

- 支持分片与还原

- 支持可视化操作界面

- 支持通用密码学操作

- 国密支持

## 环境要求

在使用本组件前，请确认系统环境已安装相关依赖软件，清单如下：

| 依赖软件 | 说明 |备注|
| --- | --- | --- |
| Java | JDK[1.8] | |
| Git | 下载源码需使用Git | |
| MySQL | >= mysql-community-server[5.7] | 使用key-mgr托管时需要|


## 文档
- [**中文**](https://gov-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Gov-Key/index.html)
- [**快速安装**](https://gov-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Gov-Key/corequickstart.html)


## 贡献代码
欢迎参与本项目的社区建设：
- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目上方Star按钮)。
- 欢迎提交代码(Pull requests)。
- [提问和提交BUG](https://github.com/WeBankBlockchain/WeBankBlockchain-Governance-Key/issues)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。

## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。
