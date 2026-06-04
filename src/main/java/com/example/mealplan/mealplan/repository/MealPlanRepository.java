package com.example.mealplan.mealplan.repository;

import com.example.mealplan.mealplan.entity.MealPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    
    Optional<MealPlan> findByUserIdAndPlanDate(Long userId, LocalDate planDate);
    
    Optional<MealPlan> findByUserEmailAndPlanDate(String email, LocalDate planDate);
    
    @Query("SELECT mp FROM MealPlan mp WHERE mp.user.email = :email " +
           "AND (:startDate IS NULL OR mp.planDate >= :startDate) " +
           "AND (:endDate IS NULL OR mp.planDate <= :endDate)")
    Page<MealPlan> findByUserEmailAndDateRange(
            @Param("email") String email,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    boolean existsByUserIdAndPlanDate(Long userId, LocalDate planDate);

    List<MealPlan> findByUserEmailAndPlanDateBetween(String email, LocalDate startDate, LocalDate endDate);
}
