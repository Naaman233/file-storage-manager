package com.example.filedemo.service;

import com.example.filedemo.entity.FileData;
import com.example.filedemo.repository.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.filedemo.util.FileUtil;

import java.io.IOException;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository repository;

     public String uploadFile(MultipartFile file) throws IOException {
        FileData upload =  repository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .fileByteData(FileUtil.compressFile(file.getBytes(), file.getOriginalFilename()))
                .build());

        if (upload != null) {
            return "Successfully uploaded file: " + file.getOriginalFilename();
        }
        return null;
    }

    public byte[] downloadFile(String file) throws IOException {
        Optional<FileData> downloadedFile = repository.findByName(file);
        byte[] bytearray = FileUtil.decompressFile(downloadedFile.get().getFileByteData(), downloadedFile.get().getName());
        return bytearray;
    }

    public String convertByteToWordDoc(String file) throws IOException {
         byte[] byteData = downloadFile(file);
         return FileUtil.extractFilter(byteData);
    }


}
