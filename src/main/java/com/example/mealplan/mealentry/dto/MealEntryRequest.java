package com.example.mealplan.mealentry.dto;

import com.example.mealplan.mealentry.entity.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealEntryRequest {

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    private String description;

    private String notes;

    @NotNull(message = "Calories is required")
    @Min(value = 0, message = "Calories must be zero or positive")
    private Integer calories;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime targetTime;
}
