package com.aviatickets.profile.controller.request;

import jakarta.validation.ValidationException;
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

        public void validate(){
                if (this.age <= 0) {
                        throw new ValidationException("Age must be greater than 0");
                }
                if (!this.email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                        throw new ValidationException("Invalid email address");
                }
        }
}
