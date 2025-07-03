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
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class FileController {
    private final MinioStorageService minioStorageService;

    @GetMapping("/directory")
    public List<ResourceDto> getDirectoryInfo(@RequestParam String path, @AuthenticationPrincipal User user ) {

        List<ResourceDto> resources = minioStorageService.getDirectoryInfo(path, user.getId());
        for (ResourceDto resource: resources) {
            System.out.println(resource );
        }

        return resources;
    }

    @GetMapping("/resource")
    public ResourceDto getFileInfo(@RequestParam String path, @AuthenticationPrincipal User user ) {

        ResourceDto resource = minioStorageService.getFileInfo(path, user.getId());
        System.out.println(resource);

        return resource;
    }



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceDto> uploadResources(@RequestParam String path, @RequestPart("object") List<MultipartFile> files, @AuthenticationPrincipal User user) throws IOException {

        return minioStorageService.upload(path, files, user.getId());
    }
}
