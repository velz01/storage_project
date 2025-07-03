package org.velz.storagefiles;

import lombok.Value;

@Value
public class ResourceDto {
    String path;
    String name;
    Long size;
    String type;
}
