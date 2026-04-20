package com.ex.logistics.TMS.Repository;

import com.ex.logistics.TMS.entity.Booking;
import com.ex.logistics.TMS.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface BookingRepo extends JpaRepository<Booking, UUID> {

    List<Booking> findByBid_Load_LoadId(UUID loadId);

    List<Booking> findByBid_Load_LoadIdAndStatus(UUID loadId,
                                                 BookingStatus status);
}
