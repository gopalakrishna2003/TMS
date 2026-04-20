package com.ex.logistics.TMS.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TruckRes{
    private UUID id;          // matches Truck entity field name
    private String truckType;
    private int count;

}
