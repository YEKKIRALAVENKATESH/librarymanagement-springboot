package com.example.librarymanagement.controller;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final LibraryService service;

    public BookController(LibraryService service) {
        this.service = service;
    }

    // 1. GET: Retrieve all books
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> all(
            @RequestParam(required = false) String q) {

        List<Book> books = service.searchBooks(q);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Books retrieved successfully",
                        books
                )
        );
    }

    // 2. GET: Retrieve a book by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> one(
            @PathVariable Long id) {

        Book book = service.getBook(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Book retrieved successfully",
                        book
                )
        );
    }

    // 3. POST: Create a new book, or update if id is provided in the JSON body
    @PostMapping
    public ResponseEntity<ApiResponse<Book>> createOrUpdate(
            @Valid @RequestBody Book book) {

        // If ID is provided in JSON body, update existing book
        if (book.getId() != null) {
            Book updatedBook = service.updateBook(book.getId(), book);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            200,
                            "Book updated successfully",
                            updatedBook
                    )
            );
        }

        // If no ID is provided, create a new book
        Book savedBook = service.addBook(book);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        201,
                        "Book created successfully",
                        savedBook
                )
        );
    }

    // 4. POST: Update a book by ID in the URL path (POST /api/books/{id})
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> update(
            @PathVariable Long id,
            @Valid @RequestBody Book book) {

        Book updatedBook = service.updateBook(id, book);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Book updated successfully",
                        updatedBook
                )
        );
    }

    // 5. DELETE: Delete a book by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        service.deleteBook(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Book deleted successfully",
                        null
                )
        );
    }
}
