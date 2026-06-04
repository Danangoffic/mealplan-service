package com.example.mealplan.mealplan.dto;

import com.example.mealplan.mealentry.dto.MealEntryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanResponse {
    private Long id;
    private LocalDate planDate;
    private String title;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MealEntryResponse> mealEntries;
}
