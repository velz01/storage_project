package org.velz.storagefiles.repository;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.velz.storagefiles.exception.InvalidFileException;
import org.velz.storagefiles.exception.StorageException;
import org.velz.storagefiles.properties.MinioProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MinioRepository {
    private final MinioProperties minioProperties;
    private final MinioClient minioClient;


    public ObjectWriteResponse createEmptyDirectory(String path) {
        try {
            return minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
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

    public Iterable<Result<Item>> getDirectoryInfo(String path) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(minioProperties.getBucket())
                .prefix(path)
                .recursive(false)
                .build());

    }

    public boolean resourceExists(String path) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
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
                    .bucket(minioProperties.getBucket())
                    .object(path)
                    .build());
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    public ObjectWriteResponse uploadObject(MultipartFile file, String pathWithFileName) {
        try {
            return minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
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
}
