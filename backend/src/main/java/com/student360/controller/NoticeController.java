package com.student360.controller;

import com.student360.dto.NoticeRequest;
import com.student360.dto.NoticeResponse;
import com.student360.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public List<NoticeResponse> getAll() {
        return noticeService.getAll();
    }

    @GetMapping("/{id}")
    public NoticeResponse getById(@PathVariable Long id) {
        return noticeService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoticeResponse create(@Valid @RequestBody NoticeRequest request, Authentication authentication) {
        return noticeService.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public NoticeResponse update(@PathVariable Long id, @Valid @RequestBody NoticeRequest request) {
        return noticeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        noticeService.delete(id);
    }

    @GetMapping("/recent")
    public List<NoticeResponse> getRecent() {
        return noticeService.getRecent();
    }
}
