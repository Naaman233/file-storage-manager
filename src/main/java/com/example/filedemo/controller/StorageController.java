package com.example.filedemo.controller;

import com.example.filedemo.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    StorageService service;

    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);


    @PostMapping
    ResponseEntity<?> uploadFile (@RequestParam("file")MultipartFile file) throws IOException {
         String uploadedEntity = service.uploadFile(file);

         return uploadedEntity == null ?
                 ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body("Problem uploading file with name: "+file.getOriginalFilename())
                 :
                 ResponseEntity.status(HttpStatus.OK)
                 .body("Successfully uploaded file with name: "+file.getOriginalFilename());
    }

    @GetMapping("/download/{file}")
    ResponseEntity<?> downloadFile(@PathVariable String file) throws IOException {
        byte[] bytedata = service.downloadFile(file);
        String mimeType = Files.probeContentType(Paths.get(file));
        if (mimeType == null) {
            logger.error("Error downloading file from database");
            mimeType = "application/octet-stream";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body("File not found");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(mimeType))
                .body(bytedata);
    }

    @GetMapping("/extract/{file}")
    ResponseEntity<?> extractTextFromByte(@PathVariable String file) throws IOException {
        String extractedData = service.convertByteToWordDoc(file);
        return extractedData == null ?
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Problem extracting content of file: "+ file)
                :
                ResponseEntity.status(HttpStatus.OK)
                        .body(extractedData);
    }

}
