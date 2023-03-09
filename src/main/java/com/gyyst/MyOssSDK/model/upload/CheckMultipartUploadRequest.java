package com.gyyst.MyOssSDK.model.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/1/9 20:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckMultipartUploadRequest {

    private String bucketName;
    private String fileName;
    private String uploadId;
    private String identifier;

}
