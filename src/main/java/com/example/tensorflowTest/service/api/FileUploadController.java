package com.example.tensorflowTest.service.api;

import com.example.tensorflowTest.service.classifier.ObjectDetector;
import com.example.tensorflowTest.service.exception.ServiceException;
import com.example.tensorflowTest.service.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class FileUploadController {
    private final StorageService storageService;
    private final ObjectDetector objectDetector;

    @Autowired
    public FileUploadController(final StorageService storageService, final ObjectDetector objectDetector) {
        this.storageService = storageService;
        this.objectDetector = objectDetector;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        return "upload-image";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        String originalImagePath = "/upload-dir/" + storageService.store(file);
        Map<String, Object> result = objectDetector.detect("." + originalImagePath);
        model.addAttribute("originalName", file.getOriginalFilename());
        model.addAttribute("originalImage", originalImagePath);
        model.addAttribute("predictedImage", result.get("labeledFilePath"));
        model.addAttribute("recognitions", result.get("recognitions"));
        return "display-result";
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleStorageFileNotFound(ServiceException ex) {
        return ResponseEntity.notFound().build();
    }
}
