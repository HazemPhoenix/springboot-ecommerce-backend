package io.spring.training.boot.server.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    String storeBookImage(MultipartFile image);

    void deleteBookImage(String imageName);

    String storeAuthorImage(MultipartFile image);

    void deleteAuthorImage(String imageName);
}
