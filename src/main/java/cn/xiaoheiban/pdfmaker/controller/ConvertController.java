package cn.xiaoheiban.pdfmaker.controller;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import cn.xiaoheiban.pdfmaker.util.FileMakerUtil;
import cn.xiaoheiban.pdfmaker.execption.StorageFileNotFoundException;
import cn.xiaoheiban.pdfmaker.service.FileSystemStorageService;
import cn.xiaoheiban.pdfmaker.service.OssFileService;
import com.aspose.words.Document;
import com.fasterxml.jackson.core.JsonParser;
import org.json.JSONObject;
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

    @ResponseBody
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam(name = "callback-url", required = false) String redirectUrl) {
        String filename;
        filename = FileMakerUtil.filename(8) + ".pdf";
        try {
            fileSystemStorageService.store(file);
            ossFileService.generateAndUpload(file.getOriginalFilename(), filename, redirectUrl);
        } catch (Exception e) {
            System.out.println(e);
        }
        return filename;
    }

    @ResponseBody
    @PostMapping("/replace")
    public String handleReplace(@RequestParam("file") MultipartFile file, @RequestParam(name = "callback-url", required = false) String redirectUrl, @RequestParam("replacement") String replacement) {
        String filename;
        String newfilename;
        filename = FileMakerUtil.filename(8) + ".docx";
        newfilename = FileMakerUtil.filename(8) + ".docx";
        List<String> values = new LinkedList<String>();
        JSONObject json = new JSONObject(replacement);
        Iterator<String> fieldIt = json.keys();
        List<String> fieldNames = new LinkedList<>();
        fieldIt.forEachRemaining(obj -> {
            values.add(json.getString(obj));
            fieldNames.add(obj);
        });
        try {
            fileSystemStorageService.store(file);
            Document doc = new Document("/tmp/pdfmake/upload-dir/" + file.getOriginalFilename());
            doc.getMailMerge().execute(fieldNames.toArray(new String[0]), values.toArray());
            doc.save("/tmp/pdfmake/generate-dir/" + newfilename);
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