package io.spring.training.boot.server.services;

import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.exceptions.StorageException;
import io.spring.training.boot.server.repositories.BookRepo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemImageStorageService implements ImageStorageService {
    private final StorageProperties storageProperties;
    private final BookRepo bookRepo;
    private final Path rootBookLocation;

    public FileSystemImageStorageService(StorageProperties storageProperties, BookRepo bookRepo) {
        this.storageProperties = storageProperties;
        this.bookRepo = bookRepo;

        if(storageProperties.getBookLocation().trim().isEmpty()) {
            throw new StorageException("Image upload location cannot be empty.");
        }

        this.rootBookLocation = Path.of(storageProperties.getBookLocation());
    }


    @Override
    public String getBookImage(String imageName) {
        return imageName;
    }

    private Path getBookImagePath(String imageName) {
        return this.rootBookLocation.resolve(imageName);
    }

    @Override
    public String storeBookImage(MultipartFile image) {
        String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(image.getOriginalFilename());

        Path destinationFile = this.rootBookLocation.resolve(fileName);

        if(!destinationFile.getParent().equals(this.rootBookLocation.toAbsolutePath())) { // this is to protect against directory traversal attacks
            throw new StorageException("File must be stored directly inside the root directory.");
        }

        try(InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }

        return fileName;
    }

    @Override
    public void deleteBookImage(Long bookId) {
    }
}
