package com.gyyst.MyOssSDK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/2/4 12:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    /**
     * 存储桶名称
     */
    String bucketName;
    /**
     * 文件名（包含路径）
     * 例如:
     * /hello.c
     * /java/hello.java
     */
    String fileName;
}
