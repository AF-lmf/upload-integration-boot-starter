package com.lmf.handler;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.lmf.constant.FileConst;
import com.lmf.entity.UploadResult;
import com.lmf.enums.AttachmentType;
import com.lmf.exception.FileOperationException;
import com.lmf.properties.AliOssProperties;
import com.lmf.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;


/**
 * Ali oss file handler.
 *
 * @author MyFaith
 * @author ryanwang
 * @author guqing
 * @date 2019-04-04
 */
@Slf4j
@Component
public class AliOssFileHandler implements FileHandler {

    private AliOssProperties aliOssProperties;

    public AliOssFileHandler(AliOssProperties aliOssProperties) {
        this.aliOssProperties = aliOssProperties;
    }

    @Override
    public @NonNull
    UploadResult upload(@NonNull MultipartFile file) {
        Assert.notNull(file, "Multipart file must not be null");

        // Get config
        String protocol = aliOssProperties.getOss_ali_domain_protocol();
        String domain = aliOssProperties.getOss_ali_domain();
        String source = aliOssProperties.getOss_ali_source();
        String endPoint = aliOssProperties.getOss_ali_endpoint();
        String accessKey = aliOssProperties.getOss_ali_access_key();
        String accessSecret = aliOssProperties.getOss_ali_access_secret();
        String bucketName = aliOssProperties.getOss_ali_bucket_name();
        String styleRule = aliOssProperties.getOss_ali_style_rule();
        String thumbnailStyleRule = aliOssProperties.getOss_ali_thumbnail_style_rule();

        // Init OSS client
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKey, accessSecret);

        StringBuilder basePath = new StringBuilder(protocol);

        if (StringUtils.isNotEmpty(domain)) {
            basePath.append(domain)
                    .append(FileConst.URL_SEPARATOR);
        } else {
            basePath.append(bucketName)
                    .append(".")
                    .append(endPoint)
                    .append(FileConst.URL_SEPARATOR);
        }

        try {
            FilePathDescriptor uploadFilePath = new FilePathDescriptor.Builder()
                    .setBasePath(basePath.toString())
                    .setSubPath(source)
                    .setAutomaticRename(true)
                    .setRenamePredicate(x -> true)
                    .setOriginalName(file.getOriginalFilename())
                    .build();

            log.info(basePath.toString());

            // Upload
            final PutObjectResult putObjectResult = ossClient.putObject(bucketName,
                    uploadFilePath.getRelativePath(),
                    file.getInputStream());

            if (putObjectResult == null) {
                throw new FileOperationException("上传附件 " + file.getOriginalFilename() + " 到阿里云失败 ");
            }

            // Response result
            final UploadResult uploadResult = new UploadResult();
            uploadResult.setFilename(uploadFilePath.getName());
            String fullPath = uploadFilePath.getFullPath();
            uploadResult
                    .setFilePath(StringUtils.isBlank(styleRule) ? fullPath : fullPath + styleRule);
            uploadResult.setKey(uploadFilePath.getRelativePath());
            uploadResult
                    .setMediaType(MediaType.valueOf(Objects.requireNonNull(file.getContentType())));
            uploadResult.setSuffix(uploadFilePath.getExtension());
            uploadResult.setSize(file.getSize());

            handleImageMetadata(file, uploadResult, () -> {
                if (ImageUtils.EXTENSION_ICO.equals(uploadFilePath.getExtension())) {
                    return fullPath;
                } else {
                    return StringUtils.isBlank(thumbnailStyleRule) ? fullPath :
                            fullPath + thumbnailStyleRule;
                }
            });

            log.info("Uploaded file: [{}] successfully", file.getOriginalFilename());
            return uploadResult;
        } catch (Exception e) {
            throw new FileOperationException("上传附件 " + file.getOriginalFilename() + " 到阿里云失败 ", e)
                    .setErrorData(file.getOriginalFilename());
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void delete(@NonNull String key) {
        Assert.notNull(key, "File key must not be blank");

        // Get config
        String endPoint = aliOssProperties.getOss_ali_endpoint();
        String accessKey = aliOssProperties.getOss_ali_access_key();
        String accessSecret = aliOssProperties.getOss_ali_access_secret();
        String bucketName = aliOssProperties.getOss_ali_bucket_name();

        // Init OSS client
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKey, accessSecret);

        try {
            ossClient.deleteObject(new DeleteObjectsRequest(bucketName).withKey(key));
        } catch (Exception e) {
            throw new FileOperationException("附件 " + key + " 从阿里云删除失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.ALIOSS;
    }

}
