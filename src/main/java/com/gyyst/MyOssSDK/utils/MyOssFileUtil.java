package com.gyyst.MyOssSDK.utils;

import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/2/3 17:22
 */

public class MyOssFileUtil {
    public static String getFileMD5(File file) {
        try {
            String md5DigestAsHex = DigestUtils.md5DigestAsHex(Files.newInputStream(file.toPath()));
            return md5DigestAsHex;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileMD5(MultipartFile multipartFile) {
        try {
            String md5DigestAsHex = DigestUtils.md5DigestAsHex(multipartFile.getInputStream());
            return md5DigestAsHex;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
