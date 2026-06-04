package com.example.mealplan.mealplan.controller;

import com.example.mealplan.common.response.ApiResponse;
import com.example.mealplan.common.response.PagedResponse;
import com.example.mealplan.mealplan.dto.MealPlanRequest;
import com.example.mealplan.mealplan.dto.MealPlanResponse;
import com.example.mealplan.mealplan.service.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MealPlanResponse>> createMealPlan(@Valid @RequestBody MealPlanRequest request) {
        MealPlanResponse response = mealPlanService.createMealPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Meal plan created successfully", response));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<MealPlanResponse>> getMealPlanByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MealPlanResponse response = mealPlanService.getMealPlanByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Meal plan retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<MealPlanResponse>>> getMealPlans(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<MealPlanResponse> response = mealPlanService.getMealPlans(startDate, endDate, page, size);
        return ResponseEntity.ok(ApiResponse.success("Meal plans retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MealPlanResponse>> updateMealPlan(
            @PathVariable Long id,
            @Valid @RequestBody MealPlanRequest request) {
        MealPlanResponse response = mealPlanService.updateMealPlan(id, request);
        return ResponseEntity.ok(ApiResponse.success("Meal plan updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMealPlan(@PathVariable Long id) {
        mealPlanService.deleteMealPlan(id);
        return ResponseEntity.ok(ApiResponse.success("Meal plan deleted successfully"));
    }
}
