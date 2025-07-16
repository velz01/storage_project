package org.velz.storagefiles.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ResourceDto {
    private String path;
    private String name;
    private Long size;
    private ResourceType type;
}
