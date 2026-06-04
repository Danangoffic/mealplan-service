package com.example.mealplan.dashboard.dto;

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
public class WeeklyProgressResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalMeals;
    private int completedMeals;
    private int skippedMeals;
    private int pendingMeals;
    private double completionRate;
}
