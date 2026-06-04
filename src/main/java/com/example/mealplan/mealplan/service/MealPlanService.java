package com.example.mealplan.mealplan.service;

import com.example.mealplan.common.exception.BadRequestException;
import com.example.mealplan.common.exception.DuplicateResourceException;
import com.example.mealplan.common.exception.ErrorCode;
import com.example.mealplan.common.exception.ResourceNotFoundException;
import com.example.mealplan.common.exception.UnauthorizedException;
import com.example.mealplan.common.response.PagedResponse;
import com.example.mealplan.common.util.SecurityUtils;
import com.example.mealplan.mealentry.dto.MealEntryResponse;
import com.example.mealplan.mealentry.entity.MealEntry;
import com.example.mealplan.mealplan.dto.MealPlanRequest;
import com.example.mealplan.mealplan.dto.MealPlanResponse;
import com.example.mealplan.mealplan.entity.MealPlan;
import com.example.mealplan.mealplan.repository.MealPlanRepository;
import com.example.mealplan.user.entity.User;
import com.example.mealplan.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository, UserRepository userRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @CacheEvict(value = {"dailyProgress", "weeklyProgress"}, allEntries = true)
    public MealPlanResponse createMealPlan(MealPlanRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", ErrorCode.RESOURCE_NOT_FOUND));

        if (mealPlanRepository.existsByUserIdAndPlanDate(user.getId(), request.getPlanDate())) {
            throw new DuplicateResourceException("Meal plan already exists for this date", ErrorCode.MEAL_PLAN_DUPLICATE_DATE);
        }

        MealPlan mealPlan = MealPlan.builder()
                .user(user)
                .planDate(request.getPlanDate())
                .title(request.getTitle())
                .notes(request.getNotes())
                .build();

        MealPlan savedPlan = mealPlanRepository.save(mealPlan);
        return mapToResponse(savedPlan);
    }

    @Transactional(readOnly = true)
    public MealPlanResponse getMealPlanByDate(LocalDate date) {
        String email = SecurityUtils.getCurrentUserEmail();
        MealPlan mealPlan = mealPlanRepository.findByUserEmailAndPlanDate(email, date)
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found for date " + date, ErrorCode.MEAL_PLAN_NOT_FOUND));

        return mapToResponse(mealPlan);
    }

    @Transactional(readOnly = true)
    public PagedResponse<MealPlanResponse> getMealPlans(LocalDate startDate, LocalDate endDate, int page, int size) {
        String email = SecurityUtils.getCurrentUserEmail();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "planDate"));

        Page<MealPlan> mealPlansPage = mealPlanRepository.findByUserEmailAndDateRange(email, startDate, endDate, pageable);

        List<MealPlanResponse> content = mealPlansPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<MealPlanResponse>builder()
                .content(content)
                .page(mealPlansPage.getNumber())
                .size(mealPlansPage.getSize())
                .totalElements(mealPlansPage.getTotalElements())
                .totalPages(mealPlansPage.getTotalPages())
                .last(mealPlansPage.isLast())
                .build();
    }

    @Transactional
    @CacheEvict(value = {"dailyProgress", "weeklyProgress"}, allEntries = true)
    public MealPlanResponse updateMealPlan(Long id, MealPlanRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found", ErrorCode.MEAL_PLAN_NOT_FOUND));

        if (!mealPlan.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized to access this resource", ErrorCode.AUTH_FORBIDDEN);
        }

        // If changing date, verify there is no duplicate
        if (!mealPlan.getPlanDate().equals(request.getPlanDate())) {
            Optional<MealPlan> existingPlan = mealPlanRepository.findByUserIdAndPlanDate(mealPlan.getUser().getId(), request.getPlanDate());
            if (existingPlan.isPresent()) {
                throw new DuplicateResourceException("Meal plan already exists for date " + request.getPlanDate(), ErrorCode.MEAL_PLAN_DUPLICATE_DATE);
            }
            mealPlan.setPlanDate(request.getPlanDate());
        }

        mealPlan.setTitle(request.getTitle());
        mealPlan.setNotes(request.getNotes());

        MealPlan updatedPlan = mealPlanRepository.save(mealPlan);
        return mapToResponse(updatedPlan);
    }

    @Transactional
    @CacheEvict(value = {"dailyProgress", "weeklyProgress"}, allEntries = true)
    public void deleteMealPlan(Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found", ErrorCode.MEAL_PLAN_NOT_FOUND));

        if (!mealPlan.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("User is not authorized to access this resource", ErrorCode.AUTH_FORBIDDEN);
        }

        mealPlanRepository.delete(mealPlan);
    }

    private MealPlanResponse mapToResponse(MealPlan mealPlan) {
        List<MealEntryResponse> entries = Optional.ofNullable(mealPlan.getMealEntries())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::mapEntryToResponse)
                .collect(Collectors.toList());

        return MealPlanResponse.builder()
                .id(mealPlan.getId())
                .planDate(mealPlan.getPlanDate())
                .title(mealPlan.getTitle())
                .notes(mealPlan.getNotes())
                .createdAt(mealPlan.getCreatedAt())
                .updatedAt(mealPlan.getUpdatedAt())
                .mealEntries(entries)
                .build();
    }

    private MealEntryResponse mapEntryToResponse(MealEntry entry) {
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
