# 腾讯云DNS解析IPV6动态配置
使用腾讯云的API，在计算机网络发生变化时将ipv6地址同步到DNS解析记录中

## 参考自腾讯云API文档
>　https://cloud.tencent.com/document/product/1278/46713


## 需要在Main.java中配置你的相关信息
```

```java
// 腾讯云API的 SECRET_ID
private static final String SECRET_ID = "";

// 腾讯云API的 SECRET_KEY
private static final String SECRET_KEY = "";

// 要同步的主域名 例：org.example
private static final String DOMAIN = "";

// 子域名 例：www
private static final String SUB_DOMAIN = "";

// 设备网卡的MAC地址 例：D9-C6-37-A1-B2-C3
private static final String PHYSICAL_ADDRESS = "";
```

## 使用
- 在Main.java中配置你的相关信息

- 配置好后在项目路径下执行:
```bash
> mvn clean package

> ./run.bat
```