package com.ex.logistics.TMS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;
@Data
@Builder
public class TransporterRes {
    @NotNull
    private UUID transporterId;
    @NotBlank
    private String companyName;
    @NotNull
    private Double rating;
    private Map<String, Integer> availableTrucks;
    private String createTransporter;
}
