package com.aviatickets.profile.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
        private String username;
        private String email;
        private String phone;
        private String fio;
        private int age;
}
