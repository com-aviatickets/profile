package com.aviatickets.profile.mapper;

import com.aviatickets.profile.controller.response.UserDto;
import com.aviatickets.profile.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto modelToDto(User user);

}
