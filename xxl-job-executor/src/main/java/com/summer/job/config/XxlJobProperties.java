package com.summer.job.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @Desc
 * @Author Summer
 * @Date 2019/11/20 13:45
 */
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    // 调度中心部署跟地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
    private String adminAddresses;

    // 执行器通讯TOKEN [选填]：非空时启用；
    private String accessToken = "6ecf6dc99546d18cc9081cefa5f881d9";

    @NestedConfigurationProperty
    private XxlJobExecutorProperties executor = new XxlJobExecutorProperties();

    public String getAdminAddresses() {
        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public XxlJobExecutorProperties getExecutor() {
        return executor;
    }

    public void setExecutor(XxlJobExecutorProperties executor) {
        this.executor = executor;
    }
}
