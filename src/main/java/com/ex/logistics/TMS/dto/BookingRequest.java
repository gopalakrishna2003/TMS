package com.ex.logistics.TMS.dto;

import com.ex.logistics.TMS.entity.Bid;
import com.ex.logistics.TMS.entity.Load;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BookingRequest {
    private UUID bidId;
    @NotNull
    private int allocatedTrucks;
    @NotNull
    private double finalRate;
}
