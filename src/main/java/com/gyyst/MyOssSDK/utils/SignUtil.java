package com.gyyst.MyOssSDK.utils;

import cn.hutool.crypto.SecureUtil;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/2/3 17:21
 */

public class SignUtil {
    public static String getSign(String accessKey, String secretKey) {
        String s = SecureUtil.md5(accessKey + secretKey);
        return s;
    }
}
