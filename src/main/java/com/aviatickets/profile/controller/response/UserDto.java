package com.aviatickets.profile.controller.response;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class UserDto {
    Long id;
    String fio;
    Integer age;
    String email;
    String phone;
    String username;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
}
