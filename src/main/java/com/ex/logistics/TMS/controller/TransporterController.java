package com.ex.logistics.TMS.controller;

import com.ex.logistics.TMS.dto.TransporterReq;
import com.ex.logistics.TMS.dto.TransporterRes;
import com.ex.logistics.TMS.service.TransporterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transporter")
@RequiredArgsConstructor
@Tag(name = "Transporter APIs", description = "Manage transporters and capacity")
public class TransporterController {

    private final TransporterService transporterService;

    @PostMapping
    @Operation(summary = "Register a new transporter",
            description = "Include availableTrucks map")
    public ResponseEntity<TransporterRes> createTransporter(
            @Valid @RequestBody TransporterReq request) {
        TransporterRes response = transporterService
                .createTransporter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{transporterId}")
    @Operation(summary = "Get transporter details")
    public ResponseEntity<TransporterRes> getTransporterById(
            @PathVariable UUID transporterId) {
        TransporterRes response = transporterService
                .getTransporterById(transporterId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{transporterId}/trucks")
    @Operation(summary = "Update available trucks",
            description = "Replace entire truck capacity map")
    public ResponseEntity<TransporterRes> updateTrucks(
            @PathVariable UUID transporterId,
            @RequestBody Map<String, Integer> trucks) {
        TransporterRes response = transporterService
                .updateTrucks(transporterId, trucks);
        return ResponseEntity.ok(response);
    }
}