package com.gyyst.MyOssSDK.client;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.gyyst.MyOssSDK.common.BaseResponse;
import com.gyyst.MyOssSDK.model.Auth;
import com.gyyst.MyOssSDK.model.FileInfo;
import com.gyyst.MyOssSDK.model.backup.BackupFileRequest;
import com.gyyst.MyOssSDK.model.backup.DeleteBackupFileRequest;
import com.gyyst.MyOssSDK.model.backup.RecoverBackupFileRequest;
import com.gyyst.MyOssSDK.model.delete.DeleteFileRequest;
import com.gyyst.MyOssSDK.model.download.GetDownloadURLRequest;
import com.gyyst.MyOssSDK.model.upload.CompleteMultipartUploadRequest;
import com.gyyst.MyOssSDK.model.upload.MultipartUploadCreateRequest;
import com.gyyst.MyOssSDK.model.upload.MultipartUploadCreateResponse;
import com.gyyst.MyOssSDK.utils.HttpUtil;
import com.gyyst.MyOssSDK.utils.MyOssFileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * @author gyyst
 * @Description
 * @Create by 2023/2/3 19:10
 */

@Component
@Data
@AllArgsConstructor
public class MyOssClient {
    private static final Long chunkSize = 5 * 1024 * 1024L;
    private Auth auth;


    public BaseResponse uploadFileByForm(FileInfo fileInfo, MultipartFile file) {
        String fileInfoJson = JSONUtil.toJsonStr(fileInfo);
        HttpResponse httpResponse = HttpUtil.postForm("/client/upload/mul", Auth.headers, file, fileInfoJson);
        BaseResponse baseResponse = JSONUtil.toBean(httpResponse.body(), BaseResponse.class);
        return baseResponse;
    }

    public BaseResponse uploadFileByForm(FileInfo fileInfo, File file) {
        String fileInfoJson = JSONUtil.toJsonStr(fileInfo);
        HttpResponse httpResponse = HttpUtil.postForm("/client/upload/mul", Auth.headers, file, fileInfoJson);
        BaseResponse baseResponse = JSONUtil.toBean(httpResponse.body(), BaseResponse.class);
        return baseResponse;
    }

    public BaseResponse uploadFileBySlice(FileInfo fileInfo, File file) throws IOException {
        //请求创建文件分片上传任务
        String identifier = MyOssFileUtil.getFileMD5(file);
        MultipartUploadCreateRequest createRequest = MultipartUploadCreateRequest.builder()
                .identifier(identifier)
                .totalSize(file.length())
                .chunkNum((int) (file.length() / chunkSize + 1))
                .chunkSize(chunkSize)
                .fileName(fileInfo.getFileName())
                .bucketName(fileInfo.getBucketName())
                .build();
        String body = JSONUtil.toJsonStr(createRequest);
        HttpResponse createResponse = HttpUtil.post("/client/upload/create", Auth.headers, body);
        BaseResponse createBaseResponse = JSONUtil.toBean(createResponse.body(), BaseResponse.class);
        if (createBaseResponse.getCode() != 0) {
            return createBaseResponse;
        }
        //文件分片上传
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        byte[] buffer = new byte[Math.toIntExact(chunkSize)];
        MultipartUploadCreateResponse multipartUploadCreateResponse = JSONUtil.parse(createBaseResponse.getData()).toBean(MultipartUploadCreateResponse.class);
        List<MultipartUploadCreateResponse.UploadCreateItem> chunks = multipartUploadCreateResponse.getChunks();
        for (int i = 0; i < chunks.size(); i++) {
            Integer partNumber = chunks.get(i).getPartNumber();
            String uploadUrl = chunks.get(i).getUploadUrl();
            long start = (partNumber - 1) * chunkSize;
            if (file.length() - start < chunkSize) {
                buffer = new byte[(int) (file.length() - start)];
            }
            try {
                randomAccessFile.seek(start);
                int read = randomAccessFile.read(buffer);
            } catch (IOException e) {
                randomAccessFile.close();
                throw new RuntimeException(e);
            }
            Response httpResponse = HttpUtil.putFileSlice(uploadUrl, buffer);
            if (!httpResponse.isSuccessful()) {
                i--;
            }
        }
        randomAccessFile.close();
        //分片上传完成，请求合并分片
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .uploadId(multipartUploadCreateResponse.getUploadId())
                .fileSize(file.length())
                .chunkNum(Math.toIntExact(file.length() / chunkSize + 1))
                .fileName(fileInfo.getFileName())
                .bucketName(fileInfo.getBucketName())
                .identifier(identifier)
                .contentType(new MimetypesFileTypeMap().getContentType(file))
                .build();
        HttpResponse completeResponse = HttpUtil.post("/client/upload/complete", Auth.headers, JSONUtil.toJsonStr(completeMultipartUploadRequest));
        BaseResponse completeBaseResponse = JSONUtil.toBean(completeResponse.body(), BaseResponse.class);
        if (completeBaseResponse.getCode() != 0) {
            return completeBaseResponse;
        }
        return BaseResponse.success("上传成功");
    }

    public File downloadFile(FileInfo fileInfo, Integer expireTime) {
        return new File((String) getDownloadFileURL(fileInfo, expireTime).getData());
    }

    public BaseResponse getDownloadFileURL(FileInfo fileInfo, Integer expireTime) {
        if (ObjectUtil.isNull(fileInfo) || StrUtil.isBlank(fileInfo.getBucketName()) || StrUtil.isBlank(fileInfo.getFileName())) {
            throw new NullPointerException("fileInfo is empty");
        }
        String fileName = fileInfo.getFileName();
        int lastIndexOf = fileName.lastIndexOf("/");
        GetDownloadURLRequest getDownloadURLRequest = GetDownloadURLRequest.builder()
                .bucketName(fileInfo.getBucketName())
                .path(fileName.substring(0, lastIndexOf))
                .fileName(fileName.substring(lastIndexOf + 1))
                .expireTime(expireTime)
                .build();
        String body = JSONUtil.toJsonStr(getDownloadURLRequest);
        HttpResponse httpResponse = HttpUtil.post("/client/download", Auth.headers, body);
        BaseResponse completeBaseResponse = JSONUtil.toBean(httpResponse.body(), BaseResponse.class);
        return completeBaseResponse;
    }

    public BaseResponse deleteFile(FileInfo fileInfo) {
        if (ObjectUtil.isNull(fileInfo) || StrUtil.isBlank(fileInfo.getBucketName()) || StrUtil.isBlank(fileInfo.getFileName())) {
            throw new NullPointerException("fileInfo is empty");
        }
        DeleteFileRequest deleteFileRequest = DeleteFileRequest.builder()
                .bucketName(fileInfo.getBucketName())
                .fileName(fileInfo.getFileName())
                .build();
        String body = JSONUtil.toJsonStr(deleteFileRequest);
        HttpResponse httpResponse = HttpUtil.post("/client/delete", Auth.headers, body);
        BaseResponse deleteBaseResponse = JSONUtil.toBean(httpResponse.body(), BaseResponse.class);
        return deleteBaseResponse;
    }

    public BaseResponse backupFile(FileInfo fileInfo) {
        if (ObjectUtil.isNull(fileInfo)) {
            throw new NullPointerException("fileInfo is empty");
        }
        String bucketName = fileInfo.getBucketName();
        String fileName = fileInfo.getFileName();
        if (StrUtil.isBlank(bucketName) || StrUtil.isBlank(fileName)) {
            throw new NullPointerException("fileInfo is empty");
        }

        BackupFileRequest backupFileRequest = BackupFileRequest.builder()
                .bucketName(bucketName)
                .fileName(fileName)
                .build();
        String body = JSONUtil.toJsonStr(backupFileRequest);
        HttpResponse httpResponse = HttpUtil.post("/client/backup/create", Auth.headers, body);
        BaseResponse deleteBaseResponse = JSONUtil.toBean(httpResponse.body(), BaseResponse.class);
        return deleteBaseResponse;
    }

    public BaseResponse delBackupFile(FileInfo fileInfo) {
        if (ObjectUtil.isNull(fileInfo)) {
            throw new NullPointerException("fileInfo is empty");
        }
        String bucketName = fileInfo.getBucketName();
        String fileName = fileInfo.getFileName();
        if (StrUtil.isBlank(bucketName) || StrUtil.isBlank(fileName)) {
            throw new NullPointerException("fileInfo is empty");
        }

        DeleteBackupFileRequest deleteBackupFileRequest = DeleteBackupFileRequest.builder()
                .bucketName(fileInfo.getBucketName())
                .fileName(fileInfo.getFileName())
                .build();
        String body = JSONUtil.toJsonStr(deleteBackupFileRequest);
        HttpResponse httpResponse = HttpUtil.post("/client/backup/delete", Auth.headers, body);
        BaseResponse deleteBaseResponse = JSONUtil.toBean(httpResponse.body(), BaseResponse.class);
        return deleteBaseResponse;
    }

    public BaseResponse recoverBackupFile(FileInfo fileInfo) {
        if (ObjectUtil.isNull(fileInfo)) {
            throw new NullPointerException("fileInfo is empty");
        }
        String bucketName = fileInfo.getBucketName();
        String fileName = fileInfo.getFileName();
        if (StrUtil.isBlank(bucketName) || StrUtil.isBlank(fileName)) {
            throw new NullPointerException("fileInfo is empty");
        }
        RecoverBackupFileRequest recoverBackupFileRequest = RecoverBackupFileRequest.builder()
                .bucketName(fileInfo.getBucketName())
                .fileName(fileInfo.getFileName())
                .build();
        String body = JSONUtil.toJsonStr(recoverBackupFileRequest);
        HttpResponse httpResponse = HttpUtil.post("/client/backup/recover", Auth.headers, body);
        BaseResponse deleteBaseResponse = JSONUtil.toBean(httpResponse.body(), BaseResponse.class);
        return deleteBaseResponse;
    }
}
