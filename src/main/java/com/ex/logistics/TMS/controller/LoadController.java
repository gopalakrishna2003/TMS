package com.ex.logistics.TMS.controller;

import com.ex.logistics.TMS.dto.BidResponse;
import com.ex.logistics.TMS.dto.LoadRequest;
import com.ex.logistics.TMS.dto.LoadResponse;
import com.ex.logistics.TMS.enums.LoadStatus;
import com.ex.logistics.TMS.service.LoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/load")
@RequiredArgsConstructor
@Tag(name = "Load APIs", description = "Manage loads")
public class LoadController {

    private final LoadService loadService;

    @PostMapping
    @Operation(summary = "Create a new load", description = "Status starts as POSTED")
    public ResponseEntity<LoadResponse> createLoad(
            @Valid @RequestBody LoadRequest request) {
        LoadResponse response = loadService.createLoad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List loads with pagination",
            description = "Filter by shipperId and/or status")
    public Page<LoadResponse> listLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) LoadStatus status,
            Pageable pageable) {
        return loadService.getLoads(shipperId, status, pageable);
    }

    @GetMapping("/{loadId}")
    @Operation(summary = "Get load details")
    public ResponseEntity<LoadResponse> getLoadById(
            @PathVariable UUID loadId) {
        LoadResponse response = loadService.getLoadById(loadId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{loadId}/cancel")
    @Operation(summary = "Cancel a load",
            description = "Cannot cancel if status is BOOKED")
    public ResponseEntity<Void> cancelLoad(
            @PathVariable UUID loadId) {
        loadService.cancelLoad(loadId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{loadId}/best-bids")
    @Operation(summary = "Get best bids for a load",
            description = "Sorted by score: (1/rate)*0.7 + (rating/5)*0.3")
    public ResponseEntity<List<BidResponse>> getBestBids(
            @PathVariable UUID loadId) {
        List<BidResponse> response = loadService.getBestBids(loadId);
        return ResponseEntity.ok(response);
    }
}