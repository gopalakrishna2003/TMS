package com.ex.logistics.TMS.service;

import com.ex.logistics.TMS.Repository.BidRepo;
import com.ex.logistics.TMS.Repository.LoadRepo;
import com.ex.logistics.TMS.dto.BidResponse;
import com.ex.logistics.TMS.dto.LoadRequest;
import com.ex.logistics.TMS.dto.LoadResponse;
import com.ex.logistics.TMS.dto.Mapper.BidMapper;
import com.ex.logistics.TMS.dto.Mapper.LoadMapper;
import com.ex.logistics.TMS.entity.Load;
import com.ex.logistics.TMS.enums.BidStatus;
import com.ex.logistics.TMS.enums.LoadStatus;
import com.ex.logistics.TMS.exceptions.InvalidStatusTransitionException;
import com.ex.logistics.TMS.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoadService {
    private final LoadRepo loadRepository;
    private final BidRepo bidRepository;
    private final BidMapper bidMapper;
    private final LoadMapper loadMapper;
    @Transactional
    public LoadResponse createLoad(LoadRequest request){
        Load load = loadMapper.toEntity(request);
        return loadMapper.toResponse(loadRepository.save(load));
    }
    public LoadResponse getLoadById(UUID loadId) {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found with id:" + loadId));
        return loadMapper.toResponse(load);
    }
    public Page<LoadResponse> getLoads(String shipperId, LoadStatus status, Pageable pageable){
        if (shipperId!= null && status != null) {
            return loadRepository
                    .findByShipperIdAndStatus(shipperId, status, pageable)
                    .map(loadMapper::toResponse);
        } else if (shipperId != null) {
            return loadRepository
                    .findByShipperId(shipperId, pageable)
                    .map(loadMapper::toResponse);
        } else if (status != null) {
            return loadRepository
                    .findByStatus(status, pageable)
                    .map(loadMapper::toResponse);
        }else {
            return loadRepository
                    .findAll(pageable)
                    .map(loadMapper::toResponse);
        }
        }
        @Transactional
    public void cancelLoad(UUID loadId){
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found with id:" +loadId));
            if (load.getStatus() == LoadStatus.BOOKED) {
                throw new InvalidStatusTransitionException(
                        "Cannot cancel a BOOKED load");
            }

            if (load.getStatus() == LoadStatus.CANCELLED) {
                throw new InvalidStatusTransitionException(
                        "Load is already CANCELLED");
            }

            load.setStatus(LoadStatus.CANCELLED);
            loadRepository.save(load);
        }
        public List<BidResponse> getBestBids(UUID loadId){   //learn stream
            loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found with id:" + loadId));
            return bidRepository
                    .findByLoad_LoadIdAndStatus(loadId, BidStatus.PENDING)
                    .stream()
                    .sorted(Comparator.comparingDouble(bid -> -((1.0/ bid.getProposedRate()) * 0.7 + (bid.getT().getRating()/5.0) *0.3)))
                    .map(bidMapper::toResponse)
                    .collect(Collectors.toList());
    }
}
