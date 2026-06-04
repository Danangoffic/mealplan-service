package com.example.mealplan.mealentry.dto;

import com.example.mealplan.mealentry.entity.MealStatus;
import com.example.mealplan.mealentry.entity.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealEntryResponse {
    private Long id;
    private Long mealPlanId;
    private MealType mealType;
    private String title;
    private String description;
    private String notes;
    private Integer calories;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime targetTime;
    
    private MealStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
