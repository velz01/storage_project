package org.velz.storagefiles.mapper;

import io.minio.ObjectWriteResponse;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;
import org.velz.storagefiles.dto.ResourceDto;
import org.velz.storagefiles.dto.ResourceType;
import org.velz.storagefiles.utils.PathUtils;

@Component
public class ResourceMapper {

    public ResourceDto mapFile(StatObjectResponse stat) {
        return map(stat.object(), stat.size(), ResourceType.FILE);
    }

    public ResourceDto mapFile(ObjectWriteResponse writeResp, long size) {
        return map(writeResp.object(), size, ResourceType.FILE);
    }

    public ResourceDto mapItem(Item item) {
        ResourceType type = item.isDir() ? ResourceType.DIRECTORY : ResourceType.FILE;
        return map(item.objectName(), item.size(), type);
    }

    public ResourceDto mapDirectory(ObjectWriteResponse writeResp) {
        return map(writeResp.object(), null, ResourceType.DIRECTORY);
    }

    public ResourceDto mapDirectory(String rawPath) {
        return map(rawPath, null, ResourceType.DIRECTORY);
    }

    private ResourceDto map(String fullObjectName, Long size, ResourceType type) {
        String relative = PathUtils.getRelativePath(fullObjectName);
        String name = PathUtils.getFileNameOrDirectoryName(fullObjectName);

        return ResourceDto.builder()
                .path(relative)
                .name(name)
                .size(size)
                .type(type)
                .build();
    }
}
