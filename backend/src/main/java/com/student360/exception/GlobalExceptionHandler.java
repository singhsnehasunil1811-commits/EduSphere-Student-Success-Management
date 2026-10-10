package com.student360.exception;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, Object>> body(HttpStatus s, String msg, Map<String, String> errors) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", s.value()); m.put("message", msg); if (errors != null) m.put("errors", errors);
        return ResponseEntity.status(s).body(m);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> notFound(ResourceNotFoundException e) { return body(HttpStatus.NOT_FOUND, e.getMessage(), null); }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> duplicate(DuplicateResourceException e) { return body(HttpStatus.CONFLICT, e.getMessage(), null); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalid(MethodArgumentNotValidException e) {
        Map<String, String> errs = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errs.put(f.getField(), f.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, "Validation failed", errs);
    }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<?> denied(org.springframework.security.access.AccessDeniedException e) { return body(HttpStatus.FORBIDDEN, "Access denied", null); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> other(Exception e) {
        e.printStackTrace();
        return body(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() != null ? e.getMessage() : "Unexpected server error", null);
    }
}
