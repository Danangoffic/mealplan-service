package com.example.mealplan;

import com.example.mealplan.auth.dto.LoginRequest;
import com.example.mealplan.auth.dto.RegisterRequest;
import com.example.mealplan.auth.security.JwtTokenProvider;
import com.example.mealplan.mealentry.dto.MealEntryRequest;
import com.example.mealplan.mealentry.dto.UpdateMealStatusRequest;
import com.example.mealplan.mealentry.entity.MealEntry;
import com.example.mealplan.mealentry.entity.MealStatus;
import com.example.mealplan.mealentry.entity.MealType;
import com.example.mealplan.mealentry.repository.MealEntryRepository;
import com.example.mealplan.mealplan.dto.MealPlanRequest;
import com.example.mealplan.mealplan.entity.MealPlan;
import com.example.mealplan.mealplan.repository.MealPlanRepository;
import com.example.mealplan.user.entity.User;
import com.example.mealplan.user.entity.UserRole;
import com.example.mealplan.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MealApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MealPlanRepository mealPlanRepository;

    @MockBean
    private MealEntryRepository mealEntryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Danang")
                .email("danang@mail.com")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.USER)
                .build();

        jwtToken = "Bearer " + jwtTokenProvider.generateToken(testUser.getEmail());

        // Mock findByEmail for authentication check in filter
        when(userRepository.findByEmail("danang@mail.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Danang")
                .email("danang2@mail.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void testRegister_DuplicateEmail_ReturnsConflict() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Danang")
                .email("danang@mail.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("AUTH_DUPLICATE_EMAIL"));
    }

    @Test
    void testRegister_ValidationFailure_ReturnsBadRequest() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("")
                .email("danang")
                .password("pwd")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("danang@mail.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    void testCreateMealPlan_Success() throws Exception {
        MealPlanRequest request = MealPlanRequest.builder()
                .planDate(LocalDate.of(2026, 6, 4))
                .title("My Plan")
                .notes("Some notes")
                .build();

        MealPlan mealPlan = MealPlan.builder()
                .id(10L)
                .user(testUser)
                .planDate(request.getPlanDate())
                .title(request.getTitle())
                .notes(request.getNotes())
                .build();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(mealPlanRepository.existsByUserIdAndPlanDate(testUser.getId(), request.getPlanDate())).thenReturn(false);
        when(mealPlanRepository.save(any(MealPlan.class))).thenReturn(mealPlan);

        mockMvc.perform(post("/api/v1/meal-plans")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("My Plan"));
    }

    @Test
    void testAddMealEntry_Success() throws Exception {
        MealEntryRequest request = MealEntryRequest.builder()
                .mealType(MealType.BREAKFAST)
                .title("Oatmeal")
                .calories(300)
                .targetTime(LocalTime.of(8, 0))
                .build();

        MealPlan plan = MealPlan.builder()
                .id(10L)
                .user(testUser)
                .planDate(LocalDate.of(2026, 6, 4))
                .build();

        MealEntry entry = MealEntry.builder()
                .id(100L)
                .mealPlan(plan)
                .mealType(request.getMealType())
                .title(request.getTitle())
                .calories(request.getCalories())
                .targetTime(request.getTargetTime())
                .status(MealStatus.PENDING)
                .build();

        when(mealPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(mealEntryRepository.save(any(MealEntry.class))).thenReturn(entry);

        mockMvc.perform(post("/api/v1/meal-plans/10/entries")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Oatmeal"));
    }

    @Test
    void testUpdateMealStatus_Success() throws Exception {
        UpdateMealStatusRequest request = UpdateMealStatusRequest.builder()
                .status(MealStatus.COMPLETED)
                .build();

        MealPlan plan = MealPlan.builder()
                .id(10L)
                .user(testUser)
                .build();

        MealEntry entry = MealEntry.builder()
                .id(100L)
                .mealPlan(plan)
                .status(MealStatus.PENDING)
                .build();

        MealEntry updatedEntry = MealEntry.builder()
                .id(100L)
                .mealPlan(plan)
                .status(MealStatus.COMPLETED)
                .build();

        when(mealEntryRepository.findById(100L)).thenReturn(Optional.of(entry));
        when(mealEntryRepository.save(any(MealEntry.class))).thenReturn(updatedEntry);

        mockMvc.perform(patch("/api/v1/meal-entries/100/status")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void testGetDailyProgress_Success() throws Exception {
        LocalDate date = LocalDate.of(2026, 6, 4);

        MealPlan plan = MealPlan.builder()
                .id(10L)
                .user(testUser)
                .build();

        MealEntry entry = MealEntry.builder()
                .id(100L)
                .mealPlan(plan)
                .status(MealStatus.COMPLETED)
                .build();

        when(mealEntryRepository.findByMealPlanUserEmailAndMealPlanPlanDate(testUser.getEmail(), date))
                .thenReturn(Collections.singletonList(entry));

        mockMvc.perform(get("/api/v1/dashboard/daily")
                        .header("Authorization", jwtToken)
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalMeals").value(1))
                .andExpect(jsonPath("$.data.completedMeals").value(1))
                .andExpect(jsonPath("$.data.completionRate").value(100.0));
    }
}
