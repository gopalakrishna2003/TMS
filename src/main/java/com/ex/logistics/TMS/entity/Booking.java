package com.ex.logistics.TMS.entity;

import com.ex.logistics.TMS.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID bookingId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Bid_Id",nullable = false)
    private Bid bid;
    @Column(nullable = false)
    private int allocatedTrucks;
    @Column(nullable = false)
    private double finalRate;
    @Column(nullable = false)
    private BookingStatus status;
    @Column(nullable = false)
    private Timestamp bookedAt;

}
