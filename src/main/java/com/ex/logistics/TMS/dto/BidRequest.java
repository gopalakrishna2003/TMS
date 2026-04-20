package com.ex.logistics.TMS.dto;

import com.ex.logistics.TMS.entity.Load;
import com.ex.logistics.TMS.entity.Transporter;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BidRequest {
    private Load load;
    private Transporter t;
    @NotNull
    private Double proposedRate;
    @NotNull
    private int trucksOffered;
}
