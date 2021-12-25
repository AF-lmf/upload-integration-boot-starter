package com.lmf.properties;

import com.lmf.constant.FileConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ali yun oss properties.
 *
 * @author MyFaith
 * @author ryanwang
 * @date 2019-04-04
 */
@Data
@ConfigurationProperties(prefix = "attachment.aliyunoss")
public class AliOssProperties {

    /**
     * Aliyun oss 协议类型 默认 https://
     */
    private String oss_ali_domain_protocol = FileConst.PROTOCOL_HTTPS;

    /**
     * Aliyun oss 域名
     */
    private String oss_ali_domain;

    /**
     * Aliyun oss 桶区域.
     */
    private String oss_ali_endpoint;

    /**
     * Aliyun oss 桶名称.
     */
    private String oss_ali_bucket_name;

    /**
     * Aliyun oss access key.
     */
    private String oss_ali_access_key;

    /**
     * Aliyun oss access secret.
     */
    private String oss_ali_access_secret;

    /**
     * Aliyun oss 子路径
     */
    private String oss_ali_source;

    /**
     * Aliyun oss 图片处理规则后缀.
     */
    private String oss_ali_style_rule;

    /**
     * Aliyun oss 图片压缩规则.
     */
    private String oss_ali_thumbnail_style_rule;

}
