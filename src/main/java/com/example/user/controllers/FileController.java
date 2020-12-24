package com.example.user.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * <description>
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-22
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private static String currentWorkingDirectory = System.getProperty("user.dir");
    private static String uploadDirectory = currentWorkingDirectory + "/src/main/resources/static/uploads";
    final List<String> supportedFileExtensions = List.of(".png,.jpg,.jpeg,.gif".split(","));

    @PostConstruct
    public void init() {
        File uploadsPath = new File(uploadDirectory);
        if(!uploadsPath.exists()) {
            uploadsPath.mkdir();
        }
        System.out.println("***" + uploadsPath);
    }

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file) {
        var filename = file.getOriginalFilename();
        var fileExtension = filename.substring(filename.lastIndexOf("."));

        if(!supportedFileExtensions.contains(fileExtension)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        try {
            Files.copy(file.getInputStream(), Paths.get(uploadDirectory + File.separator + filename), StandardCopyOption.REPLACE_EXISTING);
            //var targetLocation = new File(uploadDirectory + File.separator + filename);
            //file.transferTo(targetLocation);
            /*byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadDirectory + File.separator + filename);
            Files.write(path, bytes);*/
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
        return ResponseEntity.created(URI.create("/files/" + filename)).build();
    }

}
