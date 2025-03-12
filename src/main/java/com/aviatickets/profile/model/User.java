package com.aviatickets.profile.model;

import com.aviatickets.profile.controller.request.UpdateUserRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "profile_users")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_user_seq")
    @SequenceGenerator(
            name = "profile_user_seq",
            sequenceName = "profile_user_seq",
            allocationSize = 1
    )
    private Long id;
    private String fio;
    private Integer age;
    private String email;
    private String phone;
    private String username;
    private String password;
    @CreationTimestamp
    private ZonedDateTime createdAt;
    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public void merge(UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.getFio() != null) {
            this.fio = updateUserRequest.getFio();

        }

        if (updateUserRequest.getEmail() != null) {
            this.email = updateUserRequest.getEmail();

        }
        if (updateUserRequest.getAge() != 0) {
            this.age = updateUserRequest.getAge();

        }
        if (updateUserRequest.getUsername() != null) {
            this.username = updateUserRequest.getUsername();

        }

        if (updateUserRequest.getPhone() != null) {
            this.phone = updateUserRequest.getPhone();

        }



    }

}
