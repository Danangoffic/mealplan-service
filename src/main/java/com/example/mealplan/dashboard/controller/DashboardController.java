package com.example.mealplan.dashboard.controller;

import com.example.mealplan.common.response.ApiResponse;
import com.example.mealplan.dashboard.dto.DailyProgressResponse;
import com.example.mealplan.dashboard.dto.WeeklyProgressResponse;
import com.example.mealplan.dashboard.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyProgressResponse>> getDailyProgress(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyProgressResponse response = dashboardService.getDailyProgress(date);
        return ResponseEntity.ok(ApiResponse.success("Daily progress retrieved successfully", response));
    }

    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<WeeklyProgressResponse>> getWeeklyProgress(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        WeeklyProgressResponse response = dashboardService.getWeeklyProgress(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Weekly progress retrieved successfully", response));
    }
}
