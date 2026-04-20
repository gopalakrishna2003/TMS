package com.ex.logistics.TMS.dto.Mapper;
import com.ex.logistics.TMS.dto.BidResponse;
import com.ex.logistics.TMS.dto.BookingResponse;
import com.ex.logistics.TMS.entity.Bid;
import com.ex.logistics.TMS.entity.Booking;
import com.ex.logistics.TMS.entity.Load;
import org.springframework.stereotype.Component;



@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .bidId(booking.getBid().getBidId())
                .loadId(booking.getBid().getLoad().getLoadId())
                .transporterId(booking.getBid().getT().getTransporterId())

                .allocatedTrucks(booking.getAllocatedTrucks())
                .finalRate(booking.getFinalRate())
                .bookingStatus(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .loadingCity(booking.getBid().getLoad().getLoadingCity())
                .unloadingCity(booking.getBid().getLoad().getUnloadingCity())
                .loadingDate(booking.getBid().getLoad().getLoadingDate())
                .productType(booking.getBid().getLoad().getProductType())
                .build();
    }
}