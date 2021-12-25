package com.lmf.properties;

import com.lmf.constant.FileConst;
import com.lmf.utils.FilenameUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;


/**
 * Local File properties.
 *
 * @author lmf
 * @date 2021-12-25
 */
@Data
@ConfigurationProperties("attachment.local")
public class LocalProperties {

    /**
     * 文件目的路径
     */
    private String workDir = FilenameUtils.ensureSuffix(System.getProperty("user.home"), FileConst.URL_SEPARATOR) + FileConst.URL_SOURCE + FileConst.URL_SEPARATOR;


    /**
     * 文件子路径
     */
    private String uploadUrlPrefix = "upload";

    /**
     * 下载超时时间
     */
    private Duration downloadTimeout = Duration.ofSeconds(30);

    /**
     * cache store impl
     * memory
     * level
     */
    private String cache = "memory";
}
