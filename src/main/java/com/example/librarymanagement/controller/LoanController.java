package com.example.librarymanagement.controller;

import com.example.librarymanagement.entity.Loan;
import com.example.librarymanagement.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LibraryService service;
    public LoanController(LibraryService service) { this.service = service; }

    @GetMapping
    public List<Loan> all(@RequestParam(defaultValue = "false") boolean activeOnly) {
        return activeOnly ? service.activeLoans() : service.allLoans();
    }

    @PostMapping("/issue")
    @ResponseStatus(HttpStatus.CREATED)
    public Loan issue(@RequestBody IssueRequest request) {
        if (request.bookId() == null || request.memberId() == null) {
            throw new IllegalArgumentException("bookId and memberId are required");
        }
        int days = request.loanDays() == null ? 14 : request.loanDays();
        return service.issueBook(request.bookId(), request.memberId(), days);
    }

    @PostMapping("/{id}/return")
    public Loan returnBook(@PathVariable Long id) { return service.returnBook(id); }

    public record IssueRequest(Long bookId, Long memberId, Integer loanDays) {}
}
