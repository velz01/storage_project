package org.velz.storagefiles.dto;


import lombok.Builder;
import lombok.Value;
import org.velz.storagefiles.enums.ResourceType;

@Value
@Builder
public class ResourceDto {
    String path;
    String name;
    Long size;
    ResourceType type;
}
