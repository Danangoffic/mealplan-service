package com.example.mealplan.mealentry.repository;

import com.example.mealplan.mealentry.entity.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
    
    List<MealEntry> findByMealPlanUserIdAndMealPlanPlanDate(Long userId, LocalDate planDate);
    
    List<MealEntry> findByMealPlanUserEmailAndMealPlanPlanDate(String email, LocalDate planDate);
    
    List<MealEntry> findByMealPlanUserEmailAndMealPlanPlanDateBetween(String email, LocalDate startDate, LocalDate endDate);
}
