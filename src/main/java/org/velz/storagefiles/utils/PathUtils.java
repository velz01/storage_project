package org.velz.storagefiles.utils;

import io.minio.messages.Item;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import static org.apache.commons.io.FilenameUtils.getPath;
import static org.apache.commons.io.FilenameUtils.getPathNoEndSeparator;

@UtilityClass
public class PathUtils {

    public static final String SLASH = "/";

    public String getRelativePath(String resourceName) {

        if (isDirectory(resourceName)) {
            String relativePath = PathUtils.getPathWithoutRootFolder(getPathNoEndSeparator(resourceName));
            return relativePath.isBlank() ? SLASH : relativePath;
        }
        return getPathWithoutRootFolder(FilenameUtils.getPath(resourceName));
    }






    private String getPathWithoutRootFolder(String object) {
        return getPath(object).substring(object.indexOf("/") + 1);
    }

    private static boolean isDirectory(String resourceName) {
        return resourceName.endsWith("/");
    }

    public String getFileNameOrDirectoryName(String resourceName) {

        if (isDirectory(resourceName)){
            resourceName = deleteSlashFromDirectory(resourceName);
            return getLastPathSegment(resourceName).concat(SLASH);
        }

        return getLastPathSegment(resourceName);
    }

    private static @NotNull String getLastPathSegment(String resourceName) {
        return resourceName.substring(resourceName.lastIndexOf("/") + 1);
    }

    private @NotNull String deleteSlashFromDirectory(String resourceName) {
        return resourceName.substring(0, resourceName.lastIndexOf("/"));
    }
}
