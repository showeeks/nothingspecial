package cn.xiaoheiban.pdfmaker.service;

import org.springframework.scheduling.annotation.Async;

/**
 * @author: silen
 * @date:2019/7/21
 */
public interface OssFileService {

    /**
     * 文件上传
     * @param originalName
     * @param genName
     * @param callbackUrl
     * @throws Exception
     */
    void generateAndUpload(String originalName, String genName, String callbackUrl) throws Exception;

    void replaceAndUpload(String originalName, String genName, String callbackUrl, String json) throws Exception;

    void insertAndUpload(String originalName, String genName, String callbackUrl, String json) throws Exception;
}
