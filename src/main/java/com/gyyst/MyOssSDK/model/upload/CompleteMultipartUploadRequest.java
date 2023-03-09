package com.gyyst.MyOssSDK.model.upload;

import lombok.Builder;
import lombok.Data;

/**
 * @author winterchen
 * @version 1.0
 * @date 2022/4/21 10:57
 **/
@Data
@Builder
public class CompleteMultipartUploadRequest {
    private String bucketName;
    private String fileName;
    private String uploadId;
    private String identifier;
    private Integer chunkNum;
    private Long fileSize;
    private String contentType;
}
