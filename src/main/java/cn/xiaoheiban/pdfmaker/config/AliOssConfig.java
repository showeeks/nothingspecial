package cn.xiaoheiban.pdfmaker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: silen
 * @date:2019/7/21
 */
@Configuration
@ConfigurationProperties("aliyun.oss")
@Data
public class AliOssConfig {
    private String accessId;

    private String accessKey;

    private String bucket;

    private String endpoint;

    private String dir;

    private Integer maxSize;

    private Integer expire;

    private boolean secure;

    private String roleSessionName;
}
