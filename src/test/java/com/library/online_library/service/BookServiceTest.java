package com.library.online_library.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.library.online_library.exception.ResourceNotFoundException;
import com.library.online_library.model.Book;
import com.library.online_library.repository.BookRepository;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        book = new Book();
        book.setId(1L);
        book.setTitle("AI and Future");
        book.setAuthor("Tech Author");
        book.setIsbn("9781234567890");
        book.setPublicationYear(2100);
        book.setDescription("An in-depth look into AI.");
    }

    @Test
    void createBook_ShouldReturnSavedBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book savedBook = bookService.createBook(book);

        assertNotNull(savedBook);
        assertEquals("AI and Future", savedBook.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getBookById(1L);

        assertTrue(foundBook.isPresent());
        assertEquals("AI and Future", foundBook.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_ShouldThrowException_WhenBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            Optional<Book> result = bookService.getBookById(1L);
            if (result.isEmpty()) {
                throw new ResourceNotFoundException("Book not found");
            }
        });

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Book> books = bookService.getAllBooks(pageable);

        assertEquals(1, books.getTotalElements());
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook_WhenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Optional<Book> updatedBook = bookService.updateBook(1L, book);

        assertTrue(updatedBook.isPresent());
        assertEquals("AI and Future", updatedBook.get().getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_ShouldReturnEmptyOptional_WhenBookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.updateBook(1L, book);

        assertFalse(result.isPresent());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_ShouldCallRepositoryDelete() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks_ByTitleAndAuthor() {
        when(bookRepository.searchBooks("AI", "Tech")).thenReturn(List.of(book));

        List<Book> books = bookService.searchBooks("AI", "Tech");

        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        verify(bookRepository, times(1)).searchBooks("AI", "Tech");
    }

    @Test
    void searchBooks_ShouldReturnBooks_ByTitleOnly() {
        when(bookRepository.findByTitleContainingIgnoreCase("AI")).thenReturn(List.of(book));

        List<Book> books = bookService.searchBooks("AI", null);

        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("AI");
    }

    @Test
    void searchBooks_ShouldReturnBooks_ByAuthorOnly() {
        when(bookRepository.findByAuthorContainingIgnoreCase("Tech")).thenReturn(List.of(book));

        List<Book> books = bookService.searchBooks(null, "Tech");

        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        verify(bookRepository, times(1)).findByAuthorContainingIgnoreCase("Tech");
    }

    @Test
    void searchBooks_ShouldReturnAllBooks_WhenNoFiltersProvided() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> books = bookService.searchBooks(null, null);

        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        verify(bookRepository, times(1)).findAll();
    }
}
