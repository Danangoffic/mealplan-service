package com.example.mealplan.mealentry.controller;

import com.example.mealplan.common.response.ApiResponse;
import com.example.mealplan.mealentry.dto.MealEntryRequest;
import com.example.mealplan.mealentry.dto.MealEntryResponse;
import com.example.mealplan.mealentry.dto.UpdateMealStatusRequest;
import com.example.mealplan.mealentry.service.MealEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MealEntryController {

    private final MealEntryService mealEntryService;

    public MealEntryController(MealEntryService mealEntryService) {
        this.mealEntryService = mealEntryService;
    }

    @PostMapping("/meal-plans/{mealPlanId}/entries")
    public ResponseEntity<ApiResponse<MealEntryResponse>> addMealEntry(
            @PathVariable Long mealPlanId,
            @Valid @RequestBody MealEntryRequest request) {
        MealEntryResponse response = mealEntryService.addMealEntry(mealPlanId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Meal entry added successfully", response));
    }

    @PutMapping("/meal-entries/{id}")
    public ResponseEntity<ApiResponse<MealEntryResponse>> updateMealEntry(
            @PathVariable Long id,
            @Valid @RequestBody MealEntryRequest request) {
        MealEntryResponse response = mealEntryService.updateMealEntry(id, request);
        return ResponseEntity.ok(ApiResponse.success("Meal entry updated successfully", response));
    }

    @PatchMapping("/meal-entries/{id}/status")
    public ResponseEntity<ApiResponse<MealEntryResponse>> updateMealStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMealStatusRequest request) {
        MealEntryResponse response = mealEntryService.updateMealStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Meal status updated successfully", response));
    }

    @DeleteMapping("/meal-entries/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMealEntry(@PathVariable Long id) {
        mealEntryService.deleteMealEntry(id);
        return ResponseEntity.ok(ApiResponse.success("Meal entry deleted successfully"));
    }
}
