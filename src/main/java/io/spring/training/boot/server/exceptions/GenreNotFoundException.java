package io.spring.training.boot.server.exceptions;

public class GenreNotFoundException extends ResourceNotFoundException {
    public GenreNotFoundException(String message) {
        super(message);
    }

    public GenreNotFoundException(Long genreId) {
        super("genre", genreId);
    }
}
