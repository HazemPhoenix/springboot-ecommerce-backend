package io.spring.training.boot.server.exceptions;


public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(Long bookId){
        super("book", bookId);
    }
}
