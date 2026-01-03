package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.AuthorDto;
import io.spring.training.boot.server.DTOs.AuthorRequestDto;
import io.spring.training.boot.server.exceptions.AuthorNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.repositories.AuthorRepo;
import io.spring.training.boot.server.utils.mappers.AuthorMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepo authorRepo;
    private final ImageStorageService imageStorageService;

    @Override
    public Page<AuthorDto> getAllAuthors(Pageable pageable){
        return authorRepo.findAll(pageable).map(AuthorMapper::toAuthorDto);
    }

    @Override
    public AuthorDto getAuthorById(Long id) {
        return authorRepo.findById(id)
                .map(AuthorMapper::toAuthorDto)
                .orElseThrow(() -> new AuthorNotFoundException("No author found with the id: " + id));
    }

    @Override
    public AuthorDto createAuthor(AuthorRequestDto authorRequestDto, MultipartFile authorImage) {
        Author author = AuthorMapper.fromAuthorRequestDto(authorRequestDto);

        if(!authorImage.isEmpty()) {
            String imageName = imageStorageService.storeAuthorImage(authorImage);
            author.setPhoto(imageName);
        }

        return AuthorMapper.toAuthorDto(authorRepo.save(author));
    }

    @Override
    public AuthorDto updateAuthor(Long id, @Valid AuthorRequestDto authorRequestDto, MultipartFile authorImage) {
        Optional<Author> oldAuthor = authorRepo.findById(id);

        if(oldAuthor.isEmpty()) {
            throw new AuthorNotFoundException("No author found with the id: " + id);
        }

        Author newAuthor = AuthorMapper.fromAuthorRequestDto(authorRequestDto);
        newAuthor.setId(id);

        if(!authorImage.isEmpty()) {
            String oldImageName = oldAuthor.get().getPhoto();
            if(oldImageName != null && !oldImageName.trim().isEmpty()){
                imageStorageService.deleteAuthorImage(oldImageName);
            }
            String newImageName = imageStorageService.storeAuthorImage(authorImage);
            newAuthor.setPhoto(newImageName);
        } else {
            imageStorageService.deleteAuthorImage(oldAuthor.get().getPhoto());
        }

        return AuthorMapper.toAuthorDto(authorRepo.save(newAuthor));
    }

    @Override
    public void deleteAuthorById(Long id) {
        authorRepo.findById(id).ifPresent(author -> {
            String authorImage = author.getPhoto();
            if(!authorImage.trim().isEmpty()) {
                imageStorageService.deleteAuthorImage(authorImage);
            }
            authorRepo.delete(author);
        });
    }

    @Override
    public Set<Author> findAuthorsByIds(Set<Long> ids) {
        return new HashSet<>(authorRepo.findAllById(ids));
    }
}
