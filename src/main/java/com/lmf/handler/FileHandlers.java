package com.lmf.handler;

import com.lmf.entity.UploadResult;
import com.lmf.enums.AttachmentType;
import com.lmf.exception.FileOperationException;
import com.lmf.exception.RepeatTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import com.lmf.properties.AttachmentTypeProperties;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lmf.
 * @title: FileHandlers
 * @description: TODO
 * @date 2021/12/25
 */
@Slf4j
public class FileHandlers {

    private AttachmentType attachmentType;

    /**
     * File com.lmf.handler container.
     */
    private final ConcurrentHashMap<AttachmentType, FileHandler> fileHandlers =
            new ConcurrentHashMap<>(16);

    public FileHandlers(ApplicationContext applicationContext, AttachmentTypeProperties attachmentTypeProperties) {
        this.attachmentType = attachmentTypeProperties.getType();
        // Add all file com.lmf.handler
        addFileHandlers(applicationContext.getBeansOfType(FileHandler.class).values());
        log.info("Registered {} file com.lmf.handler(s)", fileHandlers.size());
    }


    /**
     * Uploads files.
     *
     * @param file multipart file must not be null
     * @return upload result
     * @throws FileOperationException throws when fail to delete attachment or no available file
     *                                com.lmf.handler to upload it
     */
    @NonNull
    public UploadResult upload(@NonNull MultipartFile file) {
        return getSupportedType().upload(file);
    }

    /**
     * Deletes attachment.
     *
     * @param fileKey attachment detail must not be null
     * @throws FileOperationException throws when fail to delete attachment or no available file
     *                                com.lmf.handler to delete it
     */
    public void delete(@NonNull String fileKey) {
        Assert.notNull(fileKey, "fileKey must not be null");
        getSupportedType()
                .delete(fileKey);
    }

    /**
     * Adds file handlers.
     *
     * @param fileHandlers file com.lmf.handler collection
     * @return current file handlers
     */
    @NonNull
    public FileHandlers addFileHandlers(@Nullable Collection<FileHandler> fileHandlers) {
        if (!CollectionUtils.isEmpty(fileHandlers)) {
            for (FileHandler handler : fileHandlers) {
                if (this.fileHandlers.containsKey(handler.getAttachmentType())) {
                    throw new RepeatTypeException("Same attachment type implements must be unique");
                }
                this.fileHandlers.put(handler.getAttachmentType(), handler);
            }
        }
        return this;
    }

    private FileHandler getSupportedType() {
        FileHandler handler =
                fileHandlers.getOrDefault(attachmentType, fileHandlers.get(AttachmentType.LOCAL));
        if (handler == null) {
            throw new FileOperationException("No available file handlers to operate the file")
                    .setErrorData(attachmentType);
        }
        return handler;
    }
}
