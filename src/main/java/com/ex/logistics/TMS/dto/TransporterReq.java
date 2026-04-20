package com.ex.logistics.TMS.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TransporterReq {
        private String companyName;
        @DecimalMin("1.0")
        @DecimalMax("5.0")
        private double rating;
        @NotNull
        private Map<String, Integer> availableTrucks;
}
