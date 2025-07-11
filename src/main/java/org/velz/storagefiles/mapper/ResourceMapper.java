package org.velz.storagefiles.mapper;

import io.minio.ObjectWriteResponse;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;
import org.velz.storagefiles.dto.ResourceDto;
import org.velz.storagefiles.enums.ResourceType;
import org.velz.storagefiles.utils.PathUtils;

@Component
public class ResourceMapper {
    public ResourceDto mapToResource(StatObjectResponse statObjectResponse) {

        return ResourceDto.builder()
                .path(PathUtils.getRelativePath(statObjectResponse.object()))
                .name(PathUtils.getFileNameOrDirectoryName(statObjectResponse.object()))
                .size(statObjectResponse.size())
                .type(ResourceType.FILE)
                .build();
    }

    public ResourceDto mapToResource(ObjectWriteResponse objectWriteResponse, long size) {
        return ResourceDto.builder()
                .path(PathUtils.getRelativePath(objectWriteResponse.object()))
                .name(PathUtils.getFileNameOrDirectoryName(objectWriteResponse.object()))
                .size(size)
                .type(ResourceType.FILE)
                .build();
    }

    public ResourceDto mapToResource(Item resourceInfo) {
       return ResourceDto.builder()
                .path(PathUtils.getRelativePath(resourceInfo.objectName()))
                .name(PathUtils.getFileNameOrDirectoryName(resourceInfo.objectName()))
                .size(resourceInfo.size())
                .type(resourceInfo.isDir() ? ResourceType.DIRECTORY : ResourceType.FILE)
                .build();

    }


    public ResourceDto mapToResource(ObjectWriteResponse objectWriteResponse) {
        return ResourceDto.builder()
                .path(PathUtils.getRelativePath(objectWriteResponse.object()))
                .name(PathUtils.getFileNameOrDirectoryName(objectWriteResponse.object()))
                .size(null)
                .type(ResourceType.DIRECTORY)
                .build();
    }

    public ResourceDto mapToResource(String newPath) {
        return ResourceDto.builder()
                .path(PathUtils.getRelativePath(newPath))
                .name(PathUtils.getFileNameOrDirectoryName(newPath))
                .size(null)
                .type(ResourceType.DIRECTORY)
                .build();
    }
}
