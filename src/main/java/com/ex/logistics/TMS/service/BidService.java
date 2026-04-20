package com.ex.logistics.TMS.service;

import com.ex.logistics.TMS.Repository.BidRepo;
import com.ex.logistics.TMS.Repository.LoadRepo;
import com.ex.logistics.TMS.Repository.TransporterRepo;
import com.ex.logistics.TMS.dto.BidRequest;
import com.ex.logistics.TMS.dto.BidResponse;
import com.ex.logistics.TMS.dto.Mapper.BidMapper;
import com.ex.logistics.TMS.entity.Bid;
import com.ex.logistics.TMS.entity.Load;
import com.ex.logistics.TMS.entity.Transporter;
import com.ex.logistics.TMS.enums.BidStatus;
import com.ex.logistics.TMS.enums.LoadStatus;
import com.ex.logistics.TMS.exceptions.InsufficientCapacityException;
import com.ex.logistics.TMS.exceptions.InvalidStatusTransitionException;
import com.ex.logistics.TMS.exceptions.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepo bidRepository;
    private final LoadRepo loadRepository;
    private final TransporterRepo transporterRepository;
    private final BidMapper bidMapper;

    @Transactional
    public BidResponse submitBid(BidRequest request) {
        Load load = loadRepository.findById(request.getLoad().getLoadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Load not found with id: " + request.getLoad().getLoadId()));

        // Rule 2: validate load status
        if (load.getStatus() == LoadStatus.CANCELLED ||
                load.getStatus() == LoadStatus.BOOKED) {
            throw new InvalidStatusTransitionException(
                    "Cannot bid on a " + load.getStatus() + " load");
        }

        Transporter transporter = transporterRepository
                .findById(request.getT().getTransporterId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transporter not found with id: "
                                + request.getT().getTransporterId()));

        // Rule 1: capacity validation
        int available = transporter.getAvailableTrucks()
                .getOrDefault(load.getTruckType(), 0);
        if (request.getTrucksOffered() > available) {
            throw new InsufficientCapacityException(
                    "Transporter only has " + available
                            + " trucks of type " + load.getTruckType()
                            + " available");
        }

        // Rule 2: POSTED → OPEN_FOR_BIDS on first bid
        if (load.getStatus() == LoadStatus.POSTED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
            loadRepository.save(load);
        }

        Bid bid = new Bid();
        bid.setLoad(load);
        bid.setT(transporter);
        bid.setProposedRate(request.getProposedRate());
        bid.setTrucksOffered(request.getTrucksOffered());
        bid.setStatus(BidStatus.PENDING);
        bid.setSubmittedAt(new Timestamp(System.currentTimeMillis()));

        return bidMapper.toResponse(bidRepository.save(bid));
    }

    public BidResponse getBidById(UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bid not found with id: " + bidId));
        return bidMapper.toResponse(bid);
    }

    public List<BidResponse> getBids(UUID loadId,
                                     UUID transporterId, BidStatus status) {
        return bidRepository.findAll((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (loadId != null) {
                        predicates.add(cb.equal(
                                root.get("load").get("loadId"), loadId));
                    }
                    if (transporterId != null) {
                        predicates.add(cb.equal(
                                root.get("transporter").get("transporterId"),
                                transporterId));
                    }
                    if (status != null) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                }).stream()
                .map(bidMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BidResponse rejectBid(UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bid not found with id: " + bidId));

        if (bid.getStatus() != BidStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Only PENDING bids can be rejected");
        }

        bid.setStatus(BidStatus.REJECTED);
        return bidMapper.toResponse(bidRepository.save(bid));
    }
}