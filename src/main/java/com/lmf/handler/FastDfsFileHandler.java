package com.lmf.handler;

import com.lmf.constant.FileConst;
import com.lmf.entity.UploadResult;
import com.lmf.enums.AttachmentType;
import com.lmf.exception.FileOperationException;
import com.lmf.properties.FastDfsProperties;
import com.lmf.properties.TencentCosProperties;
import com.lmf.utils.ImageUtils;
import com.lmf.utils.MultipartFileToFileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;


/**
 * Fast-DFS 处理器
 *
 * @author lmf.
 * @date 2019-07-25
 */
@Slf4j
public class FastDfsFileHandler implements FileHandler {

    private FastDfsProperties fastDfsProperties;

    public FastDfsFileHandler(FastDfsProperties fastDfsProperties) {
        this.fastDfsProperties = fastDfsProperties;
    }

    /**
     * 获取storageServer连接
     *
     * @return
     */
    private StorageClient1 getConnect() {
        StorageClient1 client = null;
        try {
            ClientGlobal.initByTrackers(fastDfsProperties.getFast_dfs_tracker_server());
            TrackerClient tracker = new TrackerClient();
            TrackerServer connection = tracker.getConnection();
            StorageServer storageServer = tracker.getStoreStorage(connection);
            client = new StorageClient1(connection, storageServer);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileOperationException("获取连接失败 (Fast-DFS)", e);
        } catch (MyException e) {
            log.error(e.getMessage(), e);
            throw new FileOperationException("获取连接失败 (Fast-DFS)", e);
        }
        return client;
    }

    @Override
    public UploadResult upload(MultipartFile file) {
        Assert.notNull(file, "Multipart file must not be null");
        StorageClient1 connect = this.getConnect();
        String filePath = "";
        try {
            File file1 = MultipartFileToFileUtil.multipartFileToFile(file);
            InputStream inputStream = new FileInputStream(file1);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);
            byte[] b = new byte[2048];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            inputStream.close();
            byte[] data = bos.toByteArray();
            bos.close();
            filePath = connect.upload_file1(data, file1.getName().split("\\.")[1], null);
            MultipartFileToFileUtil.delteTempFile(file1);
            // Response result
            UploadResult uploadResult = new UploadResult();
            uploadResult.setFilename(file.getOriginalFilename());
            uploadResult
                    .setFilePath(fastDfsProperties.getFast_dfs_resource_url() + FileConst.URL_SEPARATOR + filePath);
            uploadResult.setKey(filePath);
            uploadResult
                    .setMediaType(MediaType.valueOf(Objects.requireNonNull(file.getContentType())));
            uploadResult.setSuffix(file1.getName().split("\\.")[1]);
            uploadResult.setSize(file.getSize());
            return uploadResult;
        } catch (IOException | MyException e) {
            log.error(e.getMessage(), e);
            throw new FileOperationException("附件 " + file.getOriginalFilename() + " 上传失败(FastDfs)", e);
        } finally {
            try {
                connect.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void delete(String key) {
        StorageClient1 connect = this.getConnect();
        try {
            int i = connect.delete_file1(key);
            if (i > 0) {
                log.error("删除文件错误码：" + i);
            }
        } catch (IOException | MyException e) {
            log.error(e.getMessage());
            throw new FileOperationException("附件 " + key + " 从Fast-Dfs删除失败", e);
        } finally {
            try {
                connect.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.FASTDFS;
    }
}
