package com.example.javalabaip.controller;

import com.example.javalabaip.dto.LocationResponseDto;
import com.example.javalabaip.dto.UserDto;
import com.example.javalabaip.model.Location;
import com.example.javalabaip.service.IpLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IpLocationController {

    private final IpLocationService ipLocationService;

    @Autowired
    public IpLocationController(IpLocationService ipLocationService) {
        this.ipLocationService = ipLocationService;
    }

    @PostMapping("/location")
    public ResponseEntity<LocationResponseDto> getLocation(@RequestParam("ip") String ipAddress, @RequestBody UserDto userDto) {
        LocationResponseDto response = ipLocationService.getLocationByIp(ipAddress, userDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<Location> getLocation(@PathVariable Long id) {
        Location location = ipLocationService.getLocationById(id);
        return location != null ? ResponseEntity.ok(location) : ResponseEntity.notFound().build();
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = ipLocationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<LocationResponseDto> updateLocation(@PathVariable Long id, @RequestBody LocationResponseDto locationDto) {
        LocationResponseDto updatedLocation = ipLocationService.updateLocation(id, locationDto);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        boolean deleted = ipLocationService.deleteLocation(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationResponseDto> createLocation(@RequestBody LocationResponseDto locationDto, @RequestParam("userId") Long userId) {
        LocationResponseDto createdLocation = ipLocationService.createLocation(locationDto, userId);
        return ResponseEntity.ok(createdLocation);
    }
}