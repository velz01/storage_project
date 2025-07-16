package org.velz.storagefiles.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ResourceDto {
    String path;
    String name;
    Long size;
    ResourceType type;
}
