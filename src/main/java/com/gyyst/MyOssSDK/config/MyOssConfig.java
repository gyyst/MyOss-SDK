package com.gyyst.MyOssSDK.config;

import com.gyyst.MyOssSDK.client.MyOssClient;
import com.gyyst.MyOssSDK.model.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/2/3 17:18
 */

@Configuration
@Data
@ConfigurationProperties("myoss.config")
@ComponentScan
public class MyOssConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public MyOssClient myOssClient() {
        Auth auth = Auth.create(accessKey, secretKey);
        return new MyOssClient(auth);
    }
}
