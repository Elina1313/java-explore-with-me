package ru.practicum.main_server.Services.User;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main_server.dtos.user.UserDto;
import ru.practicum.main_server.exceptions.NameAlreadyExistException;
import ru.practicum.main_server.mappers.UserMapper;
import ru.practicum.main_server.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            throw new NameAlreadyExistException(String.format("Unable to create user name: %s", userDto.getName()));
        }
        return userMapper.toUserDto(userRepository.save(userMapper.toUserModel(userDto)));

    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        return ids != null ? userMapper.toUserDtoList(userRepository.findAllById(ids))
                : userMapper.toUserDtoList(userRepository.findAll(pageable).toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
