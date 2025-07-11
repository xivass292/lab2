package com.example.javalabaip.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
    private List<LocationResponseDto> locations = new ArrayList<>();
}