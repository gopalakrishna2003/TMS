package com.ex.logistics.TMS.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.util.*;

@Entity
@Data
public class Transporter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transporterId;
    @Column(nullable = false)
    private String companyName;
    @DecimalMin(value = "1.0", message = "Rating must be atleast 1")
    @DecimalMax(value = "5.0", message = "Rating must be atmost 5 ")
    @Column(nullable = false)
    private Double rating;
    //relationship
    @ElementCollection
    @CollectionTable(name = "available_trucks",
            joinColumns = @JoinColumn(name = "transporter_id"))
    @MapKeyColumn(name = "truck_type")
    @Column(name = "count")
    private Map<String, Integer> availableTrucks = new HashMap<>();
    @Version
    private Long version;
}