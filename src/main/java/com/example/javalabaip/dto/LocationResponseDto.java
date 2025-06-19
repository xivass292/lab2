package com.example.javalabaip.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LocationResponseDto {
    @NotBlank(message = "IP-адрес не может быть пустым")
    private String ipAddress;
    @NotBlank(message = "Город не может быть пустым")
    private String city;
    @NotBlank(message = "Страна не может быть пустой")
    private String country;
    private String continent;
    private Double latitude;
    private Double longitude;
    private String timezone;
}