package com.ex.logistics.TMS.dto;

import com.ex.logistics.TMS.enums.LoadStatus;
import com.ex.logistics.TMS.enums.WeightUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class LoadResponse {
    @NotNull
    private UUID LoadId;
    @NotBlank
    private String ShipperId;
    @NotBlank
    private String LoadingCity;
    @NotBlank
    private String UnloadingCity;
    @NotNull
    private Timestamp LoadingDate;
    @NotBlank
    private String ProductType;
    @Positive
    private double weight;
    private WeightUnit weightUnit;
    @NotBlank
    private String TruckType;
    @Positive
    private int noOfTrucks;
    @NotBlank
    private LoadStatus status;
    @NotNull
    private Timestamp DatePosted;
}
