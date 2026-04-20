package com.ex.logistics.TMS.dto;

import com.ex.logistics.TMS.entity.Bid;
import com.ex.logistics.TMS.entity.Load;
import com.ex.logistics.TMS.enums.BookingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class BookingResponse{
    private UUID bookingId;
    private UUID loadId;
    private UUID transporterId;
    private UUID bidId;
    @Positive
    private int allocatedTrucks;
    @Positive
    private double finalRate;
    private BookingStatus bookingStatus;
    @NotBlank
    private Timestamp bookedAt;
    //load details
    private String loadingCity;
    private String unloadingCity;
    private Timestamp loadingDate;
    private String productType;
}
