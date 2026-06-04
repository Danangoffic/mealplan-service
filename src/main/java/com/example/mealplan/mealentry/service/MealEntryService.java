package com.example.mealplan.mealentry.service;

import com.example.mealplan.common.exception.ResourceNotFoundException;
import com.example.mealplan.common.exception.UnauthorizedException;
import com.example.mealplan.common.exception.ErrorCode;
import com.example.mealplan.common.util.SecurityUtils;
import com.example.mealplan.mealentry.dto.MealEntryRequest;
import com.example.mealplan.mealentry.dto.MealEntryResponse;
import com.example.mealplan.mealentry.entity.MealEntry;
import com.example.mealplan.mealentry.entity.MealStatus;
import com.example.mealplan.mealentry.repository.MealEntryRepository;
import com.example.mealplan.mealplan.entity.MealPlan;
import com.example.mealplan.mealplan.repository.MealPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MealEntryService {

    private final MealEntryRepository mealEntryRepository;
    private final MealPlanRepository mealPlanRepository;

    public MealEntryService(MealEntryRepository mealEntryRepository, MealPlanRepository mealPlanRepository) {
        this.mealEntryRepository = mealEntryRepository;
        this.mealPlanRepository = mealPlanRepository;
    }

    @Transactional
    public MealEntryResponse addMealEntry(Long mealPlanId, MealEntryRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        MealPlan mealPlan = mealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found", ErrorCode.MEAL_PLAN_NOT_FOUND));

        if (!mealPlan.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized to access this resource", ErrorCode.AUTH_FORBIDDEN);
        }

        MealEntry entry = MealEntry.builder()
                .mealPlan(mealPlan)
                .mealType(request.getMealType())
                .title(request.getTitle())
                .description(request.getDescription())
                .notes(request.getNotes())
                .calories(request.getCalories())
                .targetTime(request.getTargetTime())
                .status(MealStatus.PENDING)
                .build();

        MealEntry savedEntry = mealEntryRepository.save(entry);
        return mapToResponse(savedEntry);
    }

    @Transactional
    public MealEntryResponse updateMealEntry(Long id, MealEntryRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        MealEntry entry = mealEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal entry not found", ErrorCode.MEAL_ENTRY_NOT_FOUND));

        if (!entry.getMealPlan().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized to access this resource", ErrorCode.AUTH_FORBIDDEN);
        }

        entry.setMealType(request.getMealType());
        entry.setTitle(request.getTitle());
        entry.setDescription(request.getDescription());
        entry.setNotes(request.getNotes());
        entry.setCalories(request.getCalories());
        entry.setTargetTime(request.getTargetTime());

        MealEntry updatedEntry = mealEntryRepository.save(entry);
        return mapToResponse(updatedEntry);
    }

    @Transactional
    public MealEntryResponse updateMealStatus(Long id, MealStatus status) {
        String email = SecurityUtils.getCurrentUserEmail();
        MealEntry entry = mealEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal entry not found", ErrorCode.MEAL_ENTRY_NOT_FOUND));

        if (!entry.getMealPlan().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized to access this resource", ErrorCode.AUTH_FORBIDDEN);
        }

        entry.setStatus(status);
        MealEntry updatedEntry = mealEntryRepository.save(entry);
        return mapToResponse(updatedEntry);
    }

    @Transactional
    public void deleteMealEntry(Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        MealEntry entry = mealEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal entry not found", ErrorCode.MEAL_ENTRY_NOT_FOUND));

        if (!entry.getMealPlan().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized to access this resource", ErrorCode.AUTH_FORBIDDEN);
        }

        mealEntryRepository.delete(entry);
    }

    private MealEntryResponse mapToResponse(MealEntry entry) {
        return MealEntryResponse.builder()
                .id(entry.getId())
                .mealPlanId(entry.getMealPlan().getId())
                .mealType(entry.getMealType())
                .title(entry.getTitle())
                .description(entry.getDescription())
                .notes(entry.getNotes())
                .calories(entry.getCalories())
                .targetTime(entry.getTargetTime())
                .status(entry.getStatus())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }
}
