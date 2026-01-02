package io.spring.training.boot.server.services;

import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.StorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class FileSystemImageStorageService implements ImageStorageService {
    private final StorageProperties storageProperties;
    private final BookService bookService;
    private final Path location;

    public FileSystemImageStorageService(StorageProperties storageProperties, BookService bookService) {
        this.storageProperties = storageProperties;
        this.bookService = bookService;

        if(storageProperties.getLocation().trim().isEmpty()) {
            throw new StorageException("Image upload location cannot be empty.");
        }

        this.location = Path.of(storageProperties.getLocation());
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
