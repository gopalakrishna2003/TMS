package com.ex.logistics.TMS.controller;

import com.ex.logistics.TMS.dto.BookingRequest;
import com.ex.logistics.TMS.dto.BookingResponse;
import com.ex.logistics.TMS.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@Tag(name = "Booking APIs", description = "Manage bookings (accepted bids)")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Accept a bid and create a booking",
            description = "Deducts trucks from transporter. " +
                    "Handles optimistic locking for concurrent bookings.")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking details")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable UUID bookingId) {
        BookingResponse response = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking",
            description = "Restores trucks to transporter. " +
                    "Reverts load status to OPEN_FOR_BIDS.")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable UUID bookingId) {
        BookingResponse response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(response);
    }
}