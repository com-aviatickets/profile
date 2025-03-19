package com.aviatickets.profile.model;

import com.aviatickets.profile.controller.response.UserDto;
import lombok.*;

@Data
@AllArgsConstructor
public class UserEvent {
    private String operation;
    private UserDto data;

}
