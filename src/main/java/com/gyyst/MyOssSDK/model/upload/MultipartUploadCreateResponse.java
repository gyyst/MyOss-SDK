package com.gyyst.MyOssSDK.model.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author winterchen
 * @version 1.0
 * @date 2022/4/21 9:40
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultipartUploadCreateResponse {

    private String uploadId;

    private List<UploadCreateItem> chunks;


    @Data
    public static class UploadCreateItem {

        private Integer partNumber;

        private String uploadUrl;

    }

}
