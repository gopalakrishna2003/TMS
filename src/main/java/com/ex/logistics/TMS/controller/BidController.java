package com.ex.logistics.TMS.controller;

import com.ex.logistics.TMS.dto.BidRequest;
import com.ex.logistics.TMS.dto.BidResponse;
import com.ex.logistics.TMS.enums.BidStatus;
import com.ex.logistics.TMS.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bid")
@RequiredArgsConstructor
@Tag(name = "Bid APIs", description = "Manage bids on loads")
public class BidController {

    private final BidService bidService;

    @PostMapping
    @Operation(summary = "Submit a bid on a load",
            description = "Validates capacity and load status. " +
                    "Transitions load to OPEN_FOR_BIDS if first bid.")
    public ResponseEntity<BidResponse> submitBid(
            @Valid @RequestBody BidRequest request) {
        BidResponse response = bidService.submitBid(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Filter bids",
            description = "By loadId, transporterId, and/or status")
    public ResponseEntity<List<BidResponse>> getBids(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) UUID transporterId,
            @RequestParam(required = false) BidStatus status) {
        List<BidResponse> response = bidService.getBids(
                loadId, transporterId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bidId}")
    @Operation(summary = "Get bid details")
    public ResponseEntity<BidResponse> getBidById(
            @PathVariable UUID bidId) {
        BidResponse response = bidService.getBidById(bidId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{bidId}/reject")
    @Operation(summary = "Reject a bid",
            description = "Only PENDING bids can be rejected")
    public ResponseEntity<BidResponse> rejectBid(
            @PathVariable UUID bidId) {
        BidResponse response = bidService.rejectBid(bidId);
        return ResponseEntity.ok(response);
    }
}