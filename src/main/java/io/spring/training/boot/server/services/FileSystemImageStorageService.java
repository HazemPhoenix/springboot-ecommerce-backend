package io.spring.training.boot.server.services;

import io.spring.training.boot.server.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemImageStorageService implements ImageStorageService {
    private final StorageProperties storageProperties;
    private final BookService bookService;

    public FileSystemImageStorageService(StorageProperties storageProperties, BookService bookService, String rootLocation) {
        this.storageProperties = storageProperties;
        this.bookService = bookService;
    }


    @Override
    public String getBookImage(Long bookId) {
        String imageName = bookService.findBookById(bookId).image();
        // TODO: find the url of the image
        return imageName;
    }

    @Override
    public String uploadBookImage(MultipartFile image) {
        return "test-image.png";
    }

    @Override
    public String updateBookImage(Long bookId, MultipartFile image) {
        return "test-updated-image.png";
    }

    @Override
    public void deleteBookImage(Long bookId) {
    }
}
