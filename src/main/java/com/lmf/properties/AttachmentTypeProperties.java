package com.lmf.properties;

import com.lmf.enums.AttachmentType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lmf.
 * @title: AttachmentTypeProterties
 * @description: TODO
 * @date 2021/12/25 1:25
 */
@Data
@ConfigurationProperties(prefix = "attachment")
public class AttachmentTypeProperties {

    /**
     * 文件存储类型
     */
    private AttachmentType type = AttachmentType.LOCAL;


}
