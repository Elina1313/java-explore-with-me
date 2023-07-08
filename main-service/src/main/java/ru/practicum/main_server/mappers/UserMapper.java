package ru.practicum.main_server.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.main_server.dtos.user.UserDto;
import ru.practicum.main_server.models.User;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
    User toUserModel(UserDto userDto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);
}
