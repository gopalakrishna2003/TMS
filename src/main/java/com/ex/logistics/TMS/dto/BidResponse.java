package com.ex.logistics.TMS.dto;

import com.ex.logistics.TMS.entity.Load;
import com.ex.logistics.TMS.entity.Transporter;
import com.ex.logistics.TMS.enums.BidStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class BidResponse {
    @NotNull
    private UUID bidId;
    private UUID loadId;
    private UUID TransporterId;
    @NotNull
    private Double proposedRate;
    @NotNull
    private int trucksOffered;
    private BidStatus status;
    private Timestamp submittedAt;
}