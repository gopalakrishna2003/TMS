package com.ex.logistics.TMS.entity;

import com.ex.logistics.TMS.enums.BidStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bidId;
    @ManyToOne(fetch = FetchType.LAZY)
    private Load load;
    @ManyToOne(fetch = FetchType.LAZY)
    private Transporter t;
    private double proposedRate;
    private int trucksOffered;
    private BidStatus status;
    private Timestamp submittedAt;
}
