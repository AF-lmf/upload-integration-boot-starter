package com.lmf.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lmf.
 * @title: FastDfsProperties
 * @description: TODO
 * @date 2021/12/25 20:41
 */
@Data
@ConfigurationProperties(prefix = "attachment.fast-dfs")
public class FastDfsProperties {

    /**
     * FastDfs tracker-server地址
     */
    private String fast_dfs_tracker_server;
    /**
     * FastDfs 文件访问地址
     */
    private String fast_dfs_resource_url;


}
