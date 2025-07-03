package org.velz.storagefiles.http.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.velz.storagefiles.dto.ResourceDto;
import org.velz.storagefiles.entity.User;
import org.velz.storagefiles.service.MinioStorageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {
    private final MinioStorageService minioStorageService;

    @PostMapping
    public ResourceDto createDirectory(@RequestParam String path, @AuthenticationPrincipal User user) {
        return  minioStorageService.createEmptyDirectory(path,user.getId());
    }

    @GetMapping
    public List<ResourceDto> getDirectoryInfo(@RequestParam String path, @AuthenticationPrincipal User user ) {


        return minioStorageService.getDirectoryInfo(path, user.getId());
    }

}
