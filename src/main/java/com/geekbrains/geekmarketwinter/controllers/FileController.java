package com.geekbrains.geekmarketwinter.controllers;

import com.geekbrains.geekmarketwinter.services.interfaces.IFileStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Controller
@CrossOrigin
public class FileController {

    @Autowired
    IFileStoreService fileStoreService;

    @PostMapping("/storefile")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("subtype") int subType
    ) throws IOException, NoSuchAlgorithmException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is Empty");
        }

        Date date = new Date();
        String format = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss", Locale.getDefault()).format(date);

        long size;
        String hash = fileStoreService.storeFile(file.getBytes(), file.getOriginalFilename(), subType, format, size = file.getSize());
        return ResponseEntity.ok(hash);
    }

    @GetMapping("/getfile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("hash") UUID hash) throws IOException {
        byte[] array = fileStoreService.getFile(hash);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(array));
    }

    @GetMapping("/getfiles")
    public ResponseEntity<?> getFiles(@RequestParam("subtype") int subtype) throws IOException {
        return ResponseEntity.ok(fileStoreService.getMetaFiles(subtype));
    }
}
