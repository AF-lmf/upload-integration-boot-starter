package com.lmf.properties;

import com.lmf.constant.FileConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tencent cos com.lmf.properties.
 *
 * @author lmf
 * @date 2021-12-25
 */
@Data
@ConfigurationProperties(prefix = "attachment.tencent-cos")
public class TencentCosProperties {

    /**
     * Tencent cos 协议类型 默认 https://
     */
    private String cos_tencent_domain_protocol = FileConst.PROTOCOL_HTTPS;

    /**
     * Tencent cos 域名
     */
    private String cos_tencent_domain;

    /**
     * Tencent cos 桶区域.
     */
    private String cos_tencent_region;

    /**
     * Tencent cos 桶名称.
     */
    private String cos_tencent_bucket_name;

    /**
     * Tencent cos secret id.
     */
    private String cos_tencent_secret_id;

    /**
     * Tencent cos secret key.
     */
    private String cos_tencent_secret_key;

    /**
     * Tencent cos 子路径
     */
    private String cos_tencent_source = FileConst.URL_SOURCE;

    /**
     * Tencent cos 图片处理规则.
     */
    private String cos_tencent_style_rule;

    /**
     * Tencent cos thumbnail 图片压缩规则.
     */
    private String cos_tencent_thumbnail_style_rule;

}
