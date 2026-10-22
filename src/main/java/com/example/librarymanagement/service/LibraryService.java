package com.example.librarymanagement.service;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Loan;
import com.example.librarymanagement.entity.Member;
import com.example.librarymanagement.exception.NotFoundException;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.LoanRepository;
import com.example.librarymanagement.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LibraryService {

    private final BookRepository books;
    private final MemberRepository members;
    private final LoanRepository loans;

    public LibraryService(BookRepository books, MemberRepository members, LoanRepository loans) {
        this.books = books;
        this.members = members;
        this.loans = loans;
    }

    // --- Book Operations ---

    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) {
            return books.findAll();
        }
        return books.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }

    public Book getBook(Long id) {
        return books.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with id: " + id));
    }

    public Book addBook(Book book) {
        return books.save(book);
    }

    public Book updateBook(Long id, Book updatedBook) {
        Book existing = getBook(id);
        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setIsbn(updatedBook.getIsbn());
        existing.setTotalCopies(updatedBook.getTotalCopies());
        existing.setAvailableCopies(updatedBook.getAvailableCopies());
        return books.save(existing);
    }

    public void deleteBook(Long id) {
        Book book = getBook(id);
        if (loans.existsByBookIdAndReturnDateIsNull(id)) {
            throw new IllegalArgumentException("Cannot delete book while it has active loans");
        }
        books.delete(book);
    }

    // --- Member Operations ---

    public List<Member> allMembers() {
        return members.findAll();
    }

    public Member getMember(Long id) {
        return members.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found with id: " + id));
    }

    public Member addMember(Member member) {
        if (members.existsByEmailIgnoreCase(member.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + member.getEmail());
        }
        return members.save(member);
    }

    public Member updateMember(Long id, Member updatedMember) {
        Member existing = getMember(id);
        if (!existing.getEmail().equalsIgnoreCase(updatedMember.getEmail()) &&
                members.existsByEmailIgnoreCase(updatedMember.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + updatedMember.getEmail());
        }
        existing.setName(updatedMember.getName());
        existing.setEmail(updatedMember.getEmail());
        existing.setPhone(updatedMember.getPhone());
        return members.save(existing);
    }

    public void deleteMember(Long id) {
        Member member = getMember(id);
        if (loans.existsByMemberIdAndReturnDateIsNull(id)) {
            throw new IllegalArgumentException("Cannot delete member while they have active loans");
        }
        members.delete(member);
    }

    // --- Loan Operations ---

    public List<Loan> allLoans() {
        return loans.findAll();
    }

    public List<Loan> activeLoans() {
        return loans.findByReturnDateIsNull();
    }

    @Transactional
    public Loan issueBook(Long bookId, Long memberId, int loanDays) {
        if (loanDays < 1 || loanDays > 90) {
            throw new IllegalArgumentException("Loan days must be between 1 and 90");
        }

        Book book = getBook(bookId);
        Member member = getMember(memberId);

        // Check whether a copy is available
        if (book.getAvailableCopies() <= 0) {
            // Find the active loan with the earliest due date
            Loan activeLoan = loans
                    .findFirstByBook_IdAndReturnDateIsNullOrderByDueDateAsc(bookId)
                    .orElse(null);

            if (activeLoan != null) {
                String expectedDate = activeLoan.getDueDate()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE);

                throw new IllegalArgumentException(
                        "Book is currently unavailable. Expected return date: " + expectedDate
                );
            }

            throw new IllegalArgumentException(
                "No copies are currently available. Expected return date is unknown."
            );
        }

        // Reduce the available copies by one
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        books.save(book);

        // Create the loan record
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setIssueDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(loanDays));

        return loans.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loans.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found with id: " + loanId));

        if (loan.getReturnDate() != null) {
            throw new IllegalArgumentException("Book has already been returned");
        }

        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        books.save(book);

        return loans.save(loan);
    }
}
