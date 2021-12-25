package com.lmf.handler;

import com.lmf.constant.FileConst;
import com.lmf.entity.UploadResult;
import com.lmf.enums.AttachmentType;
import com.lmf.exception.FileOperationException;
import com.lmf.properties.HuaweiObsProperties;
import com.lmf.utils.ImageUtils;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;


/**
 * Huawei obs file handler.
 *
 * @author lmf
 * @date 2021-12-25
 */
@Slf4j
public class HuaweiObsFileHandler implements FileHandler {

    private HuaweiObsProperties huaweiObsProperties;

    public HuaweiObsFileHandler(HuaweiObsProperties huaweiObsProperties) {
        this.huaweiObsProperties = huaweiObsProperties;
    }

    @Override
    public @NonNull
    UploadResult upload(@NonNull MultipartFile file) {
        Assert.notNull(file, "Multipart file must not be null");

        // Get config
        String protocol = huaweiObsProperties.getObs_huawei_domain_protocol();
        String domain = huaweiObsProperties.getObs_huawei_domain();
        String source = huaweiObsProperties.getObs_huawei_source();
        String endPoint = huaweiObsProperties.getObs_huawei_endpoint();
        String accessKey = huaweiObsProperties.getObs_huawei_access_key();
        String accessSecret = huaweiObsProperties.getObs_huawei_access_secret();
        String bucketName = huaweiObsProperties.getObs_huawei_bucket_name();
        String styleRule = huaweiObsProperties.getObs_huawei_style_rule();
        String thumbnailStyleRule = huaweiObsProperties.getObs_huawei_thumbnail_style_rule();

        // Init OSS client
        final ObsClient obsClient = new ObsClient(accessKey, accessSecret, endPoint);

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
            FilePathDescriptor pathDescriptor = new FilePathDescriptor.Builder()
                    .setBasePath(basePath.toString())
                    .setSubPath(source)
                    .setAutomaticRename(true)
                    .setRenamePredicate(x -> true)
                    .setOriginalName(file.getOriginalFilename())
                    .build();

            log.info(basePath.toString());

            // Upload
            PutObjectResult putObjectResult =
                    obsClient.putObject(bucketName, pathDescriptor.getRelativePath(),
                            file.getInputStream());
            if (putObjectResult == null) {
                throw new FileOperationException("上传附件 " + file.getOriginalFilename() + " 到华为云失败 ");
            }

            // Response result
            UploadResult uploadResult = new UploadResult();
            uploadResult.setFilename(pathDescriptor.getName());
            String fullPath = pathDescriptor.getFullPath();
            uploadResult
                    .setFilePath(StringUtils.isBlank(styleRule) ? fullPath : fullPath + styleRule);
            uploadResult.setKey(pathDescriptor.getRelativePath());
            uploadResult
                    .setMediaType(MediaType.valueOf(Objects.requireNonNull(file.getContentType())));
            uploadResult.setSuffix(pathDescriptor.getExtension());
            uploadResult.setSize(file.getSize());

            handleImageMetadata(file, uploadResult, () -> {
                if (ImageUtils.EXTENSION_ICO.equals(pathDescriptor.getExtension())) {
                    return fullPath;
                } else {
                    return StringUtils.isBlank(thumbnailStyleRule) ? fullPath :
                            fullPath + thumbnailStyleRule;
                }
            });

            log.info("Uploaded file: [{}] successfully", file.getOriginalFilename());
            return uploadResult;
        } catch (Exception e) {
            throw new FileOperationException("上传附件 " + file.getOriginalFilename() + " 到华为云失败 ", e)
                    .setErrorData(file.getOriginalFilename());
        } finally {
            try {
                obsClient.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void delete(@NonNull String key) {
        Assert.notNull(key, "File key must not be blank");

        // Get config
        String endPoint = huaweiObsProperties.getObs_huawei_endpoint();
        String accessKey = huaweiObsProperties.getObs_huawei_access_key();
        String accessSecret = huaweiObsProperties.getObs_huawei_access_secret();
        String bucketName = huaweiObsProperties.getObs_huawei_bucket_name();

        // Init OSS client
        final ObsClient obsClient = new ObsClient(accessKey, accessSecret, endPoint);

        try {
            obsClient.deleteObject(bucketName, key);
        } catch (Exception e) {
            throw new FileOperationException("附件 " + key + " 从华为云删除失败", e);
        } finally {
            try {
                obsClient.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.HUAWEIOBS;
    }

}
