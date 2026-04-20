package com.ex.logistics.TMS.Repository;

import com.ex.logistics.TMS.entity.Bid;
import com.ex.logistics.TMS.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface BidRepo extends JpaRepository< Bid, UUID>, JpaSpecificationExecutor<Bid> {

    List<Bid> findByLoad_LoadId(UUID loadId);

    List<Bid> findByLoad_LoadIdAndStatus(UUID loadId, BidStatus status);

    List<Bid> findByT_TransporterId(UUID transporterId);

    boolean existsByLoad_LoadIdAndStatus(UUID loadId, BidStatus status);

    long countByLoad_LoadIdAndStatus(UUID loadId, BidStatus status);

}
