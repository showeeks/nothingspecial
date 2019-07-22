package cn.xiaoheiban.pdfmaker;

import cn.xiaoheiban.pdfmaker.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class PdfmakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfmakerApplication.class, args);
    }

}
