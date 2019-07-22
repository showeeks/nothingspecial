package cn.xiaoheiban.pdfmaker.component;

import cn.xiaoheiban.pdfmaker.config.AliOssProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author: silen
 * @date:2019/7/21
 */
@Component
public class AliOssComponent {

    private final AliOssProperties aliOssProperties;

    public AliOssComponent(AliOssProperties aliOssProperties) {
        this.aliOssProperties = aliOssProperties;
    }

    public String upload(File file, String filename) {
        String endpoint = aliOssProperties.getEndpoint();
        String accessKeyId = aliOssProperties.getAccessId();
        String accessKeySecret = aliOssProperties.getAccessKey();
        String bucketName = aliOssProperties.getBucket();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, aliOssProperties.getDir() + filename, file);
        ossClient.shutdown();
        return filename;
    }
}
