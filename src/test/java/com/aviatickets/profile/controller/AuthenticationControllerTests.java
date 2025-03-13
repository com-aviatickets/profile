package com.aviatickets.profile.controller;

import com.aviatickets.profile.ProfileApplication;
import com.aviatickets.profile.controller.request.LoginRequest;
import com.aviatickets.profile.model.User;
import com.aviatickets.profile.repository.UserRepository;
import com.aviatickets.profile.service.UserEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ProfileApplication.class
)
@AutoConfigureMockMvc
public class AuthenticationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserEventProducer userEventProducer;

    @BeforeEach
    public void setUp(){
        assert objectMapper != null : "ObjectMapper should not be null";
    }

    @Test
    public void testSingUp_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("newUser", "password");

        when(userEventProducer.sendEvent(any())).thenReturn(true);

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("aviaticketsAccessToken"))
                .andExpect(cookie().exists("aviaticketsRefreshToken"));
        Optional<User> optPerson = userRepository.findByUsername("newUser");
        assertTrue(optPerson.isPresent());
    }

    @Test
    public void testSingUp_UserAlreadyExists() throws Exception {
        LoginRequest loginRequest = new LoginRequest("existingUser", "password");

        when(userEventProducer.sendEvent(any())).thenReturn(true);

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("aviaticketsAccessToken"))
                .andExpect(cookie().exists("aviaticketsRefreshToken"));
        mockMvc.perform(post("/auth/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists: existingUser"))
                .andExpect(jsonPath("$.status").value("Conflict"));

    }
}

