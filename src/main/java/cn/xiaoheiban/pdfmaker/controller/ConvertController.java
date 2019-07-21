package cn.xiaoheiban.pdfmaker.controller;

import java.util.List;
import java.util.stream.Collectors;

import cn.xiaoheiban.pdfmaker.util.FileMakerUtil;
import cn.xiaoheiban.pdfmaker.execption.StorageFileNotFoundException;
import cn.xiaoheiban.pdfmaker.service.FileSystemStorageService;
import cn.xiaoheiban.pdfmaker.service.OssFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


@RestController
public class ConvertController {

    private final FileSystemStorageService fileSystemStorageService;


    private final OssFileService ossFileService;

    @Autowired
    public ConvertController(FileSystemStorageService fileSystemStorageService,
                             OssFileService ossFileService) {
        this.fileSystemStorageService = fileSystemStorageService;
        this.ossFileService = ossFileService;
    }

    @GetMapping("/")
    @ResponseBody
    public List<String> listUploadedFiles() {
        return fileSystemStorageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(ConvertController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileSystemStorageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ResponseBody
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam(name = "redirect-url", required = false) String redirectUrl) {
        String filename;
        filename = FileMakerUtil.filename(8) + ".pdf";
        try {
            fileSystemStorageService.store(file);
            ossFileService.uploadFile(file.getOriginalFilename(), filename, redirectUrl);
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