package com.gyyst.MyOssSDK.model.download;

import lombok.Builder;
import lombok.Data;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/1/13 12:40
 */

@Data
@Builder
public class GetDownloadURLRequest {
    private String bucketName;
    private String path;
    private String fileName;
    private Integer expireTime;

}
