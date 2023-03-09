package com.gyyst.MyOssSDK.model.delete;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/2/7 11:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFileRequest {
    private String bucketName;
    private String fileName;
}
