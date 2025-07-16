package org.velz.storagefiles.utils;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.velz.storagefiles.exception.ResourceAlreadyExistsException;
import org.velz.storagefiles.repository.MinioRepository;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;


@UtilityClass
public class PathUtils {

    private static final Pattern PATH_PART_PATTERN = Pattern.compile("^(?! |.* $)[^/\\\\:*?\"<>|]+$");

    private static final String SLASH = "/";
    private static final String SLASHES_TO_SPLIT = "[/\\\\]+";

    public String resolvePath(String path, Long id) {

        String pathWithUserRootDirectory = PathUtils.getPathWithUserRootDirectory(path, id);

        String[] parts = pathWithUserRootDirectory.split(SLASHES_TO_SPLIT);


        for (String part : parts) {
            if (part.isBlank()) {
                throw new InvalidPathException("Невалидный путь", "пустой сегмент пути");

            }
            if (!PATH_PART_PATTERN.matcher(part).matches()) {
                throw new InvalidPathException("Невалидный путь", "недопустимый сегмент в пути");

            }
        }


        return pathWithUserRootDirectory;
    }


    public String getRelativePath(String resourceName) {
        String relativePath = "";
        String[] split = resourceName.split("/");
        long count = Arrays.stream(split).count();

        if (count <= 2) {
            return relativePath;
        } else {
            for (int i = 1; i < count - 1; i++) {
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

        for (int i = 0; i < count - 1; i++) {
            pathWithoutLastSegment = pathWithoutLastSegment.concat(split[i] + "/");
        }


        return pathWithoutLastSegment;

    }

    public static void ensurePathsNotDifferent(String oldPath, String newPath) {
        boolean oldIsDir = PathUtils.isDirectory(oldPath);
        boolean newIsDir = PathUtils.isDirectory(newPath);
        if (oldIsDir != newIsDir) {
            throw new InvalidPathException("Невалидные пути",
                    "Оба пути должны быть либо файлами либо директориями");
        }
    }

}
