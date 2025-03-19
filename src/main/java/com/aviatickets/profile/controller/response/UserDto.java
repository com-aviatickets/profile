package com.aviatickets.profile.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String fio;
    private Integer age;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
