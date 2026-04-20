package com.ex.logistics.TMS.dto.Mapper;

import com.ex.logistics.TMS.dto.BidResponse;
import com.ex.logistics.TMS.entity.Bid;
import org.springframework.stereotype.Component;

@Component
public class BidMapper {

    public BidResponse toResponse(Bid bid) {
            return BidResponse.builder()
                    .bidId(bid.getBidId())
                    .loadId(bid.getLoad().getLoadId())
                    .TransporterId(bid.getT().getTransporterId())
                    .proposedRate(bid.getProposedRate())
                    .trucksOffered(bid.getTrucksOffered())
                    .status(bid.getStatus())
                    .submittedAt(bid.getSubmittedAt())
                    .build();
        }
}
