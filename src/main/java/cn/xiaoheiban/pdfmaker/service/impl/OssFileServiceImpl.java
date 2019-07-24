package cn.xiaoheiban.pdfmaker.service.impl;

import cn.xiaoheiban.pdfmaker.component.AliOssComponent;
import cn.xiaoheiban.pdfmaker.config.StorageProperties;
import cn.xiaoheiban.pdfmaker.util.FileMakerUtil;
import cn.xiaoheiban.pdfmaker.service.OssFileService;
import com.aspose.words.Document;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
    @Override
    public void generateAndUpload(String originalName, String genName, String callbackUrl) throws Exception {
        FileMakerUtil fileMaker = new FileMakerUtil("upload-dir/" + originalName, "water.png");
        fileMaker.generatePDF(storageProperties.getLocation() + "generate-dir/" + genName);
        upload2Oss(genName, callbackUrl);
    }

    @Async
    @Override
    public void replaceAndUpload(String originalName, String genName, String callbackUrl, String json) throws Exception {
        List<String> values = new LinkedList<String>();
        JSONObject jsonObject = new JSONObject(json);
        Iterator<String> fieldIt = jsonObject.keys();
        List<String> fieldNames = new LinkedList<>();
        fieldIt.forEachRemaining(obj -> {
            values.add(jsonObject.getString(obj));
            fieldNames.add(obj);
        });
        try {
            Document doc = new Document(storageProperties.getLocation() + "upload-dir/" + originalName);
            doc.getMailMerge().execute(fieldNames.toArray(new String[0]), values.toArray());
            doc.save(storageProperties.getLocation() + "generate-dir/" + genName);
        } catch (Exception e) {
            System.out.println(e);
        }
        upload2Oss(genName, callbackUrl);
    }

    private void upload2Oss(String genName, String callbackUrl) throws IOException {
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
