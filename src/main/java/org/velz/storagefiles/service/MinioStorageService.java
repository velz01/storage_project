package org.velz.storagefiles.service;


import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.velz.storagefiles.dto.ResourceDto;
import org.velz.storagefiles.exception.ResourceNotExistsException;
import org.velz.storagefiles.exception.ParentFolderNotExistsException;
import org.velz.storagefiles.exception.ResourceAlreadyExistsException;
import org.velz.storagefiles.exception.StorageException;
import org.velz.storagefiles.mapper.ResourceMapper;
import org.velz.storagefiles.repository.MinioRepository;
import org.velz.storagefiles.utils.PathUtils;

import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinioStorageService {
    private final MinioRepository minioRepository;

    private final ResourceMapper resourceMapper;


    public void createUserRootDirectory(Long id) {
        String userRootDirectory = PathUtils.getUserRootDirectory(id);
        minioRepository.createEmptyDirectory(userRootDirectory);
    }


    public List<ResourceDto> searchResources(String partOfResourceName, Long id) {
        if (partOfResourceName.isBlank()) {
            throw new InvalidPathException("Невалидный путь", "невалидный или отсутствующий путь");
        }
        List<ResourceDto> resourceDtoList = new ArrayList<>();

        String userRootDirectory = PathUtils.getUserRootDirectory(id);
        Iterable<Result<Item>> filesInfo = minioRepository.getDirectoryInfoRecursive(userRootDirectory);


        addFoundResourcesToList(filesInfo, resourceDtoList, userRootDirectory, partOfResourceName);


        return resourceDtoList;
    }

    private void addFoundResourcesToList(Iterable<Result<Item>> resourcesInfo,
                                         List<ResourceDto> resourceDtoList,
                                         String userRootDirectory,
                                         String partOfResourceName) {
        try {
            for (Result<Item> resource : resourcesInfo) {

                Item resourceInfo = resource.get();
                String resourceName = PathUtils.getFileNameOrDirectoryName(resourceInfo.objectName());
                if (resourceName.equals(userRootDirectory)) {
                    continue;
                }
                if (resourceName.contains(partOfResourceName)) {
                    resourceDtoList.add(resourceMapper.mapToResource(resourceInfo));
                }
            }
        } catch (
                Exception e) {
            throw new StorageException("ошибка при работе с minio");
        }
    }

    public ResourceDto renameResource(String oldPath, String newPath, Long id) {
        PathUtils.ensurePathsNotDifferent(oldPath, newPath);

        String oldPathWithRootDirectory = PathUtils.resolvePath(oldPath, id);
        String newPathWithRootDirectory = PathUtils.resolvePath(newPath, id);

        ensureResourceExists(oldPathWithRootDirectory);
        ensureResourceNotExists(newPathWithRootDirectory);

        if (PathUtils.isDirectory(oldPath)) {
            minioRepository.renameDirectory(oldPathWithRootDirectory, newPathWithRootDirectory);
            return resourceMapper.mapToResource(newPath);
        } else {
            minioRepository.renameFile(oldPathWithRootDirectory, newPathWithRootDirectory);
            StatObjectResponse fileInfo = minioRepository.getFileInfo(newPathWithRootDirectory);
            return resourceMapper.mapToResource(fileInfo);
        }


    }


    public List<ResourceDto> getDirectoryInfo(String path, Long id) {
        String pathWithRootDirectory = PathUtils.resolvePath(path, id);
        List<ResourceDto> fileList = new ArrayList<>();

        ensureResourceExists(pathWithRootDirectory);

        Iterable<Result<Item>> directoryInfo = minioRepository.getDirectoryInfo(pathWithRootDirectory);

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
            throw new StorageException("ошибка при работе с minio");
        }
        return fileList;
    }

    public List<ResourceDto> upload(String path, List<MultipartFile> files, Long id) {
        List<ResourceDto> fileList = new ArrayList<>();
        String pathWithRootDirectory = PathUtils.resolvePath(path, id);


        for (MultipartFile file : files) {
            String pathWithFileName = PathUtils.getPathToFile(pathWithRootDirectory, file);

            ensureResourceNotExists(pathWithFileName);
            String[] pathSegments = pathWithFileName.split("/");
            StringBuilder prefix = new StringBuilder();

            for (int i = 1; i < pathSegments.length - 1; i++) {
                prefix.append(pathSegments[i]).append("/");

                if (!minioRepository.resourceExists(PathUtils.getPathWithUserRootDirectory(prefix.toString(), id))) {
                    ResourceDto createdDirectory = createEmptyDirectory(prefix.toString(), id);
                    fileList.add(createdDirectory);
                }
            }
            ObjectWriteResponse objectWriteResponse = minioRepository.uploadObject(file, pathWithFileName);
            ResourceDto resourceDto = resourceMapper.mapToResource(objectWriteResponse, file.getSize());

            fileList.add(resourceDto);
        }
        return fileList;

    }


    public ResourceDto createEmptyDirectory(String path, Long id) {
        if (!PathUtils.isDirectory(path)) {
            throw new InvalidPathException("Невалидный путь", "Ресурс не является папкой");
        }
        String pathWithRootDirectory = PathUtils.resolvePath(path, id);
        String pathToParentDirectory = PathUtils.removeLastSegmentFromDirectory(pathWithRootDirectory);

        if (!minioRepository.resourceExists(pathToParentDirectory)) {
            throw new ParentFolderNotExistsException("Родительская папка не существует");
        }

        ensureResourceNotExists(pathWithRootDirectory);

        ObjectWriteResponse directoryInfo = minioRepository.createEmptyDirectory(pathWithRootDirectory);

        return resourceMapper.mapToResource(directoryInfo);
    }

    public ResourceDto getFileInfo(String path, Long id) {

        String pathWithRootDirectory = PathUtils.resolvePath(path, id);
        ensureResourceExists(pathWithRootDirectory);
        StatObjectResponse statObjectResponse = minioRepository.getFileInfo(pathWithRootDirectory);

        return resourceMapper.mapToResource(statObjectResponse);
    }


    public void deleteResource(String path, Long id) {
        String pathWithRootDirectory = PathUtils.resolvePath(path, id);

        ensureResourceExists(pathWithRootDirectory);
        if (PathUtils.isDirectory(path)) {
            minioRepository.removeDirectory(pathWithRootDirectory);
        } else {
            minioRepository.removeFile(pathWithRootDirectory);
        }
    }


    public InputStreamResource downloadResource(String path, Long id) {
        String pathWithRootDirectory = PathUtils.resolvePath(path, id);
        ensureResourceExists(pathWithRootDirectory);
        if (PathUtils.isDirectory(path)) {
            return minioRepository.downloadDirectory(pathWithRootDirectory);
        } else {
            return minioRepository.downloadFile(pathWithRootDirectory);
        }
    }

    private void ensureResourceNotExists(String pathWithRootDirectory) {
        if (minioRepository.resourceExists(pathWithRootDirectory)) {
            throw new ResourceAlreadyExistsException("Ресурс уже существует");
        }
    }

    private void ensureResourceExists(String pathWithRootDirectory) {
        if (!minioRepository.resourceExists(pathWithRootDirectory)) {
            throw new ResourceNotExistsException("Ресурс не существует");
        }
    }

}
