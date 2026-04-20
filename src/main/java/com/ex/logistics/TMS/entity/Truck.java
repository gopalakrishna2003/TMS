package com.ex.logistics.TMS.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data

public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "truck_type",nullable = false)
    private String truckType;
    @Column(nullable = false)
    private int count;
    @ManyToOne
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;
}
