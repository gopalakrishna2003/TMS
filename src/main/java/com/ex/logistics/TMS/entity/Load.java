package com.ex.logistics.TMS.entity;

import com.ex.logistics.TMS.enums.LoadStatus;
import com.ex.logistics.TMS.enums.WeightUnit;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class Load {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "load_id",updatable = false,nullable = false)
    private UUID loadId;
    @Column(name = "Shipper_id",nullable = false)
    private String ShipperId;
    @Column(name = "Loading_city",nullable = false)
    private String LoadingCity;
    @Column(name = "Unloading_city",nullable = false)
    private String UnloadingCity;
    @Column(name = "Loading_Date",nullable = false)
    private Timestamp LoadingDate;
    @Column(name = "Product_Type",nullable = false)
    private String ProductType;
    @Column(name = "weight",nullable = false)
    private double weight;
    @Enumerated(EnumType.STRING)
    @Column(name = "weight_Unit",nullable = false)
    private WeightUnit weightUnit;
    @Column(name = "Truck_Type",nullable = false)
    private String TruckType;
    @Column(name = "no_Of_Trucks",nullable = false)
    private int noOfTrucks;
    private int RemainingTrucks;
    @Enumerated(EnumType.STRING)
    @Column(name = "Status",nullable = false)
    private LoadStatus status;
    @Column(name = "Date_Posted",nullable = false)
    private Timestamp DatePosted;
    //Optimistic locking
    @Version
    @Column(name = "version",nullable = false)
    private Long version;
}
