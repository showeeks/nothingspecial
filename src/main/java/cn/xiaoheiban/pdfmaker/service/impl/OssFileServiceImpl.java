package cn.xiaoheiban.pdfmaker.service.impl;

import cn.xiaoheiban.pdfmaker.component.AliOssComponent;
import cn.xiaoheiban.pdfmaker.storage.StorageProperties;
import cn.xiaoheiban.pdfmaker.util.FileMakerUtil;
import cn.xiaoheiban.pdfmaker.service.OssFileService;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author: silen
 * @date:2019/7/21
 */
@Service
public class OssFileServiceImpl implements OssFileService {

    private final AliOssComponent ossComponent;

    private final StorageProperties storageProperties;

    @Autowired
    public OssFileServiceImpl(AliOssComponent ossComponent, StorageProperties storageProperties) {
        this.ossComponent = ossComponent;
        this.storageProperties = storageProperties;
    }

    @Async
    public void generateAndUpload(String originalName, String genName, String callbackUrl) throws Exception {
        FileMakerUtil fileMaker = new FileMakerUtil("upload-dir/" + originalName, "water.png");
        fileMaker.generatePDF(genName, storageProperties.getLocation() + "generate-dir/" + genName);
        ossComponent.upload(new File(storageProperties.getLocation() + "generate-dir/" + genName), genName);
        if (callbackUrl == null) {
            return;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(callbackUrl);
        httpClient.execute(post);
        httpClient.close();
    }
}
