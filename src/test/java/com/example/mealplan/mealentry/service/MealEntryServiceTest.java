package com.example.mealplan.mealentry.service;

import com.example.mealplan.common.exception.UnauthorizedException;
import com.example.mealplan.mealentry.dto.MealEntryRequest;
import com.example.mealplan.mealentry.dto.MealEntryResponse;
import com.example.mealplan.mealentry.entity.MealEntry;
import com.example.mealplan.mealentry.entity.MealStatus;
import com.example.mealplan.mealentry.entity.MealType;
import com.example.mealplan.mealentry.repository.MealEntryRepository;
import com.example.mealplan.mealplan.entity.MealPlan;
import com.example.mealplan.mealplan.repository.MealPlanRepository;
import com.example.mealplan.user.entity.User;
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
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealEntryServiceTest {

    @Mock
    private MealEntryRepository mealEntryRepository;

    @Mock
    private MealPlanRepository mealPlanRepository;

    @InjectMocks
    private MealEntryService mealEntryService;

    private User currentUser;
    private User otherUser;
    private MealPlan userMealPlan;
    private MealPlan otherMealPlan;
    private MealEntryRequest mealEntryRequest;
    private MealEntry mealEntry;

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

        userMealPlan = MealPlan.builder()
                .id(100L)
                .user(currentUser)
                .planDate(LocalDate.of(2026, 6, 4))
                .build();

        otherMealPlan = MealPlan.builder()
                .id(200L)
                .user(otherUser)
                .planDate(LocalDate.of(2026, 6, 4))
                .build();

        mealEntryRequest = MealEntryRequest.builder()
                .mealType(MealType.BREAKFAST)
                .title("Oatmeal")
                .calories(350)
                .targetTime(LocalTime.of(8, 0))
                .build();

        mealEntry = MealEntry.builder()
                .id(1000L)
                .mealPlan(userMealPlan)
                .mealType(MealType.BREAKFAST)
                .title("Oatmeal")
                .calories(350)
                .targetTime(LocalTime.of(8, 0))
                .status(MealStatus.PENDING)
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
    void addMealEntry_Success() {
        when(mealPlanRepository.findById(100L)).thenReturn(Optional.of(userMealPlan));
        when(mealEntryRepository.save(any(MealEntry.class))).thenReturn(mealEntry);

        MealEntryResponse response = mealEntryService.addMealEntry(100L, mealEntryRequest);

        assertNotNull(response);
        assertEquals(mealEntry.getId(), response.getId());
        assertEquals(MealStatus.PENDING, response.getStatus());
    }

    @Test
    void addMealEntry_OtherUserPlan_ThrowsException() {
        when(mealPlanRepository.findById(200L)).thenReturn(Optional.of(otherMealPlan));

        assertThrows(UnauthorizedException.class, () -> mealEntryService.addMealEntry(200L, mealEntryRequest));

        verify(mealEntryRepository, never()).save(any(MealEntry.class));
    }

    @Test
    void updateMealEntry_Success() {
        when(mealEntryRepository.findById(1000L)).thenReturn(Optional.of(mealEntry));
        when(mealEntryRepository.save(any(MealEntry.class))).thenReturn(mealEntry);

        MealEntryResponse response = mealEntryService.updateMealEntry(1000L, mealEntryRequest);

        assertNotNull(response);
        assertEquals(mealEntry.getId(), response.getId());
    }

    @Test
    void updateMealEntry_OtherUserEntry_ThrowsException() {
        MealEntry otherEntry = MealEntry.builder()
                .id(2000L)
                .mealPlan(otherMealPlan)
                .build();

        when(mealEntryRepository.findById(2000L)).thenReturn(Optional.of(otherEntry));

        assertThrows(UnauthorizedException.class, () -> mealEntryService.updateMealEntry(2000L, mealEntryRequest));
    }

    @Test
    void updateMealStatus_Success() {
        when(mealEntryRepository.findById(1000L)).thenReturn(Optional.of(mealEntry));
        
        MealEntry completedEntry = MealEntry.builder()
                .id(1000L)
                .mealPlan(userMealPlan)
                .mealType(MealType.BREAKFAST)
                .title("Oatmeal")
                .calories(350)
                .targetTime(LocalTime.of(8, 0))
                .status(MealStatus.COMPLETED)
                .build();
        when(mealEntryRepository.save(any(MealEntry.class))).thenReturn(completedEntry);

        MealEntryResponse response = mealEntryService.updateMealStatus(1000L, MealStatus.COMPLETED);

        assertNotNull(response);
        assertEquals(MealStatus.COMPLETED, response.getStatus());
    }
}
