package com.student360.controller;

import com.student360.dto.TimetableRequest;
import com.student360.dto.TimetableResponse;
import com.student360.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;

    @GetMapping
    public List<TimetableResponse> getAll(
            @RequestParam(required = false) String day,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long teacherId) {
            
        if (day != null) {
            return timetableService.getByDay(day);
        } else if (courseId != null) {
            return timetableService.getByCourse(courseId);
        } else if (teacherId != null) {
            return timetableService.getByTeacher(teacherId);
        }
        return timetableService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimetableResponse create(@Valid @RequestBody TimetableRequest request) {
        return timetableService.create(request);
    }

    @PutMapping("/{id}")
    public TimetableResponse update(@PathVariable Long id, @Valid @RequestBody TimetableRequest request) {
        return timetableService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        timetableService.delete(id);
    }
}
