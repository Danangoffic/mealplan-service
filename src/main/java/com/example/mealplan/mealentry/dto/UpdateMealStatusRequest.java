package com.example.mealplan.mealentry.dto;

import com.example.mealplan.mealentry.entity.MealStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMealStatusRequest {

    @NotNull(message = "Status is required")
    private MealStatus status;
}
