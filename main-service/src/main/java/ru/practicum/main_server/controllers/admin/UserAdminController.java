package ru.practicum.main_server.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_server.Services.User.UserServiceImpl;
import ru.practicum.main_server.dtos.user.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/users")
public class UserAdminController {
    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false, name = "ids") List<Long> ids,
                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.getUsers(ids, PageRequest.of(from / size, size));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@NotNull @PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
