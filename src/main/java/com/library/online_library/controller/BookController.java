package com.library.online_library.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.online_library.exception.AiApiException;
import com.library.online_library.exception.ResourceNotFoundException;
import com.library.online_library.model.Book;
import com.library.online_library.service.BookService;
import com.library.online_library.serviceAI.AiService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;


@CrossOrigin(origins = "http://localhost:3000")
@OpenAPIDefinition(info = @Info(title = "Library API", version = "1.0", description = "API for managing books"))
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AiService aiService;

    public BookController(BookService bookService, AiService aiService) {
        this.bookService = bookService;
        this.aiService = aiService;
    }

    @Operation(summary = "Create a new book", description = "Adds a new book to the library with validation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.createBook(book));
    }

    @Operation(summary = "Get all books with pagination", description = "Returns a paginated list of books.")
    @ApiResponse(responseCode = "200", description = "List of books retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @Parameter(description = "Page number (0-based index)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of books per page", example = "10") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Get a book by ID", description = "Finds a book by its unique ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book found"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Update an existing book", description = "Updates book details by ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @Valid @RequestBody Book updatedBook) {
        Optional<Book> existingBookOptional = bookService.getBookById(id);
        
        if (existingBookOptional.isEmpty()) {
            throw new ResourceNotFoundException("Book not found with ID: " + id);
        }

        Book existingBook = existingBookOptional.get();
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPublicationYear(updatedBook.getPublicationYear());
        existingBook.setDescription(updatedBook.getDescription());

        Book savedBook = bookService.saveBook(existingBook);
        return ResponseEntity.ok(savedBook);
    }

    @Operation(summary = "Delete a book", description = "Deletes a book by ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search books", description = "Search books by title and/or author.")
    @ApiResponse(responseCode = "200", description = "Books found")
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {
        List<Book> results = bookService.searchBooks(title, author);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Generate AI insights for a book", description = "Returns an AI-generated insight for a given book ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "AI insights generated successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "503", description = "AI service unavailable")
    })
    @GetMapping("/{id}/ai-insights")
    public ResponseEntity<Map<String, Object>> getAiInsights(@PathVariable Long id) {
        Book foundBook = bookService.getBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        try {
            String aiInsight = aiService.generateInsight(foundBook);
            Map<String, Object> response = new HashMap<>();
            response.put("id", foundBook.getId());
            response.put("title", foundBook.getTitle());
            response.put("author", foundBook.getAuthor());
            response.put("publicationYear", foundBook.getPublicationYear());
            response.put("description", foundBook.getDescription());
            response.put("aiInsight", aiInsight);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new AiApiException("Failed to generate AI insights. Please try again later.");
        }
    }
    
}
