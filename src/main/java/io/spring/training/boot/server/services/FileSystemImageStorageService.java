package io.spring.training.boot.server.services;

import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.StorageException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemImageStorageService implements ImageStorageService {
    private final Path rootBookLocation;
    private final Path rootAuthorLocation;

    public FileSystemImageStorageService(StorageProperties storageProperties) {
        if(storageProperties.getBookLocation().trim().isEmpty() || storageProperties.getAuthorLocation().trim().isEmpty()) {
            throw new StorageException("Image upload location cannot be empty.");
        }

        this.rootBookLocation = Path.of(storageProperties.getBookLocation());
        this.rootAuthorLocation = Path.of(storageProperties.getAuthorLocation());
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
    public void deleteBookImage(String imageName) {
        try {
            Files.delete(rootBookLocation.resolve(imageName));
        } catch (IOException e) {
            // no action needed, the book image does not exist
        }
    }

    @Override
    public String storeAuthorImage(MultipartFile image) {
        String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(image.getOriginalFilename());
        Path destinationFile = this.rootAuthorLocation.resolve(fileName);

        if(!destinationFile.getParent().equals(this.rootAuthorLocation.toAbsolutePath())) {
            throw new StorageException("File must be stored directly inside the root directory.");
        }

        try(InputStream inputStream = image.getInputStream()){
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }

        return fileName;
    }

    @Override
    public void deleteAuthorImage(String imageName) {
        try {
            Files.delete(this.rootAuthorLocation.resolve(imageName));
        } catch (IOException e) {
            // do nothing as above
        }
    }
}
