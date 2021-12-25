package com.lmf.properties;

import com.lmf.constant.FileConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Huawei obs com.lmf.properties.
 *
 * @author lmf
 * @date 2021-12-25
 */
@Data
@ConfigurationProperties(prefix = "attachment.huawei-obs")
public class HuaweiObsProperties {

    /**
     * Huawei obs 协议类型 默认 https://
     */
    private String obs_huawei_domain_protocol = FileConst.PROTOCOL_HTTPS;

    /**
     * Huawei obs 域名
     */
    private String obs_huawei_domain;

    /**
     * Huawei obs 桶区域.
     */
    private String obs_huawei_endpoint;

    /**
     * Huawei obs 桶名称.
     */
    private String obs_huawei_bucket_name;

    /**
     * Huawei obs access key.
     */
    private String obs_huawei_access_key;

    /**
     * Huawei obs access secret.
     */
    private String obs_huawei_access_secret;

    /**
     * Huawei obs 子路径
     */
    private String obs_huawei_source = FileConst.URL_SOURCE;

    /**
     * Huawei obs 图片处理规则.
     */
    private String obs_huawei_style_rule;

    /**
     * Huawei obs 图片压缩规则.
     */
    private String obs_huawei_thumbnail_style_rule;

}
