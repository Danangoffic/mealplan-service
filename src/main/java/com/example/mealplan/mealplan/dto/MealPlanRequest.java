package com.example.mealplan.mealplan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanRequest {

    @NotNull(message = "Plan date is required")
    private LocalDate planDate;

    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    private String notes;
}
