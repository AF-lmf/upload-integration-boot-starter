package com.lmf.handler;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.lmf.constant.FileConst;
import com.lmf.entity.UploadResult;
import com.lmf.enums.AttachmentType;
import com.lmf.exception.FileOperationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import com.lmf.properties.TencentCosProperties;
import com.lmf.utils.ImageUtils;

import java.util.Objects;


/**
 * Tencent cos file com.lmf.handler.
 *
 * @author lmf
 * @date 2021-12-25
 */
@Slf4j
public class TencentCosFileHandler implements FileHandler {

    private TencentCosProperties tencentCosProperties;

    public TencentCosFileHandler(TencentCosProperties tencentCosProperties) {
        this.tencentCosProperties = tencentCosProperties;
    }

    @Override
    public UploadResult upload(MultipartFile file) {
        Assert.notNull(file, "Multipart file must not be null");

        // Get config
        String protocol = tencentCosProperties.getCos_tencent_domain_protocol();
        String domain = tencentCosProperties.getCos_tencent_domain();
        String region = tencentCosProperties.getCos_tencent_region();
        String secretId = tencentCosProperties.getCos_tencent_secret_id();
        String secretKey = tencentCosProperties.getCos_tencent_secret_key();
        String bucketName = tencentCosProperties.getCos_tencent_bucket_name();
        String source = tencentCosProperties.getCos_tencent_source();
        String styleRule = tencentCosProperties.getCos_tencent_style_rule();
        String thumbnailStyleRule = tencentCosProperties.getCos_tencent_thumbnail_style_rule();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region regionConfig = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionConfig);

        // Init OSS client
        COSClient cosClient = new COSClient(cred, clientConfig);

        StringBuilder basePath = new StringBuilder(protocol);

        if (StringUtils.isNotEmpty(domain)) {
            basePath.append(domain)
                    .append(FileConst.URL_SEPARATOR);
        } else {
            basePath.append(bucketName)
                    .append(".cos.")
                    .append(region)
                    .append(".myqcloud.com")
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

            // Upload
            ObjectMetadata objectMetadata = new ObjectMetadata();
            //提前告知输入流的长度, 否则可能导致 oom
            objectMetadata.setContentLength(file.getSize());
            // 设置 Content type, 默认是 application/octet-stream
            objectMetadata.setContentType(file.getContentType());
            PutObjectResult putObjectResponseFromInputStream = cosClient
                    .putObject(bucketName, pathDescriptor.getRelativePath(), file.getInputStream(),
                            objectMetadata);
            if (putObjectResponseFromInputStream == null) {
                throw new FileOperationException("上传附件 " + file.getOriginalFilename() + " 到腾讯云失败 ");
            }
            String fullPath = pathDescriptor.getFullPath();
            // Response result
            UploadResult uploadResult = new UploadResult();
            uploadResult.setFilename(pathDescriptor.getName());
            uploadResult
                    .setFilePath(StringUtils.isBlank(styleRule) ? fullPath : fullPath + styleRule);
            uploadResult.setKey(pathDescriptor.getRelativePath());
            uploadResult
                    .setMediaType(MediaType.valueOf(Objects.requireNonNull(file.getContentType())));
            uploadResult.setSuffix(pathDescriptor.getExtension());
            uploadResult.setSize(file.getSize());

            // Handle thumbnail
            handleImageMetadata(file, uploadResult, () -> {
                if (ImageUtils.EXTENSION_ICO.equals(pathDescriptor.getExtension())) {
                    uploadResult.setThumbPath(fullPath);
                    return fullPath;
                } else {
                    return StringUtils.isBlank(thumbnailStyleRule) ? fullPath :
                            fullPath + thumbnailStyleRule;
                }
            });
            return uploadResult;
        } catch (Exception e) {
            throw new FileOperationException("附件 " + file.getOriginalFilename() + " 上传失败(腾讯云)", e);
        } finally {
            cosClient.shutdown();
        }
    }

    @Override
    public void delete(String key) {
        Assert.notNull(key, "File key must not be blank");

        // Get config
        String region = tencentCosProperties.getCos_tencent_region();
        String secretId = tencentCosProperties.getCos_tencent_secret_id();
        String secretKey = tencentCosProperties.getCos_tencent_secret_key();
        String bucketName = tencentCosProperties.getCos_tencent_bucket_name();

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region regionConfig = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionConfig);

        // Init OSS client
        COSClient cosClient = new COSClient(cred, clientConfig);

        try {
            cosClient.deleteObject(bucketName, key);
        } catch (Exception e) {
            throw new FileOperationException("附件 " + key + " 从腾讯云删除失败", e);
        } finally {
            cosClient.shutdown();
        }
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.TENCENTCOS;
    }
}
