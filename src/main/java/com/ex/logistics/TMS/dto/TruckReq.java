package com.ex.logistics.TMS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TruckReq {
    @NotBlank
    private String truckType;
    @Positive
    private int count;
}
