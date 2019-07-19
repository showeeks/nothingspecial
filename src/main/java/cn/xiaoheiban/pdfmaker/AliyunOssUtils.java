package cn.xiaoheiban.pdfmaker;

import cn.xiaoheiban.pdfmaker.config.AliyunOssProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AliyunOssUtils {

    private final AliyunOssProperties aliyunOssProperties;

    public AliyunOssUtils(AliyunOssProperties aliyunOssProperties) {
        this.aliyunOssProperties = aliyunOssProperties;
    }

    @Async
    public String upload(File file, String filename) {
        String endpoint = aliyunOssProperties.getEndpoint();
        String accessKeyId = aliyunOssProperties.getAccessId();
        String accessKeySecret = aliyunOssProperties.getAccessKey();
        String bucketName = aliyunOssProperties.getBucket();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, filename, file);
        ossClient.shutdown();
        return filename;
    }
}
