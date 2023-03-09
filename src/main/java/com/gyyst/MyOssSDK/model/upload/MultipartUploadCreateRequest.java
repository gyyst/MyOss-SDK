package com.gyyst.MyOssSDK.model.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author winterchen
 * @version 1.0
 * @date 2022/4/21 9:44
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultipartUploadCreateRequest {
    private String bucketName;
    private String fileName;
    private String identifier;
    private Long totalSize;
    private Long chunkSize;
    private Integer chunkNum;
    private String uploadId;

}
