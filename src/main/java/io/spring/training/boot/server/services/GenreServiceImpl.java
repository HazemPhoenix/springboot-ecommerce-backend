package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.GenreRequestDto;
import io.spring.training.boot.server.DTOs.GenreResponseDto;
import io.spring.training.boot.server.exceptions.GenreNotFoundException;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.repositories.GenreRepo;
import io.spring.training.boot.server.utils.mappers.GenreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepo genreRepo;

    @Override
    public GenreResponseDto findGenreById(Long id) {
        return genreRepo
                .findById(id)
                .map(GenreMapper::toGenreResponseDto)
                .orElseThrow(() -> new GenreNotFoundException("No genre found with the id: " + id));
    }

    @Override
    public List<GenreResponseDto> getAllGenres() {
        return genreRepo
                .findAll()
                .stream()
                .map(GenreMapper::toGenreResponseDto)
                .toList();
    }

    @Override
    public GenreResponseDto createGenre(GenreRequestDto genreRequestDto) {
        Genre genre = genreRepo.save(GenreMapper.fromGenreRequestDto(genreRequestDto));
        return GenreMapper.toGenreResponseDto(genre);
    }

    @Override
    public GenreResponseDto updateGenre(Long id, GenreRequestDto genreRequestDto) {
        Optional<Genre> oldGenre = genreRepo.findById(id);

        if(oldGenre.isEmpty()) {
            throw new GenreNotFoundException("No genre found with the id: " + id);
        }

        Genre newGenre = GenreMapper.fromGenreRequestDto(genreRequestDto);
        newGenre.setId(oldGenre.get().getId());

        return GenreMapper.toGenreResponseDto(genreRepo.save(newGenre));
    }

    @Override
    public void deleteGenreById(Long id) {
        genreRepo.deleteById(id);
    }
}
