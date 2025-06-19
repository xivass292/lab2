package com.example.javalabaip.service;

import com.example.javalabaip.dto.LocationResponseDto;
import com.example.javalabaip.dto.UserDto;
import com.example.javalabaip.model.User;
import com.example.javalabaip.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto createUser(@Valid UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь с таким именем уже существует");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        User savedUser = userRepository.save(user);

        UserDto responseDto = new UserDto();
        responseDto.setId(savedUser.getId());
        responseDto.setUsername(savedUser.getUsername());
        return responseDto;
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        User user = userRepository.findByIdWithLocations(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setLocations(user.getLocations().stream().map(location -> {
            LocationResponseDto locationDto = new LocationResponseDto();
            locationDto.setIpAddress(location.getIpAddress());
            locationDto.setCity(location.getCity());
            locationDto.setCountry(location.getCountry());
            locationDto.setContinent(location.getContinent());
            locationDto.setLatitude(location.getLatitude());
            locationDto.setLongitude(location.getLongitude());
            locationDto.setTimezone(location.getTimezone());
            return locationDto;
        }).collect(Collectors.toList()));

        return userDto;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            return userDto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(Long id, @Valid UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        if (!user.getUsername().equals(userDto.getUsername()) &&
                userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь с таким именем уже существует");
        }

        user.setUsername(userDto.getUsername());
        User updatedUser = userRepository.save(user);

        UserDto responseDto = new UserDto();
        responseDto.setId(updatedUser.getId());
        responseDto.setUsername(updatedUser.getUsername());
        return responseDto;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        userRepository.deleteById(id);
    }
}