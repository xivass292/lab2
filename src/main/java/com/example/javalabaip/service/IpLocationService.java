package com.example.javalabaip.service;

import com.example.javalabaip.dto.LocationResponseDto;
import com.example.javalabaip.dto.UserDto;
import com.example.javalabaip.model.Location;
import com.example.javalabaip.model.User;
import com.example.javalabaip.repository.LocationRepository;
import com.example.javalabaip.repository.UserRepository;
import com.example.javalabaip.util.IpAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IpLocationService {

    private static final Logger logger = LoggerFactory.getLogger(IpLocationService.class);
    private final RestTemplate restTemplate;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public IpLocationService(RestTemplate restTemplate, LocationRepository locationRepository, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Cacheable(value = "ipLocations", key = "#ipAddress")
    public LocationResponseDto getLocationByIp(String ipAddress, UserDto userDto) {
        if (!IpAddressValidator.isValidIpAddress(ipAddress)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный формат IP-адреса: " + ipAddress);
        }

        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден: " + userDto.getUsername()));

        Optional<Location> existingLocation = locationRepository.findByIpAddress(ipAddress);
        if (existingLocation.isPresent()) {
            Location location = existingLocation.get();
            LocationResponseDto response = new LocationResponseDto();
            response.setIpAddress(location.getIpAddress());
            response.setCity(location.getCity());
            response.setCountry(location.getCountry());
            response.setContinent(location.getContinent());
            response.setLatitude(location.getLatitude());
            response.setLongitude(location.getLongitude());
            response.setTimezone(location.getTimezone());
            return response;
        }

        try {
            String apiUrl = "http://ip-api.com/json/" + ipAddress;
            Location location = restTemplate.getForObject(apiUrl, Location.class);

            if (location == null || location.getCity() == null || location.getCountry() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный IP-адрес или ошибка API");
            }

            location.setIpAddress(ipAddress);
            location.setUser(user);
            locationRepository.save(location);

            LocationResponseDto response = new LocationResponseDto();
            response.setIpAddress(location.getIpAddress());
            response.setCity(location.getCity());
            response.setCountry(location.getCountry());
            response.setContinent(location.getContinent());
            response.setLatitude(location.getLatitude());
            response.setLongitude(location.getLongitude());
            response.setTimezone(location.getTimezone());

            return response;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Превышен лимит запросов к API");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный IP-адрес: " + ipAddress, e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка получения данных о местоположении", e);
        }
    }

    @Transactional(readOnly = true)
    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Местоположение не найдено"));
    }

    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Transactional
    public LocationResponseDto updateLocation(Long id, LocationResponseDto locationDto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Местоположение не найдено"));

        location.setIpAddress(locationDto.getIpAddress());
        location.setCity(locationDto.getCity());
        location.setCountry(locationDto.getCountry());
        location.setContinent(locationDto.getContinent());
        location.setLatitude(locationDto.getLatitude());
        location.setLongitude(locationDto.getLongitude());
        location.setTimezone(locationDto.getTimezone());

        locationRepository.save(location);

        LocationResponseDto response = new LocationResponseDto();
        response.setIpAddress(location.getIpAddress());
        response.setCity(location.getCity());
        response.setCountry(location.getCountry());
        response.setContinent(location.getContinent());
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        response.setTimezone(location.getTimezone());

        return response;
    }

    @Transactional
    public boolean deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Местоположение не найдено"));
        locationRepository.delete(location);
        return true;
    }

    @Transactional
    public LocationResponseDto createLocation(LocationResponseDto locationDto, Long userId) {
        if (locationDto == null || locationDto.getIpAddress() == null || locationDto.getIpAddress().trim().isEmpty() ||
                locationDto.getCity() == null || locationDto.getCity().trim().isEmpty() ||
                locationDto.getCountry() == null || locationDto.getCountry().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректные данные местоположения");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден с ID: " + userId));

        Location location = new Location();
        location.setIpAddress(locationDto.getIpAddress());
        location.setCity(locationDto.getCity());
        location.setCountry(locationDto.getCountry());
        location.setContinent(locationDto.getContinent());
        location.setLatitude(locationDto.getLatitude());
        location.setLongitude(locationDto.getLongitude());
        location.setTimezone(locationDto.getTimezone());
        location.setUser(user);

        Location savedLocation = locationRepository.save(location);

        LocationResponseDto response = new LocationResponseDto();
        response.setIpAddress(savedLocation.getIpAddress());
        response.setCity(savedLocation.getCity());
        response.setCountry(savedLocation.getCountry());
        response.setContinent(savedLocation.getContinent());
        response.setLatitude(savedLocation.getLatitude());
        response.setLongitude(savedLocation.getLongitude());
        response.setTimezone(savedLocation.getTimezone());

        return response;
    }
}