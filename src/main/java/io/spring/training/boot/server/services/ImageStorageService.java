package io.spring.training.boot.server.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String getBookImage(Long bookId);

    String uploadBookImage(MultipartFile image);

    String updateBookImage(Long bookId, MultipartFile image);

    void deleteBookImage(Long bookId);
}
