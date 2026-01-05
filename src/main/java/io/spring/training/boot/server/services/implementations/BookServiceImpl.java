package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.*;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.exceptions.ReviewNotFoundException;
import io.spring.training.boot.server.models.Author;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Genre;
import io.spring.training.boot.server.models.Review;
import io.spring.training.boot.server.models.embeddables.ReviewId;
import io.spring.training.boot.server.models.projections.BookWithStats;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.repositories.ReviewRepo;
import io.spring.training.boot.server.services.AuthorService;
import io.spring.training.boot.server.services.BookService;
import io.spring.training.boot.server.services.GenreService;
import io.spring.training.boot.server.services.ImageStorageService;
import io.spring.training.boot.server.utils.mappers.BookMapper;
import io.spring.training.boot.server.utils.mappers.ReviewMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepo bookRepo;
    private final AuthorService authorService;
    private final ImageStorageService imageStorageService;
    private final GenreService genreService;
    private final ReviewRepo reviewRepo;

    @Override
    @Transactional
    public BookCreationResponseDto createBook(BookRequestDto bookRequest, MultipartFile bookImage) {
        Book book = BookMapper.fromBookRequestDto(bookRequest);

        Set<Author> authors = authorService.findAuthorsByIds(bookRequest.authorIDs());
        book.setAuthors(authors);

        Set<Genre> genres = genreService.findGenresByIds(bookRequest.genreIDs());
        book.setGenres(genres);

        if(bookImage != null && !bookImage.isEmpty()){
            String imageName = imageStorageService.storeBookImage(bookImage);
            book.setImage(imageName);
        }

        return BookMapper.toBookResponseDto(bookRepo.save(book));
    }

    @Override
    public BookResponseWithStats findBookById(long id){
        BookWithStats book = bookRepo.findBookByIdWithStats(id).orElseThrow(() -> new BookNotFoundException("No book found with the id: " + id));
        return BookMapper.toBookResponseWithStats(book);
    }

    @Override
    public Page<BookSummaryDto> getAllBooks(Pageable pageable, String keyword) {
        if(keyword == null || keyword.trim().isEmpty())
            return bookRepo.findAllBooksWithStats(pageable).map(book -> BookMapper.toBookSummaryDto(book.getBook(), book.getTotalReviews(), book.getAverageRating()));
        else
            return bookRepo.findAllBooksWithStatsContaining(pageable, keyword).map(book -> BookMapper.toBookSummaryDto(book.getBook(), book.getTotalReviews(), book.getAverageRating()));
    }

    @Override
    @Transactional
    public BookCreationResponseDto updateBookById(Long id, @Valid BookRequestDto bookRequest, MultipartFile bookImage) {
        Optional<Book> oldBook = bookRepo.findById(id);

        if(oldBook.isEmpty()){
            throw new BookNotFoundException("No book found with the id: " + id);
        }

        Book newBook = BookMapper.fromBookRequestDto(bookRequest);
        newBook.setId(id);

        Set<Author> newAuthors = authorService.findAuthorsByIds(bookRequest.authorIDs());
        newBook.setAuthors(newAuthors);

        Set<Genre> newGenres = genreService.findGenresByIds(bookRequest.genreIDs());
        newBook.setGenres(newGenres);

        if(bookImage != null && !bookImage.isEmpty()){
            String oldImageName = oldBook.get().getImage();
            if(oldImageName != null && !oldImageName.trim().isEmpty())
                imageStorageService.deleteBookImage(oldImageName);

            String newImageName = imageStorageService.storeBookImage(bookImage);
            newBook.setImage(newImageName);
        } else {
            imageStorageService.deleteBookImage(oldBook.get().getImage());
        }

        return BookMapper.toBookResponseDto(bookRepo.save(newBook));
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepo.findById(id).ifPresent(book -> {
            if(!book.getImage().trim().isEmpty()){
                imageStorageService.deleteBookImage(book.getImage());
            }
            bookRepo.delete(book);
        });
    }

    @Override
    public ReviewResponseDto createReviewForBook(Long bookId, ReviewRequestDto reviewRequestDto) {
        if(!bookRepo.existsById(bookId)) throw new BookNotFoundException("No book found with the id: " + bookId);
        Review newReview = ReviewMapper.fromReviewRequestDto(reviewRequestDto);
        // TODO: the user id should be the actual principal id when i add security
        ReviewId reviewId = new ReviewId(17L, bookId);
        newReview.setId(reviewId);
        newReview.setEdited(false);
        return ReviewMapper.toReviewResponseDto(reviewRepo.save(newReview));
    }

    // TODO: PreAuthorize this method to make sure the user principal is actually the review author before updating
    @Override
    public ReviewResponseDto updateReviewForBook(Long bookId, ReviewRequestDto reviewRequestDto) {
        if(!bookRepo.existsById(bookId)) throw new BookNotFoundException("No book found with the id: " + bookId);
        ReviewId reviewId = new ReviewId(17L, bookId);

        // check if the review exists
        if(!reviewRepo.existsById(reviewId)) {
            throw new ReviewNotFoundException("No review found for book id: " + reviewId.getBookId() + " and user id: " + reviewId.getUserId());
        }

        Review review = ReviewMapper.fromReviewRequestDto(reviewRequestDto);
        review.setId(reviewId);
        review.setEdited(true);

        return ReviewMapper.toReviewResponseDto(reviewRepo.save(review));
    }

    @Override
    public Page<ReviewResponseDto> getReviewsForBook(Long bookId, Pageable pageable, String keyword) {
        if(keyword == null || keyword.trim().isEmpty())
            return reviewRepo.findById_BookId(pageable, bookId).map(ReviewMapper::toReviewResponseDto);
        else
            return reviewRepo.findById_BookIdAndKeyword(pageable, bookId, keyword).map(ReviewMapper::toReviewResponseDto);
    }

    @Override
    public void deleteReview(Long bookId, Long userId) {
        reviewRepo.deleteById(new ReviewId(userId, bookId));
    }
}
