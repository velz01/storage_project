package org.velz.storagefiles.utils;

import lombok.experimental.UtilityClass;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;


@UtilityClass
public class PathUtils {

    public static final String SLASH = "/";


    public String getRelativePath(String resourceName) {
        String relativePath = "";
        String[] split = resourceName.split("/");
        long count = Arrays.stream(split).count();

        if (count <= 2) {
            return relativePath;
        } else {
            for (int i = 1; i < count - 1 ; i++) {
                relativePath = relativePath.concat(split[i] + "/");
            }
        }

        return relativePath;
    }

    public String getPathWithUserRootDirectory(String path, Long id) {
        return String.format("user-%d-files/".concat(path), id);
    }

    public String getPathToFile(String pathWithRootDirectory, MultipartFile file) {
        return pathWithRootDirectory.concat(Objects.requireNonNull(file.getOriginalFilename()));
    }

    public void checkPath(String path) {
        Paths.get(path);
    }

    public String getUserRootDirectory(Long id) {
        return PathUtils.getPathWithUserRootDirectory("", id);
    }

    public boolean isDirectory(String resourceName) {
        return resourceName.endsWith("/");
    }

    public String getFileNameOrDirectoryName(String resourceName) {
        String[] split = resourceName.split("/");
        int count = (int) Arrays.stream(split).count();
        int indexLastElement = count - 1;

        return isDirectory(resourceName) ? split[indexLastElement].concat(SLASH) : split[indexLastElement];

    }

    public String removeLastSegmentFromDirectory(String path) {
        String pathWithoutLastSegment = "";
        String[] split = path.split("/");
        long count = Arrays.stream(split).count();

            for (int i = 0; i < count - 1 ; i++) {
                pathWithoutLastSegment = pathWithoutLastSegment.concat(split[i] + "/");
            }


        return pathWithoutLastSegment;

    }
}
