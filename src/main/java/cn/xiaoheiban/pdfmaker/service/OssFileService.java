package cn.xiaoheiban.pdfmaker.service;

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
    void uploadFile(String originalName, String genName, String callbackUrl) throws Exception;
}
