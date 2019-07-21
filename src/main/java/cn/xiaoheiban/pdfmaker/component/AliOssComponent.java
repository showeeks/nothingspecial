package cn.xiaoheiban.pdfmaker.component;

import cn.xiaoheiban.pdfmaker.config.AliOssConfig;
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

    private final AliOssConfig aliOssConfig;

    public AliOssComponent(AliOssConfig aliOssConfig) {
        this.aliOssConfig = aliOssConfig;
    }

    @Async
    public String upload(File file, String filename) {
        String endpoint = aliOssConfig.getEndpoint();
        String accessKeyId = aliOssConfig.getAccessId();
        String accessKeySecret = aliOssConfig.getAccessKey();
        String bucketName = aliOssConfig.getBucket();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, aliOssConfig.getDir() + filename, file);
        ossClient.shutdown();
        return filename;
    }
}
