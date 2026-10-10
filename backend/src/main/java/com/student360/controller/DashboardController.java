package com.student360.controller;
import com.student360.service.DashboardService;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service) { this.service = service; }
    @GetMapping("/stats") public Map<String, Object> stats() { return service.stats(); }
}
