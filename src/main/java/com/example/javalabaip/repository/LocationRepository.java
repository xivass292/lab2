package com.example.javalabaip.repository;

import com.example.javalabaip.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByUserId(Long userId);
    Optional<Location> findByIpAddress(String ipAddress);
}