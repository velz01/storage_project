package org.velz.storagefiles.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.velz.storagefiles.dto.ResourceDto;
import org.velz.storagefiles.entity.User;
import org.velz.storagefiles.service.MinioStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DirectoryController {

    private final MinioStorageService minioStorageService;

    @PostMapping("/directory")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto createDirectory(@RequestParam String path, @AuthenticationPrincipal User user) {
        return minioStorageService.createEmptyDirectory(path, user.getId());
    }

    @GetMapping("/directory")
    public List<ResourceDto> getDirectoryInfo(@RequestParam String path, @AuthenticationPrincipal User user) {


        return minioStorageService.getDirectoryInfo(path, user.getId());
    }
}
