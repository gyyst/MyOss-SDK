package com.gyyst.MyOssSDK.model.backup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/2/25 23:29
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BackupFileRequest {
    /**
     * 存储桶名称
     */
    private String bucketName;
    /**
     * 文件路径 以/开头,以/结尾,根目录为/
     */
    private String path;
    /**
     * 文件名
     */
    private String fileName;


}
