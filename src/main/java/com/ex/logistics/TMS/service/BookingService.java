package com.ex.logistics.TMS.service;

import com.ex.logistics.TMS.Repository.BidRepo;
import com.ex.logistics.TMS.Repository.BookingRepo;
import com.ex.logistics.TMS.Repository.LoadRepo;
import com.ex.logistics.TMS.Repository.TransporterRepo;
import com.ex.logistics.TMS.dto.BookingRequest;
import com.ex.logistics.TMS.dto.BookingResponse;
import com.ex.logistics.TMS.dto.Mapper.BookingMapper;
import com.ex.logistics.TMS.entity.Bid;
import com.ex.logistics.TMS.entity.Booking;
import com.ex.logistics.TMS.entity.Load;
import com.ex.logistics.TMS.entity.Transporter;
import com.ex.logistics.TMS.enums.BidStatus;
import com.ex.logistics.TMS.enums.BookingStatus;
import com.ex.logistics.TMS.enums.LoadStatus;
import com.ex.logistics.TMS.exceptions.InsufficientCapacityException;
import com.ex.logistics.TMS.exceptions.InvalidStatusTransitionException;
import com.ex.logistics.TMS.exceptions.LoadAlreadyBookedException;
import com.ex.logistics.TMS.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepository;
    private final BidRepo bidRepository;
    private final LoadRepo loadRepository;
    private final TransporterRepo transporterRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Bid bid = bidRepository.findById(request.getBidId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bid not found with id: " + request.getBidId()));

        // validate bid is still pending
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Only PENDING bids can be accepted");
        }

        Load load = bid.getLoad();

        // Rule 2: block if load is not biddable
        if (load.getStatus() == LoadStatus.CANCELLED ||
                load.getStatus() == LoadStatus.BOOKED) {
            throw new LoadAlreadyBookedException(
                    "Load is already " + load.getStatus());
        }

        Transporter transporter = bid.getT();

        // Rule 1: re-validate capacity at booking time
        String truckType = load.getTruckType();
        int available = transporter.getAvailableTrucks()
                .getOrDefault(truckType, 0);
        if (bid.getTrucksOffered() > available) {
            throw new InsufficientCapacityException(
                    "Transporter no longer has enough trucks available");
        }

        // Rule 1: deduct trucks from transporter
        transporter.getAvailableTrucks()
                .put(truckType, available - bid.getTrucksOffered());
        transporterRepository.save(transporter);

        // Rule 3: update remainingTrucks on load
        int remaining = load.getRemainingTrucks() - bid.getTrucksOffered();
        load.setRemainingTrucks(remaining);

        // Rule 2: transition load to BOOKED if fully allocated
        if (remaining == 0) {
            load.setStatus(LoadStatus.BOOKED);
        }

        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);

        // Rule 4: @Version triggers optimistic lock here
        try {
            loadRepository.save(load);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new LoadAlreadyBookedException(
                    "Booking conflict detected. Another booking was " +
                            "confirmed simultaneously. Please retry.");
        }

        Booking booking = new Booking();
        booking.setBid(bid);
        booking.setAllocatedTrucks(bid.getTrucksOffered());
        booking.setFinalRate(bid.getProposedRate());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(new Timestamp(System.currentTimeMillis()));

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    public BookingResponse getBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));
        return bookingMapper.toResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidStatusTransitionException(
                    "Booking is already CANCELLED");
        }

        Bid bid = booking.getBid();
        Load load = bid.getLoad();
        Transporter transporter = bid.getT();

        // Rule 1: restore trucks back to transporter
        String truckType = load.getTruckType();
        int current = transporter.getAvailableTrucks()
                .getOrDefault(truckType, 0);
        transporter.getAvailableTrucks()
                .put(truckType, current + booking.getAllocatedTrucks());
        transporterRepository.save(transporter);

        // Rule 3: restore remainingTrucks on load
        load.setRemainingTrucks(
                load.getRemainingTrucks() + booking.getAllocatedTrucks());

        // Rule 2: revert load status back to OPEN_FOR_BIDS
        load.setStatus(LoadStatus.OPEN_FOR_BIDS);
        loadRepository.save(load);

        // revert bid status
        bid.setStatus(BidStatus.REJECTED);
        bidRepository.save(bid);

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }
}
