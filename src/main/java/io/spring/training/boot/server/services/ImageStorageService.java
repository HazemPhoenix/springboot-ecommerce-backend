package io.spring.training.boot.server.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String getBookImage(String imageName);

    String storeBookImage(MultipartFile image);

    void deleteBookImage(String imageName);
}
