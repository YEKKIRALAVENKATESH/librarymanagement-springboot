package com.example.librarymanagement.controller;

import com.example.librarymanagement.entity.Member;
import com.example.librarymanagement.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final LibraryService service;
    public MemberController(LibraryService service) { this.service = service; }

    @GetMapping
    public List<Member> all() { return service.allMembers(); }

    @GetMapping("/{id}")
    public Member one(@PathVariable Long id) { return service.getMember(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Member create(@Valid @RequestBody Member member) { return service.addMember(member); }

    @PutMapping("/{id}")
    public Member update(@PathVariable Long id, @Valid @RequestBody Member member) {
        return service.updateMember(id, member);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteMember(id); }
}
