package cn.xiaoheiban.pdfmaker.controller;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import cn.xiaoheiban.pdfmaker.component.AliOssComponent;
import cn.xiaoheiban.pdfmaker.pdf.FileMaker;
import cn.xiaoheiban.pdfmaker.execption.StorageFileNotFoundException;
import cn.xiaoheiban.pdfmaker.service.StorageService;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RestController
public class ConvertController {

    private final StorageService storageService;

    private final AliOssComponent ossUtils;

    @Autowired
    public ConvertController(StorageService storageService, AliOssComponent ossComponent) {
        this.storageService = storageService;
        this.ossUtils = ossComponent;
    }

    @GetMapping("/")
    @ResponseBody
    public List<String> listUploadedFiles() {
        return storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(ConvertController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ResponseBody
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam(name = "redirect-url", required = false) String redirectUrl,
                                   RedirectAttributes redirectAttributes) {
        String filename = "";
        filename = FileMaker.filename(8) + ".pdf";
        try {
            storageService.store(file);
            uploadFile(file.getOriginalFilename(), filename, redirectUrl);
        } catch (Exception e) {
            System.out.println(e);
        }
        return filename;
    }

    @Async
    void uploadFile(String originalName, String genName, String callbackUrl) throws Exception {
        FileMaker fileMaker = new FileMaker("upload-dir/" + originalName, "water.png");
        fileMaker.generatePDF(genName);
        ossUtils.upload(new File("generate-dir/" + genName), genName);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(callbackUrl);
        httpClient.execute(post);
        httpClient.close();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}