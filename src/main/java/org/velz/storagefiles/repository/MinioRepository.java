package org.velz.storagefiles.repository;

import io.minio.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.velz.storagefiles.exception.InvalidFileException;
import org.velz.storagefiles.exception.ResourceAlreadyExistsException;
import org.velz.storagefiles.exception.ResourceNotExistsException;
import org.velz.storagefiles.exception.StorageException;
import org.velz.storagefiles.properties.MinioProperties;
import org.velz.storagefiles.utils.PathUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@RequiredArgsConstructor
public class MinioRepository {

    public static final int BUFFER_SIZE = 8192;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;


    @PostConstruct
    @SneakyThrows
    public void createBucketIfNotExists() {
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
        }
    }


    public void renameFile(String oldPath, String newPath) {
        if (!resourceExists(oldPath)) {
            throw new ResourceNotExistsException("Ресурс не существует");
        }

        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newPath)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucketName)
                                            .object(oldPath)
                                            .build())
                            .build());

            removeFile(oldPath);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException(e.getMessage());
        }
    }

    public void renameDirectory(String oldPath, String newPath) {
//    if (!resourceExists(oldPath)) {
//        throw new ResourceNotExistsException("Ресурс не существует");
//    }
        try {
            for (Result<Item> resourceInfo : getDirectoryInfoRecursive(oldPath)) {

                String resourcePath = resourceInfo.get().objectName();
                if (resourcePath.equals(oldPath)) {
                    removeFile(oldPath);
                    continue;
                }
                String relativePathLocation = resourcePath.split(oldPath)[1];
                String newPathToFile = newPath.concat(relativePathLocation);
                renameFile(resourcePath, newPathToFile);
            }
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }


    }

    public InputStreamResource downloadFile(String path) {

        try {
            return new InputStreamResource(minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build()));
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }

    }

    public InputStreamResource downloadDirectory(String path) {
        Iterable<Result<Item>> resources = getDirectoryInfoRecursive(path);
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zip = new ZipOutputStream(baos);
        ) {
            byte[] buffer = new byte[BUFFER_SIZE];

            for (Result<Item> resource : resources) {
                String resourcePath = resource.get().objectName();
                String resourceName = resourcePath.substring(path.length());
                String downloadedDirectory = PathUtils.getFileNameOrDirectoryName(path);

                if (resourceName.equals(downloadedDirectory)) {
                    continue;
                }

                try (InputStream is = downloadFile(resourcePath).getInputStream();) {
                    zip.putNextEntry(new ZipEntry(resourceName));
                    int len;

                    while ((len = is.read(buffer)) > 0) {
                        zip.write(buffer, 0, len);
                    }

                    zip.closeEntry();
                }


            }
            zip.finish();
            byte[] zipBytes = baos.toByteArray();
            ByteArrayInputStream zipIs = new ByteArrayInputStream(zipBytes);

            return new InputStreamResource(zipIs);
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }


    public ObjectWriteResponse createEmptyDirectory(String path) {
        try {
            return minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .stream(
                                    new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    public void removeFile(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build());
        } catch (Exception e) {
            throw new ResourceNotExistsException("Ресурса не существует");
        }

    }


    public Iterable<Result<Item>> getDirectoryInfo(String path) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(path)
                .recursive(false)
                .build());

    }

    public boolean resourceExists(String path) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public StatObjectResponse getFileInfo(String path) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    public ObjectWriteResponse uploadObject(MultipartFile file, String pathWithFileName) {
        try {
            return minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(pathWithFileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }

    }

    public void removeDirectory(String pathWithRootDirectory) {
//        Iterable<Result<Item>> directoryInfo = getDirectoryInfo(pathWithRootDirectory);
//        List<DeleteObject> objectsToDelete = new LinkedList<>();

        Iterable<Result<Item>> directoryInfo = getDirectoryInfoRecursive(pathWithRootDirectory);

        try {
            for (Result<Item> resourceInfo : directoryInfo) {

                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(resourceInfo.get().objectName())
                                .build());
            }
        } catch (Exception e) {
            throw new ResourceNotExistsException("Папка не существует");
        }


    }

    public Iterable<Result<Item>> getDirectoryInfoRecursive(String pathWithRootDirectory) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(pathWithRootDirectory)
                .recursive(true)
                .build());
    }
}
