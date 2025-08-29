package co.com.crediya.consumer.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record UserResponse(
    String name,
    String lastName,
    Long identification,
    LocalDate birthDate,
    String address,
    long phone,
    String email,
    Long rolId,
    double baseSalary

){}
