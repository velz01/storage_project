package org.velz.storagefiles.service;


import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.velz.storagefiles.dto.ResourceDto;
import org.velz.storagefiles.exception.DirectoryNotExistsException;
import org.velz.storagefiles.exception.FileAlreadyExistsException;
import org.velz.storagefiles.mapper.ResourceMapper;
import org.velz.storagefiles.repository.MinioRepository;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinioStorageService {
    private final MinioRepository minioRepository;

    private final ResourceMapper resourceMapper;


    public List<ResourceDto> getDirectoryInfo(String path, Long id) {
        List<ResourceDto> fileList = new ArrayList<>();


        String pathWithRootDirectory = getPathWithRootDirectory(path, id) ;
        Iterable<Result<Item>> directoryInfo = minioRepository.getDirectoryInfo(pathWithRootDirectory) ;

        try {
            for (Result<Item> directory : directoryInfo) {
                Item item = directory.get();
                if (item.objectName().equals(pathWithRootDirectory)) {
                    continue;
                }
                ResourceDto resourceDto = resourceMapper.mapToResource(item);

                fileList.add(resourceDto);
            }
        } catch (Exception e) {
            throw new DirectoryNotExistsException("Папка не существует");
        }
        return fileList;
    }

    public List<ResourceDto> upload(String path, List<MultipartFile> files, Long id) {
        List<ResourceDto> fileList = new ArrayList<>();

        String pathWithRootDirectory = getPathWithRootDirectory(path, id);

        for (MultipartFile file : files) {
            String pathWithFileName = pathWithRootDirectory.concat(file.getOriginalFilename());

            if (minioRepository.resourceExists(pathWithFileName)) {
                throw new FileAlreadyExistsException("Файл уже существует");
            }

//            String pathWithFileName = pathWithRootDirectory.concat(file.getOriginalFilename());
            ObjectWriteResponse objectWriteResponse = minioRepository.uploadObject(file, pathWithFileName);
            ResourceDto resourceDto = resourceMapper.mapToResource(objectWriteResponse, file.getSize());
            System.out.println(resourceDto);
            fileList.add(resourceDto);
        }
        return fileList;

    }

    public ResourceDto createEmptyDirectory(String path, Long id) {
        String pathWithRootDirectory = getPathWithRootDirectory(path, id);
        ObjectWriteResponse directoryInfo = minioRepository.createEmptyDirectory(pathWithRootDirectory);

        return resourceMapper.mapToResource(directoryInfo);
    }

    public ResourceDto getFileInfo(String path, Long id) {
        checkPath(path);

        StatObjectResponse statObjectResponse = minioRepository.getFileInfo(getPathWithRootDirectory(path, id));
        return resourceMapper.mapToResource(statObjectResponse);


    }

    private void checkPath(String path) {
        if (path.isBlank()) {
            throw new InvalidPathException("Путь невалиден", "Путь пустой");
        }
        Paths.get(path);
    }

    private static String getPathWithRootDirectory(String path, Long id) {
//        if (!path.isBlank()) {
//            return String.format("user-%d-files/".concat(path + "/"), id);
//        }
        if (!path.isBlank()) {
            return String.format("user-%d-files/".concat(path), id);
        }
        return String.format("user-%d-files/".concat(path), id);

    }


}
