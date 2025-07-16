package org.velz.storagefiles.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.velz.storagefiles.dto.ResourceDto;
import org.velz.storagefiles.entity.User;
import org.velz.storagefiles.service.MinioStorageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResourceController {
    private final MinioStorageService minioStorageService;


    @GetMapping("/resource/search")
    public List<ResourceDto> searchResources(@RequestParam("query") String partOfResourceName,
                                             @AuthenticationPrincipal User user) {
        return minioStorageService.searchResources(partOfResourceName, user.getId());
    }


    @PostMapping("/resource/move")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto renameResource(@RequestParam("from") String oldPath,
                                      @RequestParam("to") String newPath,
                                      @AuthenticationPrincipal User user) {
        return minioStorageService.renameResource(oldPath, newPath, user.getId());
    }


    @GetMapping("/resource")
    public ResourceDto getFileInfo(@RequestParam String path, @AuthenticationPrincipal User user) {

        return minioStorageService.getFileInfo(path, user.getId());
    }

    @PostMapping("/resource")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceDto> uploadResources(@RequestParam String path, @RequestPart("object") List<MultipartFile> files, @AuthenticationPrincipal User user) throws IOException {

        return minioStorageService.upload(path, files, user.getId());
    }

    @DeleteMapping("/resource")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@RequestParam String path, @AuthenticationPrincipal User user) {
        minioStorageService.deleteResource(path, user.getId());
    }

    @GetMapping("/resource/download")
    public InputStreamResource downloadResource(@RequestParam String path, @AuthenticationPrincipal User user) {
        return minioStorageService.downloadResource(path, user.getId());

    }


}
