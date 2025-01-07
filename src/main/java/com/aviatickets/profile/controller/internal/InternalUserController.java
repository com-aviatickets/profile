package com.aviatickets.profile.controller.internal;
import com.aviatickets.profile.controller.response.UserDto;
import com.aviatickets.profile.service.UserService;
import com.aviatickets.profile.util.http.HttpUtils;
import com.aviatickets.profile.util.http.PageableResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.aviatickets.profile.controller.ControllerConstants.INTERNAL;

@RestController
@RequestMapping(INTERNAL + "/user")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping
    public PageableResult<UserDto> findAll (Pageable pageable) {
        return HttpUtils.pageableOk(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }

}
