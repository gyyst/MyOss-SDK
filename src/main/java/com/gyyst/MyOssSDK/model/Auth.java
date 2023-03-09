package com.gyyst.MyOssSDK.model;

import cn.hutool.core.util.StrUtil;
import com.gyyst.MyOssSDK.utils.SignUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/2/3 17:22
 */
@Data
@AllArgsConstructor
public class Auth {
    public static Map<String, String> headers = new HashMap<>();
    private String accessKey;
    private String sign;

    public static Auth create(String accessKey, String secretKey) {
        if (StrUtil.isBlank(accessKey) || StrUtil.isBlank(secretKey)) {
            throw new IllegalArgumentException("empty key");
        }
        String sign = SignUtil.getSign(accessKey, secretKey);
        headers.put("accessKey", accessKey);
        headers.put("sign", sign);
        return new Auth(accessKey, sign);
    }
}
