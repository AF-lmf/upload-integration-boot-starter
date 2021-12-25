package com.lmf.config;

import com.lmf.handler.*;
import com.lmf.properties.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lmf.
 * @title: FileHandlerConfig
 * @description: TODO
 * @date 2021/12/25
 */
@Configuration
@EnableConfigurationProperties({AttachmentTypeProperties.class
        , TencentCosProperties.class
        , HuaweiObsProperties.class
        , LocalProperties.class
        , FastDfsProperties.class
        , AliOssProperties.class})
public class FileHandlerConfig {

    @Bean
    public FileHandlers getFileHandlers(ApplicationContext applicationContext, AttachmentTypeProperties attachmentTypeProperties) {
        FileHandlers fileHandlers = new FileHandlers(applicationContext, attachmentTypeProperties);
        return fileHandlers;
    }

    @Bean
    public LocalFileHandler getLocalFileHandler(LocalProperties localProperties) {
        return new LocalFileHandler(localProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "attachment", name = "type", havingValue = "tencentcos")
    public TencentCosFileHandler getTencentCosFileHandler(TencentCosProperties tencentCosProperties) {
        return new TencentCosFileHandler(tencentCosProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "attachment", name = "type", havingValue = "huaweiobs")
    public HuaweiObsFileHandler getTencentCosFileHandler(HuaweiObsProperties huaweiObsProperties) {
        return new HuaweiObsFileHandler(huaweiObsProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "attachment", name = "type", havingValue = "fastdfs")
    public FastDfsFileHandler getFastDfsHandler(FastDfsProperties fastDfsProperties) {
        return new FastDfsFileHandler(fastDfsProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "attachment", name = "type", havingValue = "alioss")
    public AliOssFileHandler getAliOssFileHandler(AliOssProperties aliOssProperties) {
        return new AliOssFileHandler(aliOssProperties);
    }


}
