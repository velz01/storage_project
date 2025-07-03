package org.velz.storagefiles;

import org.springframework.boot.SpringApplication;

public class TestStorageFilesApplication {

    public static void main(String[] args) {
        SpringApplication.from(StorageFilesApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
