package cn.xiaoheiban.pdfmaker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;
import java.util.stream.Collectors;

import cn.xiaoheiban.pdfmaker.pdf.Maker;
import cn.xiaoheiban.pdfmaker.storage.StorageFileNotFoundException;
import cn.xiaoheiban.pdfmaker.storage.StorageService;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ConvertController {

    private final StorageService storageService;

    private final AliyunOssUtils ossUtils;

    @Autowired
    public ConvertController(StorageService storageService, AliyunOssUtils ossUtils) {
        this.storageService = storageService;
        this.ossUtils = ossUtils;
    }

    @GetMapping("/")
    @ResponseBody
    public List<String> listUploadedFiles(Model model) throws IOException {
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
        String filename = "Unsuccessful generation!";
        filename = Maker.filename(8) + ".pdf";
        try{
            storageService.store(file);
            uploadFile(file.getOriginalFilename(), filename, redirectUrl);
        } catch (Exception e) {
            System.out.println(e);
        }
        return filename;
    }

    @Async
    void uploadFile(String originalName, String genName, String callbackUrl) throws Exception {
        Maker maker = new Maker("upload-dir/" + originalName, "water.png");
        maker.generatePDF(genName);
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