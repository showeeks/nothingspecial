package cn.xiaoheiban.pdfmaker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("aliyun.oss")
@Data
public class AliyunOssProperties {
    private String accessId;
    private String accessKey;
    private String bucket;
    private String endpoint;
    private String dir;
    private Integer maxSize = Integer.valueOf(1);
    private Integer expire = Integer.valueOf(30);
    private boolean secure = false;
    private String roleSessionName;

    public AliyunOssProperties() {
    }


}
