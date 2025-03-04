package com.aviatickets.profile.model;

import lombok.*;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class UserEvent {
    private String operation;
    private String data;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
