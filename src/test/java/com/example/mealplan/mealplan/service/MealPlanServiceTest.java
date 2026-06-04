package com.example.mealplan.mealplan.service;

import com.example.mealplan.common.exception.DuplicateResourceException;
import com.example.mealplan.common.exception.UnauthorizedException;
import com.example.mealplan.mealplan.dto.MealPlanRequest;
import com.example.mealplan.mealplan.dto.MealPlanResponse;
import com.example.mealplan.mealplan.entity.MealPlan;
import com.example.mealplan.mealplan.repository.MealPlanRepository;
import com.example.mealplan.user.entity.User;
import com.example.mealplan.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealPlanServiceTest {

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MealPlanService mealPlanService;

    private User currentUser;
    private User otherUser;
    private MealPlanRequest mealPlanRequest;
    private MealPlan mealPlan;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(1L)
                .name("Danang")
                .email("danang@mail.com")
                .build();

        otherUser = User.builder()
                .id(2L)
                .name("Other")
                .email("other@mail.com")
                .build();

        mealPlanRequest = MealPlanRequest.builder()
                .planDate(LocalDate.of(2026, 6, 4))
                .title("Healthy Breakfast")
                .notes("High protein")
                .build();

        mealPlan = MealPlan.builder()
                .id(10L)
                .user(currentUser)
                .planDate(LocalDate.of(2026, 6, 4))
                .title("Healthy Breakfast")
                .notes("High protein")
                .mealEntries(new ArrayList<>())
                .build();

        mockSecurityContext("danang@mail.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createMealPlan_Success() {
        when(userRepository.findByEmail("danang@mail.com")).thenReturn(Optional.of(currentUser));
        when(mealPlanRepository.existsByUserIdAndPlanDate(1L, mealPlanRequest.getPlanDate())).thenReturn(false);
        when(mealPlanRepository.save(any(MealPlan.class))).thenReturn(mealPlan);

        MealPlanResponse response = mealPlanService.createMealPlan(mealPlanRequest);

        assertNotNull(response);
        assertEquals(mealPlan.getId(), response.getId());
        assertEquals(mealPlan.getTitle(), response.getTitle());
    }

    @Test
    void createMealPlan_DuplicateDate_ThrowsException() {
        when(userRepository.findByEmail("danang@mail.com")).thenReturn(Optional.of(currentUser));
        when(mealPlanRepository.existsByUserIdAndPlanDate(1L, mealPlanRequest.getPlanDate())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> mealPlanService.createMealPlan(mealPlanRequest));

        verify(mealPlanRepository, never()).save(any(MealPlan.class));
    }

    @Test
    void getMealPlanByDate_Success() {
        when(mealPlanRepository.findByUserEmailAndPlanDate("danang@mail.com", LocalDate.of(2026, 6, 4)))
                .thenReturn(Optional.of(mealPlan));

        MealPlanResponse response = mealPlanService.getMealPlanByDate(LocalDate.of(2026, 6, 4));

        assertNotNull(response);
        assertEquals(mealPlan.getId(), response.getId());
    }

    @Test
    void updateMealPlan_OtherUserPlan_ThrowsException() {
        MealPlan otherPlan = MealPlan.builder()
                .id(20L)
                .user(otherUser)
                .planDate(LocalDate.of(2026, 6, 4))
                .build();

        when(mealPlanRepository.findById(20L)).thenReturn(Optional.of(otherPlan));

        assertThrows(UnauthorizedException.class, () -> mealPlanService.updateMealPlan(20L, mealPlanRequest));
    }
}
