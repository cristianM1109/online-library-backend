package com.library.online_library.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.online_library.exception.GlobalExceptionHandler;
import com.library.online_library.exception.ResourceNotFoundException;
import com.library.online_library.model.Book;
import com.library.online_library.service.BookService;
import com.library.online_library.serviceAI.AiService;

class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Mock
    private AiService aiService;

    @InjectMocks
    private BookController bookController;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Serializare JSON

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler()) 
                .build();
    }

    @Test
    void createBook_ShouldReturnCreatedBook() throws Exception {
        Book book = new Book();
        book.setTitle("The AI Revolution");
        book.setAuthor("Jane Smith");
        book.setIsbn("9781234567890");
        book.setPublicationYear(2100);
        book.setDescription("An in-depth look into how artificial intelligence is shaping the world.");

        when(bookService.createBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The AI Revolution"))
                .andExpect(jsonPath("$.author").value("Jane Smith"))
                .andExpect(jsonPath("$.isbn").value("9781234567890"))
                .andExpect(jsonPath("$.publicationYear").value(2100))
                .andExpect(jsonPath("$.description").value("An in-depth look into how artificial intelligence is shaping the world."));

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("John Doe");

        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("John Doe"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void getBookById_ShouldReturn404_WhenBookDoesNotExist() throws Exception {
        when(bookService.getBookById(1L)).thenThrow(new ResourceNotFoundException("Book not found with ID: 1"));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isNotFound()) 
                .andExpect(jsonPath("$.error").value("Resource Not Found")) 
                .andExpect(jsonPath("$.message").value("Book not found with ID: 1"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook_WhenBookExists() throws Exception {
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");
        existingBook.setAuthor("John Doe");
        existingBook.setIsbn("9789876543210");
        existingBook.setPublicationYear(2000);
        existingBook.setDescription("Old description");

        Book updatedBook = new Book();
        updatedBook.setTitle("The AI Revolution");
        updatedBook.setAuthor("Jane Smith");
        updatedBook.setIsbn("9781234567890");
        updatedBook.setPublicationYear(2100);
        updatedBook.setDescription("An in-depth look into how artificial intelligence is shaping the world.");

        when(bookService.getBookById(1L)).thenReturn(Optional.of(existingBook));
        when(bookService.saveBook(any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The AI Revolution"))
                .andExpect(jsonPath("$.author").value("Jane Smith"))
                .andExpect(jsonPath("$.isbn").value("9781234567890"))
                .andExpect(jsonPath("$.publicationYear").value(2100))
                .andExpect(jsonPath("$.description").value("An in-depth look into how artificial intelligence is shaping the world."));

        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    void searchBooks_ShouldReturnEmptyList_WhenNoResults() throws Exception {
        when(bookService.searchBooks("Unknown", "Unknown")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/books/search?title=Unknown&author=Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookService, times(1)).searchBooks("Unknown", "Unknown");
    }

    @Test
    void getAiInsights_ShouldReturnGeneratedInsight() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("The AI Revolution");
        book.setAuthor("Jane Smith");
        book.setIsbn("9781234567890");
        book.setPublicationYear(2100);
        book.setDescription("An in-depth look into how artificial intelligence is shaping the world.");
        book.setTitle("AI and Future");
        book.setAuthor("Tech Author");

        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        when(aiService.generateInsight(book)).thenReturn("This is an AI insight.");

        mockMvc.perform(get("/books/1/ai-insights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aiInsight").value("This is an AI insight."));

        verify(aiService, times(1)).generateInsight(book);
    }

    @Test
    void updateBook_ShouldReturn400_WhenInvalidData() throws Exception {
        String invalidBookJson = "{}"; // JSON gol

        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBookJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBook_ShouldReturn400_WhenInvalidData() throws Exception {
        String invalidBookJson = "{\"title\": \"Test Book\"}"; 

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBookJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBook_ShouldReturn404_WhenBookDoesNotExist() throws Exception {
        Book updatedBook = new Book();
        updatedBook.setTitle("The AI Revolution");
        updatedBook.setAuthor("Jane Smith");
        updatedBook.setIsbn("9781234567890");
        updatedBook.setPublicationYear(2100);
        updatedBook.setDescription("AI insights");

        when(bookService.getBookById(1L)).thenThrow(new ResourceNotFoundException("Book not found with ID: 1"));

        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isNotFound()) 
                .andExpect(jsonPath("$.error").value("Resource Not Found")) 
                .andExpect(jsonPath("$.message").value("Book not found with ID: 1"));

        verify(bookService, never()).saveBook(any(Book.class));
    }

    @Test
    void deleteBook_ShouldReturn204_WhenBookExists() throws Exception {
        Book book = new Book();
        book.setId(1L);
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void deleteBook_ShouldReturn404_WhenBookDoesNotExist() throws Exception {
        when(bookService.getBookById(1L)).thenThrow(new ResourceNotFoundException("Book not found with ID: 1"));

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNotFound()) 
                .andExpect(jsonPath("$.error").value("Resource Not Found")) 
                .andExpect(jsonPath("$.message").value("Book not found with ID: 1"));

        verify(bookService, never()).deleteBook(anyLong());
    }

    @Test
    void searchBooks_ShouldReturnListOfBooks() throws Exception {
        Book book1 = new Book();
        book1.setTitle("Spring Boot");
        book1.setAuthor("John Doe");

        Book book2 = new Book();
        book2.setTitle("Java Programming");
        book2.setAuthor("Jane Doe");

        when(bookService.searchBooks("Spring Boot", "John Doe")).thenReturn(List.of(book1));

        mockMvc.perform(get("/books/search")
                .param("title", "Spring Boot")
                .param("author", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Boot"))
                .andExpect(jsonPath("$[0].author").value("John Doe"));

        verify(bookService, times(1)).searchBooks("Spring Boot", "John Doe");
    }
}
