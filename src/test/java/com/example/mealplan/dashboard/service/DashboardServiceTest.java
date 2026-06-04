package com.example.mealplan.dashboard.service;

import com.example.mealplan.dashboard.dto.DailyProgressResponse;
import com.example.mealplan.dashboard.dto.WeeklyProgressResponse;
import com.example.mealplan.mealentry.entity.MealEntry;
import com.example.mealplan.mealentry.entity.MealStatus;
import com.example.mealplan.mealentry.repository.MealEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private MealEntryRepository mealEntryRepository;

    @InjectMocks
    private DashboardService dashboardService;



    @Test
    void getDailyProgress_Success() {
        LocalDate date = LocalDate.of(2026, 6, 4);
        List<MealEntry> entries = new ArrayList<>();
        entries.add(MealEntry.builder().status(MealStatus.COMPLETED).build());
        entries.add(MealEntry.builder().status(MealStatus.COMPLETED).build());
        entries.add(MealEntry.builder().status(MealStatus.SKIPPED).build());
        entries.add(MealEntry.builder().status(MealStatus.PENDING).build());

        when(mealEntryRepository.findByMealPlanUserEmailAndMealPlanPlanDate("danang@mail.com", date))
                .thenReturn(entries);

        DailyProgressResponse response = dashboardService.getDailyProgress("danang@mail.com", date);

        assertNotNull(response);
        assertEquals(date, response.getDate());
        assertEquals(4, response.getTotalMeals());
        assertEquals(2, response.getCompletedMeals());
        assertEquals(1, response.getSkippedMeals());
        assertEquals(1, response.getPendingMeals());
        assertEquals(50.0, response.getCompletionRate());
    }

    @Test
    void getDailyProgress_ZeroMeals_ReturnsZeroCompletionRate() {
        LocalDate date = LocalDate.of(2026, 6, 4);
        when(mealEntryRepository.findByMealPlanUserEmailAndMealPlanPlanDate("danang@mail.com", date))
                .thenReturn(Collections.emptyList());

        DailyProgressResponse response = dashboardService.getDailyProgress("danang@mail.com", date);

        assertNotNull(response);
        assertEquals(0, response.getTotalMeals());
        assertEquals(0.0, response.getCompletionRate());
    }

    @Test
    void getWeeklyProgress_Success() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 7);

        List<MealEntry> entries = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            entries.add(MealEntry.builder().status(MealStatus.COMPLETED).build());
        }
        for (int i = 0; i < 2; i++) {
            entries.add(MealEntry.builder().status(MealStatus.SKIPPED).build());
        }
        for (int i = 0; i < 4; i++) {
            entries.add(MealEntry.builder().status(MealStatus.PENDING).build());
        }

        when(mealEntryRepository.findByMealPlanUserEmailAndMealPlanPlanDateBetween("danang@mail.com", start, end))
                .thenReturn(entries);

        WeeklyProgressResponse response = dashboardService.getWeeklyProgress("danang@mail.com", start, end);

        assertNotNull(response);
        assertEquals(start, response.getStartDate());
        assertEquals(end, response.getEndDate());
        assertEquals(20, response.getTotalMeals());
        assertEquals(14, response.getCompletedMeals());
        assertEquals(2, response.getSkippedMeals());
        assertEquals(4, response.getPendingMeals());
        assertEquals(70.0, response.getCompletionRate());
    }
}
