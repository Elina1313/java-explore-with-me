package ru.practicum.main_server.Services.User;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_server.dtos.user.UserDto;
import ru.practicum.main_server.models.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    void deleteUser(Long userId);

    User getUserById(Long userId);
}
