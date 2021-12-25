package com.lmf.entity;


import lombok.Data;
import lombok.ToString;
import org.springframework.http.MediaType;

/**
 * @author lmf.
 * @title: UploadResult
 * @description: TODO
 * @date 2021/12/25
 */
@Data
@ToString
public class UploadResult {

    private String filename;

    private String filePath;

    private String key;

    private String thumbPath;

    private String suffix;

    private MediaType mediaType;

    private Long size;

    private Integer width;

    private Integer height;
}
