package com.example.mealplan.dashboard.service;

import com.example.mealplan.dashboard.dto.DailyProgressResponse;
import com.example.mealplan.dashboard.dto.WeeklyProgressResponse;
import com.example.mealplan.mealentry.entity.MealEntry;
import com.example.mealplan.mealentry.entity.MealStatus;
import com.example.mealplan.mealentry.repository.MealEntryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final MealEntryRepository mealEntryRepository;

    public DashboardService(MealEntryRepository mealEntryRepository) {
        this.mealEntryRepository = mealEntryRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "dailyProgress", key = "#email + '-' + #date")
    public DailyProgressResponse getDailyProgress(String email, LocalDate date) {
        List<MealEntry> entries = mealEntryRepository.findByMealPlanUserEmailAndMealPlanPlanDate(email, date);

        int totalMeals = entries.size();
        int completedMeals = 0;
        int skippedMeals = 0;
        int pendingMeals = 0;

        for (MealEntry entry : entries) {
            if (entry.getStatus() == MealStatus.COMPLETED) {
                completedMeals++;
            } else if (entry.getStatus() == MealStatus.SKIPPED) {
                skippedMeals++;
            } else if (entry.getStatus() == MealStatus.PENDING) {
                pendingMeals++;
            }
        }

        double completionRate = 0.0;
        if (totalMeals > 0) {
            completionRate = ((double) completedMeals / totalMeals) * 100;
            completionRate = Math.round(completionRate * 100.0) / 100.0;
        }

        return DailyProgressResponse.builder()
                .date(date)
                .totalMeals(totalMeals)
                .completedMeals(completedMeals)
                .skippedMeals(skippedMeals)
                .pendingMeals(pendingMeals)
                .completionRate(completionRate)
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "weeklyProgress", key = "#email + '-' + #startDate + '-' + #endDate")
    public WeeklyProgressResponse getWeeklyProgress(String email, LocalDate startDate, LocalDate endDate) {
        List<MealEntry> entries = mealEntryRepository.findByMealPlanUserEmailAndMealPlanPlanDateBetween(email, startDate, endDate);

        int totalMeals = entries.size();
        int completedMeals = 0;
        int skippedMeals = 0;
        int pendingMeals = 0;

        for (MealEntry entry : entries) {
            if (entry.getStatus() == MealStatus.COMPLETED) {
                completedMeals++;
            } else if (entry.getStatus() == MealStatus.SKIPPED) {
                skippedMeals++;
            } else if (entry.getStatus() == MealStatus.PENDING) {
                pendingMeals++;
            }
        }

        double completionRate = 0.0;
        if (totalMeals > 0) {
            completionRate = ((double) completedMeals / totalMeals) * 100;
            completionRate = Math.round(completionRate * 100.0) / 100.0;
        }

        return WeeklyProgressResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalMeals(totalMeals)
                .completedMeals(completedMeals)
                .skippedMeals(skippedMeals)
                .pendingMeals(pendingMeals)
                .completionRate(completionRate)
                .build();
    }
}
