package cn.xiaoheiban.pdfmaker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import cn.xiaoheiban.pdfmaker.pdf.Maker;
import cn.xiaoheiban.pdfmaker.storage.StorageFileNotFoundException;
import cn.xiaoheiban.pdfmaker.storage.StorageService;
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
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        Maker maker = new Maker("upload-dir/" + file.getOriginalFilename(), "water.png");
        try {
            filename = maker.generatePDF();
            ossUtils.upload(new File("generate-dir/" + filename + ".pdf"), filename);
        } catch (Exception e) {
            System.out.println(e);
        }
        return filename;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}