package com.ex.logistics.TMS.service;

import com.ex.logistics.TMS.Repository.TransporterRepo;
import com.ex.logistics.TMS.dto.Mapper.TransportMapper;
import com.ex.logistics.TMS.dto.TransporterReq;
import com.ex.logistics.TMS.dto.TransporterRes;
import com.ex.logistics.TMS.entity.Transporter;
import com.ex.logistics.TMS.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransporterService {
    private final TransporterRepo transporterRepository;
    private final TransportMapper transporterMapper;

    @Transactional
    public TransporterRes createTransporter(TransporterReq request) {
        Transporter transporter = transporterMapper.toEntity(request);
        Transporter saved = transporterRepository.save(transporter);
        return transporterMapper.toResponse(saved);
    }
    public TransporterRes getTransporterById(UUID transporterId) {
        Transporter transporter = transporterRepository
                .findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transporter not found with id: " + transporterId));
        return transporterMapper.toResponse(transporter);
    }

    @Transactional
    public TransporterRes updateTrucks(UUID transporterId,
                                            Map<String, Integer> updatedTrucks) {
        Transporter transporter = transporterRepository
                .findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transporter not found with id: " + transporterId));

        transporter.setAvailableTrucks(new HashMap<>(updatedTrucks));
        Transporter saved = transporterRepository.save(transporter);
        return transporterMapper.toResponse(saved);
    }

}
