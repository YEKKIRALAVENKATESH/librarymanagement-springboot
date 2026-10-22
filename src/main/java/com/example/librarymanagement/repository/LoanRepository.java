
package com.example.librarymanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.librarymanagement.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByReturnDateIsNull();

    boolean existsByBookIdAndReturnDateIsNull(Long bookId);

    boolean existsByMemberIdAndReturnDateIsNull(Long memberId);

    // Find the active loan with the earliest due date for a book
    Optional<Loan> findFirstByBook_IdAndReturnDateIsNullOrderByDueDateAsc(
            Long bookId
    );
}

