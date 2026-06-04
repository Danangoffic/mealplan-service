package com.example.mealplan.mealentry.entity;

import com.example.mealplan.common.entity.Auditable;
import com.example.mealplan.mealplan.entity.MealPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "meal_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealEntry extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Column(name = "meal_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Integer calories;

    @Column(name = "target_time")
    private LocalTime targetTime;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MealStatus status;

    @PrePersist
    protected void onCreateMealEntry() {
        if (status == null) {
            status = MealStatus.PENDING;
        }
        if (calories == null) {
            calories = 0;
        }
    }
}
