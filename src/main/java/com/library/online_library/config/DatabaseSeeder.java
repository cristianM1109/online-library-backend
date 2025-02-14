package com.library.online_library.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.library.online_library.model.Book;
import com.library.online_library.repository.BookRepository;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() == 0) { // Avoid dupplicate books
                bookRepository.saveAll(List.of(
                        new Book("The AI Revolution", "Jane Smith", "9780134685991", 2022, "An insightful book about AI."),
                        new Book("Spring Boot in Action", "Craig Walls", "9781617292545", 2019, "A practical guide to Spring Boot."),
                        new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, "A must-read book for software engineers."),
                        new Book("Deep Learning", "Ian Goodfellow", "9780262035613", 2016, "A comprehensive book on deep learning."),
                        new Book("The Pragmatic Programmer", "Andrew Hunt", "9780201616224", 1999, "A book full of programming wisdom."),
                        new Book("Introduction to Algorithms", "Thomas H. Cormen", "9780262033848", 2009, "A foundational book on algorithms."),
                        new Book("You Don't Know JS", "Kyle Simpson", "9781491904244", 2015, "A deep dive into JavaScript."),
                        new Book("Design Patterns", "Erich Gamma", "9780201633610", 1994, "A classic book on software design patterns."),
                        new Book("Java Concurrency in Practice", "Brian Goetz", "9780321349606", 2006, "A detailed book on Java concurrency."),
                        new Book("The Mythical Man-Month", "Frederick P. Brooks Jr.", "9780201835954", 1975, "Essays on software engineering."),
                        new Book("Cracking the Coding Interview", "Gayle Laakmann McDowell", "9780984782857", 2015, "A guide to technical interviews."),
                        new Book("Eloquent JavaScript", "Marijn Haverbeke", "9781593279509", 2018, "A modern introduction to JavaScript."),
                        new Book("Refactoring", "Martin Fowler", "9780134757599", 2018, "Improving the design of existing code."),
                        new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018, "Best practices for writing Java code."),
                        new Book("The Phoenix Project", "Gene Kim", "9780988262591", 2013, "A novel about DevOps and IT management.")
                ));
                System.out.println("Database seeded with default books!");
            }
        };
    }
}
